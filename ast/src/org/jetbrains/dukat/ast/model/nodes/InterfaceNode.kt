package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class InterfaceNode(
        override val name: String,
        val members: List<MemberNode>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageNode>,
        val annotations: MutableList<AnnotationNode>,

        var owner: DocumentRootNode?,
        val generated: Boolean,
        val uid: String
) : ClassLikeDeclaration, ClassLikeNode {
    companion object {
        val AUTOGENERATED = "AUTOGENERATED"
    }
}