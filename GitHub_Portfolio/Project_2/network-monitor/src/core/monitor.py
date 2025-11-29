"""
Network Performance Monitor - Core Module

This module implements the main monitoring engine for real-time network
performance analysis. It coordinates latency, throughput, and packet loss
measurements across multiple network targets.

Author: Network Engineering Lab
Date: 2025
"""

import time
import threading
import logging
from typing import List, Dict, Optional
from dataclasses import dataclass
from datetime import datetime

from .latency import LatencyMonitor
from .packet_loss import PacketLossAnalyzer
from ..database.db_manager import DatabaseManager
from ..alerts.alert_manager import AlertManager
from ..utils.config import ConfigManager


@dataclass
class MonitorTarget:
    """
    Represents a network monitoring target.
    
    Attributes:
        host: IP address or hostname
        name: Friendly name for the target
        enabled: Whether monitoring is active
    """
    host: str
    name: str
    enabled: bool = True


@dataclass
class NetworkMetrics:
    """
    Container for network performance metrics.
    
    Attributes:
        timestamp: Measurement time
        target: Target identifier
        latency_ms: Round-trip time in milliseconds
        packet_loss_pct: Packet loss percentage
        jitter_ms: Inter-packet delay variation
    """
    timestamp: datetime
    target: str
    latency_ms: float
    packet_loss_pct: float
    jitter_ms: float


