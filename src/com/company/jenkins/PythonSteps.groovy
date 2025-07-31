package com.company.jenkins

/**
 * Python Pipeline Steps
 * 
 * Contains all the individual step functions for the Python CI pipeline
 */
class PythonSteps implements Serializable {
    def script
    def env
    def currentBuild
    
    PythonSteps(script) {
        this.script = script
        this.env = script.env
        this.currentBuild = script.currentBuild
    }
    
    /**
     * Checkout source code from repository
     */
    def checkoutSourceCode(Map config) {
        script.echo "Checking out source code from multi-branch pipeline"
        
        // In multi-branch pipeline, checkout is already done by Jenkins
        // Just verify we have the source code
        script.sh """
            echo "Current branch: \${BRANCH_NAME:-GIT_BRANCH}"
            echo "Repository URL: \${GIT_URL}"
            echo "Working directory: \$(pwd)"
            ls -la
        """
        
        script.echo "Source code checkout verified"
    }
    
    /**
     * Setup Python environment
     */
    def setupPythonEnvironment(Map config) {
        script.echo "Setting up Python environment with version ${config.pythonVersion}"
        
        // Use pyenv or conda to manage Python versions
        script.sh """
            # Install Python version if not available
            if ! command -v python${config.pythonVersion} &> /dev/null; then
                echo "Installing Python ${config.pythonVersion}"
                pyenv install ${config.pythonVersion} || conda create -n py${config.pythonVersion} python=${config.pythonVersion} -y
            fi
            
            # Set Python version
            pyenv local ${config.pythonVersion} || conda activate py${config.pythonVersion}
            
            # Upgrade pip
            python -m pip install --upgrade pip
            
            # Create virtual environment
            python -m venv venv
            source venv/bin/activate
            
            # Verify Python version
            python --version
            pip --version
        """
        
        script.echo "Python environment setup complete"
    }
    
    /**
     * Bump version based on PR title
     */
    def bumpVersion(Map config) {
        def prTitle = env.CHANGE_TITLE ?: ''
        script.echo "Bumping version based on PR title: ${prTitle}"
        
        if (!prTitle) {
            script.echo "No PR title found, skipping version bump"
            return
        }
        
        def versionBumpType = determineVersionBumpType(prTitle)
        if (!versionBumpType) {
            script.echo "No version bump type determined from PR title"
            return
        }
        
        script.sh """
            source venv/bin/activate
            
            # Read current version
            if [ -f "${config.versionFile}" ]; then
                CURRENT_VERSION=\$(cat ${config.versionFile})
            elif [ -f "${config.setupFile}" ]; then
                CURRENT_VERSION=\$(python -c "import re; print(re.search(r'version=[\"\']([^\"\']+)[\"\']', open('${config.setupFile}').read()).group(1))")
            else
                echo "No version file found, starting with 0.1.0"
                CURRENT_VERSION="0.1.0"
            fi
            
            echo "Current version: \$CURRENT_VERSION"
            
            # Bump version
            NEW_VERSION=\$(python -c "
import re
from packaging import version

current = version.parse('\$CURRENT_VERSION')
if '${versionBumpType}' == 'major':
    new = version.Version(f'{current.major + 1}.0.0')
elif '${versionBumpType}' == 'minor':
    new = version.Version(f'{current.major}.{current.minor + 1}.0')
elif '${versionBumpType}' == 'patch':
    new = version.Version(f'{current.major}.{current.minor}.{current.micro + 1}')
else:
    new = current

print(str(new))
")
            
            echo "New version: \$NEW_VERSION"
            
            # Update version files
            if [ -f "${config.versionFile}" ]; then
                echo "\$NEW_VERSION" > ${config.versionFile}
            fi
            
            if [ -f "${config.setupFile}" ]; then
                sed -i "s/version=[\"'][^\"']*[\"']/version=\"\$NEW_VERSION\"/" ${config.setupFile}
            fi
            
            # Update __init__.py if it exists
            if [ -f "__init__.py" ]; then
                sed -i "s/__version__ = [\"'][^\"']*[\"']/__version__ = \"\$NEW_VERSION\"/" __init__.py
            fi
            
            echo "Version bumped to \$NEW_VERSION"
        """
        
        // Store the new version in environment for later use
        script.env.NEW_VERSION = script.sh(
            script: "source venv/bin/activate && cat ${config.versionFile}",
            returnStdout: true
        ).trim()
        
        script.echo "Version bumped successfully to: ${script.env.NEW_VERSION}"
    }
    
