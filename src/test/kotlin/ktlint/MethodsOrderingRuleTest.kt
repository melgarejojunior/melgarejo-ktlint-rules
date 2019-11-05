package ktlint

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
import ktlintrules.MethodsOrderingRule
import org.assertj.core.api.Assertions
import org.junit.Test

class MethodsOrderingRuleTest {
    @Test
    fun wrongMethodsOrderRule() {
        Assertions.assertThat(MethodsOrderingRule().lint("""
            class Foo : DummyInterface {
                val variable1 = 1
                private val variable2 = 2
                
                private fun a(): Int {
                    return variable1*100
                }
                
                fun b() {
                    return variable2
                }
                override fun c() {
                    println("teste")
                }
            }
        """.trimIndent()
        )).containsExactly(
                LintError(1, 1, "methods-ordering-rule",
                        "Methods are in the wrong order")
        )
    }

    @Test
    fun correctMethodsOrderRule() {
        Assertions.assertThat(
                MethodsOrderingRule().lint("""
            class Foo : DummyInterface {
                val variable1 = 1
                private val variable2 = 2
                override fun c() {
                    println("teste")
                }
                
                fun b() {
                    return variable2
                }
               
                private fun a(): Int {
                    return variable1*100
                }
            }
        """.trimIndent()
                )
        ).isEmpty()
    }

    @Test
    fun annotatedMethodsOrderRule() {
        Assertions.assertThat(
                MethodsOrderingRule().lint("""
           class LogInTest {

    // Set the main coroutines dispatcher for unit testing.
    // We are setting the above-defined testDispatcher as the Main thread dispatcher.
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var logIn: LogIn

    @Before
    fun setup() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            logIn = LogIn(returnMockedRepository())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun executeSuccessful() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            runBlocking {
                val result = logIn.execute(mockedUser.email ?: "", password, null)
                assert(result == mockedUser)
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun executeFail() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            runBlocking {
                try {
                    val result = logIn.execute("", password, null)
                    assert(result == mockedUser)
                } catch (e: Throwable) {
                    assert(e is InvalidFieldsException)
                }
            }
        }
    }

    private suspend fun returnMockedRepository(): UserRepository {
        val mockRepository = mock(UserRepository::class.java)
        `when`(mockRepository.signIn(mockedUser.email ?: "", password, null)).thenReturn(mockedUser)
        return mockRepository
    }
}
        """.trimIndent()
                )
        ).isEmpty()
    }
}