package com.company.jenkins

/**
 * Artifactory Integration for Python Library Pipeline
 * 
 * Handles uploading packages to JFrog Artifactory with conditional
 * repository selection based on branch
 */
class ArtifactoryIntegration implements Serializable {
    def script
    def env
    
    ArtifactoryIntegration(script) {
        this.script = script
        this.env = script.env
    }
    
    /**
     * Upload package to Artifactory
     */
    def uploadPackage(Map config) {
        script.echo "Starting Artifactory upload for branch: ${config.branch}"
        
        // Determine repository and path
        def repoInfo = determineRepository(config)
        script.echo "Using repository: ${repoInfo.repository}"
        
        // Find package artifacts
        def artifacts = findPackageArtifacts()
        if (artifacts.isEmpty()) {
            script.error "No package artifacts found for upload"
        }
        
        // Upload each artifact
        artifacts.each { artifact ->
            uploadArtifact(config, repoInfo, artifact)
        }
        
        // Generate metadata
        generateMetadata(config, repoInfo, artifacts)
        
        script.echo "Artifactory upload completed successfully"
        
        return [
            repository: repoInfo.repository,
            artifacts: artifacts,
            metadataUrl: repoInfo.metadataUrl
        ]
    }
    
    /**
     * Determine repository based on branch
     */
    def determineRepository(Map config) {
        def repository
        def metadataUrl
        
        if (config.branch == 'master') {
            repository = "${config.artifactoryRepo}-release"
            metadataUrl = "${config.artifactoryUrl}/artifactory/${repository}/metadata"
        } else {
            repository = "${config.artifactoryRepo}-snapshot"
            metadataUrl = "${config.artifactoryUrl}/artifactory/${repository}/metadata"
        }
        
        return [
            repository: repository,
            metadataUrl: metadataUrl
        ]
    }
    
    /**
     * Find package artifacts in dist directory
     */
    def findPackageArtifacts() {
        def artifacts = []
        
        if (script.fileExists('dist/')) {
            def distFiles = script.sh(
                script: "find dist/ -name '*.tar.gz' -o -name '*.whl'",
                returnStdout: true
            ).trim().split('\n')
            
            distFiles.each { file ->
                if (file) {
                    artifacts.add(file)
                }
            }
        }
        
        return artifacts
    }
    
    /**
     * Upload individual artifact to Artifactory
     */
    def uploadArtifact(Map config, def repoInfo, String artifactPath) {
        script.echo "Uploading artifact: ${artifactPath}"
        
        def artifactName = script.sh(
            script: "basename ${artifactPath}",
            returnStdout: true
        ).trim()
        
        def targetPath = generateTargetPath(config, repoInfo, artifactName)
        
        def uploadResult = script.sh(
            script: """
                # Upload to Artifactory using curl
                curl -X PUT \
                    -H "X-JFrog-Art-Api: ${config.artifactoryPassword}" \
                    -H "Content-Type: application/octet-stream" \
                    --data-binary @${artifactPath} \
                    "${config.artifactoryUrl}/artifactory/${repoInfo.repository}/${targetPath}"
            """,
            returnStatus: true
        )
        
        if (uploadResult != 0) {
            script.error "Failed to upload artifact: ${artifactPath}"
        }
        
        script.echo "Successfully uploaded: ${targetPath}"
        
        return targetPath
    }
    
    /**
     * Generate target path for artifact
     */
    def generateTargetPath(Map config, def repoInfo, String artifactName) {
        def version = env.NEW_VERSION ?: '1.0.0'
        def timestamp = script.sh(
            script: "date +%Y%m%d.%H%M%S",
            returnStdout: true
        ).trim()
        
        if (config.branch == 'master') {
            // Release version - use semantic versioning
            return "${config.sonarProjectKey}/${version}/${artifactName}"
        } else {
            // Snapshot version - include timestamp
            return "${config.sonarProjectKey}/${version}-SNAPSHOT/${artifactName}"
        }
    }
    
