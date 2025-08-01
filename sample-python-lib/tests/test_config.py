"""
Tests for the config module.
"""

import json
import os
import tempfile
import pytest
from sample_python_lib.config import Config


class TestConfig:
    """Test cases for the Config class."""

    def setup_method(self):
        """Set up test fixtures."""
        self.config = Config()

    def test_init_empty(self):
        """Test initialization with no config file."""
        config = Config()
        assert config._config == {}
        assert config.config_file is None

    def test_init_with_file(self):
        """Test initialization with config file."""
        with tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False) as f:
            json.dump({"test": "value"}, f)
            config_file = f.name

        try:
            config = Config(config_file)
            assert config.config_file == config_file
            assert config._config == {"test": "value"}
        finally:
            os.unlink(config_file)

    def test_load_existing_file(self):
        """Test loading from existing file."""
        with tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False) as f:
            json.dump({"database": {"host": "localhost", "port": 5432}}, f)
            config_file = f.name

        try:
            config = Config()
            config.load(config_file)
            assert config._config == {"database": {"host": "localhost", "port": 5432}}
        finally:
            os.unlink(config_file)

    def test_load_nonexistent_file(self):
        """Test loading from non-existent file raises exception."""
        config = Config()
        with pytest.raises(FileNotFoundError):
            config.load("nonexistent.json")

    def test_save_to_file(self):
        """Test saving to file."""
        with tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False) as f:
            config_file = f.name

        try:
            config = Config()
            config.set("database.host", "localhost")
            config.set("database.port", 5432)
            config.save(config_file)

            # Verify file was created and contains correct data
            with open(config_file, 'r') as f:
                saved_data = json.load(f)
            assert saved_data == {"database": {"host": "localhost", "port": 5432}}
        finally:
            if os.path.exists(config_file):
                os.unlink(config_file)

    def test_get_simple_key(self):
        """Test getting simple key."""
        self.config._config = {"host": "localhost"}
        assert self.config.get("host") == "localhost"
        assert self.config.get("nonexistent", "default") == "default"

    def test_get_nested_key(self):
        """Test getting nested key with dot notation."""
        self.config._config = {
            "database": {
                "host": "localhost",
                "port": 5432
            }
        }
        assert self.config.get("database.host") == "localhost"
        assert self.config.get("database.port") == 5432
        assert self.config.get("database.nonexistent", "default") == "default"

    def test_set_simple_key(self):
        """Test setting simple key."""
        self.config.set("host", "localhost")
        assert self.config._config["host"] == "localhost"

    def test_set_nested_key(self):
        """Test setting nested key with dot notation."""
        self.config.set("database.host", "localhost")
        self.config.set("database.port", 5432)
        
        assert self.config._config["database"]["host"] == "localhost"
        assert self.config._config["database"]["port"] == 5432

    def test_set_deeply_nested_key(self):
        """Test setting deeply nested key."""
        self.config.set("app.database.postgres.host", "localhost")
        self.config.set("app.database.postgres.port", 5432)
        
        assert self.config._config["app"]["database"]["postgres"]["host"] == "localhost"
        assert self.config._config["app"]["database"]["postgres"]["port"] == 5432

    def test_has_key(self):
        """Test checking if key exists."""
        self.config._config = {"host": "localhost", "database": {"port": 5432}}
        
        assert self.config.has("host") is True
        assert self.config.has("database.port") is True
        assert self.config.has("nonexistent") is False
        assert self.config.has("database.nonexistent") is False

    def test_delete_key(self):
        """Test deleting keys."""
        self.config._config = {
            "host": "localhost",
            "database": {
                "host": "db.example.com",
                "port": 5432
            }
        }
        
        assert self.config.delete("host") is True
        assert "host" not in self.config._config
        
        assert self.config.delete("database.port") is True
        assert "port" not in self.config._config["database"]
        
        assert self.config.delete("nonexistent") is False

    def test_clear(self):
        """Test clearing all configuration."""
        self.config._config = {"host": "localhost", "port": 5432}
        self.config.clear()
        assert self.config._config == {}

    def test_to_dict(self):
        """Test getting configuration as dictionary."""
        original_config = {"host": "localhost", "port": 5432}
        self.config._config = original_config.copy()
        
        config_dict = self.config.to_dict()
        assert config_dict == original_config
        
        # Verify it's a copy, not a reference
        config_dict["host"] = "modified"
        assert self.config._config["host"] == "localhost"

    def test_from_dict(self):
        """Test loading configuration from dictionary."""
        config_dict = {"host": "localhost", "port": 5432}
        self.config.from_dict(config_dict)
        assert self.config._config == config_dict

    def test_merge(self):
        """Test merging configuration."""
        self.config._config = {"host": "localhost", "port": 5432}
        merge_dict = {"port": 8080, "debug": True}
        
        self.config.merge(merge_dict)
        assert self.config._config == {
            "host": "localhost",
            "port": 8080,
            "debug": True
        }

    def test_merge_nested(self):
        """Test merging nested configuration."""
        self.config._config = {
            "database": {
                "host": "localhost",
                "port": 5432
            }
        }
        merge_dict = {
            "database": {
                "port": 8080,
                "name": "testdb"
            }
        }
        
        self.config.merge(merge_dict)
        assert self.config._config == {
            "database": {
                "host": "localhost",
                "port": 8080,
                "name": "testdb"
            }
        }

    def test_get_section(self):
        """Test getting configuration section."""
        self.config._config = {
            "database": {
                "host": "localhost",
                "port": 5432
            },
            "app": {
                "name": "testapp"
            }
        }
        
        db_section = self.config.get_section("database")
        assert db_section == {"host": "localhost", "port": 5432}
        
        app_section = self.config.get_section("app")
        assert app_section == {"name": "testapp"}
        
        nonexistent_section = self.config.get_section("nonexistent")
        assert nonexistent_section == {}

    def test_set_section(self):
        """Test setting configuration section."""
        db_config = {"host": "localhost", "port": 5432}
        self.config.set_section("database", db_config)
        assert self.config._config["database"] == db_config

    def test_list_sections(self):
        """Test listing configuration sections."""
        self.config._config = {
            "database": {},
            "app": {},
            "logging": {}
        }
        
        sections = self.config.list_sections()
        assert set(sections) == {"database", "app", "logging"}

    def test_validate_simple_schema(self):
        """Test validation against simple schema."""
        self.config._config = {
            "host": "localhost",
            "port": 5432
        }
        
        schema = {
            "host": {"type": str, "required": True},
            "port": {"type": int, "required": True}
        }
        
        assert self.config.validate(schema) is True

    def test_validate_missing_required(self):
        """Test validation with missing required field."""
        self.config._config = {"host": "localhost"}
        
        schema = {
            "host": {"type": str, "required": True},
            "port": {"type": int, "required": True}
        }
        
        assert self.config.validate(schema) is False

    def test_validate_wrong_type(self):
        """Test validation with wrong type."""
        self.config._config = {"host": "localhost", "port": "5432"}
        
        schema = {
            "host": {"type": str, "required": True},
            "port": {"type": int, "required": True}
        }
        
        assert self.config.validate(schema) is False

    def test_validate_nested_schema(self):
        """Test validation against nested schema."""
        self.config._config = {
            "database": {
                "host": "localhost",
                "port": 5432
            }
        }
        
        schema = {
            "database": {
                "type": dict,
                "schema": {
                    "host": {"type": str, "required": True},
                    "port": {"type": int, "required": True}
                }
            }
        }
        
        assert self.config.validate(schema) is True

    def test_edge_cases(self):
        """Test edge cases and boundary conditions."""
        # Empty configuration
        assert self.config.get("any.key") is None
        assert self.config.get("any.key", "default") == "default"
        
        # Empty string key
        self.config.set("", "value")
        assert self.config.get("") == "value"
        
        # Key with only dots
        self.config.set("...", "value")
        assert self.config.get("...") == "value"

    def test_file_operations_with_directory_creation(self):
        """Test saving to file with directory creation."""
        with tempfile.TemporaryDirectory() as temp_dir:
            config_file = os.path.join(temp_dir, "subdir", "config.json")
            
            config = Config()
            config.set("host", "localhost")
            config.save(config_file)
            
            # Verify directory was created and file was saved
            assert os.path.exists(config_file)
            
            # Load and verify content
            loaded_config = Config()
            loaded_config.load(config_file)
            assert loaded_config.get("host") == "localhost" 