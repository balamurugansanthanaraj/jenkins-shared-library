#!/usr/bin/env groovy

/**
 * Python Library CI Pipeline
 * 
 * This pipeline orchestrates the complete CI process for a Python library including:
 * - Version bumping based on PR title
 * - Building and testing
 * - Code quality checks
 * - Security scans
 * - Artifact publishing
 * 
 * @param config Pipeline configuration map
 */
def call(Map config = [:]) {
    // Import required classes
    def pythonSteps = new com.company.jenkins.PythonLibrarySteps(this)
    def sonarQube = new com.company.jenkins.SonarQubeIntegration(this)
    def nexusIQ = new com.company.jenkins.NexusIQIntegration(this)
    def artifactory = new com.company.jenkins.ArtifactoryIntegration(this)
    def gitOps = new com.company.jenkins.GitOperations(this)
    
    // Set default configuration
    def defaultConfig = [
        // Agent configuration
        agentLabel: 'python-agent',
        
        // Repository configuration
        repoUrl: env.GIT_URL ?: '',
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
        sonarHostUrl: 'http://sonarqube:9000',
        sonarToken: env.SONAR_TOKEN ?: '',
        
        // Nexus IQ configuration
        nexusIqUrl: 'http://nexus-iq:8070',
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
    
    // Merge provided config with defaults
    config = defaultConfig + config
    
    // Initialize pipeline
    pipeline {
        agent {
            label config.agentLabel
        }
        
        environment {
            // Set environment variables
            PYTHON_VERSION = config.pythonVersion
            PIP_CACHE_DIR = '/tmp/pip-cache'
            PYTHONPATH = "${WORKSPACE}"
        }
        
        stages {
            stage('Checkout') {
                steps {
                    script {
                        pythonSteps.checkoutSourceCode(config)
                    }
                }
            }
            
            stage('Setup Environment') {
                steps {
                    script {
                        pythonSteps.setupPythonEnvironment(config)
                    }
                }
            }
            
            stage('Version Bump') {
                when {
                    expression { config.autoVersionBump && config.prTitle }
                }
                steps {
                    script {
                        pythonSteps.bumpVersion(config)
                    }
                }
            }
            
            stage('Install Dependencies') {
                steps {
                    script {
                        pythonSteps.installDependencies(config)
                    }
                }
            }
            
            stage('Build Package') {
                steps {
                    script {
                        pythonSteps.buildPackage(config)
                    }
                }
            }
            
            stage('Code Quality - Ruff') {
                steps {
                    script {
                        pythonSteps.runRuffLinting(config)
                    }
                }
            }
            
            stage('Unit Tests') {
                steps {
                    script {
                        pythonSteps.runUnitTests(config)
                    }
                }
            }
            
            stage('Mutation Tests') {
                when {
                    expression { config.enableMutationTests }
                }
                steps {
                    script {
                        pythonSteps.runMutationTests(config)
                    }
                }
            }
            
            stage('SonarQube Analysis') {
                when {
                    expression { config.enableSonarQube }
                }
                steps {
                    script {
                        sonarQube.runAnalysis(config)
                    }
                }
            }
            
            stage('Nexus IQ Scan') {
                when {
                    expression { config.enableNexusIQ }
                }
                steps {
                    script {
                        nexusIQ.runScan(config)
                    }
                }
            }
            
            stage('Upload to Artifactory') {
                when {
                    expression { config.enableArtifactory }
                }
                steps {
                    script {
                        artifactory.uploadPackage(config)
                    }
                }
            }
            
            stage('Git Operations') {
                when {
                    branch 'master'
                }
                steps {
                    script {
                        gitOps.performGitOperations(config)
                    }
                }
            }
        }
        
        post {
            always {
                script {
                    pythonSteps.cleanup(config)
                }
            }
            success {
                script {
                    pythonSteps.notifySuccess(config)
                }
            }
            failure {
                script {
                    pythonSteps.notifyFailure(config)
                }
            }
        }
    }
} 