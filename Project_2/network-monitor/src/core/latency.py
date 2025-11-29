"""
Latency Measurement Module

Implements ICMP-based latency measurement using ping methodology.
Measures Round-Trip Time (RTT) between local host and remote target.

Reference: RFC 792 - Internet Control Message Protocol
"""

import platform
import subprocess
import re
import statistics
from typing import Optional, List
from dataclasses import dataclass
import logging


@dataclass
class LatencyStats:
    """
    Statistical summary of latency measurements.
    
    Attributes:
        min_ms: Minimum latency observed
        max_ms: Maximum latency observed
        avg_ms: Average latency
        stddev_ms: Standard deviation
        percentile_95_ms: 95th percentile latency
        samples: Number of measurements
    """
    min_ms: float
    max_ms: float
    avg_ms: float
    stddev_ms: float
    percentile_95_ms: float
    samples: int


class LatencyMonitor:
    """
    Monitors network latency using ICMP echo request/reply.
    
    This class provides cross-platform latency measurement capabilities
    using the system's native ping utility.
    """
    
    def __init__(self):
        """Initialize the latency monitor."""
        self.logger = logging.getLogger(__name__)
        self.system = platform.system().lower()
        self.logger.debug(f"LatencyMonitor initialized for {self.system}")
    
    def measure(self, host: str, count: int = 1, timeout: int = 2) -> Optional[float]:
        """
        Measure latency to a target host.
        
        Args:
            host: Target IP address or hostname
            count: Number of ping packets to send
            timeout: Timeout in seconds for each ping
            
        Returns:
            Average latency in milliseconds, or None if measurement failed
        """
        try:
            # Build platform-specific ping command
            if self.system == "windows":
                cmd = ["ping", "-n", str(count), "-w", str(timeout * 1000), host]
            else:  # Linux, macOS
                cmd = ["ping", "-c", str(count), "-W", str(timeout), host]
            
            # Execute ping command
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=timeout * count + 2
            )
            
            if result.returncode == 0:
                # Parse output to extract latency
                latency = self._parse_ping_output(result.stdout)
                if latency is not None:
                    self.logger.debug(f"Latency to {host}: {latency:.2f}ms")
                return latency
            else:
                self.logger.warning(f"Ping to {host} failed: {result.stderr}")
                return None
                
        except subprocess.TimeoutExpired:
            self.logger.warning(f"Ping to {host} timed out")
            return None
        except Exception as e:
            self.logger.error(f"Error measuring latency to {host}: {e}")
            return None
    
    def measure_detailed(self, host: str, count: int = 10) -> Optional[LatencyStats]:
        """
        Perform detailed latency measurement with statistics.
        
        Args:
            host: Target IP address or hostname
            count: Number of measurements to perform
            
        Returns:
            LatencyStats object or None if measurement failed
        """
        latencies: List[float] = []
        
        for i in range(count):
            latency = self.measure(host, count=1)
            if latency is not None:
                latencies.append(latency)
        
        if not latencies:
            self.logger.warning(f"No successful measurements for {host}")
            return None
        
        # Calculate statistics
        try:
            # Calculate 95th percentile only if enough samples
            if len(latencies) >= 20:
                percentile_95 = statistics.quantiles(latencies, n=20)[18]
            else:
                # Use max for small sample sets
                percentile_95 = max(latencies)
            
            stats = LatencyStats(
                min_ms=min(latencies),
                max_ms=max(latencies),
                avg_ms=statistics.mean(latencies),
                stddev_ms=statistics.stdev(latencies) if len(latencies) > 1 else 0.0,
                percentile_95_ms=percentile_95,
                samples=len(latencies)
            )
            
            self.logger.info(
                f"Detailed stats for {host}: avg={stats.avg_ms:.2f}ms, "
                f"min={stats.min_ms:.2f}ms, max={stats.max_ms:.2f}ms, "
                f"stddev={stats.stddev_ms:.2f}ms"
            )
            
            return stats
            
        except Exception as e:
            self.logger.error(f"Error calculating statistics: {e}")
            return None
    
    def _parse_ping_output(self, output: str) -> Optional[float]:
        """
        Parse ping command output to extract latency.
        
        Args:
            output: Raw ping command output
            
        Returns:
            Average latency in milliseconds or None
        """
        try:
            if self.system == "windows":
                # Windows format: "Average = XXXms"
                match = re.search(r'Average\s*=\s*(\d+)ms', output)
                if match:
                    return float(match.group(1))
                # Alternative format: "time=XXms" or "time<1ms"
                matches = re.findall(r'time[=<](\d+)ms', output)
                if matches:
                    return statistics.mean([float(x) for x in matches])
            else:
                # Linux/macOS format: "rtt min/avg/max/mdev = X/Y/Z/W ms"
                match = re.search(r'rtt min/avg/max/mdev = [\d.]+/([\d.]+)/[\d.]+/[\d.]+ ms', output)
                if match:
                    return float(match.group(1))
                # Alternative format: "time=XX.X ms"
                matches = re.findall(r'time=([\d.]+)\s*ms', output)
                if matches:
                    return statistics.mean([float(x) for x in matches])
            
            self.logger.warning("Could not parse latency from ping output")
            return None
            
        except Exception as e:
            self.logger.error(f"Error parsing ping output: {e}")
            return None


