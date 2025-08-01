"""
Command-line interface for the sample Python library.
"""

import click
from .calculator import Calculator
from .string_utils import StringUtils
from .config import Config
from .logger import Logger


@click.group()
@click.version_option(version="1.0.0")
def cli():
    """Sample Python Library CLI - A demonstration of the Jenkins CI/CD pipeline."""
    pass


@cli.group()
def calc():
    """Calculator operations."""
    pass


@calc.command()
@click.argument('a', type=float)
@click.argument('b', type=float)
def add(a, b):
    """Add two numbers."""
    calculator = Calculator()
    result = calculator.add(a, b)
    click.echo(f"{a} + {b} = {result}")


@calc.command()
@click.argument('a', type=float)
@click.argument('b', type=float)
def subtract(a, b):
    """Subtract two numbers."""
    calculator = Calculator()
    result = calculator.subtract(a, b)
    click.echo(f"{a} - {b} = {result}")


@calc.command()
@click.argument('a', type=float)
@click.argument('b', type=float)
def multiply(a, b):
    """Multiply two numbers."""
    calculator = Calculator()
    result = calculator.multiply(a, b)
    click.echo(f"{a} * {b} = {result}")


@calc.command()
@click.argument('a', type=float)
@click.argument('b', type=float)
def divide(a, b):
    """Divide two numbers."""
    calculator = Calculator()
    try:
        result = calculator.divide(a, b)
        click.echo(f"{a} / {b} = {result}")
    except ZeroDivisionError:
        click.echo("Error: Cannot divide by zero", err=True)


@calc.command()
@click.argument('base', type=float)
@click.argument('exponent', type=float)
def power(base, exponent):
    """Raise a number to a power."""
    calculator = Calculator()
    result = calculator.power(base, exponent)
    click.echo(f"{base} ^ {exponent} = {result}")


@calc.command()
@click.argument('number', type=float)
def sqrt(number):
    """Calculate square root."""
    calculator = Calculator()
    try:
        result = calculator.sqrt(number)
        click.echo(f"√{number} = {result}")
    except ValueError as e:
        click.echo(f"Error: {e}", err=True)


@cli.group()
def string():
    """String utility operations."""
    pass


@string.command()
@click.argument('text')
def reverse(text):
    """Reverse a string."""
    utils = StringUtils()
    result = utils.reverse(text)
    click.echo(f"'{text}' reversed is '{result}'")


@string.command()
@click.argument('text')
def palindrome(text):
    """Check if a string is a palindrome."""
    utils = StringUtils()
    result = utils.palindrome(text)
    status = "is" if result else "is not"
    click.echo(f"'{text}' {status} a palindrome")


@string.command()
@click.argument('text')
def count_vowels(text):
    """Count vowels in a string."""
    utils = StringUtils()
    result = utils.count_vowels(text)
    click.echo(f"'{text}' has {result} vowels")


@string.command()
@click.argument('text')
def count_consonants(text):
    """Count consonants in a string."""
    utils = StringUtils()
    result = utils.count_consonants(text)
    click.echo(f"'{text}' has {result} consonants")


@string.command()
@click.argument('text')
def capitalize(text):
    """Capitalize words in a string."""
    utils = StringUtils()
    result = utils.capitalize_words(text)
    click.echo(f"'{text}' capitalized is '{result}'")


@string.command()
@click.argument('text')
def slugify(text):
    """Convert string to URL-friendly slug."""
    utils = StringUtils()
    result = utils.slugify(text)
    click.echo(f"'{text}' slugified is '{result}'")


@cli.group()
def config():
    """Configuration management operations."""
    pass


@config.command()
@click.argument('config_file')
@click.argument('key')
@click.argument('value')
def set_value(config_file, key, value):
    """Set a configuration value."""
    config = Config(config_file)
    config.set(key, value)
    config.save()
    click.echo(f"Set {key} = {value} in {config_file}")


@config.command()
@click.argument('config_file')
@click.argument('key')
def get_value(config_file, key):
    """Get a configuration value."""
    config = Config(config_file)
    value = config.get(key)
    if value is not None:
        click.echo(f"{key} = {value}")
    else:
        click.echo(f"Key '{key}' not found", err=True)


@config.command()
@click.argument('config_file')
def list_sections(config_file):
    """List configuration sections."""
    config = Config(config_file)
    sections = config.list_sections()
    if sections:
        click.echo("Configuration sections:")
        for section in sections:
            click.echo(f"  - {section}")
    else:
        click.echo("No sections found")


@cli.command()
@click.option('--name', default='cli', help='Logger name')
@click.option('--level', default='INFO', help='Logging level')
@click.option('--format', 'format_type', default='json', help='Output format')
def demo_logger(name, level, format_type):
    """Demonstrate logging functionality."""
    logger = Logger(name, level=level, format_type=format_type)
    
    logger.info("Application started", version="1.0.0")
    logger.debug("Debug information", user="demo")
    logger.warning("Configuration file not found", file="config.json")
    logger.error("Database connection failed", host="localhost", port=5432)
    
    click.echo(f"Logged messages with {name} logger (level: {level}, format: {format_type})")


@cli.command()
def demo():
    """Run a demonstration of all features."""
    click.echo("=== Sample Python Library Demo ===\n")
    
    # Calculator demo
    click.echo("Calculator Operations:")
    calc = Calculator()
    click.echo(f"  5 + 3 = {calc.add(5, 3)}")
    click.echo(f"  10 - 4 = {calc.subtract(10, 4)}")
    click.echo(f"  3 * 7 = {calc.multiply(3, 7)}")
    click.echo(f"  15 / 3 = {calc.divide(15, 3)}")
    click.echo(f"  2 ^ 8 = {calc.power(2, 8)}")
    click.echo(f"  √16 = {calc.sqrt(16)}")
    click.echo()
    
    # String utils demo
    click.echo("String Utilities:")
    utils = StringUtils()
    click.echo(f"  'hello' reversed = '{utils.reverse('hello')}'")
    click.echo(f"  'racecar' is palindrome = {utils.palindrome('racecar')}")
    click.echo(f"  'hello world' has {utils.count_vowels('hello world')} vowels")
    click.echo(f"  'hello world' capitalized = '{utils.capitalize_words('hello world')}'")
    click.echo(f"  'Hello World!' slugified = '{utils.slugify('Hello World!')}'")
    click.echo()
    
    # Config demo
    click.echo("Configuration Management:")
    config = Config()
    config.set("database.host", "localhost")
    config.set("database.port", 5432)
    config.set("app.name", "demo")
    click.echo(f"  Database host: {config.get('database.host')}")
    click.echo(f"  Database port: {config.get('database.port')}")
    click.echo(f"  App name: {config.get('app.name')}")
    click.echo()
    
    # Logger demo
    click.echo("Logging (check console output):")
    logger = Logger("demo", level="INFO", format_type="text")
    logger.info("Demo completed successfully", features=["calculator", "string_utils", "config", "logger"])
    
    click.echo("Demo completed! 🎉")


def main():
    """Main entry point for the CLI."""
    cli()


if __name__ == '__main__':
    main() 