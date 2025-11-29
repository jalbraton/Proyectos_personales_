"""
Alert Management Module

Handles threshold-based alerting and notification system.
Supports multiple severity levels and notification channels.
"""

import logging
from datetime import datetime
from typing import Optional
from ..database.db_manager import DatabaseManager


class AlertManager:
    """
    Manages network monitoring alerts and notifications.
    
    Provides threshold-based alerting with configurable severity levels
    and notification channels (log, email, webhook).
    """
    
    SEVERITY_LEVELS = ["INFO", "WARNING", "CRITICAL"]
    
    def __init__(self, config):
        """
        Initialize alert manager.
        
        Args:
            config: Configuration manager instance
        """
        self.logger = logging.getLogger(__name__)
        self.config = config
        
        # Get database path with default fallback
        db_path = config.get("database.path", "data/metrics.db")
        self.db_manager = DatabaseManager(db_path)
        
        # Alert tracking to prevent spam
        self.recent_alerts = {}
        self.cooldown_seconds = 300  # 5 minutes between duplicate alerts
        
        self.logger.info("AlertManager initialized")
    
    def trigger_alert(self, severity: str, message: str, target: Optional[str] = None):
        """
        Trigger an alert.
        
        Args:
            severity: Alert severity (INFO, WARNING, CRITICAL)
            message: Alert message
            target: Optional target identifier
        """
        if severity not in self.SEVERITY_LEVELS:
            self.logger.error(f"Invalid severity level: {severity}")
            return
        
        # Check if this is a duplicate recent alert
        alert_key = f"{severity}:{message}"
        if self._is_duplicate_alert(alert_key):
            self.logger.debug(f"Suppressing duplicate alert: {alert_key}")
            return
        
        # Record alert
        timestamp = datetime.now()
        self.db_manager.insert_alert(timestamp, severity, message)
        
        # Log alert
        log_func = {
            "INFO": self.logger.info,
            "WARNING": self.logger.warning,
            "CRITICAL": self.logger.critical
        }
        log_func[severity](f"ALERT [{severity}]: {message}")
        
        # Update recent alerts tracking
        self.recent_alerts[alert_key] = timestamp
        
        # Send notifications based on severity
        if severity == "CRITICAL":
            self._send_critical_notification(message, target)
    
    def _is_duplicate_alert(self, alert_key: str) -> bool:
        """
        Check if alert is a duplicate within cooldown period.
        
        Args:
            alert_key: Unique alert identifier
            
        Returns:
            True if duplicate, False otherwise
        """
        if alert_key not in self.recent_alerts:
            return False
        
        last_alert = self.recent_alerts[alert_key]
        time_diff = (datetime.now() - last_alert).total_seconds()
        
        return time_diff < self.cooldown_seconds
    
    def _send_critical_notification(self, message: str, target: Optional[str]):
        """
        Send notification for critical alerts.
        
        Args:
            message: Alert message
            target: Target identifier
        """
        # Implementation would include email/webhook notifications
        # For now, just enhanced logging
        self.logger.critical(
            f"CRITICAL ALERT NOTIFICATION: {message}"
            + (f" [Target: {target}]" if target else "")
        )
