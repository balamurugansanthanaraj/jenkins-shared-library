# Usage Guide

## Quick Start

### 1. Create Your Jenkinsfile

Create a `Jenkinsfile` in your Python project root with the following content:

### 2. Customize Configuration

Edit the `Jenkinsfile` and customize the `pipelineConfig`:

```groovy
def pipelineConfig = [
    environment: 'production',  // Your environment
    pythonVersion: '3.11',     // Your Python version
    // ... other configurations
]
```

### 3. Ensure Jenkins Configuration

Make sure your Jenkins has the shared library configured:
- Go to **Manage Jenkins** → **Configure System**
- Add the shared library under **Global Pipeline Libraries**
- Set the name to `python-library-shared-lib`

## Minimal Example

```groovy
#!/usr/bin/env groovy

@Library('python-library-shared-lib') _

def pipelineConfig = [:]
pythonCIPipeline(pipelineConfig)
```

## Complete Example

Here's a complete configuration example with all options:

```groovy
#!/usr/bin/env groovy

@Library('python-library-shared-lib') _

def pipelineConfig = [
    // Environment configuration
    environment: 'production',  // Options: 'development', 'staging', 'production'
    
    // Agent configuration
    agentLabel: 'python-agent',  // Can be customized: 'docker-agent', 'ubuntu-agent', etc.
    
    // Python configuration
    pythonVersion: '3.11',
    requirementsFile: 'requirements.txt',
    setupFile: 'setup.py',
    versionFile: 'version.txt',
    
    // Tool configurations
    ruffConfig: '.ruff.toml',
    uvConfig: 'pyproject.toml',
    
    // Pipeline behavior
    enableMutationTests: true,
    enableSonarQube: true,
    enableNexusIQ: true,
    enableArtifactory: true,
    autoVersionBump: true
    
    // Note: sonarProjectKey, nexusIqApplicationId, and artifactoryRepo are automatically
    // set to the repository name and don't need to be specified
]

pythonCIPipeline(pipelineConfig)
```

## What Happens

The pipeline will automatically:
1. ✅ Checkout your code
2. ✅ Setup Python environment with uv
3. ✅ Install dependencies
4. ✅ Run tests and quality checks
5. ✅ Upload to Artifactory (if enabled)
6. ✅ Create Git tags (on master branch)

## Configuration Options

See the main README.md for all available configuration options. 