"""
Test Suite for API Health Monitor
==================================

Basic tests to validate monitor and dashboard functionality.
"""

import json
import time
from pathlib import Path
import sys

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent))

from monitor import APIMonitor
from dashboard import DashboardGenerator


def test_monitor_initialization():
    """Test monitor initialization."""
    print("Testing monitor initialization...", end=" ")
    try:
        monitor = APIMonitor('config.json')
        assert len(monitor.config['endpoints']) > 0
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def test_single_endpoint_check():
    """Test checking a single endpoint."""
    print("Testing single endpoint check...", end=" ")
    try:
        monitor = APIMonitor('config.json')
        endpoint = monitor.config['endpoints'][0]
        result = monitor.check_endpoint(endpoint)
        
        assert 'name' in result
        assert 'status' in result
        assert 'timestamp' in result
        assert result['status'] in ['UP', 'DOWN', 'DEGRADED']
        
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def test_run_all_checks():
    """Test running checks on all endpoints."""
    print("Testing all endpoint checks...", end=" ")
    try:
        monitor = APIMonitor('config.json')
        results = monitor.run_checks()
        
        assert len(results) == len(monitor.config['endpoints'])
        assert all('status' in r for r in results)
        
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def test_history_persistence():
    """Test that history is saved correctly."""
    print("Testing history persistence...", end=" ")
    try:
        monitor = APIMonitor('config.json')
        initial_count = len(monitor.history)
        
        monitor.run_checks()
        monitor = APIMonitor('config.json')  # Reload
        
        assert len(monitor.history) > initial_count
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def test_statistics_calculation():
    """Test statistics calculation."""
    print("Testing statistics calculation...", end=" ")
    try:
        monitor = APIMonitor('config.json')
        
        # Ensure we have some data
        monitor.run_checks()
        
        endpoint_name = monitor.config['endpoints'][0]['name']
        stats = monitor.get_statistics(endpoint_name, hours=24)
        
        assert 'uptime_pct' in stats
        assert 'avg_latency_ms' in stats
        assert 'checks' in stats
        
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def test_dashboard_generation():
    """Test dashboard generation."""
    print("Testing dashboard generation...", end=" ")
    try:
        # Ensure we have data
        monitor = APIMonitor('config.json')
        monitor.run_checks()
        
        # Generate dashboard
        generator = DashboardGenerator('data/history.json')
        output_path = generator.generate('output/test_dashboard.html')
        
        # Verify file was created
        assert Path(output_path).exists()
        
        # Verify content
        with open(output_path, 'r', encoding='utf-8') as f:
            content = f.read()
            assert '<html' in content.lower()
            assert 'API Health Monitor' in content
        
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def test_error_handling():
    """Test error handling for invalid endpoints."""
    print("Testing error handling...", end=" ")
    try:
        monitor = APIMonitor('config.json')
        
        # Test with invalid endpoint
        invalid_endpoint = {
            'name': 'Invalid Endpoint',
            'url': 'https://this-domain-definitely-does-not-exist-12345.com',
            'timeout': 2
        }
        
        result = monitor.check_endpoint(invalid_endpoint)
        
        assert result['status'] == 'DOWN'
        assert result['error'] is not None
        
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def test_timeout_handling():
    """Test timeout handling."""
    print("Testing timeout handling...", end=" ")
    try:
        monitor = APIMonitor('config.json')
        
        # Test with very short timeout
        endpoint = {
            'name': 'Timeout Test',
            'url': 'https://httpbin.org/delay/10',  # Delays 10 seconds
            'timeout': 1  # 1 second timeout
        }
        
        start = time.time()
        result = monitor.check_endpoint(endpoint)
        duration = time.time() - start
        
        # Should timeout quickly
        assert duration < 3
        assert result['status'] == 'DOWN'
        assert 'timeout' in result['error'].lower() or result['error'] is not None
        
        print("✓ PASS")
        return True
    except Exception as e:
        print(f"✗ FAIL: {e}")
        return False


def run_all_tests():
    """Run all tests."""
    print("\n" + "="*60)
    print("API Health Monitor - Test Suite")
    print("="*60 + "\n")
    
    tests = [
        test_monitor_initialization,
        test_single_endpoint_check,
        test_run_all_checks,
        test_history_persistence,
        test_statistics_calculation,
        test_dashboard_generation,
        test_error_handling,
        test_timeout_handling
    ]
    
    results = [test() for test in tests]
    
    print("\n" + "="*60)
    passed = sum(results)
    total = len(results)
    
    print(f"Test Results: {passed}/{total} passed")
    
    if passed == total:
        print("✓ All tests passed!")
    else:
        print(f"✗ {total - passed} test(s) failed")
    
    print("="*60 + "\n")
    
    return passed == total


if __name__ == "__main__":
    success = run_all_tests()
    sys.exit(0 if success else 1)
