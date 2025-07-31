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
    // Agent configuration
    agentLabel: 'python-agent',  // Can be customized: 'docker-agent', 'ubuntu-agent', etc.
    
    // Repository configuration
    repoUrl: 'https://github.com/company/python-library.git',
    branch: env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'main',
    prTitle: env.CHANGE_TITLE ?: '',
    
    // Python configuration
    pythonVersion: '3.11',
    requirementsFile: 'requirements.txt',
    setupFile: 'setup.py',
    versionFile: 'version.txt',
    
    // Tool configurations
    ruffConfig: '.ruff.toml',
    testCommand: 'python -m pytest',
    coverageCommand: 'python -m pytest --cov=. --cov-report=xml',
    
    // SonarQube configuration
    sonarProjectKey: 'python-library',
    sonarHostUrl: 'http://sonarqube.company.com:9000',
    sonarToken: env.SONAR_TOKEN ?: '',
    
    // Nexus IQ configuration
    nexusIqUrl: 'http://nexus-iq.company.com:8070',
    nexusIqToken: env.NEXUS_IQ_TOKEN ?: '',
    nexusIqApplicationId: 'python-library',
    
    // Artifactory configuration
    artifactoryUrl: 'https://artifactory.company.com',
    artifactoryRepo: 'python-libs',
    artifactoryUser: env.ARTIFACTORY_USER ?: '',
    artifactoryPassword: env.ARTIFACTORY_PASSWORD ?: '',
    
    // Git configuration
    gitUser: env.GIT_USER ?: 'jenkins',
    gitEmail: env.GIT_EMAIL ?: 'jenkins@company.com',
    
    // Pipeline behavior
    enableMutationTests: true,
    enableSonarQube: true,
    enableNexusIQ: true,
    enableArtifactory: true,
    autoVersionBump: true
]

// Run the pipeline
pythonCIPipeline(pipelineConfig) 