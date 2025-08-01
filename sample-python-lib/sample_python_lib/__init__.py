"""
Sample Python Library

A sample Python library that demonstrates how to use the Jenkins CI/CD pipeline
for Python library projects.
"""

__version__ = "1.0.0"
__author__ = "Sample Team"
__email__ = "team@company.com"

from .calculator import Calculator
from .string_utils import StringUtils
from .config import Config
from .logger import Logger

__all__ = ["Calculator", "StringUtils", "Config", "Logger"] 