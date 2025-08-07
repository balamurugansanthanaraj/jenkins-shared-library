# Jenkins Shared Library for Python Library CI Pipeline

A comprehensive Jenkins Shared Library that provides a complete CI/CD pipeline for Python library projects. This shared library includes automated version bumping, code quality checks, security scanning, and artifact publishing.

## Features

### ⚡ Ultra-Fast Package Management with uv
- **Lightning-fast installation**: uv is 10-100x faster than pip
- **Intelligent dependency resolution**: Advanced resolver with better conflict handling
- **Built-in virtual environments**: Native venv creation and management
- **Compatible with existing workflows**: Works with requirements.txt and setup.py
- **Rust-based performance**: Written in Rust for maximum speed and reliability

### 🔄 Automated Version Management
- **Semantic Versioning**: Automatically bumps version based on PR title prefixes
  - `fix-*` → Patch version increment (1.0.0 → 1.0.1)
  - `feature-*` → Minor version increment (1.0.0 → 1.1.0)
  - `breaking-*` → Major version increment (1.0.0 → 2.0.0)
- **Version File Updates**: Updates multiple version files (setup.py, version.txt, __init__.py, pyproject.toml)
- **Changelog Generation**: Automatically generates changelog from Git commits

### 🛠️ Development Tools Integration
- **Ruff Linting**: Fast Python linter with code formatting checks
- **Unit Testing**: pytest with coverage reporting
- **Mutation Testing**: Mutmut for test quality validation
- **Python Environment Management**: Virtual environment setup with pyenv/conda support
- **uv Package Manager**: Ultra-fast Python package installer and resolver

### 🔍 Code Quality & Security
- **SonarQube Integration**: Code quality analysis with branch-specific reporting
- **Quality Gate Enforcement**: Pipeline fails if quality gate doesn't pass
- **Nexus IQ Security Scanning**: Component security and policy compliance
- **Conditional Stage Handling**: Different scan stages for development vs release

### 📦 Artifact Management
- **JFrog Artifactory Integration**: Package upload with repository selection
- **Branch-based Publishing**: 
  - `master` branch → Release repository
  - Other branches → Snapshot/Development repository
- **Metadata Generation**: Comprehensive package metadata with build information

### 🔒 Safe Git Operations
- **Master Branch Only**: Git operations (tagging, pushing) only from master branch
- **Automated Tagging**: Creates Git tags with build information
- **Release Notes**: Generates comprehensive release notes

## Installation

### 1. Jenkins Shared Library Setup

1. Create a new Git repository for the shared library
2. Clone this repository structure into your shared library repo
3. Configure Jenkins to use the shared library:
   - Go to **Manage Jenkins** → **Configure System**
   - Add the shared library under **Global Pipeline Libraries**
   - Set the name (e.g., `python-library-shared-lib`)
   - Configure the source (Git repository)

### 2. Required Jenkins Plugins

Ensure the following Jenkins plugins are installed:
- Pipeline
- Git
- HTML Publisher
- Credentials Binding
- Pipeline Utility Steps

### 3. Environment Variables

Set up the following environment variables in Jenkins:

```bash
# SonarQube
SONAR_TOKEN=your_sonarqube_token

# Nexus IQ
NEXUS_IQ_TOKEN=your_nexus_iq_token

# Artifactory
ARTIFACTORY_USER=your_artifactory_username
ARTIFACTORY_PASSWORD=your_artifactory_password

# Git (optional, defaults provided)
GIT_USER=jenkins
GIT_EMAIL=jenkins@company.com

# uv Package Manager (optional, will auto-install if not available)
UV_CACHE_DIR=/tmp/uv-cache  # Optional: Custom cache directory
```

### 4. Configuration File

The shared library uses a centralized configuration file (`resources/common-config.yml`) that contains:

- **Infrastructure URLs**: SonarQube, Nexus IQ, and Artifactory server URLs (always production)
- **Default configurations**: Common settings for Python version, tools, and pipeline behavior

