"""
API Health Monitor
==================

Simple monitoring system for API endpoints.
Tracks availability, latency, and response status codes.

Features:
    - Multi-endpoint monitoring
    - Latency measurement
    - Historical data persistence
    - JSON-based storage

Example:
    >>> monitor = APIMonitor('config.json')
    >>> results = monitor.run_checks()
    >>> print(f"Checked {len(results)} endpoints")
"""

import requests
import json
import time
from datetime import datetime
from pathlib import Path
from typing import List, Dict, Optional


class APIMonitor:
    """
    Monitor multiple API endpoints for availability and performance.
    
    Attributes:
        config: Configuration dictionary with endpoints
        history: Historical monitoring results
        history_path: Path to history JSON file
    """
    
    def __init__(self, config_path: str = "config.json"):
        """
        Initialize API monitor.
        
        Args:
            config_path: Path to configuration JSON file
        """
        self.config_path = config_path
        self.history_path = Path("data/history.json")
        
        # Load configuration
        with open(config_path, 'r') as f:
            self.config = json.load(f)
        
        # Load or initialize history
        self.history = self._load_history()
        
        # Ensure data directory exists
        self.history_path.parent.mkdir(parents=True, exist_ok=True)
        
        print(f"API Monitor initialized with {len(self.config['endpoints'])} endpoints")
    
    def _load_history(self) -> List[Dict]:
        """
        Load historical monitoring data.
        
        Returns:
            List of historical check results
        """
        if self.history_path.exists():
            try:
                with open(self.history_path, 'r') as f:
                    history = json.load(f)
                print(f"Loaded {len(history)} historical records")
                return history
            except Exception as e:
                print(f"Error loading history: {e}")
                return []
        return []
    
    def _save_history(self):
        """Save monitoring history to JSON file."""
        try:
            with open(self.history_path, 'w') as f:
                json.dump(self.history, f, indent=2)
        except Exception as e:
            print(f"Error saving history: {e}")
    
    def check_endpoint(self, endpoint: Dict) -> Dict:
        """
        Check single API endpoint.
        
        Args:
            endpoint: Endpoint configuration dictionary
            
        Returns:
            Dictionary with check results including status, latency, timestamp
        """
        start_time = time.time()
        
        try:
            response = requests.get(
                endpoint['url'],
                timeout=endpoint.get('timeout', 5),
                headers=endpoint.get('headers', {})
            )
            
            latency_ms = (time.time() - start_time) * 1000
            
            result = {
                'name': endpoint['name'],
                'url': endpoint['url'],
                'status': 'UP' if response.status_code == 200 else 'DEGRADED',
                'status_code': response.status_code,
                'latency_ms': round(latency_ms, 2),
                'timestamp': datetime.now().isoformat(),
                'error': None
            }
            
            # Check expected response if configured
            if 'expected_response' in endpoint:
                try:
                    data = response.json()
                    if endpoint['expected_response'] not in str(data):
                        result['status'] = 'DEGRADED'
                        result['error'] = 'Unexpected response content'
                except:
                    pass
            
            return result
            
        except requests.Timeout:
            return {
                'name': endpoint['name'],
                'url': endpoint['url'],
                'status': 'DOWN',
                'status_code': None,
                'latency_ms': (time.time() - start_time) * 1000,
                'timestamp': datetime.now().isoformat(),
                'error': 'Request timeout'
            }
        except requests.ConnectionError:
            return {
                'name': endpoint['name'],
                'url': endpoint['url'],
                'status': 'DOWN',
                'status_code': None,
                'latency_ms': None,
                'timestamp': datetime.now().isoformat(),
                'error': 'Connection failed'
            }
        except Exception as e:
            return {
                'name': endpoint['name'],
                'url': endpoint['url'],
                'status': 'DOWN',
                'status_code': None,
                'latency_ms': None,
                'timestamp': datetime.now().isoformat(),
                'error': str(e)
            }
    
    def run_checks(self) -> List[Dict]:
        """
        Run health checks on all configured endpoints.
        
        Returns:
            List of check results for all endpoints
        """
        print(f"\n{'='*60}")
        print(f"Running health checks at {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"{'='*60}\n")
        
        results = []
        
        for endpoint in self.config['endpoints']:
            print(f"Checking {endpoint['name']}...", end=' ')
            result = self.check_endpoint(endpoint)
            results.append(result)
            
            # Print result with color coding
            status_symbol = {
                'UP': '✓',
                'DEGRADED': '⚠',
                'DOWN': '✗'
            }
            
            symbol = status_symbol.get(result['status'], '?')
            latency_str = f"{result['latency_ms']:.0f}ms" if result['latency_ms'] else "N/A"
            
            print(f"{symbol} {result['status']} ({latency_str})")
            
            if result['error']:
                print(f"  Error: {result['error']}")
        
        # Save to history
        self._save_results(results)
        
        # Print summary
        print(f"\n{'='*60}")
        up_count = sum(1 for r in results if r['status'] == 'UP')
        print(f"Summary: {up_count}/{len(results)} endpoints healthy")
        print(f"{'='*60}\n")
        
        return results
    
    def _save_results(self, results: List[Dict]):
        """
        Save check results to history.
        
        Args:
            results: List of check results
        """
        # Add batch timestamp
        batch = {
            'timestamp': datetime.now().isoformat(),
            'results': results
        }
        
        self.history.append(batch)
        
        # Keep only last 100 checks to prevent file bloat
        if len(self.history) > 100:
            self.history = self.history[-100:]
        
        self._save_history()
    
    def get_statistics(self, endpoint_name: str, hours: int = 24) -> Dict:
        """
        Calculate statistics for an endpoint.
        
        Args:
            endpoint_name: Name of endpoint
            hours: Time window in hours
            
        Returns:
            Dictionary with uptime percentage and average latency
        """
        from datetime import timedelta
        
        cutoff_time = datetime.now() - timedelta(hours=hours)
        
        checks = []
        for batch in self.history:
            batch_time = datetime.fromisoformat(batch['timestamp'])
            if batch_time > cutoff_time:
                for result in batch['results']:
                    if result['name'] == endpoint_name:
                        checks.append(result)
        
        if not checks:
            return {'uptime_pct': None, 'avg_latency_ms': None, 'checks': 0}
        
        up_count = sum(1 for c in checks if c['status'] == 'UP')
        latencies = [c['latency_ms'] for c in checks if c['latency_ms'] is not None]
        
        return {
            'uptime_pct': round((up_count / len(checks)) * 100, 2),
            'avg_latency_ms': round(sum(latencies) / len(latencies), 2) if latencies else None,
            'checks': len(checks)
        }
    
    def get_recent_downtime(self, limit: int = 10) -> List[Dict]:
        """
        Get recent downtime incidents.
        
        Args:
            limit: Maximum number of incidents to return
            
        Returns:
            List of downtime incidents
        """
        incidents = []
        
        for batch in reversed(self.history):
            for result in batch['results']:
                if result['status'] == 'DOWN':
                    incidents.append({
                        'name': result['name'],
                        'timestamp': result['timestamp'],
                        'error': result['error']
                    })
                    
                    if len(incidents) >= limit:
                        return incidents
        
        return incidents


def main():
    """Main entry point for command-line execution."""
    import argparse
    
    parser = argparse.ArgumentParser(
        description='API Health Monitor - Track API availability and performance'
    )
    parser.add_argument(
        '--config',
        default='config.json',
        help='Configuration file path (default: config.json)'
    )
    parser.add_argument(
        '--continuous',
        action='store_true',
        help='Run continuously with interval from config'
    )
    parser.add_argument(
        '--stats',
        metavar='ENDPOINT',
        help='Show statistics for specific endpoint'
    )
    
    args = parser.parse_args()
    
    # Initialize monitor
    monitor = APIMonitor(args.config)
    
    # Show statistics mode
    if args.stats:
        stats = monitor.get_statistics(args.stats)
        print(f"\nStatistics for {args.stats} (last 24h):")
        print(f"  Uptime: {stats['uptime_pct']}%")
        print(f"  Avg Latency: {stats['avg_latency_ms']}ms")
        print(f"  Checks: {stats['checks']}")
        return
    
    # Continuous monitoring mode
    if args.continuous:
        interval = monitor.config.get('check_interval', 60)
        print(f"Starting continuous monitoring (interval: {interval}s)")
        print("Press Ctrl+C to stop\n")
        
        try:
            while True:
                monitor.run_checks()
                time.sleep(interval)
        except KeyboardInterrupt:
            print("\nMonitoring stopped by user")
    else:
        # Single check
        monitor.run_checks()


if __name__ == "__main__":
    main()
