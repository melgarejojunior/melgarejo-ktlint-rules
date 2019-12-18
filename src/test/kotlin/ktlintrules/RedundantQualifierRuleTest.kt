package ktlintrules

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions
import org.junit.Test

class RedundantQualifierRuleTest {
    @Test
    fun redundantQualifierRule() {
        Assertions.assertThat(RedundantQualifierRule().lint("""
            import com.example.application.ClassExample
            
            class Foo : DummyInterface {
                val variable1 = 1
                private val variable2 = 2
                private var example: com.example.application.ClassExample? = null
            }
        """.trimIndent()
        )).containsExactly(
            LintError(3, 1, RedundantQualifierRule.RULE_ID,
                RedundantQualifierRule.RULE_DESCRIPTION)
        )
    }

    @Test
    fun noRedundantQualifierRule() {
        Assertions.assertThat(RedundantQualifierRule().lint("""
            import com.example.application.ClassExample
            
            class Foo : DummyInterface {
                val variable1 = 1
                private val variable2 = 2
                private var example: ClassExample? = null
            }
        """.trimIndent()
        )).isEmpty()
    }
}