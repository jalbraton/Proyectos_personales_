"""
Dashboard Generator
===================

Generates HTML dashboard from monitoring history.
Creates static HTML with inline CSS and JavaScript.

Features:
    - Status cards for each endpoint
    - Latency charts
    - Uptime statistics
    - Recent incidents list

Example:
    >>> generator = DashboardGenerator('data/history.json')
    >>> generator.generate('output/index.html')
"""

import json
from datetime import datetime, timedelta
from pathlib import Path
from typing import List, Dict


class DashboardGenerator:
    """
    Generate HTML dashboard from monitoring history.
    
    Creates a self-contained HTML file with inline CSS and JavaScript
    for visualizing API health metrics.
    """
    
    def __init__(self, history_path: str = "data/history.json"):
        """
        Initialize dashboard generator.
        
        Args:
            history_path: Path to history JSON file
        """
        self.history_path = Path(history_path)
        self.history = self._load_history()
    
    def _load_history(self) -> List[Dict]:
        """Load monitoring history."""
        if self.history_path.exists():
            with open(self.history_path, 'r') as f:
                return json.load(f)
        return []
    
    def _get_latest_status(self) -> Dict[str, Dict]:
        """Get latest status for each endpoint."""
        latest_status = {}
        
        if self.history:
            latest_batch = self.history[-1]
            for result in latest_batch['results']:
                latest_status[result['name']] = result
        
        return latest_status
    
    def _calculate_uptime(self, endpoint_name: str, hours: int = 24) -> float:
        """Calculate uptime percentage for endpoint."""
        cutoff_time = datetime.now() - timedelta(hours=hours)
        
        checks = []
        for batch in self.history:
            try:
                batch_time = datetime.fromisoformat(batch['timestamp'])
                if batch_time > cutoff_time:
                    for result in batch['results']:
                        if result['name'] == endpoint_name:
                            checks.append(result)
            except:
                continue
        
        if not checks:
            return 0.0
        
        up_count = sum(1 for c in checks if c['status'] == 'UP')
        return round((up_count / len(checks)) * 100, 2)
    
    def _get_latency_data(self, endpoint_name: str, limit: int = 20) -> List[Dict]:
        """Get recent latency data for endpoint."""
        data = []
        
        for batch in reversed(self.history[-limit:]):
            for result in batch['results']:
                if result['name'] == endpoint_name and result['latency_ms']:
                    data.append({
                        'timestamp': batch['timestamp'],
                        'latency': result['latency_ms']
                    })
        
        return list(reversed(data))
    
    def _generate_status_card(self, name: str, status: Dict) -> str:
        """Generate HTML for single endpoint status card."""
        status_class = status['status'].lower()
        status_color = {
            'up': '#d4edda',
            'degraded': '#fff3cd',
            'down': '#f8d7da'
        }
        
        uptime = self._calculate_uptime(name, hours=24)
        latency_data = self._get_latency_data(name, limit=10)
        avg_latency = sum(d['latency'] for d in latency_data) / len(latency_data) if latency_data else 0
        
        error_info = f"<div class='error'>Error: {status['error']}</div>" if status['error'] else ""
        
        return f"""
        <div class="card {status_class}" style="background-color: {status_color.get(status_class, '#f8f9fa')}">
            <div class="card-header">
                <h2>{name}</h2>
                <span class="badge badge-{status_class}">{status['status']}</span>
            </div>
            <div class="card-body">
                <div class="metric">
                    <span class="metric-label">URL:</span>
                    <span class="metric-value url">{status['url']}</span>
                </div>
                <div class="metrics-row">
                    <div class="metric">
                        <span class="metric-label">Status Code:</span>
                        <span class="metric-value">{status['status_code'] or 'N/A'}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Latency:</span>
                        <span class="metric-value">{status['latency_ms']:.0f}ms</span>
                    </div>
                </div>
                <div class="metrics-row">
                    <div class="metric">
                        <span class="metric-label">24h Uptime:</span>
                        <span class="metric-value uptime">{uptime}%</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Avg Latency:</span>
                        <span class="metric-value">{avg_latency:.0f}ms</span>
                    </div>
                </div>
                {error_info}
                <div class="timestamp">Last checked: {status['timestamp']}</div>
            </div>
        </div>
        """
    
    def _generate_incidents_list(self, limit: int = 10) -> str:
        """Generate HTML for recent incidents."""
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
                        break
            if len(incidents) >= limit:
                break
        
        if not incidents:
            return "<p class='no-incidents'>No recent incidents 🎉</p>"
        
        html = "<div class='incidents'>"
        for incident in incidents:
            html += f"""
            <div class='incident'>
                <div class='incident-name'>{incident['name']}</div>
                <div class='incident-time'>{incident['timestamp']}</div>
                <div class='incident-error'>{incident['error']}</div>
            </div>
            """
        html += "</div>"
        
        return html
    
    def generate(self, output_path: str = "output/index.html") -> str:
        """
        Generate complete HTML dashboard.
        
        Args:
            output_path: Path to save generated HTML file
            
        Returns:
            Path to generated file
        """
        output_path = Path(output_path)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        latest_status = self._get_latest_status()
        
        # Generate status cards
        cards_html = ""
        for name, status in latest_status.items():
            cards_html += self._generate_status_card(name, status)
        
        # Generate incidents list
        incidents_html = self._generate_incidents_list(limit=10)
        
        # Calculate overall statistics
        total_endpoints = len(latest_status)
        healthy_count = sum(1 for s in latest_status.values() if s['status'] == 'UP')
        overall_health = round((healthy_count / total_endpoints * 100), 1) if total_endpoints > 0 else 0
        
        # Generate complete HTML
        html = f"""
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>API Health Monitor Dashboard</title>
    <style>
        * {{
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }}
        
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }}
        
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            overflow: hidden;
        }}
        
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }}
        
        .header h1 {{
            font-size: 2.5em;
            margin-bottom: 10px;
        }}
        
        .header .subtitle {{
            font-size: 1.1em;
            opacity: 0.9;
        }}
        
        .summary {{
            display: flex;
            justify-content: space-around;
            padding: 30px;
            background: #f8f9fa;
            border-bottom: 1px solid #dee2e6;
        }}
        
        .summary-item {{
            text-align: center;
        }}
        
        .summary-item .value {{
            font-size: 2.5em;
            font-weight: bold;
            color: #667eea;
        }}
        
        .summary-item .label {{
            font-size: 0.9em;
            color: #6c757d;
            margin-top: 5px;
        }}
        
        .content {{
            padding: 30px;
        }}
        
        .section {{
            margin-bottom: 40px;
        }}
        
        .section h2 {{
            font-size: 1.8em;
            margin-bottom: 20px;
            color: #333;
        }}
        
        .card {{
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            transition: transform 0.2s, box-shadow 0.2s;
        }}
        
        .card:hover {{
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }}
        
        .card-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid rgba(0,0,0,0.1);
        }}
        
        .card-header h2 {{
            font-size: 1.5em;
            margin: 0;
            color: #333;
        }}
        
        .badge {{
            padding: 6px 12px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 0.85em;
        }}
        
        .badge-up {{
            background: #28a745;
            color: white;
        }}
        
        .badge-degraded {{
            background: #ffc107;
            color: #333;
        }}
        
        .badge-down {{
            background: #dc3545;
            color: white;
        }}
        
        .card-body {{
            padding: 10px 0;
        }}
        
        .metrics-row {{
            display: flex;
            gap: 20px;
            margin-bottom: 15px;
        }}
        
        .metric {{
            flex: 1;
            margin-bottom: 10px;
        }}
        
        .metric-label {{
            display: block;
            font-size: 0.85em;
            color: #6c757d;
            margin-bottom: 5px;
        }}
        
        .metric-value {{
            display: block;
            font-size: 1.3em;
            font-weight: bold;
            color: #333;
        }}
        
        .metric-value.url {{
            font-size: 0.9em;
            font-weight: normal;
            word-break: break-all;
            color: #667eea;
        }}
        
        .metric-value.uptime {{
            color: #28a745;
        }}
        
        .error {{
            background: #fff3cd;
            border: 1px solid #ffc107;
            border-radius: 4px;
            padding: 10px;
            margin: 10px 0;
            color: #856404;
        }}
        
        .timestamp {{
            font-size: 0.85em;
            color: #6c757d;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid rgba(0,0,0,0.1);
        }}
        
        .incidents {{
            background: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
        }}
        
        .incident {{
            background: white;
            border-left: 4px solid #dc3545;
            border-radius: 4px;
            padding: 15px;
            margin-bottom: 15px;
        }}
        
        .incident-name {{
            font-weight: bold;
            font-size: 1.1em;
            color: #333;
            margin-bottom: 5px;
        }}
        
        .incident-time {{
            font-size: 0.85em;
            color: #6c757d;
            margin-bottom: 5px;
        }}
        
        .incident-error {{
            color: #dc3545;
            font-size: 0.9em;
        }}
        
        .no-incidents {{
            text-align: center;
            padding: 40px;
            font-size: 1.2em;
            color: #28a745;
        }}
        
        .footer {{
            text-align: center;
            padding: 20px;
            background: #f8f9fa;
            border-top: 1px solid #dee2e6;
            color: #6c757d;
        }}
        
        @media (max-width: 768px) {{
            .summary {{
                flex-direction: column;
                gap: 20px;
            }}
            
            .metrics-row {{
                flex-direction: column;
            }}
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📊 API Health Monitor</h1>
            <div class="subtitle">Real-time API Availability & Performance Dashboard</div>
        </div>
        
        <div class="summary">
            <div class="summary-item">
                <div class="value">{total_endpoints}</div>
                <div class="label">Total Endpoints</div>
            </div>
            <div class="summary-item">
                <div class="value" style="color: #28a745">{healthy_count}</div>
                <div class="label">Healthy</div>
            </div>
            <div class="summary-item">
                <div class="value">{overall_health}%</div>
                <div class="label">Overall Health</div>
            </div>
        </div>
        
        <div class="content">
            <div class="section">
                <h2>🔍 Endpoint Status</h2>
                {cards_html if cards_html else "<p>No monitoring data available yet. Run monitor.py to start collecting data.</p>"}
            </div>
            
            <div class="section">
                <h2>⚠️ Recent Incidents</h2>
                {incidents_html}
            </div>
        </div>
        
        <div class="footer">
            <p>Last updated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')} | Auto-refresh: <span id="countdown">60</span>s</p>
        </div>
    </div>
    
    <script>
        // Auto-refresh countdown
        let countdown = 60;
        setInterval(() => {{
            countdown--;
            if (countdown <= 0) {{
                location.reload();
            }}
            document.getElementById('countdown').textContent = countdown;
        }}, 1000);
    </script>
</body>
</html>
"""
        
        # Write to file
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(html)
        
        print(f"Dashboard generated: {output_path}")
        return str(output_path)


def main():
    """Main entry point for command-line execution."""
    import argparse
    
    parser = argparse.ArgumentParser(
        description='Generate HTML dashboard from monitoring history'
    )
    parser.add_argument(
        '--history',
        default='data/history.json',
        help='History JSON file path (default: data/history.json)'
    )
    parser.add_argument(
        '--output',
        default='output/index.html',
        help='Output HTML file path (default: output/index.html)'
    )
    
    args = parser.parse_args()
    
    generator = DashboardGenerator(args.history)
    output_path = generator.generate(args.output)
    
    print(f"\n✓ Dashboard generated successfully!")
    print(f"  Open: {output_path}")


if __name__ == "__main__":
    main()
