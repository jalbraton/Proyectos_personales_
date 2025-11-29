"""
Data Models

Defines data structures used throughout the application.
"""

from dataclasses import dataclass
from datetime import datetime
from typing import Optional


@dataclass
class Metric:
    """
    Represents a single network metric measurement.
    """
    timestamp: datetime
    target: str
    metric_type: str
    value: float
    unit: str


@dataclass
class Alert:
    """
    Represents a monitoring alert.
    """
    id: Optional[int]
    timestamp: datetime
    severity: str
    message: str
    acknowledged: bool = False
    acknowledged_at: Optional[datetime] = None
