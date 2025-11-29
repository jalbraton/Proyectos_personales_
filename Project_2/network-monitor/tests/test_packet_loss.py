"""
Unit Tests for Packet Loss Analyzer

Tests packet loss detection and classification functionality.
"""

import pytest
from src.core.packet_loss import PacketLossAnalyzer, PacketLossClassifier


class TestPacketLossAnalyzer:
    """Test suite for PacketLossAnalyzer class."""
    
    def setup_method(self):
        """Setup test fixtures."""
        self.analyzer = PacketLossAnalyzer()
    
    def test_analyze_localhost(self):
        """Test packet loss analysis for localhost."""
        loss = self.analyzer.analyze("127.0.0.1", count=5, timeout=2)
        
        assert loss is not None
        assert 0.0 <= loss <= 100.0
        # Localhost should have minimal or no packet loss
        assert loss < 10.0
    
    def test_analyze_invalid_host(self):
        """Test packet loss analysis for invalid host."""
        loss = self.analyzer.analyze("invalid.host.example", count=3, timeout=1)
        
        # Should return 100% loss for unreachable host
        assert loss == 100.0


class TestPacketLossClassifier:
    """Test suite for PacketLossClassifier class."""
    
    def test_classify_excellent(self):
        """Test classification for excellent network."""
        classification = PacketLossClassifier.classify(0.0)
        assert classification == "EXCELLENT"
    
    def test_classify_very_good(self):
        """Test classification for very good network."""
        classification = PacketLossClassifier.classify(0.05)
        assert classification == "VERY_GOOD"
    
    def test_classify_good(self):
        """Test classification for good network."""
        classification = PacketLossClassifier.classify(0.5)
        assert classification == "GOOD"
    
    def test_classify_fair(self):
        """Test classification for fair network."""
        classification = PacketLossClassifier.classify(2.0)
        assert classification == "FAIR"
    
    def test_classify_poor(self):
        """Test classification for poor network."""
        classification = PacketLossClassifier.classify(7.0)
        assert classification == "POOR"
    
    def test_classify_unacceptable(self):
        """Test classification for unacceptable network."""
        classification = PacketLossClassifier.classify(15.0)
        assert classification == "UNACCEPTABLE"
    
    def test_quality_descriptions(self):
        """Test quality description retrieval."""
        for classification in ["EXCELLENT", "GOOD", "POOR", "UNACCEPTABLE"]:
            description = PacketLossClassifier.get_quality_description(classification)
            assert description is not None
            assert len(description) > 0


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
