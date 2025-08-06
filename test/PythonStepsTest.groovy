import com.company.jenkins.PythonSteps
import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

/**
 * Unit tests for PythonSteps class
 */
class PythonStepsTest extends BasePipelineTest {
    
    PythonSteps steps
    
    @Before
    void setUp() {
        super.setUp()
        steps = new PythonSteps(this)
    }
    
    @Test
    void testDetermineVersionBumpType() {
        // Test major version bump
        assertEquals('major', steps.determineVersionBumpType('breaking-change-api'))
        assertEquals('major', steps.determineVersionBumpType('BREAKING-remove-old-feature'))
        
        // Test minor version bump
        assertEquals('minor', steps.determineVersionBumpType('feature-add-new-functionality'))
        assertEquals('minor', steps.determineVersionBumpType('FEATURE-enhance-performance'))
        
        // Test patch version bump
        assertEquals('patch', steps.determineVersionBumpType('fix-bug-in-calculation'))
        assertEquals('patch', steps.determineVersionBumpType('FIX-correct-typo'))
        
        // Test no version bump
        assertNull(steps.determineVersionBumpType('update-documentation'))
        assertNull(steps.determineVersionBumpType('refactor-code'))
        assertNull(steps.determineVersionBumpType(''))
        assertNull(steps.determineVersionBumpType(null))
    }
    