class LatencyAnalyzer:
    """
    Analyzes latency trends and detects anomalies.
    
    Provides statistical analysis of latency measurements over time
    to identify performance degradation and network issues.
    """
    
    def __init__(self, baseline_samples: int = 100):
        """
        Initialize the analyzer.
        
        Args:
            baseline_samples: Number of samples for baseline calculation
        """
        self.baseline_samples = baseline_samples
        self.measurements: List[float] = []
        self.logger = logging.getLogger(__name__)
    
    def add_measurement(self, latency: float):
        """
        Add a latency measurement to the analyzer.
        
        Args:
            latency: Latency value in milliseconds
        """
        self.measurements.append(latency)
        
        # Keep only recent measurements for performance
        if len(self.measurements) > self.baseline_samples * 2:
            self.measurements = self.measurements[-self.baseline_samples:]
    
    def get_baseline(self) -> Optional[float]:
        """
        Calculate baseline latency from historical data.
        
        Returns:
            Baseline latency (median of recent measurements) or None
        """
        if len(self.measurements) < 10:
            return None
        
        # Use median as baseline to reduce impact of outliers
        return statistics.median(self.measurements[-self.baseline_samples:])
    
    def detect_anomaly(self, latency: float, threshold_multiplier: float = 2.0) -> bool:
        """
        Detect if current latency is anomalous.
        
        Args:
            latency: Current latency measurement
            threshold_multiplier: Multiplier for anomaly detection
            
        Returns:
            True if latency is anomalous, False otherwise
        """
        baseline = self.get_baseline()
        if baseline is None:
            return False
        
        # Calculate standard deviation of baseline
        baseline_data = self.measurements[-self.baseline_samples:]
        if len(baseline_data) < 10:
            return False
        
        stddev = statistics.stdev(baseline_data)
        threshold = baseline + (stddev * threshold_multiplier)
        
        is_anomalous = latency > threshold
        
        if is_anomalous:
            self.logger.warning(
                f"Anomalous latency detected: {latency:.2f}ms "
                f"(baseline: {baseline:.2f}ms, threshold: {threshold:.2f}ms)"
            )
        
        return is_anomalous
    
    def get_trend(self, window_size: int = 20) -> str:
        """
        Determine latency trend over recent measurements.
        
        Args:
            window_size: Number of recent measurements to analyze
            
        Returns:
            Trend direction: "increasing", "decreasing", or "stable"
        """
        if len(self.measurements) < window_size:
            return "insufficient_data"
        
        recent = self.measurements[-window_size:]
        first_half = recent[:window_size//2]
        second_half = recent[window_size//2:]
        
        avg_first = statistics.mean(first_half)
        avg_second = statistics.mean(second_half)
        
        # Calculate percentage change
        change_pct = ((avg_second - avg_first) / avg_first) * 100
        
        if change_pct > 10:
            return "increasing"
        elif change_pct < -10:
            return "decreasing"
        else:
            return "stable"
