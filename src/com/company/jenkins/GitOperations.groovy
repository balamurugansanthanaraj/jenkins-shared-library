package com.company.jenkins

/**
 * Git Operations for Python Library Pipeline
 * 
 * Handles safe Git operations including version updates,
 * changelog generation, and Git tagging (only from master branch)
 */
class GitOperations implements Serializable {
    def script
    def env
    
    GitOperations(script) {
        this.script = script
        this.env = script.env
    }
    
    /**
     * Perform Git operations (only from master branch)
     */
    def performGitOperations(Map config) {
        script.echo "Performing Git operations for release"
        
        // Verify we're on master branch
        if (config.branch != 'master') {
            script.echo "Not on master branch, skipping Git operations"
            return
        }
        
        // Configure Git
        configureGit(config)
        
        // Update version files
        updateVersionFiles(config)
        
        // Generate changelog
        generateChangelog(config)
        
        // Commit changes
        commitChanges(config)
        
        // Create Git tag
        createGitTag(config)
        
        // Push changes
        pushChanges(config)
        
        script.echo "Git operations completed successfully"
    }
    
    /**
     * Configure Git user and email
     */
    def configureGit(Map config) {
        script.echo "Configuring Git user and email"
        
        script.sh """
            git config user.name "${config.gitUser}"
            git config user.email "${config.gitEmail}"
            git config push.default simple
        """
    }
    
    /**
     * Update version files
     */
    def updateVersionFiles(Map config) {
        script.echo "Updating version files"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        
        script.sh """
            # Update version.txt if it exists
            if [ -f "${config.versionFile}" ]; then
                echo "${version}" > ${config.versionFile}
                echo "Updated ${config.versionFile} to ${version}"
            fi
            
            # Update setup.py if it exists
            if [ -f "${config.setupFile}" ]; then
                sed -i "s/version=[\"'][^\"']*[\"']/version=\"${version}\"/" ${config.setupFile}
                echo "Updated ${config.setupFile} to version ${version}"
            fi
            
            # Update __init__.py if it exists
            if [ -f "__init__.py" ]; then
                sed -i "s/__version__ = [\"'][^\"']*[\"']/__version__ = \"${version}\"/" __init__.py
                echo "Updated __init__.py to version ${version}"
            fi
            
            # Update pyproject.toml if it exists
            if [ -f "pyproject.toml" ]; then
                sed -i "s/version = [\"'][^\"']*[\"']/version = \"${version}\"/" pyproject.toml
                echo "Updated pyproject.toml to version ${version}"
            fi
        """
    }
    
    /**
     * Generate changelog
     */
    def generateChangelog(Map config) {
        script.echo "Generating changelog"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        def previousTag = getPreviousTag()
        
        script.sh """
            # Generate changelog from Git commits
            if [ -n "${previousTag}" ]; then
                echo "# Changelog for version ${version}" > CHANGELOG.md.new
                echo "" >> CHANGELOG.md.new
                echo "## [${version}] - $(date +%Y-%m-%d)" >> CHANGELOG.md.new
                echo "" >> CHANGELOG.md.new
                echo "### Changes" >> CHANGELOG.md.new
                echo "" >> CHANGELOG.md.new
                
                # Get commits since last tag
                git log --pretty=format:"- %s (%h)" ${previousTag}..HEAD >> CHANGELOG.md.new
                echo "" >> CHANGELOG.md.new
                
                # Prepend to existing changelog
                if [ -f "CHANGELOG.md" ]; then
                    cat CHANGELOG.md >> CHANGELOG.md.new
                fi
                
                mv CHANGELOG.md.new CHANGELOG.md
                echo "Generated changelog for version ${version}"
            else
                echo "# Changelog" > CHANGELOG.md
                echo "" >> CHANGELOG.md
                echo "## [${version}] - $(date +%Y-%m-%d)" >> CHANGELOG.md
                echo "" >> CHANGELOG.md
                echo "### Initial Release" >> CHANGELOG.md
                echo "" >> CHANGELOG.md
                echo "Generated initial changelog for version ${version}"
            fi
        """
    }
    
