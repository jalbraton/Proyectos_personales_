# Real-Time Network Performance Monitor

## Overview

A comprehensive network monitoring system designed to analyze and visualize key network performance metrics in real-time. This project demonstrates practical implementation of network analysis techniques commonly used in telecommunications and IT infrastructure monitoring.

## Project Context

This system was developed as part of network engineering coursework to understand Quality of Service (QoS) parameters and their impact on network performance. The tool provides real-time monitoring capabilities for latency, throughput, packet loss, and jitter analysis.

## Features

- **Real-Time Latency Monitoring**: Continuous ping-based latency measurement with statistical analysis
- **Throughput Analysis**: TCP/UDP bandwidth measurement using iperf3 integration
- **Packet Loss Detection**: Real-time packet loss percentage calculation
- **Jitter Measurement**: Inter-packet delay variation analysis for VoIP and streaming applications
- **Multi-Target Monitoring**: Simultaneous monitoring of multiple network endpoints
- **Historical Data Storage**: SQLite database for long-term performance tracking
- **Alert System**: Configurable thresholds with email/log notifications
- **Visualization Dashboard**: Real-time plotting of network metrics

## Technical Architecture

### System Components

```
network-monitor/
├── src/
│   ├── core/
│   │   ├── monitor.py          # Main monitoring engine
│   │   ├── latency.py          # ICMP latency measurement
│   │   ├── throughput.py       # Bandwidth testing
│   │   └── packet_loss.py      # Packet loss analysis
│   ├── database/
│   │   ├── db_manager.py       # Database operations
│   │   └── models.py           # Data models
│   ├── visualization/
│   │   ├── plotter.py          # Real-time plotting
│   │   └── dashboard.py        # Web dashboard
│   ├── alerts/
│   │   └── alert_manager.py    # Notification system
│   └── utils/
│       ├── config.py           # Configuration management
│       └── validators.py       # Input validation
├── config/
│   └── config.yaml             # System configuration
├── tests/
│   ├── test_monitor.py
│   ├── test_latency.py
│   └── test_throughput.py
├── docs/
│   ├── API.md
│   └── USAGE.md
├── requirements.txt
├── setup.py
└── README.md
```

## Installation

### Prerequisites

- Python 3.8 or higher
- pip package manager
- Network access with ICMP permissions
- Optional: iperf3 for throughput testing

### Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/network-monitor.git
cd network-monitor

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Run tests
python -m pytest tests/
```

## Configuration

Edit `config/config.yaml` to customize monitoring parameters:

```yaml
monitoring:
  interval: 5                    # Seconds between measurements
  targets:
    - host: "8.8.8.8"
      name: "Google DNS"
    - host: "1.1.1.1"
      name: "Cloudflare DNS"
  
thresholds:
  latency_warning: 100           # ms
  latency_critical: 200          # ms
  packet_loss_warning: 1.0       # percentage
  packet_loss_critical: 5.0      # percentage
  
database:
  path: "data/metrics.db"
  retention_days: 30
```

## Usage

### Basic Monitoring

```python
from src.core.monitor import NetworkMonitor

# Initialize monitor
monitor = NetworkMonitor(config_path="config/config.yaml")

# Start monitoring
monitor.start()

# Monitor runs in background, press Ctrl+C to stop
```

### Command Line Interface

```bash
# Monitor single host
python -m src.core.monitor --host 8.8.8.8 --interval 5

# Monitor multiple hosts
python -m src.core.monitor --config config/config.yaml

# Generate report
python -m src.core.monitor --report --duration 24h
```

### Web Dashboard

```bash
# Start dashboard server
python -m src.visualization.dashboard

# Access at http://localhost:5000
```

## Performance Metrics

### Latency Measurement

Uses ICMP Echo Request/Reply (ping) to measure Round-Trip Time (RTT):

- **Average Latency**: Mean RTT over measurement interval
- **Min/Max Latency**: Range of observed latencies
- **Standard Deviation**: Latency variation metric
- **95th Percentile**: High-percentile latency for SLA compliance

### Throughput Measurement

TCP-based throughput testing using socket connections:

- **Download Speed**: Inbound bandwidth capacity
- **Upload Speed**: Outbound bandwidth capacity
- **Bidirectional Test**: Simultaneous up/down measurement

### Packet Loss

Calculated as: `(packets_sent - packets_received) / packets_sent * 100`

### Jitter

Inter-packet delay variation: `jitter = |latency_n - latency_(n-1)|`

## Technical Implementation

### Latency Monitoring Algorithm

```
1. Send ICMP Echo Request packet
2. Wait for Echo Reply with timeout
3. Calculate RTT = reply_time - send_time
4. Store measurement with timestamp
5. Calculate statistics (avg, min, max, stddev)
6. Check against thresholds
7. Trigger alerts if threshold exceeded
```

### Database Schema

```sql
CREATE TABLE metrics (
    id INTEGER PRIMARY KEY,
    timestamp DATETIME,
    target_host TEXT,
    metric_type TEXT,
    value REAL,
    unit TEXT
);

CREATE TABLE alerts (
    id INTEGER PRIMARY KEY,
    timestamp DATETIME,
    severity TEXT,
    message TEXT,
    acknowledged BOOLEAN
);
```

## Results and Analysis

### Sample Output

```
Network Monitor - Real-Time Statistics
======================================
Target: 8.8.8.8 (Google DNS)
Time: 2025-11-28 14:30:00

Latency:
  Average: 12.3 ms
  Min: 10.1 ms
  Max: 18.7 ms
  Std Dev: 2.1 ms
  95th %ile: 16.2 ms

Packet Loss: 0.0%
Jitter: 1.2 ms

Status: HEALTHY
```

## Testing

Comprehensive test suite using pytest:

```bash
# Run all tests
pytest tests/

# Run specific test module
pytest tests/test_latency.py

# Run with coverage report
pytest --cov=src tests/
```

## Limitations and Future Work

### Current Limitations

- ICMP may be blocked by firewalls
- Requires elevated privileges on some systems
- TCP throughput testing affects network load
- Single-threaded monitoring may limit scalability

### Planned Enhancements

- SNMP integration for network device monitoring
- BGP route analysis
- Traceroute integration for path analysis
- Machine learning for anomaly detection
- REST API for external integrations
- Docker containerization

## References

1. RFC 792 - Internet Control Message Protocol
2. RFC 2679 - One-way Delay Metric for IPPM
3. RFC 3393 - IP Packet Delay Variation Metric for IPPM
4. Stevens, W. R. (1994). TCP/IP Illustrated, Volume 1

## License

MIT License - see LICENSE file for details

## Author

Developed as part of Network Engineering coursework
Telecommunications Engineering Program
2025

## Acknowledgments

Special thanks to the network engineering faculty for guidance on measurement methodologies and industry best practices.
