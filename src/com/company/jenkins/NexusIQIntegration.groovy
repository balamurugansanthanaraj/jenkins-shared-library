package com.company.jenkins

/**
 * Nexus IQ Integration for Python Library Pipeline
 * 
 * Handles Nexus IQ security and policy compliance scanning
 * with conditional stage handling based on branch
 */
class NexusIQIntegration implements Serializable {
    def script
    def env
    
    NexusIQIntegration(script) {
        this.script = script
        this.env = script.env
    }
    
    /**
     * Run Nexus IQ scan
     */
    def runScan(Map config) {
        script.echo "Starting Nexus IQ scan for application: ${config.nexusIqApplicationId}"
        
        // Determine scan stage based on branch
        def scanStage = determineScanStage(config)
        script.echo "Using scan stage: ${scanStage}"
        
        // Run the scan
        def scanResult = executeScan(config, scanStage)
        
        if (scanResult.exitCode != 0) {
            script.error "Nexus IQ scan failed with exit code: ${scanResult.exitCode}"
        }
        
        // Parse scan results
        def results = parseScanResults(scanResult.output)
        
        // Upload results conditionally
        if (config.branch == 'master') {
            uploadToReleaseStage(config, results)
        } else {
            uploadToDevelopmentStage(config, results)
        }
        
        // Check policy compliance
        checkPolicyCompliance(results)
        
        script.echo "Nexus IQ scan completed successfully"
        
        return results
    }
    
    /**
     * Determine scan stage based on branch
     */
    def determineScanStage(Map config) {
        if (config.branch == 'master') {
            return 'release'
        } else {
            return 'development'
        }
    }
    
    /**
     * Execute Nexus IQ scan
     */
    def executeScan(Map config, String stage) {
        script.echo "Executing Nexus IQ scan in ${stage} stage"
        
        def scanOutput = script.sh(
            script: """
                # Install Nexus IQ CLI if not available
                if ! command -v nexus-iq-cli &> /dev/null; then
                    echo "Installing Nexus IQ CLI"
                    wget https://download.sonatype.com/clm/scanner/latest/nexus-iq-cli.jar -O nexus-iq-cli.jar
                    echo '#!/bin/bash' > nexus-iq-cli
                    echo 'java -jar nexus-iq-cli.jar "\$@"' >> nexus-iq-cli
                    chmod +x nexus-iq-cli
                    export PATH=\$PATH:\$PWD
                fi
                
                # Run Nexus IQ scan
                nexus-iq-cli evaluate \
                    --application ${config.nexusIqApplicationId} \
                    --stage ${stage} \
                    --server-url ${config.nexusIqUrl} \
                    --username admin \
                    --password ${config.nexusIqToken} \
                    --scan-target . \
                    --output-file nexus-iq-report.json \
                    --format json
            """,
            returnStdout: true
        )
        
        def exitCode = script.sh(
            script: "echo \$?",
            returnStdout: true
        ).trim().toInteger()
        
        return [
            output: scanOutput,
            exitCode: exitCode
        ]
    }
    
    /**
     * Parse scan results from JSON output
     */
    def parseScanResults(String output) {
        def reportFile = 'nexus-iq-report.json'
        
        if (!script.fileExists(reportFile)) {
            script.error "Nexus IQ report file not found: ${reportFile}"
        }
        
        def reportContent = script.readFile(reportFile)
        def report = script.readJSON text: reportContent
        
        return [
            policyAction: report.policyAction ?: 'NONE',
            reportHtmlUrl: report.reportHtmlUrl ?: '',
            components: report.components ?: [],
            policyViolations: report.policyViolations ?: [],
            summary: extractSummary(report)
        ]
    }
    
    /**
     * Extract summary from scan results
     */
    def extractSummary(def report) {
        return [
            totalComponents: report.components?.size() ?: 0,
            criticalCount: report.policyViolations?.findAll { it.threatLevel == 'Critical' }?.size() ?: 0,
            severeCount: report.policyViolations?.findAll { it.threatLevel == 'Severe' }?.size() ?: 0,
            moderateCount: report.policyViolations?.findAll { it.threatLevel == 'Moderate' }?.size() ?: 0,
            policyAction: report.policyAction ?: 'NONE'
        ]
    }
    
    /**
     * Upload results to release stage
     */
    def uploadToReleaseStage(Map config, def results) {
        script.echo "Uploading scan results to release stage"
        
        script.sh """
            # Upload to release stage
            curl -X POST \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer ${config.nexusIqToken}" \
                -d '{
                    "applicationId": "${config.nexusIqApplicationId}",
                    "stage": "release",
                    "scanResults": ${script.writeJSON returnText: true, json: results}
                }' \
                "${config.nexusIqUrl}/api/v2/applications/${config.nexusIqApplicationId}/reports"
        """
        
        script.echo "Results uploaded to release stage"
    }
    
    /**
     * Upload results to development stage
     */
    def uploadToDevelopmentStage(Map config, def results) {
        script.echo "Uploading scan results to development stage"
        
        script.sh """
            # Upload to development stage
            curl -X POST \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer ${config.nexusIqToken}" \
                -d '{
                    "applicationId": "${config.nexusIqApplicationId}",
                    "stage": "development",
                    "scanResults": ${script.writeJSON returnText: true, json: results}
                }' \
                "${config.nexusIqUrl}/api/v2/applications/${config.nexusIqApplicationId}/reports"
        """
        
        script.echo "Results uploaded to development stage"
    }
    
    /**
     * Check policy compliance
     */
    def checkPolicyCompliance(def results) {
        script.echo "Checking policy compliance"
        
        def summary = results.summary
        script.echo "Scan Summary:"
        script.echo "  Total Components: ${summary.totalComponents}"
        script.echo "  Critical Issues: ${summary.criticalCount}"
        script.echo "  Severe Issues: ${summary.severeCount}"
        script.echo "  Moderate Issues: ${summary.moderateCount}"
        script.echo "  Policy Action: ${summary.policyAction}"
        
        // Fail build if there are critical or severe issues
        if (summary.criticalCount > 0 || summary.severeCount > 0) {
            script.error "Policy compliance check failed: Found ${summary.criticalCount} critical and ${summary.severeCount} severe issues"
        }
        
        // Warn if there are moderate issues
        if (summary.moderateCount > 0) {
            script.echo "Warning: Found ${summary.moderateCount} moderate issues"
        }
        
        script.echo "Policy compliance check passed"
    }
    
    /**
     * Generate Nexus IQ report URL
     */
    def getReportUrl(Map config, def results) {
        if (results.reportHtmlUrl) {
            return results.reportHtmlUrl
        }
        
        return "${config.nexusIqUrl}/ui/links/application/${config.nexusIqApplicationId}/report/${results.reportId}"
    }
    
    /**
     * Get scan history
     */
    def getScanHistory(Map config) {
        def response = script.sh(
            script: """
                curl -s -H "Authorization: Bearer ${config.nexusIqToken}" \
                    "${config.nexusIqUrl}/api/v2/applications/${config.nexusIqApplicationId}/reports" | \
                    jq -r '.reports[] | "\\(.reportTime): \\(.policyAction)"' | head -10
            """,
            returnStdout: true
        ).trim()
        
        return response
    }
} 