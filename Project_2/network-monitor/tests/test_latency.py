"""
Unit Tests for Latency Monitor

Tests the latency measurement functionality including:
- Basic latency measurement
- Detailed statistical analysis
- Anomaly detection
- Trend analysis
"""

import pytest
from src.core.latency import LatencyMonitor, LatencyAnalyzer, LatencyStats


class TestLatencyMonitor:
    """Test suite for LatencyMonitor class."""
    
    def setup_method(self):
        """Setup test fixtures."""
        self.monitor = LatencyMonitor()
    
    def test_measure_valid_host(self):
        """Test latency measurement with valid host."""
        # Using localhost should always work
        latency = self.monitor.measure("127.0.0.1", count=1, timeout=2)
        
        assert latency is not None
        assert latency >= 0
        assert latency < 1000  # Should be very low for localhost
    
    def test_measure_invalid_host(self):
        """Test latency measurement with invalid host."""
        latency = self.monitor.measure("invalid.host.example", count=1, timeout=1)
        
        assert latency is None
    
    def test_measure_detailed(self):
        """Test detailed latency measurement."""
        stats = self.monitor.measure_detailed("127.0.0.1", count=5)
        
        assert stats is not None
        assert isinstance(stats, LatencyStats)
        assert stats.samples == 5
        assert stats.min_ms <= stats.avg_ms <= stats.max_ms
        assert stats.stddev_ms >= 0


class TestLatencyAnalyzer:
    """Test suite for LatencyAnalyzer class."""
    
    def setup_method(self):
        """Setup test fixtures."""
        self.analyzer = LatencyAnalyzer(baseline_samples=20)
    
    def test_add_measurement(self):
        """Test adding measurements to analyzer."""
        self.analyzer.add_measurement(10.0)
        self.analyzer.add_measurement(12.0)
        
        assert len(self.analyzer.measurements) == 2
    
    def test_get_baseline_insufficient_data(self):
        """Test baseline calculation with insufficient data."""
        self.analyzer.add_measurement(10.0)
        baseline = self.analyzer.get_baseline()
        
        assert baseline is None
    
    def test_get_baseline_sufficient_data(self):
        """Test baseline calculation with sufficient data."""
        for i in range(20):
            self.analyzer.add_measurement(10.0 + i * 0.5)
        
        baseline = self.analyzer.get_baseline()
        
        assert baseline is not None
        assert 10.0 <= baseline <= 20.0
    
    def test_anomaly_detection(self):
        """Test anomaly detection."""
        # Add baseline measurements
        for _ in range(50):
            self.analyzer.add_measurement(10.0)
        
        # Normal value should not be anomalous
        is_anomalous = self.analyzer.detect_anomaly(11.0)
        assert not is_anomalous
        
        # Very high value should be anomalous
        is_anomalous = self.analyzer.detect_anomaly(100.0)
        assert is_anomalous
    
    def test_trend_analysis_stable(self):
        """Test trend detection for stable latency."""
        for _ in range(30):
            self.analyzer.add_measurement(10.0)
        
        trend = self.analyzer.get_trend(window_size=20)
        assert trend == "stable"
    
    def test_trend_analysis_increasing(self):
        """Test trend detection for increasing latency."""
        for i in range(30):
            self.analyzer.add_measurement(10.0 + i * 2)
        
        trend = self.analyzer.get_trend(window_size=20)
        assert trend == "increasing"


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
