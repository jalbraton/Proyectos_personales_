# API Health Monitor - Quick Start Guide

## Installation

1. **Clone or download** the project
2. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

## First Run

### 1. Run a single check
```bash
python monitor.py
```

**Expected output:**
```
============================================================
Running health checks at 2025-11-28 14:30:00
============================================================

Checking GitHub API... ✓ UP (245ms)
Checking JSONPlaceholder... ✓ UP (189ms)
Checking OpenWeatherMap API... ✓ UP (312ms)
Checking REST Countries API... ✓ UP (156ms)
Checking CoinGecko API... ✓ UP (198ms)

============================================================
Summary: 5/5 endpoints healthy
============================================================
```

### 2. Generate dashboard
```bash
python dashboard.py
```

**Expected output:**
```
Dashboard generated: output/index.html
✓ Dashboard generated successfully!
  Open: output/index.html
```

### 3. Open the dashboard
Open `output/index.html` in your web browser to see the beautiful dashboard!

## Customization

### Add Your Own APIs

Edit `config.json`:

```json
{
  "endpoints": [
    {
      "name": "My API",
      "url": "https://api.myservice.com/health",
      "timeout": 5
    }
  ]
}
```

### Change Check Interval

For continuous monitoring:

```json
{
  "check_interval": 300  // Check every 5 minutes
}
```

## Running Tests

```bash
python test_monitor.py
```

## Continuous Monitoring

Run in the background and auto-generate dashboards:

```bash
# Monitor continuously
python monitor.py --continuous

# In another terminal, regenerate dashboard periodically
while true; do python dashboard.py; sleep 60; done
```

## Troubleshooting

**Problem:** `ModuleNotFoundError: No module named 'requests'`
**Solution:** Run `pip install requests`

**Problem:** No data in dashboard
**Solution:** Run `python monitor.py` first to collect data

**Problem:** Permission denied creating files
**Solution:** Check write permissions in `data/` and `output/` directories

## Next Steps

- Add your own APIs to `config.json`
- Set up automated monitoring with cron/Task Scheduler
- Deploy dashboard to GitHub Pages
- Add email notifications (see README for roadmap)

Enjoy monitoring! 🚀
