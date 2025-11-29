"""
Database Manager Module

Manages persistent storage of network performance metrics using SQLite.
Provides data insertion, retrieval, and statistical analysis capabilities.
"""

import sqlite3
import logging
from typing import List, Dict, Optional
from datetime import datetime, timedelta
from pathlib import Path


class DatabaseManager:
    """
    Manages database operations for network monitoring data.
    
    Uses SQLite for lightweight, embedded database storage.
    Stores time-series metrics and alert history.
    """
    
    def __init__(self, db_path: str = "data/metrics.db"):
        """
        Initialize database manager.
        
        Args:
            db_path: Path to SQLite database file
        """
        self.logger = logging.getLogger(__name__)
        self.db_path = db_path
        
        # Ensure directory exists
        Path(db_path).parent.mkdir(parents=True, exist_ok=True)
        
        # Initialize database schema
        self._initialize_schema()
        self.logger.info(f"DatabaseManager initialized with {db_path}")
    
    def _get_connection(self) -> sqlite3.Connection:
        """
        Create database connection.
        
        Returns:
            SQLite connection object
        """
        conn = sqlite3.connect(self.db_path)
        conn.row_factory = sqlite3.Row
        return conn
    
    def _initialize_schema(self):
        """Create database tables if they don't exist."""
        with self._get_connection() as conn:
            cursor = conn.cursor()
            
            # Metrics table (removed INDEX syntax from CREATE TABLE)
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS metrics (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp DATETIME NOT NULL,
                    target TEXT NOT NULL,
                    metric_type TEXT NOT NULL,
                    value REAL NOT NULL,
                    unit TEXT NOT NULL
                )
            """)
            
            # Create indexes separately
            cursor.execute("CREATE INDEX IF NOT EXISTS idx_timestamp ON metrics(timestamp)")
            cursor.execute("CREATE INDEX IF NOT EXISTS idx_target ON metrics(target)")
            cursor.execute("CREATE INDEX IF NOT EXISTS idx_metric_type ON metrics(metric_type)")
            
            # Alerts table (removed INDEX syntax from CREATE TABLE)
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS alerts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp DATETIME NOT NULL,
                    severity TEXT NOT NULL,
                    message TEXT NOT NULL,
                    acknowledged BOOLEAN DEFAULT 0,
                    acknowledged_at DATETIME
                )
            """)
            
            # Create indexes separately
            cursor.execute("CREATE INDEX IF NOT EXISTS idx_alerts_timestamp ON alerts(timestamp)")
            cursor.execute("CREATE INDEX IF NOT EXISTS idx_alerts_severity ON alerts(severity)")
            
            conn.commit()
            self.logger.debug("Database schema initialized")
    
    def insert_metric(
        self,
        timestamp: datetime,
        target: str,
        metric_type: str,
        value: float,
        unit: str
    ):
        """
        Insert a metric into the database.
        
        Args:
            timestamp: Measurement timestamp
            target: Target identifier
            metric_type: Type of metric (latency, packet_loss, etc.)
            value: Metric value
            unit: Unit of measurement
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute(
                    """
                    INSERT INTO metrics (timestamp, target, metric_type, value, unit)
                    VALUES (?, ?, ?, ?, ?)
                    """,
                    (timestamp, target, metric_type, value, unit)
                )
                conn.commit()
                self.logger.debug(
                    f"Inserted metric: {target} {metric_type}={value}{unit}"
                )
        except Exception as e:
            self.logger.error(f"Error inserting metric: {e}")
    
    def insert_alert(self, timestamp: datetime, severity: str, message: str):
        """
        Insert an alert into the database.
        
        Args:
            timestamp: Alert timestamp
            severity: Alert severity level
            message: Alert message
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute(
                    """
                    INSERT INTO alerts (timestamp, severity, message)
                    VALUES (?, ?, ?)
                    """,
                    (timestamp, severity, message)
                )
                conn.commit()
                self.logger.debug(f"Inserted alert: {severity} - {message}")
        except Exception as e:
            self.logger.error(f"Error inserting alert: {e}")
    
    def get_metrics(
        self,
        target: str,
        metric_type: str,
        start_time: Optional[datetime] = None,
        end_time: Optional[datetime] = None
    ) -> List[Dict]:
        """
        Retrieve metrics from database.
        
        Args:
            target: Target identifier
            metric_type: Type of metric to retrieve
            start_time: Start of time range (optional)
            end_time: End of time range (optional)
            
        Returns:
            List of metric dictionaries
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                
                query = """
                    SELECT timestamp, value, unit
                    FROM metrics
                    WHERE target = ? AND metric_type = ?
                """
                params = [target, metric_type]
                
                if start_time:
                    query += " AND timestamp >= ?"
                    params.append(start_time)
                
                if end_time:
                    query += " AND timestamp <= ?"
                    params.append(end_time)
                
                query += " ORDER BY timestamp ASC"
                
                cursor.execute(query, params)
                rows = cursor.fetchall()
                
                return [dict(row) for row in rows]
                
        except Exception as e:
            self.logger.error(f"Error retrieving metrics: {e}")
            return []
    
    def get_statistics(self, target: str, duration_hours: int = 24) -> Dict:
        """
        Calculate statistical summary for a target.
        
        Args:
            target: Target identifier
            duration_hours: Time period to analyze
            
        Returns:
            Dictionary containing statistics
        """
        start_time = datetime.now() - timedelta(hours=duration_hours)
        
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                
                stats = {}
                
                for metric_type in ["latency", "packet_loss", "jitter"]:
                    cursor.execute(
                        """
                        SELECT
                            AVG(value) as avg,
                            MIN(value) as min,
                            MAX(value) as max,
                            COUNT(*) as count
                        FROM metrics
                        WHERE target = ? AND metric_type = ? AND timestamp >= ?
                        """,
                        (target, metric_type, start_time)
                    )
                    
                    row = cursor.fetchone()
                    if row and row["count"] > 0:
                        stats[metric_type] = {
                            "average": row["avg"],
                            "minimum": row["min"],
                            "maximum": row["max"],
                            "samples": row["count"]
                        }
                
                return stats
                
        except Exception as e:
            self.logger.error(f"Error calculating statistics: {e}")
            return {}
    
    def cleanup_old_data(self, retention_days: int = 30):
        """
        Remove old data beyond retention period.
        
        Args:
            retention_days: Number of days to retain data
        """
        cutoff_date = datetime.now() - timedelta(days=retention_days)
        
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                
                # Delete old metrics
                cursor.execute(
                    "DELETE FROM metrics WHERE timestamp < ?",
                    (cutoff_date,)
                )
                metrics_deleted = cursor.rowcount
                
                # Delete old acknowledged alerts
                cursor.execute(
                    "DELETE FROM alerts WHERE timestamp < ? AND acknowledged = 1",
                    (cutoff_date,)
                )
                alerts_deleted = cursor.rowcount
                
                conn.commit()
                
                self.logger.info(
                    f"Cleanup complete: {metrics_deleted} metrics, "
                    f"{alerts_deleted} alerts removed"
                )
                
        except Exception as e:
            self.logger.error(f"Error during cleanup: {e}")
