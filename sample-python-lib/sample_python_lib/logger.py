"""
Logging module providing structured logging capabilities.
"""

import logging
import sys
from datetime import datetime
from typing import Any, Dict, Optional, Union
import structlog


class Logger:
    """A structured logger class with multiple output formats."""

    def __init__(self, name: str, level: str = "INFO", format_type: str = "json"):
        """Initialize the logger.

        Args:
            name: Logger name
            level: Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
            format_type: Output format (json, text, simple)
        """
        self.name = name
        self.level = getattr(logging, level.upper())
        self.format_type = format_type
        
        # Configure structlog
        self._configure_structlog()
        
        # Create the logger
        self.logger = structlog.get_logger(name)

    def _configure_structlog(self) -> None:
        """Configure structlog with the specified format."""
        processors = [
            structlog.stdlib.filter_by_level,
            structlog.stdlib.add_logger_name,
            structlog.stdlib.add_log_level,
            structlog.stdlib.PositionalArgumentsFormatter(),
            structlog.processors.TimeStamper(fmt="iso"),
            structlog.processors.StackInfoRenderer(),
            structlog.processors.format_exc_info,
        ]
        
        if self.format_type == "json":
            processors.append(structlog.processors.JSONRenderer())
        elif self.format_type == "text":
            processors.append(
                structlog.dev.ConsoleRenderer(colors=True)
            )
        else:  # simple
            processors.append(
                structlog.processors.KeyValueRenderer(
                    key_order=["timestamp", "level", "logger", "event"]
                )
            )
        
        structlog.configure(
            processors=processors,
            context_class=dict,
            logger_factory=structlog.stdlib.LoggerFactory(),
            wrapper_class=structlog.stdlib.BoundLogger,
            cache_logger_on_first_use=True,
        )

    def debug(self, message: str, **kwargs: Any) -> None:
        """Log a debug message.

        Args:
            message: The message to log
            **kwargs: Additional context data
        """
        self.logger.debug(message, **kwargs)

    def info(self, message: str, **kwargs: Any) -> None:
        """Log an info message.

        Args:
            message: The message to log
            **kwargs: Additional context data
        """
        self.logger.info(message, **kwargs)

    def warning(self, message: str, **kwargs: Any) -> None:
        """Log a warning message.

        Args:
            message: The message to log
            **kwargs: Additional context data
        """
        self.logger.warning(message, **kwargs)

    def error(self, message: str, **kwargs: Any) -> None:
        """Log an error message.

        Args:
            message: The message to log
            **kwargs: Additional context data
        """
        self.logger.error(message, **kwargs)

    def critical(self, message: str, **kwargs: Any) -> None:
        """Log a critical message.

        Args:
            message: The message to log
            **kwargs: Additional context data
        """
        self.logger.critical(message, **kwargs)

    def exception(self, message: str, **kwargs: Any) -> None:
        """Log an exception message.

        Args:
            message: The message to log
            **kwargs: Additional context data
        """
        self.logger.exception(message, **kwargs)

    def log(self, level: str, message: str, **kwargs: Any) -> None:
        """Log a message at the specified level.

        Args:
            level: Logging level
            message: The message to log
            **kwargs: Additional context data
        """
        level_method = getattr(self.logger, level.lower(), self.logger.info)
        level_method(message, **kwargs)

    def bind(self, **kwargs: Any) -> "Logger":
        """Bind context data to the logger.

        Args:
            **kwargs: Context data to bind

        Returns:
            A new logger instance with bound context
        """
        bound_logger = self.logger.bind(**kwargs)
        new_logger = Logger(self.name, level=logging.getLevelName(self.level))
        new_logger.logger = bound_logger
        return new_logger

    def unbind(self, *keys: str) -> "Logger":
        """Unbind context data from the logger.

        Args:
            *keys: Keys to unbind

        Returns:
            A new logger instance with unbound context
        """
        bound_logger = self.logger.unbind(*keys)
        new_logger = Logger(self.name, level=logging.getLevelName(self.level))
        new_logger.logger = bound_logger
        return new_logger

    def set_level(self, level: str) -> None:
        """Set the logging level.

        Args:
            level: Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
        """
        self.level = getattr(logging, level.upper())
        logging.getLogger(self.name).setLevel(self.level)

    def add_context(self, **kwargs: Any) -> None:
        """Add context data to all subsequent log messages.

        Args:
            **kwargs: Context data to add
        """
        self.logger = self.logger.bind(**kwargs)

    def remove_context(self, *keys: str) -> None:
        """Remove context data from all subsequent log messages.

        Args:
            *keys: Keys to remove
        """
        self.logger = self.logger.unbind(*keys)

    def log_function_call(self, func_name: str, args: tuple, kwargs: dict, result: Any = None) -> None:
        """Log a function call with its arguments and result.

        Args:
            func_name: Name of the function
            args: Function arguments
            kwargs: Function keyword arguments
            result: Function result (optional)
        """
        self.debug(
            "Function called",
            function=func_name,
            args=args,
            kwargs=kwargs,
            result=result,
        )

    def log_performance(self, operation: str, duration: float, **kwargs: Any) -> None:
        """Log performance metrics.

        Args:
            operation: Name of the operation
            duration: Duration in seconds
            **kwargs: Additional performance data
        """
        self.info(
            "Performance metric",
            operation=operation,
            duration=duration,
            **kwargs,
        )

    def log_user_action(self, user_id: str, action: str, **kwargs: Any) -> None:
        """Log user actions.

        Args:
            user_id: User identifier
            action: Action performed
            **kwargs: Additional action data
        """
        self.info(
            "User action",
            user_id=user_id,
            action=action,
            **kwargs,
        )

    def log_system_event(self, event: str, **kwargs: Any) -> None:
        """Log system events.

        Args:
            event: Event name
            **kwargs: Additional event data
        """
        self.info(
            "System event",
            event=event,
            **kwargs,
        )

    def log_error_with_context(self, error: Exception, context: Dict[str, Any]) -> None:
        """Log an error with additional context.

        Args:
            error: The exception that occurred
            context: Additional context data
        """
        self.error(
            "Error occurred",
            error_type=type(error).__name__,
            error_message=str(error),
            **context,
        )

    def log_data_access(self, operation: str, resource: str, **kwargs: Any) -> None:
        """Log data access operations.

        Args:
            operation: Type of operation (read, write, delete, etc.)
            resource: Resource being accessed
            **kwargs: Additional access data
        """
        self.info(
            "Data access",
            operation=operation,
            resource=resource,
            **kwargs,
        )

    def log_security_event(self, event_type: str, **kwargs: Any) -> None:
        """Log security-related events.

        Args:
            event_type: Type of security event
            **kwargs: Additional security data
        """
        self.warning(
            "Security event",
            event_type=event_type,
            **kwargs,
        )

    def get_logger(self) -> structlog.BoundLogger:
        """Get the underlying structlog logger.

        Returns:
            The structlog logger instance
        """
        return self.logger 