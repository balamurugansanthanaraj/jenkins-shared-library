# Configuration Options

This document provides a comprehensive reference for all configuration options available in the Jenkins Shared Library for Python Library CI Pipeline.

## Overview

The pipeline supports extensive configuration through a configuration map. All options have sensible defaults, so you can start with minimal configuration and customize as needed.

## Basic Usage

```groovy
def pipelineConfig = [
    environment: 'production'  // Minimal configuration
]

pythonCIPipeline(pipelineConfig)
```

## Configuration Categories

### Agent Configuration
- `agentLabel`: Jenkins agent label to run the pipeline on (default: 'python-agent')

### Repository Configuration
- `branch`: Branch name (auto-detected from environment in multi-branch pipelines)
- `prTitle`: Pull request title for version bumping

### Python Configuration
- `pythonVersion`: Python version to use (default: '3.11')
- `requirementsFile`: Requirements file path (default: 'requirements.txt')
- `setupFile`: Setup.py file path (default: 'setup.py')
- `versionFile`: Version file path (default: 'version.txt')

### Tool Configurations
- `ruffConfig`: Ruff configuration file (default: '.ruff.toml')
- `uvConfig`: uv configuration file (default: 'pyproject.toml')
- `testCommand`: Test command (default: 'python -m pytest')
- `coverageCommand`: Coverage command (default: 'python -m pytest --cov=. --cov-report=xml')
- `lintCommand`: Lint command (default: 'ruff .')
- `formatCommand`: Format command (default: 'black .')

### SonarQube Configuration
- `sonarHostUrl`: SonarQube server URL (auto-loaded from config, can be overridden)
- `sonarProjectKey`: SonarQube project key (auto-detected from repository name, can be overridden)
- `sonarToken`: SonarQube authentication token

### Nexus IQ Configuration
- `nexusIqUrl`: Nexus IQ server URL (auto-loaded from config, can be overridden)
- `nexusIqToken`: Nexus IQ authentication token
- `nexusIqApplicationId`: Nexus IQ application ID (auto-detected from repository name, can be overridden)

### Artifactory Configuration
- `artifactoryUrl`: Artifactory server URL (auto-loaded from config, can be overridden)
- `artifactoryRepo`: Artifactory repository name (auto-detected from repository name, can be overridden)
- `artifactoryUser`: Artifactory username
- `artifactoryPassword`: Artifactory password

### Git Configuration
- `gitUser`: Git user name for commits
- `gitEmail`: Git email for commits

### Pipeline Behavior
- `enableMutationTests`: Enable mutation testing (default: true)
- `enableSonarQube`: Enable SonarQube analysis (default: true)
- `enableNexusIQ`: Enable Nexus IQ scanning (default: true)
- `enableArtifactory`: Enable Artifactory upload (default: true)
- `autoVersionBump`: Enable automatic version bumping (default: true)

## Configuration Examples

### Minimal Configuration
```groovy
def pipelineConfig = [
    environment: 'production'
]
```

### Development Environment
```groovy
def pipelineConfig = [
    environment: 'development',
    pythonVersion: '3.11',
    enableMutationTests: false,  // Skip mutation tests in development
    enableSonarQube: false       // Skip SonarQube in development
]
```

### Custom Python Setup
```groovy
def pipelineConfig = [
    environment: 'production',
    pythonVersion: '3.12',
    requirementsFile: 'requirements-prod.txt',
    setupFile: 'setup.py',
    versionFile: 'VERSION'
]
```

### Custom Tool Configuration
```groovy
def pipelineConfig = [
    environment: 'production',
    ruffConfig: '.ruff.toml',
    testCommand: 'python -m pytest -v',
    coverageCommand: 'python -m pytest --cov=. --cov-report=xml --cov-report=html'
]
```

### Override Infrastructure URLs
```groovy
def pipelineConfig = [
    environment: 'production',
    sonarHostUrl: 'http://custom-sonarqube.company.com:9000',
    nexusIqUrl: 'http://custom-nexus-iq.company.com:8070',
    artifactoryUrl: 'https://custom-artifactory.company.com'
]
```

