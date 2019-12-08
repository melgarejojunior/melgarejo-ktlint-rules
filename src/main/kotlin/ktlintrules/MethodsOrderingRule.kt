package ktlintrules

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeFirstWord

class MethodsOrderingRule : Rule("methods-ordering-rule") {
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
                    emit(node.startOffset, "Methods are in the wrong order", false)
                }
            }
        }
    }

    private fun resolveFunctionModifier(function: KtNamedFunction): Int {
        return with(function) {
            when {
                isPublic -> Modifiers.PUBLIC
                hasModifier(KtTokens.OVERRIDE_KEYWORD) -> Modifiers.OVERRIDE
                hasModifier(KtTokens.INTERNAL_KEYWORD) -> Modifiers.INTERNAL
                hasModifier(KtTokens.PROTECTED_KEYWORD) -> Modifiers.PROTECTED
                hasModifier(KtTokens.OPEN_KEYWORD) -> Modifiers.OPEN
                hasModifier(KtTokens.ABSTRACT_KEYWORD) -> Modifiers.ABSTRACT
                hasModifier(KtTokens.PRIVATE_KEYWORD) -> Modifiers.PRIVATE
                hasModifier(KtTokens.INLINE_KEYWORD) -> Modifiers.INLINE
                else -> Modifiers.PUBLIC
            }
        }
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