class NetworkMonitor:
    """
    Main network monitoring engine.
    
    Coordinates multiple monitoring components and manages the collection,
    storage, and analysis of network performance metrics.
    """
    
    def __init__(self, config_path: str = "config/config.yaml"):
        """
        Initialize the network monitor.
        
        Args:
            config_path: Path to YAML configuration file
        """
        self.logger = logging.getLogger(__name__)
        self.config = ConfigManager(config_path)
        
        # Initialize components
        self.db_manager = DatabaseManager(self.config.get("database.path"))
        self.alert_manager = AlertManager(self.config)
        self.latency_monitor = LatencyMonitor()
        self.packet_loss_analyzer = PacketLossAnalyzer()
        
        # Load monitoring targets
        self.targets = self._load_targets()
        
        # Control variables
        self.running = False
        self.monitor_threads: List[threading.Thread] = []
        self.interval = self.config.get("monitoring.interval", 5)
        
        self.logger.info(f"NetworkMonitor initialized with {len(self.targets)} targets")
    
    def _load_targets(self) -> List[MonitorTarget]:
        """
        Load monitoring targets from configuration.
        
        Returns:
            List of MonitorTarget objects
        """
        targets = []
        target_configs = self.config.get("monitoring.targets", [])
        
        for config in target_configs:
            target = MonitorTarget(
                host=config["host"],
                name=config.get("name", config["host"])
            )
            targets.append(target)
            self.logger.debug(f"Loaded target: {target.name} ({target.host})")
        
        return targets
    
    def start(self):
        """
        Start the monitoring system.
        
        Creates monitoring threads for each target and begins data collection.
        """
        if self.running:
            self.logger.warning("Monitor already running")
            return
        
        self.running = True
        self.logger.info("Starting network monitor")
        
        # Start monitoring thread for each target
        for target in self.targets:
            if target.enabled:
                thread = threading.Thread(
                    target=self._monitor_target,
                    args=(target,),
                    daemon=True,
                    name=f"Monitor-{target.name}"
                )
                thread.start()
                self.monitor_threads.append(thread)
                self.logger.info(f"Started monitoring thread for {target.name}")
        
        # Wait for threads (blocks until Ctrl+C)
        try:
            while self.running:
                time.sleep(1)
        except KeyboardInterrupt:
            self.logger.info("Received interrupt signal")
            self.stop()
    
    def stop(self):
        """
        Stop the monitoring system.
        
        Signals all monitoring threads to terminate and waits for cleanup.
        """
        self.logger.info("Stopping network monitor")
        self.running = False
        
        # Wait for all threads to complete
        for thread in self.monitor_threads:
            thread.join(timeout=5)
        
        self.monitor_threads.clear()
        self.logger.info("Network monitor stopped")
    
    def _monitor_target(self, target: MonitorTarget):
        """
        Monitoring loop for a single target.
        
        Args:
            target: Target to monitor
        """
        self.logger.info(f"Monitoring {target.name} started")
        previous_latency = None
        
        while self.running:
            try:
                # Perform measurements
                latency = self.latency_monitor.measure(target.host)
                packet_loss = self.packet_loss_analyzer.analyze(
                    target.host, 
                    count=10
                )
                
                # Calculate jitter
                jitter = 0.0
                if previous_latency is not None and latency is not None and previous_latency > 0:
                    jitter = abs(latency - previous_latency)
                previous_latency = latency
                
                # Create metrics object
                if latency is not None:
                    metrics = NetworkMetrics(
                        timestamp=datetime.now(),
                        target=target.name,
                        latency_ms=latency,
                        packet_loss_pct=packet_loss,
                        jitter_ms=jitter
                    )
                    
                    # Store metrics
                    self._store_metrics(metrics)
                    
                    # Check thresholds and generate alerts
                    self._check_thresholds(metrics)
                    
                    # Log current status
                    self.logger.debug(
                        f"{target.name}: latency={latency:.2f}ms, "
                        f"loss={packet_loss:.2f}%, jitter={jitter:.2f}ms"
                    )
                else:
                    self.logger.warning(f"Failed to measure {target.name}")
                
            except Exception as e:
                self.logger.error(f"Error monitoring {target.name}: {e}")
            
            # Wait for next interval
            time.sleep(self.interval)
        
        self.logger.info(f"Monitoring {target.name} stopped")
    
    def _store_metrics(self, metrics: NetworkMetrics):
        """
        Store metrics in database.
        
        Args:
            metrics: Metrics to store
        """
        try:
            # Store latency
            self.db_manager.insert_metric(
                timestamp=metrics.timestamp,
                target=metrics.target,
                metric_type="latency",
                value=metrics.latency_ms,
                unit="ms"
            )
            
            # Store packet loss
            self.db_manager.insert_metric(
                timestamp=metrics.timestamp,
                target=metrics.target,
                metric_type="packet_loss",
                value=metrics.packet_loss_pct,
                unit="percent"
            )
            
            # Store jitter
            self.db_manager.insert_metric(
                timestamp=metrics.timestamp,
                target=metrics.target,
                metric_type="jitter",
                value=metrics.jitter_ms,
                unit="ms"
            )
            
        except Exception as e:
            self.logger.error(f"Failed to store metrics: {e}")
    
    def _check_thresholds(self, metrics: NetworkMetrics):
        """
        Check metrics against configured thresholds.
        
        Args:
            metrics: Metrics to check
        """
        # Check latency thresholds
        latency_warn = self.config.get("thresholds.latency_warning", 100)
        latency_crit = self.config.get("thresholds.latency_critical", 200)
        
        if metrics.latency_ms >= latency_crit:
            self.alert_manager.trigger_alert(
                severity="CRITICAL",
                message=f"{metrics.target}: Latency {metrics.latency_ms:.2f}ms exceeds critical threshold"
            )
        elif metrics.latency_ms >= latency_warn:
            self.alert_manager.trigger_alert(
                severity="WARNING",
                message=f"{metrics.target}: Latency {metrics.latency_ms:.2f}ms exceeds warning threshold"
            )
        
        # Check packet loss thresholds
        loss_warn = self.config.get("thresholds.packet_loss_warning", 1.0)
        loss_crit = self.config.get("thresholds.packet_loss_critical", 5.0)
        
        if metrics.packet_loss_pct >= loss_crit:
            self.alert_manager.trigger_alert(
                severity="CRITICAL",
                message=f"{metrics.target}: Packet loss {metrics.packet_loss_pct:.2f}% exceeds critical threshold"
            )
        elif metrics.packet_loss_pct >= loss_warn:
            self.alert_manager.trigger_alert(
                severity="WARNING",
                message=f"{metrics.target}: Packet loss {metrics.packet_loss_pct:.2f}% exceeds warning threshold"
            )
    
    def get_statistics(self, target: str, duration_hours: int = 24) -> Dict:
        """
        Get statistical summary for a target.
        
        Args:
            target: Target name
            duration_hours: Time period to analyze
            
        Returns:
            Dictionary containing statistics
        """
        return self.db_manager.get_statistics(target, duration_hours)


def main():
    """Main entry point for command-line execution."""
    import argparse
    
    # Configure logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    parser = argparse.ArgumentParser(
        description='Real-Time Network Performance Monitor'
    )
    parser.add_argument(
        '--config',
        default='config/config.yaml',
        help='Configuration file path'
    )
    parser.add_argument(
        '--host',
        help='Single host to monitor (overrides config)'
    )
    parser.add_argument(
        '--interval',
        type=int,
        help='Monitoring interval in seconds'
    )
    
    args = parser.parse_args()
    
    # Initialize and start monitor
    monitor = NetworkMonitor(config_path=args.config)
    
    if args.host:
        # Override config with single host
        monitor.targets = [MonitorTarget(host=args.host, name=args.host)]
    
    if args.interval:
        monitor.interval = args.interval
    
    monitor.start()


if __name__ == "__main__":
    main()
