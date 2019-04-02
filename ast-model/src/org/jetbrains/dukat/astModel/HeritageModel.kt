package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.Declaration

data class HeritageModel(
        var value: TypeNode,
        val delegateTo: DelegationModel?
) : Declaration