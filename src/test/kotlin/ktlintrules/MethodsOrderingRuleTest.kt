package ktlintrules

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
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
                LintError(1, 1, MethodsOrderingRule.RULE_ID,
                        MethodsOrderingRule.RULE_DESCRIPTION)
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
    fun annotatedMethodsOrderRuleInternalAfterPublic() {
        Assertions.assertThat(
                MethodsOrderingRule().lint("""
           class LogInTest {

    // Set the main coroutines dispatcher for unit testing.
    // We are setting the above-defined testDispatcher as the Main thread dispatcher.
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var logIn: LogIn

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
    
    @Before
    internal fun setup() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            logIn = LogIn(returnMockedRepository())
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

    @Test
    fun internalMethodTestRule() {
        Assertions.assertThat(
                MethodsOrderingRule().lint("""
           class TrainingDashboardViewModel @Inject constructor() : BaseViewModel() {

    val date: LiveData<Date> get() = dateLiveData
    val datePicked: LiveData<Event<Calendar>> get() = datePickedLiveData

    private val dateLiveData: MutableLiveData<Date> = MutableLiveData()
    private val datePickedLiveData: MutableLiveData<Event<Calendar>> = MutableLiveData()

    private var localDate: Date = Date()
    private var localDatePicked: Calendar = Calendar.getInstance()

    internal fun onNextDayClicked() {
        dateLiveData.value = localDate.addDays(1)
    }

    internal fun onPreviousDayClicked() {
        dateLiveData.value = localDate.backDays(1)
    }

    internal fun onDiaryClicked() {
        datePickedLiveData.value = Event(localDatePicked)
    }

    internal fun onDateChosen() {
        localDate = localDatePicked.time
        dateLiveData.value = localDate
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        dateLiveData.value = localDate
    }
}
        """.trimIndent()
                )
        ).isEmpty()
    }

    @Test
    fun publicInternalProtectedTestRule() {
        Assertions.assertThat(
                MethodsOrderingRule().lint("""
           class PublicInternalProtected  {

    fun onNextDayClicked() {
        dateLiveData.value = localDate.addDays(1)
    }

    internal fun onPreviousDayClicked() {
        dateLiveData.value = localDate.backDays(1)
    }

    protected fun onDiaryClicked() {
        datePickedLiveData.value = Event(localDatePicked)
    }
}
        """.trimIndent()
                )
        ).isEmpty()
    }

    @Test
    fun protectedInternalTestRule() {
        Assertions.assertThat(
                MethodsOrderingRule().lint("""
           class ProtectedInternal  {

    protected fun onNextDayClicked() {
        dateLiveData.value = localDate.addDays(1)
    }

    internal fun onPreviousDayClicked() {
        dateLiveData.value = localDate.backDays(1)
    }
}
        """.trimIndent()
                )
        ).containsExactly(
                LintError(1, 12, MethodsOrderingRule.RULE_ID,
                        MethodsOrderingRule.RULE_DESCRIPTION)
        )
    }
}