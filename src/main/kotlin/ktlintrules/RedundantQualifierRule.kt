package ktlintrules

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class RedundantQualifierRule : Rule(RULE_ID) {
    private val importPaths = mutableListOf<String>()

    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        when (node.elementType) {
            KtStubElementTypes.CLASS -> resolveRuleForClass(node, emit)
            KtStubElementTypes.IMPORT_DIRECTIVE -> (node.psi as? KtImportDirective)?.importPath?.fqName?.asString()?.let(importPaths::add)
        }
    }

    private fun resolveRuleForClass(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        (node.psi as? KtClass)?.body?.declarations?.filterIsInstance<KtProperty>()?.forEach { property ->
            importPaths.forEach {
                if (property.text.contains(it)) emit(node.startOffset, RULE_DESCRIPTION, false)
            }
        }
    }

    companion object {
        const val RULE_ID = "redundant-qualifier-name"
        const val RULE_DESCRIPTION: String = "Qualifier name is redundant. Remove import or remove qualifier"

    }
}