#!/usr/bin/env groovy

/**
 * Configuration Loader for Jenkins Shared Library
 * 
 * Loads common configuration from YAML files and provides
 * infrastructure URLs and default configurations
 */
class ConfigLoader implements Serializable {
    
    def script
    def configCache = [:]
    
    ConfigLoader(script) {
        this.script = script
    }
    
    /**
     * Load configuration from YAML file
     */
    def loadConfig(String configPath = 'resources/common-config.yml') {
        if (configCache.containsKey(configPath)) {
            return configCache[configPath]
        }
        
        try {
            def configContent = script.libraryResource(configPath)
            def config = script.readYaml text: configContent
            configCache[configPath] = config
            return config
        } catch (Exception e) {
            script.echo "Warning: Could not load config from ${configPath}: ${e.message}"
            return [:]
        }
    }
    
    /**
     * Get infrastructure URLs (always production)
     */
    def getInfrastructureUrls() {
        def config = loadConfig()
        def urls = [:]
        
        // Always use production infrastructure URLs
        if (config.infrastructure) {
            if (config.infrastructure.sonarqube) {
                urls.sonarHostUrl = config.infrastructure.sonarqube.host_url
            }
            if (config.infrastructure.nexus_iq) {
                urls.nexusIqUrl = config.infrastructure.nexus_iq.host_url
            }
            if (config.infrastructure.artifactory) {
                urls.artifactoryUrl = config.infrastructure.artifactory.host_url
            }
        }
        
        return urls
    }
    
    /**
     * Get default configurations
     */
    def getDefaults() {
        def config = loadConfig()
        def defaults = [:]
        
        if (config.defaults) {
            // Agent configuration
            if (config.defaults.agent) {
                defaults.agentLabel = config.defaults.agent.label
            }
            
            // Python configuration
            if (config.defaults.python) {
                defaults.pythonVersion = config.defaults.python.version
                defaults.requirementsFile = config.defaults.python.requirements_file
                defaults.setupFile = config.defaults.python.setup_file
                defaults.versionFile = config.defaults.python.version_file
            }
            
            // Tool configurations
            if (config.defaults.tools) {
                defaults.ruffConfig = config.defaults.tools.ruff_config
            }
            
            // Git configuration
            if (config.defaults.git) {
                defaults.gitUser = config.defaults.git.user
                defaults.gitEmail = config.defaults.git.email
            }
            
            // Pipeline behavior
            if (config.defaults.pipeline) {
                defaults.enableMutationTests = config.defaults.pipeline.enable_mutation_tests
                defaults.enableSonarQube = config.defaults.pipeline.enable_sonarqube
                defaults.enableNexusIQ = config.defaults.pipeline.enable_nexus_iq
                defaults.enableArtifactory = config.defaults.pipeline.enable_artifactory
                defaults.autoVersionBump = config.defaults.pipeline.auto_version_bump
            }
        }
        
        return defaults
    }
    
    /**
     * Get default project keys and IDs based on repository name
     */
    def getDefaultProjectConfig() {
        def config = loadConfig()
        def projectConfig = [:]
        
        // Get repository name from Jenkins environment
        def repoName = getRepositoryName()
        
        // Use repository name as default for all project configurations
        projectConfig.sonarProjectKey = repoName
        projectConfig.nexusIqApplicationId = repoName
        projectConfig.artifactoryRepo = repoName
        
        return projectConfig
    }
    
    /**
     * Extract repository name from Jenkins environment
     */
    def getRepositoryName() {
        try {
            // Try to get from GIT_URL environment variable
            if (script.env.GIT_URL) {
                def gitUrl = script.env.GIT_URL
                // Extract repository name from various Git URL formats
                def repoName = gitUrl.replaceAll(/\.git$/, '')  // Remove .git extension
                repoName = repoName.replaceAll(/.*[\/\\]/, '')  // Get last part after slash/backslash
                return repoName
            }
            
            // Fallback to workspace name if GIT_URL is not available
            if (script.env.WORKSPACE) {
                def workspace = script.env.WORKSPACE
                return workspace.tokenize('/')[-1]  // Get last part of workspace path
            }
            
            // Final fallback to default
            return 'python-library'
        } catch (Exception e) {
            script.echo "Warning: Could not determine repository name: ${e.message}"
            return 'python-library'
        }
    }
    
    /**
     * Get complete default configuration including infrastructure URLs
     */
    def getCompleteDefaults() {
        def defaults = getDefaults()
        def urls = getInfrastructureUrls()
        def projectConfig = getDefaultProjectConfig()
        
        // Merge all configurations
        def completeConfig = [:]
        completeConfig.putAll(defaults)
        completeConfig.putAll(urls)
        completeConfig.putAll(projectConfig)
        
        return completeConfig
    }
} 