    /**
     * Get previous Git tag
     */
    def getPreviousTag() {
        def previousTag = script.sh(
            script: "git describe --tags --abbrev=0 2>/dev/null || echo ''",
            returnStdout: true
        ).trim()
        
        return previousTag
    }
    
    /**
     * Commit changes
     */
    def commitChanges(Map config) {
        script.echo "Committing version changes"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        
        script.sh """
            # Add all changes
            git add .
            
            # Check if there are changes to commit
            if ! git diff --cached --quiet; then
                git commit -m "Bump version to ${version}
                
                - Updated version files
                - Generated changelog
                - Build: ${env.BUILD_NUMBER}"
                
                echo "Committed version changes for ${version}"
            else
                echo "No changes to commit"
            fi
        """
    }
    
    /**
     * Create Git tag
     */
    def createGitTag(Map config) {
        script.echo "Creating Git tag"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        
        script.sh """
            # Create annotated tag
            git tag -a "v${version}" -m "Release version ${version}
            
            Build: ${env.BUILD_NUMBER}
            Build URL: ${env.BUILD_URL}"
            
            echo "Created Git tag v${version}"
        """
    }
    
    /**
     * Push changes to remote repository
     */
    def pushChanges(Map config) {
        script.echo "Pushing changes to remote repository"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        
        script.sh """
            # Push commits
            git push origin master
            
            # Push tags
            git push origin "v${version}"
            
            echo "Pushed changes and tag v${version} to remote repository"
        """
    }
    
    /**
     * Create release notes
     */
    def createReleaseNotes(Map config) {
        script.echo "Creating release notes"
        
        def version = env.NEW_VERSION ?: '1.0.0'
        def previousTag = getPreviousTag()
        
        def releaseNotes = script.sh(
            script: """
                echo "# Release Notes for Version ${version}"
                echo ""
                echo "## Summary"
                echo ""
                echo "This release includes the following changes:"
                echo ""
                
                if [ -n "${previousTag}" ]; then
                    echo "### Commits since ${previousTag}:"
                    echo ""
                    git log --pretty=format:"- %s (%h) by %an" ${previousTag}..HEAD
                else
                    echo "### Initial release"
                fi
                
                echo ""
                echo "## Build Information"
                echo ""
                echo "- Build Number: ${env.BUILD_NUMBER}"
                echo "- Build URL: ${env.BUILD_URL}"
                echo "- Commit: ${env.GIT_COMMIT}"
                echo "- Branch: ${env.GIT_BRANCH}"
            """,
            returnStdout: true
        ).trim()
        
        // Write release notes to file
        script.writeFile file: "RELEASE_NOTES_${version}.md", text: releaseNotes
        
        script.echo "Release notes created: RELEASE_NOTES_${version}.md"
        
        return releaseNotes
    }
    
    /**
     * Verify Git status
     */
    def verifyGitStatus(Map config) {
        script.echo "Verifying Git status"
        
        def status = script.sh(
            script: "git status --porcelain",
            returnStdout: true
        ).trim()
        
        if (status) {
            script.echo "Warning: Uncommitted changes detected:"
            script.echo status
        } else {
            script.echo "Git working directory is clean"
        }
        
        return status
    }
    
    /**
     * Get Git commit information
     */
    def getCommitInfo() {
        def commitInfo = [
            hash: script.sh(script: "git rev-parse HEAD", returnStdout: true).trim(),
            shortHash: script.sh(script: "git rev-parse --short HEAD", returnStdout: true).trim(),
            author: script.sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim(),
            date: script.sh(script: "git log -1 --pretty=format:'%ad'", returnStdout: true).trim(),
            message: script.sh(script: "git log -1 --pretty=format:'%s'", returnStdout: true).trim()
        ]
        
        return commitInfo
    }
} 