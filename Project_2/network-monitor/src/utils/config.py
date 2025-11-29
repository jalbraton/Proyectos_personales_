"""
Configuration Management Module

Handles loading and validation of YAML configuration files.
"""

import yaml
import logging
from pathlib import Path
from typing import Any, Optional


class ConfigManager:
    """
    Manages application configuration from YAML files.
    
    Provides dotted-path access to nested configuration values
    with default value support.
    """
    
    def __init__(self, config_path: str):
        """
        Initialize configuration manager.
        
        Args:
            config_path: Path to YAML configuration file
        """
        self.logger = logging.getLogger(__name__)
        self.config_path = config_path
        self.config_data = {}
        
        self._load_config()
    
    def _load_config(self):
        """Load configuration from YAML file."""
        try:
            config_file = Path(self.config_path)
            
            if not config_file.exists():
                self.logger.warning(f"Config file not found: {self.config_path}")
                self._create_default_config()
                return
            
            with open(config_file, 'r') as f:
                self.config_data = yaml.safe_load(f) or {}
            
            self.logger.info(f"Configuration loaded from {self.config_path}")
            
        except Exception as e:
            self.logger.error(f"Error loading configuration: {e}")
            self.config_data = {}
    
    def _create_default_config(self):
        """Create default configuration file."""
        default_config = {
            "monitoring": {
                "interval": 5,
                "targets": [
                    {"host": "8.8.8.8", "name": "Google DNS"},
                    {"host": "1.1.1.1", "name": "Cloudflare DNS"}
                ]
            },
            "thresholds": {
                "latency_warning": 100,
                "latency_critical": 200,
                "packet_loss_warning": 1.0,
                "packet_loss_critical": 5.0
            },
            "database": {
                "path": "data/metrics.db",
                "retention_days": 30
            }
        }
        
        try:
            config_file = Path(self.config_path)
            config_file.parent.mkdir(parents=True, exist_ok=True)
            
            with open(config_file, 'w') as f:
                yaml.dump(default_config, f, default_flow_style=False)
            
            self.config_data = default_config
            self.logger.info(f"Created default configuration: {self.config_path}")
            
        except PermissionError:
            self.logger.error(f"Permission denied creating config file: {self.config_path}")
            self.config_data = default_config  # Use in-memory config
        except Exception as e:
            self.logger.error(f"Error creating default config: {e}")
            self.config_data = default_config  # Use in-memory config
    
    def get(self, key_path: str, default: Any = None) -> Any:
        """
        Get configuration value using dotted path notation.
        
        Args:
            key_path: Dotted path to configuration value (e.g., "database.path")
            default: Default value if key not found
            
        Returns:
            Configuration value or default
        """
        keys = key_path.split('.')
        value = self.config_data
        
        try:
            for key in keys:
                value = value[key]
            return value
        except (KeyError, TypeError):
            return default
