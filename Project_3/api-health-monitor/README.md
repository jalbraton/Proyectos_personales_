# API Health Monitor 🔍

A lightweight Python-based monitoring system for tracking API availability and performance. Generate beautiful HTML dashboards with real-time status updates.

![Python](https://img.shields.io/badge/python-3.8+-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## Features ✨

- **Multi-Endpoint Monitoring**: Track multiple APIs simultaneously
- **Real-time Status**: Check availability, response time, and status codes
- **Historical Data**: JSON-based persistence (no database required)
- **Beautiful Dashboard**: Auto-generated HTML with inline CSS
- **Uptime Statistics**: 24-hour uptime percentage tracking
- **Incident Tracking**: Recent downtime incidents with error details
- **Lightweight**: Only requires `requests` library
- **Easy Setup**: Configuration via simple JSON file

## Quick Start 🚀

### Installation

```bash
# Clone repository
git clone https://github.com/yourusername/api-health-monitor.git
cd api-health-monitor

# Install dependencies
pip install -r requirements.txt
```

### Configuration

Edit `config.json` to add your API endpoints:

```json
{
  "endpoints": [
    {
      "name": "GitHub API",
      "url": "https://api.github.com",
      "timeout": 5
    },
    {
      "name": "Your API",
      "url": "https://api.example.com/health",
      "timeout": 10
    }
  ],
  "check_interval": 60
}
```

### Usage

**Single Check:**
```bash
python monitor.py
```

**Continuous Monitoring:**
```bash
python monitor.py --continuous
```

**Generate Dashboard:**
```bash
python dashboard.py
```

**View Statistics:**
```bash
python monitor.py --stats "GitHub API"
```

## Project Structure 📁

```
api-health-monitor/
├── monitor.py          # Core monitoring logic
├── dashboard.py        # HTML dashboard generator
├── config.json         # API endpoint configuration
├── requirements.txt    # Python dependencies
├── data/
│   └── history.json    # Monitoring history (auto-generated)
├── output/
│   └── index.html      # Generated dashboard (auto-generated)
└── README.md
```

## Dashboard Preview 📊

The generated dashboard includes:

- **Overall Health Summary**: Total endpoints, healthy count, health percentage
- **Endpoint Status Cards**: Individual cards showing:
  - Current status (UP/DOWN/DEGRADED)
  - Response time (latency in ms)
  - 24-hour uptime percentage
  - Status code
  - Error details (if any)
- **Recent Incidents**: List of recent downtime events with timestamps
- **Auto-refresh**: Dashboard auto-refreshes every 60 seconds

## Examples 💡

### Monitor Custom Endpoints

```python
from monitor import APIMonitor

monitor = APIMonitor('config.json')
results = monitor.run_checks()

for result in results:
    print(f"{result['name']}: {result['status']} ({result['latency_ms']}ms)")
```

### Generate Custom Dashboard

```python
from dashboard import DashboardGenerator

generator = DashboardGenerator('data/history.json')
generator.generate('custom_dashboard.html')
```

### Get Endpoint Statistics

```python
monitor = APIMonitor('config.json')
stats = monitor.get_statistics('GitHub API', hours=24)

print(f"Uptime: {stats['uptime_pct']}%")
print(f"Avg Latency: {stats['avg_latency_ms']}ms")
```

## Configuration Options ⚙️

### Endpoint Configuration

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Friendly name for the endpoint |
| `url` | string | Yes | Full URL to monitor |
| `timeout` | integer | No | Request timeout in seconds (default: 5) |
| `headers` | object | No | Custom HTTP headers |
| `expected_response` | string | No | Expected response content for validation |

### Global Configuration

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `check_interval` | integer | 60 | Seconds between checks (continuous mode) |
| `retention_days` | integer | 7 | Days to keep historical data |

## Advanced Usage 🔧

### Adding Custom Headers

```json
{
  "name": "Authenticated API",
  "url": "https://api.example.com/data",
  "timeout": 5,
  "headers": {
    "Authorization": "Bearer YOUR_TOKEN",
    "User-Agent": "API-Monitor/1.0"
  }
}
```

### Response Validation

```json
{
  "name": "API with Validation",
  "url": "https://api.example.com/status",
  "timeout": 5,
  "expected_response": "healthy"
}
```

### Automated Monitoring with Cron

```bash
# Run checks every 5 minutes
*/5 * * * * cd /path/to/api-health-monitor && python monitor.py

# Generate dashboard every hour
0 * * * * cd /path/to/api-health-monitor && python dashboard.py
```

### GitHub Pages Deployment

1. Generate dashboard: `python dashboard.py`
2. Copy `output/index.html` to GitHub Pages repository
3. Commit and push
4. Access at: `https://yourusername.github.io/your-repo`

## Output Examples 📝

### Console Output

```
============================================================
Running health checks at 2025-11-28 14:30:00
============================================================

Checking GitHub API... ✓ UP (245ms)
Checking JSONPlaceholder... ✓ UP (189ms)
Checking Your API... ✗ DOWN (N/A)
  Error: Connection failed

============================================================
Summary: 2/3 endpoints healthy
============================================================
```

### JSON History Format

```json
[
  {
    "timestamp": "2025-11-28T14:30:00",
    "results": [
      {
        "name": "GitHub API",
        "url": "https://api.github.com",
        "status": "UP",
        "status_code": 200,
        "latency_ms": 245.67,
        "timestamp": "2025-11-28T14:30:00",
        "error": null
      }
    ]
  }
]
```

## Requirements 📋

- Python 3.8+
- `requests` library (2.31.0+)

## Use Cases 🎯

- **DevOps**: Monitor production APIs and third-party services
- **SRE**: Track uptime and performance metrics
- **Development**: Verify API availability during development
- **Portfolio**: Demonstrate monitoring and dashboard creation skills
- **Education**: Learn about API monitoring, JSON persistence, and HTML generation

## Contributing 🤝

Contributions are welcome! Feel free to:

- Report bugs
- Suggest new features
- Submit pull requests
- Improve documentation

## License 📄

MIT License - feel free to use this project for personal or commercial purposes.

## Roadmap 🗺️

- [ ] Add email/Slack notifications
- [ ] Support for POST/PUT requests
- [ ] Configurable alert thresholds
- [ ] Export statistics to CSV
- [ ] Docker container support
- [ ] Prometheus metrics export

## Author ✍️

Created with ❤️ for learning and demonstrating API monitoring concepts.

## Acknowledgments 🙏

- Built with Python and the amazing `requests` library
- Inspired by production monitoring tools like Pingdom and StatusCake
- Dashboard styling inspired by modern UI/UX principles

---

**⭐ Star this repository if you find it useful!**
