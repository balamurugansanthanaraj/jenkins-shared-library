# Sample Python Library API Documentation

This document provides detailed API documentation for the Sample Python Library, which demonstrates how to use the Jenkins CI/CD pipeline for Python library projects.

## Table of Contents

- [Calculator](#calculator)
- [String Utilities](#string-utilities)
- [Configuration Management](#configuration-management)
- [Logging](#logging)
- [Command Line Interface](#command-line-interface)

## Calculator

The `Calculator` class provides basic mathematical operations.

### Constructor

```python
Calculator()
```

Creates a new calculator instance.

### Methods

#### `add(a, b)`

Adds two numbers.

**Parameters:**
- `a` (Union[int, float]): First number
- `b` (Union[int, float]): Second number

**Returns:**
- Union[int, float]: Sum of the two numbers

**Example:**
```python
calc = Calculator()
result = calc.add(5, 3)  # Returns 8
```

#### `subtract(a, b)`

Subtracts two numbers.

**Parameters:**
- `a` (Union[int, float]): First number
- `b` (Union[int, float]): Second number

**Returns:**
- Union[int, float]: Difference of the two numbers

**Example:**
```python
calc = Calculator()
result = calc.subtract(10, 4)  # Returns 6
```

#### `multiply(a, b)`

Multiplies two numbers.

**Parameters:**
- `a` (Union[int, float]): First number
- `b` (Union[int, float]): Second number

**Returns:**
- Union[int, float]: Product of the two numbers

**Example:**
```python
calc = Calculator()
result = calc.multiply(3, 7)  # Returns 21
```

#### `divide(a, b)`

Divides two numbers.

**Parameters:**
- `a` (Union[int, float]): First number
- `b` (Union[int, float]): Second number

**Returns:**
- float: Quotient of the two numbers

**Raises:**
- ZeroDivisionError: If b is zero

**Example:**
```python
calc = Calculator()
result = calc.divide(15, 3)  # Returns 5.0
```

#### `power(base, exponent)`

Raises a number to a power.

**Parameters:**
- `base` (Union[int, float]): The base number
- `exponent` (Union[int, float]): The exponent

**Returns:**
- Union[int, float]: The result of base raised to the power of exponent

**Example:**
```python
calc = Calculator()
result = calc.power(2, 8)  # Returns 256
```

#### `sqrt(number)`

Calculates the square root of a number.

**Parameters:**
- `number` (Union[int, float]): The number to find the square root of

**Returns:**
- float: The square root of the number

**Raises:**
- ValueError: If number is negative

**Example:**
```python
calc = Calculator()
result = calc.sqrt(16)  # Returns 4.0
```

#### `factorial(n)`

Calculates the factorial of a number.

**Parameters:**
- `n` (int): The number to calculate factorial for

**Returns:**
- int: The factorial of the number

**Raises:**
- ValueError: If n is negative

**Example:**
```python
calc = Calculator()
result = calc.factorial(5)  # Returns 120
```

#### `percentage(value, total)`

Calculates percentage of a value relative to total.

**Parameters:**
- `value` (Union[int, float]): The value to calculate percentage for
- `total` (Union[int, float]): The total value

**Returns:**
- float: The percentage as a float

**Raises:**
- ZeroDivisionError: If total is zero

**Example:**
```python
calc = Calculator()
result = calc.percentage(25, 100)  # Returns 25.0
```

#### `average(numbers)`

Calculates the average of a list of numbers.

**Parameters:**
- `numbers` (list[Union[int, float]]): List of numbers

**Returns:**
- float: The average of the numbers

**Raises:**
- ValueError: If the list is empty

**Example:**
```python
calc = Calculator()
result = calc.average([1, 2, 3, 4, 5])  # Returns 3.0
```

## String Utilities

The `StringUtils` class provides common string manipulation functions.

### Constructor

```python
StringUtils()
```

Creates a new string utilities instance.

### Methods

#### `reverse(text)`

Reverses a string.

**Parameters:**
- `text` (str): The string to reverse

**Returns:**
- str: The reversed string

**Example:**
```python
utils = StringUtils()
result = utils.reverse("hello")  # Returns "olleh"
```

#### `palindrome(text)`

Checks if a string is a palindrome.

**Parameters:**
- `text` (str): The string to check

**Returns:**
- bool: True if the string is a palindrome, False otherwise

**Example:**
```python
utils = StringUtils()
result = utils.palindrome("racecar")  # Returns True
```

#### `count_vowels(text)`

Counts the number of vowels in a string.

**Parameters:**
- `text` (str): The string to count vowels in

**Returns:**
- int: The number of vowels in the string

**Example:**
```python
utils = StringUtils()
result = utils.count_vowels("hello world")  # Returns 3
```

#### `count_consonants(text)`

Counts the number of consonants in a string.

**Parameters:**
- `text` (str): The string to count consonants in

**Returns:**
- int: The number of consonants in the string

**Example:**
```python
utils = StringUtils()
result = utils.count_consonants("hello world")  # Returns 7
```

#### `capitalize_words(text)`

Capitalizes the first letter of each word in a string.

**Parameters:**
- `text` (str): The string to capitalize

**Returns:**
- str: The string with each word capitalized

**Example:**
```python
utils = StringUtils()
result = utils.capitalize_words("hello world")  # Returns "Hello World"
```

#### `remove_duplicates(text)`

Removes duplicate characters from a string.

**Parameters:**
- `text` (str): The string to remove duplicates from

**Returns:**
- str: The string with duplicate characters removed

**Example:**
```python
utils = StringUtils()
result = utils.remove_duplicates("hello")  # Returns "helo"
```

#### `word_count(text)`

Counts the number of words in a string.

**Parameters:**
- `text` (str): The string to count words in

**Returns:**
- int: The number of words in the string

**Example:**
```python
utils = StringUtils()
result = utils.word_count("hello world")  # Returns 2
```

#### `char_count(text, char)`

Counts the occurrences of a specific character in a string.

**Parameters:**
- `text` (str): The string to search in
- `char` (str): The character to count

**Returns:**
- int: The number of occurrences of the character

**Example:**
```python
utils = StringUtils()
result = utils.char_count("hello", "l")  # Returns 2
```

#### `is_anagram(text1, text2)`

Checks if two strings are anagrams.

**Parameters:**
- `text1` (str): First string
- `text2` (str): Second string

**Returns:**
- bool: True if the strings are anagrams, False otherwise

**Example:**
```python
utils = StringUtils()
result = utils.is_anagram("listen", "silent")  # Returns True
```

#### `extract_numbers(text)`

Extracts all numbers from a string.

**Parameters:**
- `text` (str): The string to extract numbers from

**Returns:**
- List[int]: List of integers found in the string

**Example:**
```python
utils = StringUtils()
result = utils.extract_numbers("hello123world456")  # Returns [123, 456]
```

#### `extract_emails(text)`

Extracts all email addresses from a string.

**Parameters:**
- `text` (str): The string to extract emails from

**Returns:**
- List[str]: List of email addresses found in the string

**Example:**
```python
utils = StringUtils()
result = utils.extract_emails("Contact us at test@example.com")  # Returns ["test@example.com"]
```

#### `truncate(text, length, suffix='...')`

Truncates a string to a specified length.

**Parameters:**
- `text` (str): The string to truncate
- `length` (int): The maximum length
- `suffix` (str): The suffix to add if truncated (default: '...')

**Returns:**
- str: The truncated string

**Example:**
```python
utils = StringUtils()
result = utils.truncate("hello world", 5)  # Returns "he..."
```

#### `slugify(text)`

Converts a string to a URL-friendly slug.

**Parameters:**
- `text` (str): The string to convert

**Returns:**
- str: The slugified string

**Example:**
```python
utils = StringUtils()
result = utils.slugify("Hello World!")  # Returns "hello-world"
```

## Configuration Management

The `Config` class provides simple configuration management.

### Constructor

```python
Config(config_file=None)
```

Creates a new configuration manager.

**Parameters:**
- `config_file` (Optional[str]): Path to the configuration file (optional)

### Methods

#### `load(config_file=None)`

Loads configuration from a file.

**Parameters:**
- `config_file` (Optional[str]): Path to the configuration file (optional)

**Raises:**
- FileNotFoundError: If the file doesn't exist
- ValueError: If no configuration file is specified

#### `save(config_file=None)`

Saves configuration to a file.

**Parameters:**
- `config_file` (Optional[str]): Path to the configuration file (optional)

**Raises:**
- ValueError: If no configuration file is specified

#### `get(key, default=None)`

Gets a configuration value using dot notation.

**Parameters:**
- `key` (str): Configuration key (supports dot notation like 'database.host')
- `default` (Any): Default value if key is not found

**Returns:**
- Any: The configuration value or default

**Example:**
```python
config = Config()
value = config.get("database.host", "localhost")
```

#### `set(key, value)`

Sets a configuration value using dot notation.

**Parameters:**
- `key` (str): Configuration key (supports dot notation like 'database.host')
- `value` (Any): The value to set

**Example:**
```python
config = Config()
config.set("database.port", 5432)
```

#### `has(key)`

Checks if a configuration key exists.

**Parameters:**
- `key` (str): Configuration key (supports dot notation)

**Returns:**
- bool: True if the key exists, False otherwise

#### `delete(key)`

Deletes a configuration key.

**Parameters:**
- `key` (str): Configuration key (supports dot notation)

**Returns:**
- bool: True if the key was deleted, False if it didn't exist

#### `clear()`

Clears all configuration values.

#### `to_dict()`

Gets the configuration as a dictionary.

**Returns:**
- Dict[str, Any]: A copy of the configuration dictionary

#### `from_dict(config_dict)`

Loads configuration from a dictionary.

**Parameters:**
- `config_dict` (Dict[str, Any]): Dictionary containing configuration values

#### `merge(config_dict)`

Merges configuration from a dictionary.

**Parameters:**
- `config_dict` (Dict[str, Any]): Dictionary containing configuration values to merge

#### `get_section(section)`

Gets a configuration section.

**Parameters:**
- `section` (str): Section name

**Returns:**
- Dict[str, Any]: Dictionary containing the section configuration

#### `set_section(section, config_dict)`

Sets a configuration section.

**Parameters:**
- `section` (str): Section name
- `config_dict` (Dict[str, Any]): Dictionary containing section configuration

#### `list_sections()`

Lists all configuration sections.

**Returns:**
- list[str]: List of section names

#### `validate(schema)`

Validates configuration against a schema.

**Parameters:**
- `schema` (Dict[str, Any]): Schema dictionary defining required keys and types

**Returns:**
- bool: True if configuration is valid, False otherwise

## Logging

The `Logger` class provides structured logging capabilities.

### Constructor

```python
Logger(name, level="INFO", format_type="json")
```

Creates a new logger instance.

**Parameters:**
- `name` (str): Logger name
- `level` (str): Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
- `format_type` (str): Output format (json, text, simple)

### Methods

#### `debug(message, **kwargs)`

Logs a debug message.

**Parameters:**
- `message` (str): The message to log
- `**kwargs`: Additional context data

#### `info(message, **kwargs)`

Logs an info message.

**Parameters:**
- `message` (str): The message to log
- `**kwargs`: Additional context data

#### `warning(message, **kwargs)`

Logs a warning message.

**Parameters:**
- `message` (str): The message to log
- `**kwargs`: Additional context data

#### `error(message, **kwargs)`

Logs an error message.

**Parameters:**
- `message` (str): The message to log
- `**kwargs`: Additional context data

#### `critical(message, **kwargs)`

Logs a critical message.

**Parameters:**
- `message` (str): The message to log
- `**kwargs`: Additional context data

#### `exception(message, **kwargs)`

Logs an exception message.

**Parameters:**
- `message` (str): The message to log
- `**kwargs`: Additional context data

#### `log(level, message, **kwargs)`

Logs a message at the specified level.

**Parameters:**
- `level` (str): Logging level
- `message` (str): The message to log
- `**kwargs`: Additional context data

#### `bind(**kwargs)`

Binds context data to the logger.

**Parameters:**
- `**kwargs`: Context data to bind

**Returns:**
- Logger: A new logger instance with bound context

#### `unbind(*keys)`

Unbinds context data from the logger.

**Parameters:**
- `*keys`: Keys to unbind

**Returns:**
- Logger: A new logger instance with unbound context

#### `set_level(level)`

Sets the logging level.

**Parameters:**
- `level` (str): Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)

#### `add_context(**kwargs)`

Adds context data to all subsequent log messages.

**Parameters:**
- `**kwargs`: Context data to add

#### `remove_context(*keys)`

Removes context data from all subsequent log messages.

**Parameters:**
- `*keys`: Keys to remove

#### `log_function_call(func_name, args, kwargs, result=None)`

Logs a function call with its arguments and result.

**Parameters:**
- `func_name` (str): Name of the function
- `args` (tuple): Function arguments
- `kwargs` (dict): Function keyword arguments
- `result` (Any): Function result (optional)

#### `log_performance(operation, duration, **kwargs)`

Logs performance metrics.

**Parameters:**
- `operation` (str): Name of the operation
- `duration` (float): Duration in seconds
- `**kwargs`: Additional performance data

#### `log_user_action(user_id, action, **kwargs)`

Logs user actions.

**Parameters:**
- `user_id` (str): User identifier
- `action` (str): Action performed
- `**kwargs`: Additional action data

#### `log_system_event(event, **kwargs)`

Logs system events.

**Parameters:**
- `event` (str): Event name
- `**kwargs`: Additional event data

#### `log_error_with_context(error, context)`

Logs an error with additional context.

**Parameters:**
- `error` (Exception): The exception that occurred
- `context` (Dict[str, Any]): Additional context data

#### `log_data_access(operation, resource, **kwargs)`

Logs data access operations.

**Parameters:**
- `operation` (str): Type of operation (read, write, delete, etc.)
- `resource` (str): Resource being accessed
- `**kwargs`: Additional access data

#### `log_security_event(event_type, **kwargs)`

Logs security-related events.

**Parameters:**
- `event_type` (str): Type of security event
- `**kwargs`: Additional security data

#### `get_logger()`

Gets the underlying structlog logger.

**Returns:**
- structlog.BoundLogger: The structlog logger instance

## Command Line Interface

The library provides a comprehensive command-line interface with the following commands:

### Calculator Commands

```bash
# Add two numbers
sample-lib calc add 5 3

# Subtract two numbers
sample-lib calc subtract 10 4

# Multiply two numbers
sample-lib calc multiply 3 7

# Divide two numbers
sample-lib calc divide 15 3

# Raise a number to a power
sample-lib calc power 2 8

# Calculate square root
sample-lib calc sqrt 16
```

### String Utility Commands

```bash
# Reverse a string
sample-lib string reverse "hello"

# Check if string is palindrome
sample-lib string palindrome "racecar"

# Count vowels
sample-lib string count-vowels "hello world"

# Count consonants
sample-lib string count-consonants "hello world"

# Capitalize words
sample-lib string capitalize "hello world"

# Convert to slug
sample-lib string slugify "Hello World!"
```

### Configuration Commands

```bash
# Set a configuration value
sample-lib config set-value config.json database.host localhost

# Get a configuration value
sample-lib config get-value config.json database.host

# List configuration sections
sample-lib config list-sections config.json
```

### Logging Commands

```bash
# Demonstrate logging
sample-lib demo-logger --name test --level INFO --format text
```

### Demo Command

```bash
# Run a complete demonstration
sample-lib demo
```

### Help

```bash
# Get help for all commands
sample-lib --help

# Get help for specific command group
sample-lib calc --help
sample-lib string --help
sample-lib config --help
```

## Examples

### Basic Usage

```python
from sample_python_lib import Calculator, StringUtils, Config, Logger

# Calculator
calc = Calculator()
result = calc.add(5, 3)
print(f"5 + 3 = {result}")

# String utilities
utils = StringUtils()
reversed_text = utils.reverse("hello")
print(f"Reversed: {reversed_text}")

# Configuration
config = Config()
config.set("database.host", "localhost")
config.set("database.port", 5432)
host = config.get("database.host")
print(f"Database host: {host}")

# Logging
logger = Logger("my_app")
logger.info("Application started", version="1.0.0")
```

### Advanced Usage

```python
from sample_python_lib import Calculator, StringUtils, Config, Logger

# Calculator with error handling
calc = Calculator()
try:
    result = calc.divide(10, 0)
except ZeroDivisionError:
    print("Cannot divide by zero")

# String utilities with complex operations
utils = StringUtils()
text = "A man a plan a canal Panama"
is_palindrome = utils.palindrome(text)
print(f"Is palindrome: {is_palindrome}")

# Configuration with validation
config = Config()
config.set("app.name", "myapp")
config.set("app.version", "1.0.0")

schema = {
    "app": {
        "type": dict,
        "schema": {
            "name": {"type": str, "required": True},
            "version": {"type": str, "required": True}
        }
    }
}

is_valid = config.validate(schema)
print(f"Configuration valid: {is_valid}")

# Logging with context
logger = Logger("my_app", level="DEBUG", format_type="json")
logger.add_context(user_id="123", session_id="abc")
logger.info("User action", action="login", ip="192.168.1.1")
```

This library demonstrates best practices for Python library development and provides a complete example of how to use the Jenkins CI/CD pipeline for automated testing, code quality checks, and deployment. 