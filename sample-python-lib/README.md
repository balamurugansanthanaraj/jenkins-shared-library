# Sample Python Library

A sample Python library that demonstrates how to use the Jenkins CI/CD pipeline for Python library projects.

## Features

- **Simple Calculator**: Basic mathematical operations
- **String Utilities**: Common string manipulation functions
- **Configuration Management**: Simple configuration handling
- **Logging**: Structured logging with different levels

## Installation

```bash
pip install sample-python-lib
```

## Usage

### Calculator Functions

```python
from sample_python_lib.calculator import Calculator

calc = Calculator()

# Basic operations
result = calc.add(5, 3)  # 8
result = calc.subtract(10, 4)  # 6
result = calc.multiply(3, 7)  # 21
result = calc.divide(15, 3)  # 5.0

# Advanced operations
result = calc.power(2, 8)  # 256
result = calc.sqrt(16)  # 4.0
```

### String Utilities

```python
from sample_python_lib.string_utils import StringUtils

utils = StringUtils()

# String manipulation
result = utils.reverse("hello")  # "olleh"
result = utils.palindrome("racecar")  # True
result = utils.count_vowels("hello world")  # 3
result = utils.capitalize_words("hello world")  # "Hello World"
```

### Configuration Management

```python
from sample_python_lib.config import Config

# Load configuration
config = Config("config.json")
value = config.get("database.host", "localhost")

# Set configuration
config.set("database.port", 5432)
config.save()
```

### Logging

```python
from sample_python_lib.logger import Logger

logger = Logger("my_app")

logger.info("Application started")
logger.warning("Configuration file not found")
logger.error("Database connection failed")
logger.debug("Processing user request")
```

## Development

### Setup Development Environment

```bash
# Clone the repository
git clone https://github.com/company/sample-python-lib.git
cd sample-python-lib

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install development dependencies
pip install -r requirements-dev.txt

# Install package in editable mode
pip install -e .
```

### Running Tests

```bash
# Run all tests
pytest

# Run with coverage
pytest --cov=sample_python_lib

# Run specific test file
pytest tests/test_calculator.py
```

### Code Quality

```bash
# Run linting
ruff check .

# Format code
ruff format .

# Run type checking
mypy sample_python_lib/
```

## Project Structure

```
sample-python-lib/
├── sample_python_lib/
│   ├── __init__.py
│   ├── calculator.py
│   ├── string_utils.py
│   ├── config.py
│   └── logger.py
├── tests/
│   ├── __init__.py
│   ├── test_calculator.py
│   ├── test_string_utils.py
│   ├── test_config.py
│   └── test_logger.py
├── docs/
│   └── api.md
├── requirements.txt
├── requirements-dev.txt
├── setup.py
├── pyproject.toml
├── .ruff.toml
├── Jenkinsfile
└── README.md
```

## CI/CD Pipeline

This project uses the Jenkins shared library for automated CI/CD. The pipeline includes:

- **Automated Testing**: Unit tests with coverage reporting
- **Code Quality**: Ruff linting and formatting
- **Security Scanning**: Nexus IQ security analysis
- **Quality Gates**: SonarQube code quality analysis
- **Artifact Publishing**: Automatic package publishing to Artifactory
- **Version Management**: Automated version bumping based on PR titles

### Pipeline Configuration

The project uses minimal configuration - most settings are auto-detected:

```groovy
@Library('python-library-shared-lib') _

def pipelineConfig = [
    environment: 'production'
]

pythonCIPipeline(pipelineConfig)
```

The pipeline automatically:
- Detects the repository name (`sample-python-lib`)
- Sets project keys and IDs to match the repository name
- Loads infrastructure URLs from the shared library configuration
- Runs all quality checks and tests

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass (`pytest`)
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Create a Pull Request

### PR Title Conventions

Use these prefixes in your PR titles for automatic version bumping:

- `fix-*` → Patch version increment (1.0.0 → 1.0.1)
- `feature-*` → Minor version increment (1.0.0 → 1.1.0)
- `breaking-*` → Major version increment (1.0.0 → 2.0.0)

Examples:
- `fix-calculator-division-by-zero`
- `feature-add-logging-configuration`
- `breaking-change-rename-calculator-class`

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section in the main documentation 