    /**
     * Determine version bump type from PR title
     */
    def determineVersionBumpType(String prTitle) {
        if (prTitle.toLowerCase().startsWith('breaking-')) {
            return 'major'
        } else if (prTitle.toLowerCase().startsWith('feature-')) {
            return 'minor'
        } else if (prTitle.toLowerCase().startsWith('fix-')) {
            return 'patch'
        }
        return null
    }
    
    /**
     * Install Python dependencies
     */
    def installDependencies(Map config) {
        script.echo "Installing Python dependencies"
        
        script.sh """
            source venv/bin/activate
            
            # Install dependencies from requirements file
            if [ -f "${config.requirementsFile}" ]; then
                pip install -r ${config.requirementsFile}
            fi
            
            # Install development dependencies
            if [ -f "requirements-dev.txt" ]; then
                pip install -r requirements-dev.txt
            fi
            
            # Install test dependencies
            pip install pytest pytest-cov ruff mutmut
            
            # Install package in editable mode
            if [ -f "${config.setupFile}" ]; then
                pip install -e .
            fi
            
            # List installed packages
            pip list
        """
        
        script.echo "Dependencies installed successfully"
    }
    
    /**
     * Build Python package
     */
    def buildPackage(Map config) {
        script.echo "Building Python package"
        
        script.sh """
            source venv/bin/activate
            
            # Clean previous builds
            rm -rf build/ dist/ *.egg-info/
            
            # Build package
            if [ -f "${config.setupFile}" ]; then
                python ${config.setupFile} sdist bdist_wheel
            else
                echo "No setup.py found, skipping package build"
            fi
            
            # List built artifacts
            ls -la dist/ || echo "No dist directory found"
        """
        
        script.echo "Package build complete"
    }
    
    /**
     * Run Ruff linting
     */
    def runRuffLinting(Map config) {
        script.echo "Running Ruff linting"
        
        def ruffResult = script.sh(
            script: """
                source venv/bin/activate
                
                # Run Ruff check
                ruff check . --config ${config.ruffConfig} || exit 1
                
                # Run Ruff format check
                ruff format --check . --config ${config.ruffConfig} || exit 1
            """,
            returnStatus: true
        )
        
        if (ruffResult != 0) {
            script.error "Ruff linting failed"
        }
        
        script.echo "Ruff linting passed"
    }
    
    /**
     * Run unit tests
     */
    def runUnitTests(Map config) {
        script.echo "Running unit tests"
        
        script.sh """
            source venv/bin/activate
            
            # Run tests with coverage
            python -m pytest --cov=. --cov-report=xml --cov-report=html
            
            # Generate coverage report
            coverage report
            coverage html
        """
        
        // Publish test results
        script.publishHTML([
            allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'htmlcov',
            reportFiles: 'index.html',
            reportName: 'Coverage Report'
        ])
        
        script.echo "Unit tests completed"
    }
    
    /**
     * Run mutation tests
     */
    def runMutationTests(Map config) {
        script.echo "Running mutation tests"
        
        script.sh """
            source venv/bin/activate
            
            # Run mutation tests
            mutmut run --paths-to-mutate=src/ || echo "Mutation tests completed"
            
            # Generate mutation test report
            mutmut results --format=html > mutation_report.html || echo "No mutation report generated"
        """
        
        // Publish mutation test results
        script.publishHTML([
            allowMissing: true,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: '.',
            reportFiles: 'mutation_report.html',
            reportName: 'Mutation Test Report'
        ])
        
        script.echo "Mutation tests completed"
    }
    
    /**
     * Cleanup after pipeline execution
     */
    def cleanup(Map config) {
        script.echo "Performing cleanup"
        
        script.sh """
            # Clean up virtual environment
            rm -rf venv/
            
            # Clean up build artifacts
            rm -rf build/ dist/ *.egg-info/
            
            # Clean up cache
            rm -rf .pytest_cache/ .ruff_cache/ .mutmut-cache/
        """
        
        script.echo "Cleanup completed"
    }
    
    /**
     * Send success notification
     */
    def notifySuccess(Map config) {
        script.echo "Pipeline completed successfully"
        // Add notification logic here (email, Slack, etc.)
    }
    
    /**
     * Send failure notification
     */
    def notifyFailure(Map config) {
        script.echo "Pipeline failed"
        // Add notification logic here (email, Slack, etc.)
    }
} 