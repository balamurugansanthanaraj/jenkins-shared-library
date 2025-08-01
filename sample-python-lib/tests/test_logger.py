"""
Tests for the logger module.
"""

import logging
import pytest
from unittest.mock import patch, MagicMock
from sample_python_lib.logger import Logger


class TestLogger:
    """Test cases for the Logger class."""

    def setup_method(self):
        """Set up test fixtures."""
        self.logger = Logger("test_logger")

    def test_init_defaults(self):
        """Test initialization with default parameters."""
        logger = Logger("test")
        assert logger.name == "test"
        assert logger.level == logging.INFO
        assert logger.format_type == "json"

    def test_init_custom_level(self):
        """Test initialization with custom level."""
        logger = Logger("test", level="DEBUG")
        assert logger.level == logging.DEBUG

    def test_init_custom_format(self):
        """Test initialization with custom format."""
        logger = Logger("test", format_type="text")
        assert logger.format_type == "text"

    @patch('structlog.get_logger')
    def test_debug(self, mock_get_logger):
        """Test debug logging."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.debug("test message", key="value")
        
        mock_logger.debug.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_info(self, mock_get_logger):
        """Test info logging."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.info("test message", key="value")
        
        mock_logger.info.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_warning(self, mock_get_logger):
        """Test warning logging."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.warning("test message", key="value")
        
        mock_logger.warning.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_error(self, mock_get_logger):
        """Test error logging."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.error("test message", key="value")
        
        mock_logger.error.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_critical(self, mock_get_logger):
        """Test critical logging."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.critical("test message", key="value")
        
        mock_logger.critical.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_exception(self, mock_get_logger):
        """Test exception logging."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.exception("test message", key="value")
        
        mock_logger.exception.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_log_custom_level(self, mock_get_logger):
        """Test logging with custom level."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log("debug", "test message", key="value")
        
        mock_logger.debug.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_log_invalid_level(self, mock_get_logger):
        """Test logging with invalid level defaults to info."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log("invalid_level", "test message", key="value")
        
        mock_logger.info.assert_called_once_with("test message", key="value")

    @patch('structlog.get_logger')
    def test_bind(self, mock_get_logger):
        """Test binding context data."""
        mock_logger = MagicMock()
        mock_bound_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        mock_logger.bind.return_value = mock_bound_logger
        
        logger = Logger("test")
        bound_logger = logger.bind(user_id="123", session_id="abc")
        
        mock_logger.bind.assert_called_once_with(user_id="123", session_id="abc")
        assert bound_logger.logger == mock_bound_logger

    @patch('structlog.get_logger')
    def test_unbind(self, mock_get_logger):
        """Test unbinding context data."""
        mock_logger = MagicMock()
        mock_unbound_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        mock_logger.unbind.return_value = mock_unbound_logger
        
        logger = Logger("test")
        unbound_logger = logger.unbind("user_id", "session_id")
        
        mock_logger.unbind.assert_called_once_with("user_id", "session_id")
        assert unbound_logger.logger == mock_unbound_logger

    def test_set_level(self):
        """Test setting logging level."""
        logger = Logger("test", level="INFO")
        logger.set_level("DEBUG")
        assert logger.level == logging.DEBUG

    @patch('structlog.get_logger')
    def test_add_context(self, mock_get_logger):
        """Test adding context data."""
        mock_logger = MagicMock()
        mock_bound_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        mock_logger.bind.return_value = mock_bound_logger
        
        logger = Logger("test")
        logger.add_context(user_id="123", session_id="abc")
        
        mock_logger.bind.assert_called_once_with(user_id="123", session_id="abc")
        assert logger.logger == mock_bound_logger

    @patch('structlog.get_logger')
    def test_remove_context(self, mock_get_logger):
        """Test removing context data."""
        mock_logger = MagicMock()
        mock_unbound_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        mock_logger.unbind.return_value = mock_unbound_logger
        
        logger = Logger("test")
        logger.remove_context("user_id", "session_id")
        
        mock_logger.unbind.assert_called_once_with("user_id", "session_id")
        assert logger.logger == mock_unbound_logger

    @patch('structlog.get_logger')
    def test_log_function_call(self, mock_get_logger):
        """Test logging function calls."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log_function_call("test_func", (1, 2), {"key": "value"}, "result")
        
        mock_logger.debug.assert_called_once_with(
            "Function called",
            function="test_func",
            args=(1, 2),
            kwargs={"key": "value"},
            result="result"
        )

    @patch('structlog.get_logger')
    def test_log_performance(self, mock_get_logger):
        """Test logging performance metrics."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log_performance("database_query", 0.5, rows=100)
        
        mock_logger.info.assert_called_once_with(
            "Performance metric",
            operation="database_query",
            duration=0.5,
            rows=100
        )

    @patch('structlog.get_logger')
    def test_log_user_action(self, mock_get_logger):
        """Test logging user actions."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log_user_action("user123", "login", ip="192.168.1.1")
        
        mock_logger.info.assert_called_once_with(
            "User action",
            user_id="user123",
            action="login",
            ip="192.168.1.1"
        )

    @patch('structlog.get_logger')
    def test_log_system_event(self, mock_get_logger):
        """Test logging system events."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log_system_event("server_start", port=8080)
        
        mock_logger.info.assert_called_once_with(
            "System event",
            event="server_start",
            port=8080
        )

    @patch('structlog.get_logger')
    def test_log_error_with_context(self, mock_get_logger):
        """Test logging errors with context."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        error = ValueError("test error")
        context = {"user_id": "123", "operation": "test"}
        
        logger.log_error_with_context(error, context)
        
        mock_logger.error.assert_called_once_with(
            "Error occurred",
            error_type="ValueError",
            error_message="test error",
            user_id="123",
            operation="test"
        )

    @patch('structlog.get_logger')
    def test_log_data_access(self, mock_get_logger):
        """Test logging data access operations."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log_data_access("read", "users", user_id="123")
        
        mock_logger.info.assert_called_once_with(
            "Data access",
            operation="read",
            resource="users",
            user_id="123"
        )

    @patch('structlog.get_logger')
    def test_log_security_event(self, mock_get_logger):
        """Test logging security events."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        logger.log_security_event("failed_login", ip="192.168.1.1")
        
        mock_logger.warning.assert_called_once_with(
            "Security event",
            event_type="failed_login",
            ip="192.168.1.1"
        )

    @patch('structlog.get_logger')
    def test_get_logger(self, mock_get_logger):
        """Test getting the underlying logger."""
        mock_logger = MagicMock()
        mock_get_logger.return_value = mock_logger
        
        logger = Logger("test")
        underlying_logger = logger.get_logger()
        
        assert underlying_logger == mock_logger

    def test_format_types(self):
        """Test different format types."""
        # Test JSON format
        json_logger = Logger("test", format_type="json")
        assert json_logger.format_type == "json"
        
        # Test text format
        text_logger = Logger("test", format_type="text")
        assert text_logger.format_type == "text"
        
        # Test simple format
        simple_logger = Logger("test", format_type="simple")
        assert simple_logger.format_type == "simple"

    def test_level_constants(self):
        """Test logging level constants."""
        logger = Logger("test", level="DEBUG")
        assert logger.level == logging.DEBUG
        
        logger = Logger("test", level="INFO")
        assert logger.level == logging.INFO
        
        logger = Logger("test", level="WARNING")
        assert logger.level == logging.WARNING
        
        logger = Logger("test", level="ERROR")
        assert logger.level == logging.ERROR
        
        logger = Logger("test", level="CRITICAL")
        assert logger.level == logging.CRITICAL

    def test_invalid_level(self):
        """Test handling of invalid level."""
        with pytest.raises(AttributeError):
            Logger("test", level="INVALID_LEVEL")

    def test_structlog_configuration(self):
        """Test that structlog is properly configured."""
        # This test verifies that the logger can be created without errors
        logger = Logger("test", format_type="json")
        assert logger.logger is not None
        
        logger = Logger("test", format_type="text")
        assert logger.logger is not None
        
        logger = Logger("test", format_type="simple")
        assert logger.logger is not None 