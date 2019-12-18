package ktlintrules

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class MethodsOrderingRule : Rule(ERROR_ID) {
    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {

        if (node.elementType == KtStubElementTypes.CLASS) {
            (node.psi as? KtClass)?.body?.declarations?.filterIsInstance<KtNamedFunction>()?.run {
                val originalMappedModifiers = map { function ->
                    resolveFunctionModifier(function)
                }
                if (originalMappedModifiers != originalMappedModifiers.sortedBy { it }) {
                    emit(node.startOffset, ERROR_MESSAGE, false)
                }
            }
        }
    }

    private fun resolveFunctionModifier(function: KtNamedFunction): Int {
        return with(function) {
            when {
                isPublic -> Modifiers.PUBLIC
                hasModifier(OVERRIDE_KEYWORD) -> Modifiers.OVERRIDE
                hasModifier(INTERNAL_KEYWORD) -> Modifiers.INTERNAL
                hasModifier(PROTECTED_KEYWORD) -> Modifiers.PROTECTED
                hasModifier(OPEN_KEYWORD) -> Modifiers.OPEN
                hasModifier(ABSTRACT_KEYWORD) -> Modifiers.ABSTRACT
                hasModifier(PRIVATE_KEYWORD) -> Modifiers.PRIVATE
                hasModifier(INLINE_KEYWORD) -> Modifiers.INLINE
                else -> Modifiers.PUBLIC
            }
        }
    }

    companion object {
        const val ERROR_ID = "methods-ordering-rule"
        const val ERROR_MESSAGE: String = "Methods are in the wrong order, the right one, should be:\n" +
            "PUBLIC\n" +
            "INTERNAL\n" +
            "OVERRIDE\n" +
            "PROTECTED\n" +
            "OPEN\n" +
            "ABSTRACT\n" +
            "PRIVATE\n" +
            "INLINE\n"
    }

    object Modifiers {
        const val PUBLIC = 0
        const val INTERNAL = 1
        const val OVERRIDE = 2
        const val PROTECTED = 3
        const val OPEN = 4
        const val ABSTRACT = 5
        const val PRIVATE = 6
        const val INLINE = 7
    }
}