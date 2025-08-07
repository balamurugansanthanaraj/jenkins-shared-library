# Jenkins Shared Library Structure

This document provides an overview of the complete Jenkins Shared Library structure for Python library CI pipeline.

## Directory Structure

```
python-library-shared-lib/
├── vars/
│   └── pythonCIPipeline.groovy    # Main pipeline entry point
├── src/
│   └── com/
│       └── company/
│           └── jenkins/
│               ├── PythonSteps.groovy      # Core Python pipeline steps
│               ├── PipelineSteps.groovy    # Language-agnostic pipeline operations
│               ├── SonarQubeIntegration.groovy    # SonarQube code quality integration
│               ├── NexusIQIntegration.groovy      # Nexus IQ security scanning
│               ├── ArtifactoryIntegration.groovy  # JFrog Artifactory upload
│               ├── GitOperations.groovy           # Safe Git operations
│               └── ConfigLoader.groovy            # YAML configuration loader
├── resources/
│   └── common-config.yml           # Common infrastructure configuration
├── test/
│   └── PythonStepsTest.groovy   # Unit tests for pipeline steps

├── build.gradle                         # Gradle build configuration
├── .ruff.toml                          # Ruff linting configuration
├── README.md                           # Comprehensive documentation
├── CONFIGURATION.md                    # Configuration options reference
├── USAGE.md                           # Usage guide
└── STRUCTURE.md                        # This file
```

## File Descriptions

### Core Pipeline Files

#### `vars/pythonCIPipeline.groovy`
- **Purpose**: Main pipeline entry point that orchestrates all CI steps
- **Features**: 
  - Configurable pipeline with sensible defaults
  - Modular stage execution
  - Conditional stage execution based on configuration
  - Comprehensive error handling and notifications

#### `src/com/company/jenkins/PythonSteps.groovy`
- **Purpose**: Core Python development pipeline steps
- **Features**:
  - Python environment setup with pyenv/conda
  - Automated version bumping based on PR titles
  - Dependency installation
  - Package building
  - Ruff linting
  - Unit testing with coverage
  - Mutation testing with Mutmut

#### `src/com/company/jenkins/PipelineSteps.groovy`
- **Purpose**: Language-agnostic pipeline operations
- **Features**:
  - Source code checkout and verification
  - Pipeline cleanup and artifact management
  - Success and failure notifications
  - Configuration validation
  - Environment setup
  - Extensible notification system (Slack, email, etc.)

#### `src/com/company/jenkins/SonarQubeIntegration.groovy`
- **Purpose**: SonarQube code quality analysis integration
- **Features**:
  - Branch-specific analysis
  - Version tagging
  - Quality gate enforcement
  - Analysis completion waiting
  - Detailed reporting

#### `src/com/company/jenkins/NexusIQIntegration.groovy`
- **Purpose**: Nexus IQ security and policy compliance scanning
- **Features**:
  - Conditional stage handling (release vs development)
  - Security vulnerability scanning
  - Policy compliance checking
  - Result upload to appropriate stages
  - Detailed reporting

#### `src/com/company/jenkins/ArtifactoryIntegration.groovy`
- **Purpose**: JFrog Artifactory package upload
- **Features**:
  - Branch-based repository selection
  - Package metadata generation
  - Upload verification
  - Version management
  - Cleanup of old versions

#### `src/com/company/jenkins/GitOperations.groovy`
- **Purpose**: Safe Git operations for releases
- **Features**:
  - Master branch-only operations
  - Version file updates
  - Changelog generation
  - Git tagging
  - Release notes creation

#### `src/com/company/jenkins/ConfigLoader.groovy`
- **Purpose**: YAML configuration loader for shared infrastructure settings
- **Features**:
  - Loads common configuration from YAML files
  - Environment-specific configuration overrides
  - Infrastructure URL management
  - Default configuration provision
  - Configuration caching for performance

### Testing and Build Files

