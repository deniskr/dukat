package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

fun ParameterValueDeclaration.asIntersection(): TypeDeclaration? {
    if ((this is TypeDeclaration) && (value == "@@Intersection")) {
        return this
    }

    return null
}


fun ParameterValueDeclaration.asSelfReference() : TypeDeclaration? {
    if ((this is TypeDeclaration) && (value == "@@SELF_REFERENCE")) {
        return this
    }

    return null
}
