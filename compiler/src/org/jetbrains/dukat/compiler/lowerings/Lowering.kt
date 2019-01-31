package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DynamicTypeNode
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface Lowering {
    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration
    fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration
    fun lowerClassNode(declaration: ClassNode): ClassNode
    fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration
    fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration
    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration
    fun lowerObjectLiteral(declaration: ObjectLiteralDeclaration): ObjectLiteralDeclaration
    fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration): UnionTypeDeclaration
    fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration
    fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration)
            is ObjectLiteralDeclaration -> lowerObjectLiteral(declaration)
            is UnionTypeDeclaration -> lowerUnionTypeDeclation(declaration)
            is DynamicTypeNode -> declaration
            else -> throw Exception("can not lowerParameterValue unknown ParameterValueDeclaration subtype:  ${this} : ${declaration}")
        }
    }


    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is ClassNode -> lowerClassNode(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration)
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is DocumentRootDeclaration -> lowerDocumentRoot(declaration)
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(declaration)
            else -> declaration.duplicate()
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerDocumentRoot(documenRoot: DocumentRootDeclaration): DocumentRootDeclaration {
        return documenRoot.copy(declarations = lowerTopLevelDeclarations(documenRoot.declarations))
    }
}