You can customize this file to match your infrastructure setup:

```yaml
infrastructure:
  sonarqube:
    host_url: "http://your-sonarqube.company.com:9000"
  nexus_iq:
    host_url: "http://your-nexus-iq.company.com:8070"
  artifactory:
    host_url: "https://your-artifactory.company.com"
```

### 5. Multi-Branch Pipeline Setup

This shared library is designed for **Multi-Branch Pipeline** jobs in Jenkins:

1. **Create a Multi-Branch Pipeline job** in Jenkins
2. **Configure the branch source** (GitHub, GitLab, Bitbucket, etc.)
3. **Set the Jenkinsfile path** to point to your project's Jenkinsfile
4. **Configure credentials** for your Git repository if needed

The pipeline will automatically:
- Detect and build all branches/PRs
- Use the appropriate branch name and repository URL
- Handle checkout automatically for each branch

## Usage

### Basic Usage

Create a `Jenkinsfile` in your Python library project:

```groovy
#!/usr/bin/env groovy

@Library('python-library-shared-lib') _

// Minimal configuration - everything else is auto-detected
def pipelineConfig = [:]

pythonCIPipeline(pipelineConfig)
```

**Note**: See the `USAGE.md` file for detailed usage instructions and examples.

**Note**: 
- Infrastructure URLs (SonarQube, Nexus IQ, Artifactory) are automatically loaded from the shared library's configuration file
- Project keys and IDs (`sonarProjectKey`, `nexusIqApplicationId`, `artifactoryRepo`) are automatically set to your repository name
- You can override any of these values by providing them in the config if needed

### Auto-Detection Logic

The pipeline automatically detects your repository name and uses it for project configurations:

1. **Repository Name Detection**: 
   - Primary: Extracts from `GIT_URL` environment variable (e.g., `https://github.com/company/my-python-lib.git` → `my-python-lib`)
   - Fallback: Uses workspace name if `GIT_URL` is not available
   - Final fallback: Uses `python-library` as default

2. **Automatic Configuration**:
   - `sonarProjectKey` = repository name
   - `nexusIqApplicationId` = repository name  
   - `artifactoryRepo` = repository name

3. **Override Capability**: You can still override any of these values in your pipeline configuration if needed.

### Advanced Configuration

```groovy
#!/usr/bin/env groovy

@Library('python-library-shared-lib') _

def pipelineConfig = [

    
    // Agent configuration
    agentLabel: 'python-agent',  // Can be customized: 'docker-agent', 'ubuntu-agent', etc.
    
    // Python configuration
    pythonVersion: '3.11',
    requirementsFile: 'requirements.txt',
    setupFile: 'setup.py',
    versionFile: 'version.txt',
    
    // Tool configurations
    ruffConfig: '.ruff.toml',
    
    // Override project-specific configuration if needed
    // sonarProjectKey: 'custom-project-key',      // Auto-detected from repo name
    // nexusIqApplicationId: 'custom-app-id',      // Auto-detected from repo name
    // artifactoryRepo: 'custom-repo',             // Auto-detected from repo name
    
    // Override infrastructure URLs if needed
    sonarHostUrl: 'http://custom-sonarqube.company.com:9000',
    nexusIqUrl: 'http://custom-nexus-iq.company.com:8070',
    artifactoryUrl: 'https://custom-artifactory.company.com',
    
    // Pipeline behavior
    enableMutationTests: true,
    enableSonarQube: true,
    enableNexusIQ: true,
    enableArtifactory: true,
    autoVersionBump: true
]

pythonCIPipeline(pipelineConfig)
```

## Pipeline Stages

### 1. Checkout
- Clones the source code from the repository
- Supports submodules and clean checkout

### 2. Setup Environment
- Sets up Python environment using pyenv or conda
- Creates virtual environment using uv
- Installs uv package manager if not available

### 3. Version Bump (Conditional)
- Only runs when PR title is available
- Determines version bump type from PR title prefix
- Updates version files automatically

