"""
Configuration management module for handling application settings.
"""

import json
import os
from typing import Any, Dict, Optional, Union
from pathlib import Path


class Config:
    """A simple configuration management class."""

    def __init__(self, config_file: Optional[str] = None):
        """Initialize the configuration manager.

        Args:
            config_file: Path to the configuration file (optional)
        """
        self.config_file = config_file
        self._config: Dict[str, Any] = {}
        
        if config_file and os.path.exists(config_file):
            self.load()

    def load(self, config_file: Optional[str] = None) -> None:
        """Load configuration from a file.

        Args:
            config_file: Path to the configuration file (optional)
        """
        file_path = config_file or self.config_file
        if not file_path:
            raise ValueError("No configuration file specified")
        
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"Configuration file not found: {file_path}")
        
        with open(file_path, "r", encoding="utf-8") as f:
            self._config = json.load(f)

    def save(self, config_file: Optional[str] = None) -> None:
        """Save configuration to a file.

        Args:
            config_file: Path to the configuration file (optional)
        """
        file_path = config_file or self.config_file
        if not file_path:
            raise ValueError("No configuration file specified")
        
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(file_path), exist_ok=True)
        
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(self._config, f, indent=2, ensure_ascii=False)

    def get(self, key: str, default: Any = None) -> Any:
        """Get a configuration value using dot notation.

        Args:
            key: Configuration key (supports dot notation like 'database.host')
            default: Default value if key is not found

        Returns:
            The configuration value or default
        """
        keys = key.split(".")
        value = self._config
        
        try:
            for k in keys:
                value = value[k]
            return value
        except (KeyError, TypeError):
            return default

    def set(self, key: str, value: Any) -> None:
        """Set a configuration value using dot notation.

        Args:
            key: Configuration key (supports dot notation like 'database.host')
            value: The value to set
        """
        keys = key.split(".")
        config = self._config
        
        # Navigate to the parent of the target key
        for k in keys[:-1]:
            if k not in config:
                config[k] = {}
            config = config[k]
        
        # Set the value
        config[keys[-1]] = value

    def has(self, key: str) -> bool:
        """Check if a configuration key exists.

        Args:
            key: Configuration key (supports dot notation)

        Returns:
            True if the key exists, False otherwise
        """
        return self.get(key) is not None

    def delete(self, key: str) -> bool:
        """Delete a configuration key.

        Args:
            key: Configuration key (supports dot notation)

        Returns:
            True if the key was deleted, False if it didn't exist
        """
        keys = key.split(".")
        config = self._config
        
        try:
            # Navigate to the parent of the target key
            for k in keys[:-1]:
                config = config[k]
            
            # Delete the key
            del config[keys[-1]]
            return True
        except (KeyError, TypeError):
            return False

    def clear(self) -> None:
        """Clear all configuration values."""
        self._config.clear()

    def to_dict(self) -> Dict[str, Any]:
        """Get the configuration as a dictionary.

        Returns:
            A copy of the configuration dictionary
        """
        return self._config.copy()

    def from_dict(self, config_dict: Dict[str, Any]) -> None:
        """Load configuration from a dictionary.

        Args:
            config_dict: Dictionary containing configuration values
        """
        self._config = config_dict.copy()

    def merge(self, config_dict: Dict[str, Any]) -> None:
        """Merge configuration from a dictionary.

        Args:
            config_dict: Dictionary containing configuration values to merge
        """
        self._merge_dicts(self._config, config_dict)

    def _merge_dicts(self, target: Dict[str, Any], source: Dict[str, Any]) -> None:
        """Recursively merge two dictionaries.

        Args:
            target: Target dictionary to merge into
            source: Source dictionary to merge from
        """
        for key, value in source.items():
            if key in target and isinstance(target[key], dict) and isinstance(value, dict):
                self._merge_dicts(target[key], value)
            else:
                target[key] = value

    def get_section(self, section: str) -> Dict[str, Any]:
        """Get a configuration section.

        Args:
            section: Section name

        Returns:
            Dictionary containing the section configuration
        """
        return self.get(section, {})

    def set_section(self, section: str, config_dict: Dict[str, Any]) -> None:
        """Set a configuration section.

        Args:
            section: Section name
            config_dict: Dictionary containing section configuration
        """
        self.set(section, config_dict)

    def list_sections(self) -> list[str]:
        """List all configuration sections.

        Returns:
            List of section names
        """
        return list(self._config.keys())

    def validate(self, schema: Dict[str, Any]) -> bool:
        """Validate configuration against a schema.

        Args:
            schema: Schema dictionary defining required keys and types

        Returns:
            True if configuration is valid, False otherwise
        """
        try:
            self._validate_schema(self._config, schema)
            return True
        except ValueError:
            return False

    def _validate_schema(self, config: Dict[str, Any], schema: Dict[str, Any]) -> None:
        """Recursively validate configuration against schema.

        Args:
            config: Configuration to validate
            schema: Schema to validate against

        Raises:
            ValueError: If validation fails
        """
        for key, schema_value in schema.items():
            if key not in config:
                if schema_value.get("required", False):
                    raise ValueError(f"Required key '{key}' is missing")
                continue
            
            config_value = config[key]
            expected_type = schema_value.get("type")
            
            if expected_type and not isinstance(config_value, expected_type):
                raise ValueError(f"Key '{key}' should be of type {expected_type}")
            
            if "schema" in schema_value and isinstance(config_value, dict):
                self._validate_schema(config_value, schema_value["schema"]) 