#### `test/PythonStepsTest.groovy`
- **Purpose**: Unit tests for pipeline steps
- **Features**:
  - Comprehensive test coverage
  - Mock Jenkins pipeline environment
  - Error condition testing
  - Configuration validation

#### `build.gradle`
- **Purpose**: Gradle build configuration
- **Features**:
  - Jenkins Pipeline Unit testing framework
  - Code coverage with JaCoCo
  - Maven publishing
  - Custom build tasks
  - Structure validation

### Configuration Files

#### `resources/common-config.yml`
- **Purpose**: Common infrastructure and default configuration
- **Features**:
  - Infrastructure URLs (SonarQube, Nexus IQ, Artifactory) - always production
  - Default pipeline configurations
  - Project-specific defaults
  - Centralized configuration management

#### `.ruff.toml`
- **Purpose**: Ruff linting and formatting configuration
- **Features**:
  - Python 3.11 target
  - 88-character line length
  - Import sorting
  - Multiple linter configurations
  - Per-file rule overrides



### Documentation

#### `README.md`
- **Purpose**: Comprehensive documentation
- **Features**:
  - Installation instructions
  - Usage examples
  - Overview of configuration options
  - Troubleshooting guide
  - Best practices

#### `CONFIGURATION.md`
- **Purpose**: Detailed configuration options reference
- **Features**:
  - Complete list of all configuration options
  - Configuration examples for different scenarios
  - Auto-detection features documentation
  - Environment variables reference
  - Configuration troubleshooting guide

## Key Features Implemented

### ✅ Version Management
- [x] Semantic versioning based on PR title prefixes
- [x] Automatic version file updates
- [x] Changelog generation
- [x] Git tagging

### ✅ Code Quality
- [x] Ruff linting and formatting
- [x] SonarQube integration with quality gates
- [x] Unit testing with coverage
- [x] Mutation testing

### ✅ Security
- [x] Nexus IQ security scanning
- [x] Policy compliance checking
- [x] Conditional stage handling

### ✅ Artifact Management
- [x] JFrog Artifactory integration
- [x] Branch-based repository selection
- [x] Package metadata generation

### ✅ Git Operations
- [x] Safe master branch operations
- [x] Automated version updates
- [x] Release notes generation

### ✅ Testing
- [x] Comprehensive unit tests
- [x] Jenkins Pipeline Unit framework
- [x] Code coverage reporting

## Usage Example

```groovy
@Library('python-library-shared-lib') _

def pipelineConfig = [
    environment: 'production'  // Environment configuration
    
    // Note: sonarProjectKey, nexusIqApplicationId, and artifactoryRepo are automatically
    // set to the repository name (e.g., 'my-python-lib')
]

pythonCIPipeline(pipelineConfig)
```

## Pipeline Stages

1. **Checkout** - Source code checkout
2. **Setup Environment** - Python environment setup
3. **Version Bump** - Automated version bumping (conditional)
4. **Install Dependencies** - Python package installation
5. **Build Package** - Package building
6. **Code Quality - Ruff** - Linting and formatting
7. **Unit Tests** - Testing with coverage
8. **Mutation Tests** - Test quality validation (conditional)
9. **SonarQube Analysis** - Code quality analysis (conditional)
10. **Nexus IQ Scan** - Security scanning (conditional)
11. **Upload to Artifactory** - Package publishing (conditional)
12. **Git Operations** - Release operations (master only)

## Configuration Options

The pipeline supports extensive configuration through a configuration map, including:
- Repository settings
- Python environment settings
- Tool configurations
- Integration endpoints
- Pipeline behavior flags

## Best Practices Implemented

- **Modularity**: Each integration is in its own class
- **Error Handling**: Comprehensive error handling and reporting
- **Security**: Credential management and secure operations
- **Testing**: Full unit test coverage
- **Documentation**: Comprehensive documentation and examples
- **Configuration**: Flexible configuration with sensible defaults

This shared library provides a complete, production-ready CI/CD solution for Python library projects with enterprise-grade features for code quality, security, and artifact management. 