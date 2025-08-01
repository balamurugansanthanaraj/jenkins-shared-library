#!/usr/bin/env groovy

/**
 * Sample Jenkinsfile for Python Library CI Pipeline
 * 
 * This demonstrates how to use the Python Library Shared Library
 * for continuous integration of a Python library project.
 */

@Library('python-library-shared-lib') _

// Define pipeline configuration
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
    
    // Pipeline behavior
    enableMutationTests: true,
    enableSonarQube: true,
    enableNexusIQ: true,
    enableArtifactory: true,
    autoVersionBump: true
    
    // Note: sonarProjectKey, nexusIqApplicationId, and artifactoryRepo are automatically
    // set to the repository name and don't need to be specified
]

// Run the pipeline
pythonCIPipeline(pipelineConfig) 