### Disable Specific Features
```groovy
def pipelineConfig = [
    environment: 'production',
    enableMutationTests: false,    // Disable mutation testing
    enableSonarQube: false,        // Disable SonarQube analysis
    enableNexusIQ: false,          // Disable Nexus IQ scanning
    enableArtifactory: false,      // Disable Artifactory upload
    autoVersionBump: false         // Disable automatic version bumping
]
```

### Complete Configuration Example
```groovy
def pipelineConfig = [
    // Environment configuration
    environment: 'production',
    
    // Agent configuration
    agentLabel: 'python-agent',
    
    // Python configuration
    pythonVersion: '3.11',
    requirementsFile: 'requirements.txt',
    setupFile: 'setup.py',
    versionFile: 'version.txt',
    
    // Tool configurations
    ruffConfig: '.ruff.toml',
    uvConfig: 'pyproject.toml',
    testCommand: 'python -m pytest',
    coverageCommand: 'python -m pytest --cov=. --cov-report=xml',
    
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
```

## Auto-Detection Features

The pipeline automatically detects and sets several configuration values:

### Repository Name Detection
- **Primary**: Extracts from `GIT_URL` environment variable (e.g., `https://github.com/company/my-python-lib.git` → `my-python-lib`)
- **Fallback**: Uses workspace name if `GIT_URL` is not available
- **Final fallback**: Uses `python-library` as default

### Automatic Configuration
- `sonarProjectKey` = repository name
- `nexusIqApplicationId` = repository name  
- `artifactoryRepo` = repository name

### Infrastructure URLs
- **Auto-loaded**: From `resources/common-config.yml` (always production URLs)
- **Override capability**: You can still override any of these values in your pipeline configuration

## Environment Variables

The following environment variables are automatically used if available:

- `GIT_URL`: Repository URL (auto-detected in multi-branch pipelines)
- `BRANCH_NAME` or `GIT_BRANCH`: Current branch name
- `CHANGE_TITLE`: Pull request title for version bumping
- `SONAR_TOKEN`: SonarQube authentication token
- `NEXUS_IQ_TOKEN`: Nexus IQ authentication token
- `ARTIFACTORY_USER`: Artifactory username
- `ARTIFACTORY_PASSWORD`: Artifactory password

## Best Practices

### 1. Start Simple
Begin with minimal configuration and add options as needed:
```groovy
def pipelineConfig = [environment: 'production']
```

### 2. Use Environment-Specific Configs
Create different configurations for different environments:
```groovy
def pipelineConfig = [
    environment: env.BUILD_ENVIRONMENT ?: 'production',
    enableMutationTests: env.BUILD_ENVIRONMENT != 'development'
]
```

### 3. Override Only When Necessary
Most values have sensible defaults and auto-detection:
```groovy
// Only override if you need custom values
def pipelineConfig = [
    environment: 'production',
    pythonVersion: '3.12'  // Only if you need Python 3.12
]
```

### 4. Use Environment Variables for Secrets
Store sensitive information as Jenkins environment variables:
```groovy
// Don't hardcode tokens in your Jenkinsfile
// Use environment variables instead
```

## Troubleshooting Configuration

### Common Issues

1. **Configuration Not Applied**
   - Ensure configuration map is passed to `pythonCIPipeline()`
   - Check that option names match exactly (case-sensitive)
   - Verify configuration is merged correctly

2. **Auto-Detection Not Working**
   - Check that `GIT_URL` environment variable is set
   - Verify repository name extraction logic
   - Review fallback values

3. **Infrastructure URLs Not Loading**
   - Check `resources/common-config.yml` file
   - Verify environment setting matches config file
   - Ensure YAML syntax is correct

4. **Default Values Not Applied**
   - Check that configuration is not being overridden
   - Verify option names are correct
   - Review configuration merge order 