    @Test
    void testCheckoutSourceCode() {
        def config = [
            repoUrl: 'https://github.com/company/python-library.git',
            branch: 'feature/new-feature'
        ]
        
        steps.checkoutSourceCode(config)
        
        // Verify checkout was called with correct parameters
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'checkout'
        }.size())
        
        def checkoutCall = helper.callStack.find { call ->
            call.methodName == 'checkout'
        }
        
        assertNotNull(checkoutCall)
        assertEquals('feature/new-feature', checkoutCall.args[0].branches[0].name)
    }
    
    @Test
    void testSetupPythonEnvironment() {
        def config = [
            pythonVersion: '3.11'
        ]
        
        steps.setupPythonEnvironment(config)
        
        // Verify shell command was executed
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
        
        def shCall = helper.callStack.find { call ->
            call.methodName == 'sh'
        }
        
        assertNotNull(shCall)
        assertTrue(shCall.args[0].contains('python3.11'))
        assertTrue(shCall.args[0].contains('venv'))
    }
    
    @Test
    void testBumpVersionWithValidPRTitle() {
        def config = [
            versionFile: 'version.txt',
            setupFile: 'setup.py'
        ]

        // Provide PR title through environment
        steps.env.CHANGE_TITLE = 'feature-add-new-api'

        // Mock file operations
        helper.registerAllowedMethod('sh', [Map.class], { args ->
            if (args.returnStdout && args.script.contains('cat version.txt')) {
                return '1.2.3'
            }
            return 0
        })

        steps.bumpVersion(config)

        // Verify version bump logic was executed and NEW_VERSION is set
        assertEquals('1.2.3', steps.env.NEW_VERSION)
    }

    @Test
    void testBumpVersionWithNoPRTitle() {
        def config = [
            versionFile: 'version.txt'
        ]

        // Explicitly clear PR title
        steps.env.CHANGE_TITLE = ''

        steps.bumpVersion(config)

        // Verify no shell commands were executed for version bump
        assertEquals(0, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
    }

    @Test
    void testBumpVersionWithSetupFileOnly() {
        def config = [
            setupFile: 'setup.py'
        ]

        steps.env.CHANGE_TITLE = 'feature-new-api'

        helper.registerAllowedMethod('sh', [Map.class], { args ->
            if (args.returnStdout && args.script.contains("python -c")) {
                return '2.0.0'
            }
            return 0
        })

        steps.bumpVersion(config)

        assertEquals('2.0.0', steps.env.NEW_VERSION)
    }
    
    @Test
    void testInstallDependencies() {
        def config = [
            requirementsFile: 'requirements.txt',
            setupFile: 'setup.py'
        ]
        
        steps.installDependencies(config)
        
        // Verify shell command was executed
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
        
        def shCall = helper.callStack.find { call ->
            call.methodName == 'sh'
        }
        
        assertNotNull(shCall)
        assertTrue(shCall.args[0].contains('pip install'))
        assertTrue(shCall.args[0].contains('requirements.txt'))
    }
    
    @Test
    void testBuildPackage() {
        def config = [
            setupFile: 'setup.py'
        ]
        
        steps.buildPackage(config)
        
        // Verify shell command was executed
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
        
        def shCall = helper.callStack.find { call ->
            call.methodName == 'sh'
        }
        
        assertNotNull(shCall)
        assertTrue(shCall.args[0].contains('setup.py'))
        assertTrue(shCall.args[0].contains('sdist'))
        assertTrue(shCall.args[0].contains('bdist_wheel'))
    }
    
    @Test
    void testRunRuffLintingSuccess() {
        def config = [
            ruffConfig: '.ruff.toml'
        ]
        
        // Mock successful ruff execution
        helper.registerAllowedMethod('sh', [Map.class], { args ->
            if (args.returnStatus) {
                return 0
            }
            return ''
        })
        
        steps.runRuffLinting(config)
        
        // Verify shell command was executed
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
        
        def shCall = helper.callStack.find { call ->
            call.methodName == 'sh'
        }
        
        assertNotNull(shCall)
        assertTrue(shCall.args[0].contains('ruff check'))
        assertTrue(shCall.args[0].contains('ruff format'))
    }
    
    @Test
    void testRunRuffLintingFailure() {
        def config = [
            ruffConfig: '.ruff.toml'
        ]
        
        // Mock failed ruff execution
        helper.registerAllowedMethod('sh', [Map.class], { args ->
            if (args.returnStatus) {
                return 1
            }
            return ''
        })
        
        // Should throw an error
        try {
            steps.runRuffLinting(config)
            fail('Expected error to be thrown')
        } catch (Exception e) {
            assertTrue(e.getMessage().contains('Ruff linting failed'))
        }
    }
    
    @Test
    void testRunUnitTests() {
        def config = [
            coverageCommand: 'python -m pytest --cov=. --cov-report=xml'
        ]
        
        steps.runUnitTests(config)
        
        // Verify shell command was executed
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
        
        // Verify publishHTML was called
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'publishHTML'
        }.size())
        
        def publishCall = helper.callStack.find { call ->
            call.methodName == 'publishHTML'
        }
        
        assertNotNull(publishCall)
        assertEquals('htmlcov', publishCall.args[0].reportDir)
        assertEquals('Coverage Report', publishCall.args[0].reportName)
    }
    
    @Test
    void testRunMutationTests() {
        def config = [:]
        
        steps.runMutationTests(config)
        
        // Verify shell command was executed
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
        
        // Verify publishHTML was called
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'publishHTML'
        }.size())
        
        def publishCall = helper.callStack.find { call ->
            call.methodName == 'publishHTML'
        }
        
        assertNotNull(publishCall)
        assertEquals('mutation_report.html', publishCall.args[0].reportFiles)
        assertEquals('Mutation Test Report', publishCall.args[0].reportName)
    }
    
    @Test
    void testCleanup() {
        def config = [:]
        
        steps.cleanup(config)
        
        // Verify shell command was executed
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'sh'
        }.size())
        
        def shCall = helper.callStack.find { call ->
            call.methodName == 'sh'
        }
        
        assertNotNull(shCall)
        assertTrue(shCall.args[0].contains('rm -rf venv/'))
        assertTrue(shCall.args[0].contains('rm -rf build/'))
        assertTrue(shCall.args[0].contains('rm -rf .pytest_cache/'))
    }
    
    @Test
    void testNotifySuccess() {
        def config = [:]
        
        steps.notifySuccess(config)
        
        // Verify echo was called
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'echo'
        }.size())
        
        def echoCall = helper.callStack.find { call ->
            call.methodName == 'echo'
        }
        
        assertNotNull(echoCall)
        assertTrue(echoCall.args[0].contains('Pipeline completed successfully'))
    }
    
    @Test
    void testNotifyFailure() {
        def config = [:]
        
        steps.notifyFailure(config)
        
        // Verify echo was called
        assertEquals(1, helper.callStack.findAll { call ->
            call.methodName == 'echo'
        }.size())
        
        def echoCall = helper.callStack.find { call ->
            call.methodName == 'echo'
        }
        
        assertNotNull(echoCall)
        assertTrue(echoCall.args[0].contains('Pipeline failed'))
    }
} 