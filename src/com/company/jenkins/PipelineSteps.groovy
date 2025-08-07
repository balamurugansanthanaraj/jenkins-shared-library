package com.company.jenkins

import groovy.transform.Field

/**
 * Generic pipeline steps that are language-agnostic
 * Handles cleanup, notifications, and other common pipeline operations
 */
class PipelineSteps implements Serializable {
    
    @Field
    def script
    
    PipelineSteps(script) {
        this.script = script
    }
    
    /**
     * Cleanup pipeline artifacts and temporary files
     */
    def cleanup(Map config) {
        script.echo "Cleaning up pipeline artifacts"
        
        script.sh """
            # Clean up temporary files
            rm -rf .pytest_cache/
            rm -rf .coverage
            rm -rf htmlcov/
            rm -rf .mutmut-cache/
            rm -rf build/
            rm -rf dist/
            rm -rf *.egg-info/
            
            # Clean up virtual environment if it exists
            if [ -d "venv" ]; then
                rm -rf venv/
            fi
            
            # Clean up any other temporary files
            find . -name "*.pyc" -delete
            find . -name "__pycache__" -type d -exec rm -rf {} + 2>/dev/null || true
        """
        
        script.echo "Cleanup completed"
    }
    
    /**
     * Send success notification
     */
    def notifySuccess(Map config) {
        script.echo "Pipeline completed successfully"
        
        // Get build information
        def buildInfo = [
            jobName: script.env.JOB_NAME ?: 'Unknown',
            buildNumber: script.env.BUILD_NUMBER ?: 'Unknown',
            buildUrl: script.env.BUILD_URL ?: 'Unknown',
            gitBranch: script.env.GIT_BRANCH ?: 'Unknown',
            gitCommit: script.env.GIT_COMMIT ?: 'Unknown'
        ]
        
        script.echo """
            ✅ Pipeline Success
            Job: ${buildInfo.jobName}
            Build: ${buildInfo.buildNumber}
            Branch: ${buildInfo.gitBranch}
            Commit: ${buildInfo.gitCommit}
            URL: ${buildInfo.buildUrl}
        """
        
        // Add any custom success notification logic here
        // For example: Slack, email, Teams, etc.
        if (config.enableNotifications) {
            sendCustomSuccessNotification(buildInfo, config)
        }
    }
    
    /**
     * Send failure notification
     */
    def notifyFailure(Map config) {
        script.echo "Pipeline failed"
        
        // Get build information
        def buildInfo = [
            jobName: script.env.JOB_NAME ?: 'Unknown',
            buildNumber: script.env.BUILD_NUMBER ?: 'Unknown',
            buildUrl: script.env.BUILD_URL ?: 'Unknown',
            gitBranch: script.env.GIT_BRANCH ?: 'Unknown',
            gitCommit: script.env.GIT_COMMIT ?: 'Unknown'
        ]
        
        script.echo """
            ❌ Pipeline Failed
            Job: ${buildInfo.jobName}
            Build: ${buildInfo.buildNumber}
            Branch: ${buildInfo.gitBranch}
            Commit: ${buildInfo.gitCommit}
            URL: ${buildInfo.buildUrl}
        """
        
        // Add any custom failure notification logic here
        // For example: Slack, email, Teams, etc.
        if (config.enableNotifications) {
            sendCustomFailureNotification(buildInfo, config)
        }
    }
    
    /**
     * Send custom success notification (placeholder for extensibility)
     */
    private def sendCustomSuccessNotification(Map buildInfo, Map config) {
        // Placeholder for custom notification logic
        // Examples: Slack webhook, email, Teams, etc.
        script.echo "Custom success notification would be sent here"
        
        // Example Slack notification:
        // if (config.slackWebhookUrl) {
        //     script.slackSend(
        //         channel: config.slackChannel,
        //         color: 'good',
        //         message: "✅ Pipeline Success: ${buildInfo.jobName} #${buildInfo.buildNumber}"
        //     )
        // }
    }
    
    /**
     * Send custom failure notification (placeholder for extensibility)
     */
    private def sendCustomFailureNotification(Map buildInfo, Map config) {
        // Placeholder for custom notification logic
        // Examples: Slack webhook, email, Teams, etc.
        script.echo "Custom failure notification would be sent here"
        
        // Example Slack notification:
        // if (config.slackWebhookUrl) {
        //     script.slackSend(
        //         channel: config.slackChannel,
        //         color: 'danger',
        //         message: "❌ Pipeline Failed: ${buildInfo.jobName} #${buildInfo.buildNumber}"
        //     )
        // }
    }
    
    /**
     * Validate pipeline configuration
     */
    def validateConfig(Map config) {
        script.echo "Validating pipeline configuration"
        
        // Check required fields
        def requiredFields = ['environment']
        def missingFields = requiredFields.findAll { !config.containsKey(it) }
        
        if (missingFields) {
            script.error "Missing required configuration fields: ${missingFields.join(', ')}"
        }
        
        // Validate environment
        def validEnvironments = ['development', 'staging', 'production']
        if (!validEnvironments.contains(config.environment)) {
            script.error "Invalid environment: ${config.environment}. Must be one of: ${validEnvironments.join(', ')}"
        }
        
        script.echo "Configuration validation passed"
    }
    
    /**
     * Setup pipeline environment variables
     */
    def setupEnvironment(Map config) {
        script.echo "Setting up pipeline environment"
        
        // Set default values if not provided
        config.agentLabel = config.agentLabel ?: 'python-agent'
        config.enableNotifications = config.enableNotifications ?: false
        
        // Set environment-specific variables
        script.env.PIPELINE_ENVIRONMENT = config.environment
        script.env.PIPELINE_AGENT = config.agentLabel
        
        script.echo "Pipeline environment setup completed"
    }
    
    /**
     * Checkout source code from repository
     * For multi-branch pipelines, Jenkins automatically handles checkout
     */
    def checkoutSourceCode(Map config) {
        script.echo "Verifying source code checkout for multi-branch pipeline"
        
        // In multi-branch pipelines, Jenkins automatically performs checkout
        // We just need to verify the checkout was successful
        script.sh """
            echo "Current branch: \${BRANCH_NAME:-GIT_BRANCH}"
            echo "Repository URL: \${GIT_URL}"
            echo "Working directory: \$(pwd)"
            echo "Git status:"
            git status
            echo "Files in workspace:"
            ls -la
        """
        
        script.echo "Source code checkout verified successfully"
    }
} 