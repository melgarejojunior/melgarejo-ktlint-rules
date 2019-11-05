package ktlintrules

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class MethodsOrderingRule : Rule("methods-ordering-rule") {
    override fun visit(
            node: ASTNode,
            autoCorrect: Boolean,
            emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        if (node.elementType == KtStubElementTypes.CLASS) {
            (node.psi as? KtClass)?.body?.declarations?.filterIsInstance<KtNamedFunction>()?.run {
                val originalMappedModifiers = map { function ->
                    modifiersOrder(resolveFunctionModifier(function))
                }
                if (originalMappedModifiers != originalMappedModifiers.sorted()) {
                    emit(node.startOffset, "Methods are in the wrong order", false)
                }
            }
        }
    }

    private fun resolveFunctionModifier(function: KtNamedFunction): String? {
        return function.modifierList?.run {
            if (children.isNotEmpty()) children.firstOrNull { it !is KtAnnotationEntry }?.text
            else firstChild?.text
        }
    }

    private fun modifiersOrder(text: String?): Int {
        return when (text) {
            Modifiers.OVERRIDE -> 0
            Modifiers.PUBLIC -> 1
            Modifiers.PROTECTED -> 2
            Modifiers.OPEN -> 3
            Modifiers.ABSTRACT -> 4
            Modifiers.PRIVATE -> 5
            Modifiers.INLINE -> 6
            else -> Int.MAX_VALUE
        }
    }

    object Modifiers {
        val PUBLIC = null
        const val OVERRIDE = "override"
        const val PROTECTED = "protected"
        const val OPEN = "open"
        const val ABSTRACT = "abstract"
        const val PRIVATE = "private"
        const val INLINE = "inline"
    }
}