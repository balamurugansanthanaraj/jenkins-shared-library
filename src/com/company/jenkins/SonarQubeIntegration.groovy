package com.company.jenkins

/**
 * SonarQube Integration for Python Library Pipeline
 * 
 * Handles SonarQube analysis with branch name and version tagging,
 * and quality gate enforcement
 */
class SonarQubeIntegration implements Serializable {
    def script
    def env
    
    SonarQubeIntegration(script) {
        this.script = script
        this.env = script.env
    }
    
    /**
     * Run SonarQube analysis
     */
    def runAnalysis(Map config) {
        script.echo "Starting SonarQube analysis for project: ${config.sonarProjectKey}"
        
        // Prepare SonarQube properties
        def sonarProperties = prepareSonarProperties(config)
        
        // Run SonarQube scanner
        def scannerResult = runSonarScanner(config, sonarProperties)
        
        if (scannerResult != 0) {
            script.error "SonarQube analysis failed"
        }
        
        // Wait for analysis to complete
        waitForAnalysisCompletion(config)
        
        // Check quality gate
        def qualityGateResult = checkQualityGate(config)
        
        if (!qualityGateResult.passed) {
            script.error "SonarQube quality gate failed: ${qualityGateResult.status}"
        }
        
        script.echo "SonarQube analysis completed successfully. Quality Gate: ${qualityGateResult.status}"
        
        return qualityGateResult
    }
    
    /**
     * Prepare SonarQube properties
     */
    def prepareSonarProperties(Map config) {
        def properties = [
            "sonar.projectKey=${config.sonarProjectKey}",
            "sonar.projectName=${config.sonarProjectKey}",
            "sonar.projectVersion=${env.NEW_VERSION ?: '1.0.0'}",
            "sonar.sources=src",
            "sonar.tests=tests",
            "sonar.python.version=${config.pythonVersion}",
            "sonar.python.coverage.reportPaths=coverage.xml",
            "sonar.python.xunit.reportPath=test-results.xml",
            "sonar.sourceEncoding=UTF-8",
            "sonar.host.url=${config.sonarHostUrl}",
            "sonar.login=${config.sonarToken}",
            "sonar.branch.name=${config.branch}",
            "sonar.branch.target=${config.branch == 'master' ? '' : 'master'}",
            "sonar.qualitygate.wait=true",
            "sonar.qualitygate.timeout=300"
        ]
        
        // Add branch-specific properties
        if (config.branch != 'master') {
            properties.add("sonar.branch.name=${config.branch}")
        }
        
        // Add coverage and test results if available
        if (script.fileExists('coverage.xml')) {
            properties.add("sonar.python.coverage.reportPaths=coverage.xml")
        }
        
        if (script.fileExists('test-results.xml')) {
            properties.add("sonar.python.xunit.reportPath=test-results.xml")
        }
        
        return properties
    }
    
    /**
     * Run SonarQube scanner
     */
    def runSonarScanner(Map config, List properties) {
        script.echo "Running SonarQube scanner"
        
        // Create sonar-project.properties file
        def propertiesContent = properties.join('\n')
        script.writeFile file: 'sonar-project.properties', text: propertiesContent
        
        // Run scanner
        def result = script.sh(
            script: """
                # Install SonarQube scanner if not available
                if ! command -v sonar-scanner &> /dev/null; then
                    echo "Installing SonarQube scanner"
                    wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.7.0.2747-linux.zip
                    unzip sonar-scanner-cli-4.7.0.2747-linux.zip
                    export PATH=\$PATH:\$PWD/sonar-scanner-4.7.0.2747-linux/bin
                fi
                
                # Run SonarQube scanner
                sonar-scanner -Dproject.settings=sonar-project.properties
            """,
            returnStatus: true
        )
        
        return result
    }
    
    /**
     * Wait for analysis completion
     */
    def waitForAnalysisCompletion(Map config) {
        script.echo "Waiting for SonarQube analysis to complete"
        
        def maxAttempts = 30
        def attempt = 0
        
        while (attempt < maxAttempts) {
            attempt++
            
            def status = getAnalysisStatus(config)
            
            if (status == 'SUCCESS') {
                script.echo "Analysis completed successfully"
                return
            } else if (status == 'FAILED') {
                script.error "SonarQube analysis failed"
            } else if (status == 'CANCELED') {
                script.error "SonarQube analysis was canceled"
            }
            
            script.echo "Analysis status: ${status}, waiting 10 seconds..."
            script.sleep(10)
        }
        
        script.error "SonarQube analysis did not complete within expected time"
    }
    
    /**
     * Get analysis status from SonarQube
     */
    def getAnalysisStatus(Map config) {
        def taskId = getTaskId(config)
        if (!taskId) {
            return 'UNKNOWN'
        }
        
        def response = script.sh(
            script: """
                curl -s -u ${config.sonarToken}: "${config.sonarHostUrl}/api/ce/task?id=${taskId}" | jq -r '.task.status'
            """,
            returnStdout: true
        ).trim()
        
        return response
    }
    
    /**
     * Get task ID from SonarQube
     */
    def getTaskId(Map config) {
        def response = script.sh(
            script: """
                curl -s -u ${config.sonarToken}: "${config.sonarHostUrl}/api/ce/component?component=${config.sonarProjectKey}" | jq -r '.current.tasks[0].id'
            """,
            returnStdout: true
        ).trim()
        
        return response == 'null' ? null : response
    }
    
    /**
     * Check quality gate
     */
    def checkQualityGate(Map config) {
        script.echo "Checking SonarQube quality gate"
        
        def maxAttempts = 30
        def attempt = 0
        
        while (attempt < maxAttempts) {
            attempt++
            
            def qualityGate = getQualityGateStatus(config)
            
            if (qualityGate.status != 'PENDING') {
                return qualityGate
            }
            
            script.echo "Quality gate still pending, waiting 10 seconds..."
            script.sleep(10)
        }
        
        return [status: 'TIMEOUT', passed: false, details: 'Quality gate check timed out']
    }
    
    /**
     * Get quality gate status from SonarQube
     */
    def getQualityGateStatus(Map config) {
        def response = script.sh(
            script: """
                curl -s -u ${config.sonarToken}: "${config.sonarHostUrl}/api/qualitygates/project_status?projectKey=${config.sonarProjectKey}" | jq -r '.projectStatus.status'
            """,
            returnStdout: true
        ).trim()
        
        def passed = response == 'OK'
        def details = getQualityGateDetails(config)
        
        return [
            status: response,
            passed: passed,
            details: details
        ]
    }
    
    /**
     * Get detailed quality gate information
     */
    def getQualityGateDetails(Map config) {
        def response = script.sh(
            script: """
                curl -s -u ${config.sonarToken}: "${config.sonarHostUrl}/api/qualitygates/project_status?projectKey=${config.sonarProjectKey}" | jq -r '.projectStatus.conditions[] | "\\(.metricKey): \\(.status) (\\(.actualValue)/\\(.errorThreshold))"'
            """,
            returnStdout: true
        ).trim()
        
        return response ?: 'No detailed information available'
    }
    
    /**
     * Generate SonarQube report URL
     */
    def getReportUrl(Map config) {
        return "${config.sonarHostUrl}/dashboard?id=${config.sonarProjectKey}&branch=${config.branch}"
    }
} 