    /**
     * Generate metadata for the package
     */
    def generateMetadata(Map config, def repoInfo, List artifacts) {
        script.echo "Generating package metadata"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        def metadata = [
            name: config.sonarProjectKey,
            version: version,
            branch: config.branch,
            buildNumber: env.BUILD_NUMBER,
            buildUrl: env.BUILD_URL,
            artifacts: artifacts,
            uploadTime: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'"),
            repository: repoInfo.repository
        ]
        
        def metadataJson = script.writeJSON returnText: true, json: metadata
        
        // Upload metadata
        def metadataResult = script.sh(
            script: """
                curl -X PUT \
                    -H "X-JFrog-Art-Api: ${config.artifactoryPassword}" \
                    -H "Content-Type: application/json" \
                    -d '${metadataJson}' \
                    "${config.artifactoryUrl}/artifactory/${repoInfo.repository}/${config.sonarProjectKey}/${version}/metadata.json"
            """,
            returnStatus: true
        )
        
        if (metadataResult != 0) {
            script.echo "Warning: Failed to upload metadata"
        } else {
            script.echo "Metadata uploaded successfully"
        }
    }
    
    /**
     * Verify package upload
     */
    def verifyUpload(Map config, def repoInfo, List artifacts) {
        script.echo "Verifying package upload"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        def targetPath = "${config.sonarProjectKey}/${version}"
        
        def storageUrl = "${config.artifactoryUrl}/artifactory/api/storage/${repoInfo.repository}/${targetPath}"
        
        def verificationResult = script.sh(
            script: """
                curl -s -H "X-JFrog-Art-Api: ${config.artifactoryPassword}" \\
                    "${storageUrl}" | \\
                    jq -r '.children[]?.uri' | grep -E '\\\\.(tar\\\\.gz|whl)$$'
            """,
            returnStdout: true
        ).trim()
        
        def uploadedFiles = verificationResult.split('\n').findAll { it }
        
        if (uploadedFiles.size() != artifacts.size()) {
            script.echo "Warning: Upload verification failed. Expected ${artifacts.size()} files, found ${uploadedFiles.size()}"
        } else {
            script.echo "Upload verification successful"
        }
        
        return uploadedFiles
    }
    
    /**
     * Get package download URL
     */
    def getDownloadUrl(Map config, def repoInfo, String artifactName) {
        def version = env.NEW_VERSION ?: '1.0.0'
        def targetPath = generateTargetPath(config, repoInfo, artifactName)
        
        return "${config.artifactoryUrl}/artifactory/${repoInfo.repository}/${targetPath}"
    }
    
    /**
     * List available versions
     */
    def listVersions(Map config, def repoInfo) {
        def response = script.sh(
            script: """
                curl -s -H "X-JFrog-Art-Api: ${config.artifactoryPassword}" \
                    "${config.artifactoryUrl}/artifactory/api/storage/${repoInfo.repository}/${config.sonarProjectKey}" | \
                    jq -r '.children[]?.uri' | sed 's|/||'
            """,
            returnStdout: true
        ).trim()
        
        return response.split('\n').findAll { it }
    }
    
    /**
     * Delete old versions (cleanup)
     */
    def cleanupOldVersions(Map config, def repoInfo, int keepVersions = 10) {
        script.echo "Cleaning up old versions (keeping ${keepVersions} latest)"
        
        def versions = listVersions(config, repoInfo)
        if (versions.size() <= keepVersions) {
            script.echo "No cleanup needed"
            return
        }
        
        def versionsToDelete = versions.sort().take(versions.size() - keepVersions)
        
        versionsToDelete.each { version ->
            script.echo "Deleting version: ${version}"
            
            def deleteResult = script.sh(
                script: """
                    curl -X DELETE \
                        -H "X-JFrog-Art-Api: ${config.artifactoryPassword}" \
                        "${config.artifactoryUrl}/artifactory/${repoInfo.repository}/${config.sonarProjectKey}/${version}"
                """,
                returnStatus: true
            )
            
            if (deleteResult == 0) {
                script.echo "Deleted version: ${version}"
            } else {
                script.echo "Failed to delete version: ${version}"
            }
        }
    }
} 