### 4. Install Dependencies
- Installs requirements from `requirements.txt` using uv
- Installs development dependencies from `requirements-dev.txt` using uv
- Installs test dependencies (pytest, ruff, mutmut) using uv
- Installs package in editable mode using uv

### 5. Build Package
- Cleans previous builds
- Builds source distribution and wheel
- Creates distributable packages

### 6. Code Quality - Ruff
- Runs Ruff linting with configuration file
- Checks code formatting
- Fails pipeline if linting issues found

### 7. Unit Tests
- Runs pytest with coverage
- Generates coverage reports
- Publishes HTML coverage report

### 8. Mutation Tests (Conditional)
- Runs Mutmut mutation testing
- Generates mutation test report
- Publishes HTML mutation report

### 9. SonarQube Analysis (Conditional)
- Runs SonarQube scanner
- Passes branch name and version as parameters
- Waits for analysis completion
- Enforces quality gate

### 10. Nexus IQ Scan (Conditional)
- Runs security and policy compliance scan
- Uploads to 'release' stage for master branch
- Uploads to 'development' stage for other branches
- Checks policy compliance

### 11. Upload to Artifactory (Conditional)
- Uploads packages to appropriate repository
- Master branch → Release repository
- Other branches → Snapshot repository
- Generates package metadata

### 12. Git Operations (Master Branch Only)
- Updates version files
- Generates changelog
- Commits changes
- Creates Git tag
- Pushes changes and tags

## Configuration Options

For a comprehensive reference of all available configuration options, see [CONFIGURATION.md](CONFIGURATION.md).

The pipeline supports extensive configuration through a configuration map with sensible defaults. You can start with minimal configuration:

```groovy
def pipelineConfig = [
    environment: 'production'  // Minimal configuration
]

pythonCIPipeline(pipelineConfig)
```

Key configuration categories include:
- **Agent Configuration**: Jenkins agent labels
- **Python Configuration**: Python version, file paths
- **Tool Configurations**: Ruff, uv, test commands
- **Environment Configuration**: Development, staging, production
- **Integration Configuration**: SonarQube, Nexus IQ, Artifactory
- **Pipeline Behavior**: Feature toggles and automation settings

## Testing

### Running Unit Tests

The shared library includes comprehensive unit tests using the Jenkins Pipeline Unit testing framework:

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests PythonStepsTest

# Run with coverage
./gradlew test jacocoTestReport
```

### Test Coverage

The unit tests cover:
- Version bumping logic
- Pipeline step execution
- Error handling
- Configuration validation
- Integration point testing

## Best Practices

### 1. Version Management
- Use consistent PR title prefixes for automatic version bumping
- Review version changes before merging to master
- Keep version files in sync

### 2. Security
- Store sensitive tokens as Jenkins credentials
- Use environment variables for configuration
- Regularly rotate authentication tokens

### 3. Quality Gates
- Configure appropriate SonarQube quality gates
- Set up Nexus IQ policies for your organization
- Monitor and address quality issues promptly

### 4. Branch Strategy
- Use feature branches for development
- Only perform releases from master branch
- Maintain clean Git history

### 5. Monitoring
- Monitor pipeline execution times
- Set up alerts for pipeline failures
- Track quality metrics over time

## Troubleshooting

### Common Issues

1. **Version Bump Not Working**
   - Ensure PR title has correct prefix (fix-, feature-, breaking-)
   - Check that `autoVersionBump` is enabled
   - Verify version files exist and are writable

2. **SonarQube Quality Gate Failing**
   - Review SonarQube dashboard for specific issues
   - Check quality gate configuration
   - Address code quality issues before merging

3. **Nexus IQ Scan Failures**
   - Review security vulnerabilities in Nexus IQ
   - Check policy compliance requirements
   - Update dependencies if needed

4. **Artifactory Upload Issues**
   - Verify credentials are correct
   - Check repository permissions
   - Ensure package files are generated

### Debug Mode

Enable debug logging by setting the `DEBUG` environment variable:

```bash
export DEBUG=true
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section above 