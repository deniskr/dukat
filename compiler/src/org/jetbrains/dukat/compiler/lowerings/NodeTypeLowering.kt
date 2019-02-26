package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


interface NodeTypeLowering : NodeLowering {

    fun lowerStringIdentificator(identificator: String): String {
        return identificator;
    }

    fun lowerMethodNode(ownerContext: NodeOwner<MethodNode>): MethodNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerStringIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(NodeOwner(parameter, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) })
                },
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    fun lowerPropertyNode(ownerContext: NodeOwner<PropertyNode>): PropertyNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerStringIdentificator(declaration.name),
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext)),
                typeParameters = declaration.typeParameters.map {
                    typeParameter -> lowerTypeParameter(NodeOwner(typeParameter, ownerContext))
                }
        )
    }

    override fun lowerMemberNode(ownerContext: NodeOwner<MemberNode>): MemberNode {
        val declaration = ownerContext.node
        return when (declaration) {
            is MethodNode -> lowerMethodNode(NodeOwner(declaration, ownerContext))
            is PropertyNode -> lowerPropertyNode(NodeOwner(declaration, ownerContext))
            is ConstructorNode -> lowerConstructorNode(NodeOwner(declaration, ownerContext))
            else -> {
                println("[WARN] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionNode(ownerContext: NodeOwner<FunctionNode>): FunctionNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerStringIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(NodeOwner(parameter, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) })
                },
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerTypeParameter(ownerContext: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(
            name = lowerStringIdentificator(declaration.name),
            constraints = declaration.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) }
        )
    }

    override fun lowerUnionTypeDeclaration(ownerContext: NodeOwner<UnionTypeDeclaration>): UnionTypeDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(NodeOwner(param, ownerContext))
        })
    }

    override fun lowerTupleDeclaration(ownerContext: NodeOwner<TupleDeclaration>): ParameterValueDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(NodeOwner(param, ownerContext))
        })
    }

    override fun lowerUnionTypeNode(ownerContext: NodeOwner<UnionTypeNode>): UnionTypeNode {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(NodeOwner(param, ownerContext))
        })
    }

    override fun lowerIntersectionTypeDeclaration(ownerContext: NodeOwner<IntersectionTypeDeclaration>): IntersectionTypeDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(NodeOwner(param, ownerContext))
        })
    }

    override fun lowerTypeNode(ownerContext: NodeOwner<TypeNode>): TypeNode {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(NodeOwner(param, ownerContext))
        })
    }

    override fun lowerFunctionNode(ownerContext: NodeOwner<FunctionTypeNode>): FunctionTypeNode {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(NodeOwner(param, ownerContext)) },
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerParameterDeclaration(ownerContext: NodeOwner<ParameterDeclaration>): ParameterDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerStringIdentificator(declaration.name),
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerVariableNode(ownerContext: NodeOwner<VariableNode>): VariableNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerStringIdentificator(declaration.name),
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    fun lowerHeritageNode(ownerContext: NodeOwner<HeritageNode>): HeritageNode {
        val heritageClause = ownerContext.node
        val typeArguments = heritageClause.typeArguments.map {
            // TODO: obviously very clumsy place
            val typeNode = TypeNode(it.value, emptyList())
            val lowerParameterDeclaration = lowerParameterValue(NodeOwner(typeNode, ownerContext)) as TypeNode
            lowerParameterDeclaration.value as IdentifierNode
        }
        return heritageClause.copy(typeArguments = typeArguments)
    }


    override fun lowerInterfaceNode(ownerContext: NodeOwner<InterfaceNode>): InterfaceNode {

        val declaration = ownerContext.node

        return declaration.copy(
                name = lowerStringIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(NodeOwner(heritageClause, ownerContext))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(NodeOwner(typeParameter, ownerContext))
                }
        )
    }

    override fun lowerTypeAliasDeclaration(ownerContext: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(typeReference = lowerParameterValue(NodeOwner(declaration.typeReference, ownerContext)))
    }

    fun lowerConstructorNode(ownerContext: NodeOwner<ConstructorNode>): ConstructorNode {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(NodeOwner(parameter, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) })
                }
        )
    }

    override fun lowerObjectNode(ownerContext: NodeOwner<ObjectNode>): ObjectNode {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) }
        )

    }

    override fun lowerClassNode(ownerContext: NodeOwner<ClassNode>): ClassNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerStringIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(NodeOwner(heritageClause, ownerContext))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(NodeOwner(typeParameter, ownerContext))
                }
        )
    }
}