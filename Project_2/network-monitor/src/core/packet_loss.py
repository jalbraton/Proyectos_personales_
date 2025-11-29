"""
Packet Loss Analysis Module

Implements packet loss detection and analysis using ICMP echo requests.
Calculates packet loss percentage and provides statistical analysis.

Packet Loss Formula: (packets_sent - packets_received) / packets_sent * 100
"""

import subprocess
import platform
import re
import logging
from typing import Optional
from dataclasses import dataclass


@dataclass
class PacketLossResult:
    """
    Result of packet loss analysis.
    
    Attributes:
        sent: Number of packets sent
        received: Number of packets received
        loss_percentage: Packet loss as percentage
        host: Target host
    """
    sent: int
    received: int
    loss_percentage: float
    host: str


class PacketLossAnalyzer:
    """
    Analyzes packet loss for network connections.
    
    Uses ICMP ping to determine packet loss rate, which is a key
    indicator of network quality and reliability.
    """
    
    def __init__(self):
        """Initialize the packet loss analyzer."""
        self.logger = logging.getLogger(__name__)
        self.system = platform.system().lower()
    
    def analyze(self, host: str, count: int = 10, timeout: int = 2) -> float:
        """
        Analyze packet loss for a target host.
        
        Args:
            host: Target IP address or hostname
            count: Number of packets to send
            timeout: Timeout in seconds for each packet
            
        Returns:
            Packet loss percentage (0-100)
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
            
            # Parse packet loss from output
            loss_pct = self._parse_packet_loss(result.stdout, count)
            
            if loss_pct is not None:
                self.logger.debug(f"Packet loss to {host}: {loss_pct:.2f}%")
                return loss_pct
            else:
                # If parsing failed, assume 100% loss
                self.logger.warning(f"Could not determine packet loss for {host}")
                return 100.0
                
        except subprocess.TimeoutExpired:
            self.logger.warning(f"Ping to {host} timed out completely")
            return 100.0
        except Exception as e:
            self.logger.error(f"Error analyzing packet loss for {host}: {e}")
            return 100.0
    
    def analyze_detailed(self, host: str, count: int = 100) -> Optional[PacketLossResult]:
        """
        Perform detailed packet loss analysis.
        
        Args:
            host: Target IP address or hostname
            count: Number of packets to send
            
        Returns:
            PacketLossResult object or None if analysis failed
        """
        try:
            if self.system == "windows":
                cmd = ["ping", "-n", str(count), "-w", "2000", host]
            else:
                cmd = ["ping", "-c", str(count), "-W", "2", host]
            
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=count * 2 + 5
            )
            
            # Parse detailed results
            sent, received = self._parse_packet_counts(result.stdout)
            
            if sent > 0:
                loss_pct = ((sent - received) / sent) * 100
            elif sent == 0:
                self.logger.error(f"No packets sent to {host}")
                return None
            else:
                self.logger.error(f"Invalid packet count for {host}")
                return None
                
                result_obj = PacketLossResult(
                    sent=sent,
                    received=received,
                    loss_percentage=loss_pct,
                    host=host
                )
                
                self.logger.info(
                    f"Detailed packet loss for {host}: "
                    f"{received}/{sent} packets received ({loss_pct:.2f}% loss)"
                )
                
                return result_obj
            else:
                self.logger.error(f"No packets sent to {host}")
                return None
                
        except Exception as e:
            self.logger.error(f"Error in detailed packet loss analysis: {e}")
            return None
    
    def _parse_packet_loss(self, output: str, expected_count: int) -> Optional[float]:
        """
        Parse packet loss percentage from ping output.
        
        Args:
            output: Raw ping command output
            expected_count: Expected number of packets sent
            
        Returns:
            Packet loss percentage or None
        """
        try:
            if self.system == "windows":
                # Windows format: "Lost = X (Y% loss)"
                match = re.search(r'Lost\s*=\s*\d+\s*\((\d+)%\s*loss\)', output)
                if match:
                    return float(match.group(1))
                
                # Alternative: count received packets
                match = re.search(r'Received\s*=\s*(\d+)', output)
                if match:
                    received = int(match.group(1))
                    return ((expected_count - received) / expected_count) * 100
            else:
                # Linux/macOS format: "X packets transmitted, Y received, Z% packet loss"
                match = re.search(r'(\d+)%\s*packet loss', output)
                if match:
                    return float(match.group(1))
                
                # Alternative: parse transmitted and received
                match = re.search(r'(\d+)\s*packets transmitted,\s*(\d+)\s*received', output)
                if match:
                    sent = int(match.group(1))
                    received = int(match.group(2))
                    return ((sent - received) / sent) * 100 if sent > 0 else 100.0
            
            return None
            
        except Exception as e:
            self.logger.error(f"Error parsing packet loss: {e}")
            return None
    
    def _parse_packet_counts(self, output: str) -> tuple[int, int]:
        """
        Parse packet sent and received counts.
        
        Args:
            output: Raw ping command output
            
        Returns:
            Tuple of (sent, received) packet counts
        """
        sent = 0
        received = 0
        
        try:
            if self.system == "windows":
                # Windows format
                match_sent = re.search(r'Sent\s*=\s*(\d+)', output)
                match_received = re.search(r'Received\s*=\s*(\d+)', output)
                
                if match_sent:
                    sent = int(match_sent.group(1))
                if match_received:
                    received = int(match_received.group(1))
            else:
                # Linux/macOS format
                match = re.search(r'(\d+)\s*packets transmitted,\s*(\d+)\s*received', output)
                if match:
                    sent = int(match.group(1))
                    received = int(match.group(2))
            
        except Exception as e:
            self.logger.error(f"Error parsing packet counts: {e}")
        
        return sent, received


class PacketLossClassifier:
    """
    Classifies network quality based on packet loss.
    
    Reference: ITU-T Y.1541 - Network Performance Objectives
    """
    
    @staticmethod
    def classify(loss_percentage: float) -> str:
        """
        Classify network quality based on packet loss.
        
        Args:
            loss_percentage: Packet loss percentage
            
        Returns:
            Quality classification string
        """
        if loss_percentage == 0.0:
            return "EXCELLENT"
        elif loss_percentage < 0.1:
            return "VERY_GOOD"
        elif loss_percentage < 1.0:
            return "GOOD"
        elif loss_percentage < 5.0:
            return "FAIR"
        elif loss_percentage < 10.0:
            return "POOR"
        else:
            return "UNACCEPTABLE"
    
    @staticmethod
    def get_quality_description(classification: str) -> str:
        """
        Get human-readable quality description.
        
        Args:
            classification: Quality classification
            
        Returns:
            Description string
        """
        descriptions = {
            "EXCELLENT": "No packet loss detected. Optimal network conditions.",
            "VERY_GOOD": "Minimal packet loss. Suitable for all applications.",
            "GOOD": "Low packet loss. Acceptable for most applications.",
            "FAIR": "Moderate packet loss. May affect real-time applications.",
            "POOR": "High packet loss. Significant impact on performance.",
            "UNACCEPTABLE": "Severe packet loss. Network is not usable."
        }
        return descriptions.get(classification, "Unknown quality level")
