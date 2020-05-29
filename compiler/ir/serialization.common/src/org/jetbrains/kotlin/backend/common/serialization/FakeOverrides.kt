/*
 * Copyright 2010-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.backend.common.serialization

import org.jetbrains.kotlin.backend.common.serialization.signature.IdSignatureSerializer
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrOverridableMember
import org.jetbrains.kotlin.ir.declarations.impl.IrFakeOverrideFunctionImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrFakeOverridePropertyImpl
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.descriptors.WrappedPropertyDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedSimpleFunctionDescriptor
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.SymbolTable
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid

interface PlatformFakeOverrideClassFilter {
    fun constructFakeOverrides(clazz: IrClass): Boolean = true
}

object DefaultFakeOverrideClassFilter : PlatformFakeOverrideClassFilter

object FakeOverrideControl {
    // If set to true: all fake overrides go to klib serialized IR.
    // If set to false: eligible fake overrides are not serialized.
    val serializeFakeOverrides: Boolean = false

    // If set to true: fake overrides are deserialized from klib serialized IR.
    // If set to false: eligible fake overrides are constructed within IR linker.
    val deserializeFakeOverrides: Boolean = false
}

class FakeOverrideBuilderImpl(
    val symbolTable: SymbolTable,
    val signaturer: IdSignatureSerializer,
    val irBuiltIns: IrBuiltIns,
    private val platformSpecificClassFilter: PlatformFakeOverrideClassFilter = DefaultFakeOverrideClassFilter
) : FakeOverrideBuilder, FakeOverrideBuilderStrategy {
    private val haveFakeOverrides = mutableSetOf<IrClass>()
    override val propertyOverriddenSymbols = mutableMapOf<IrOverridableMember, List<IrSymbol>>()
    private val irOverridingUtil = IrOverridingUtil(irBuiltIns, this)

    override fun fakeOverrideMember(
        superType: IrType,
        member: IrOverridableMember,
        clazz: IrClass
    ): IrOverridableMember {
        require(superType is IrSimpleType) { "superType is $superType, expected IrSimpleType" }
        val classifier = superType.classifier
        require(classifier is IrClassSymbol) { "superType classifier is not IrClassSymbol: $classifier" }

        val typeParameters = classifier.owner.typeParameters.map { it.symbol }
        val typeArguments =
            superType.arguments.map { it as? IrSimpleType ?: error("Unexpected super type $it") }

        assert(typeParameters.size == typeArguments.size) {
            "typeParameters = $typeParameters size != typeArguments = $typeArguments size "
        }

        val substitutionMap = typeParameters.zip(typeArguments).toMap()
        val copier =
            DeepCopyIrTreeWithSymbolsForFakeOverrides(substitutionMap, superType, clazz)

        val deepCopyFakeOverride = copier.copy(member) as IrOverridableMember
        deepCopyFakeOverride.parent = clazz

        return deepCopyFakeOverride
    }

    fun buildFakeOverrideChainsForClass(clazz: IrClass) {
        if (haveFakeOverrides.contains(clazz)) return
        if (!platformSpecificClassFilter.constructFakeOverrides(clazz) || !clazz.symbol.isPublicApi) return

        val superTypes = clazz.superTypes

        val superClasses = superTypes.map {
            it.getClass() ?: error("Unexpected super type: $it")
        }

        superClasses.forEach {
            buildFakeOverrideChainsForClass(it)
            haveFakeOverrides.add(it)
        }

        irOverridingUtil.buildFakeOverridesForClass(clazz)
    }

    override fun buildFakeOverridesForClass(clazz: IrClass) = irOverridingUtil.buildFakeOverridesForClass(clazz)

    override fun linkFakeOverride(fakeOverride: IrOverridableMember) {
        when (fakeOverride) {
            is IrFakeOverrideFunctionImpl -> linkFunctionFakeOverride(fakeOverride)
            is IrFakeOverridePropertyImpl -> linkPropertyFakeOverride(fakeOverride)
            else -> error("Unexpected fake override: $fakeOverride")
        }
    }

    private fun linkFunctionFakeOverride(declaration: IrFakeOverrideFunctionImpl) {
        val signature = signaturer.composePublicIdSignature(declaration)

        symbolTable.declareSimpleFunctionFromLinker(WrappedSimpleFunctionDescriptor(), signature) {
            declaration.acquireSymbol(it)
            declaration
        }
    }

    private fun linkPropertyFakeOverride(declaration: IrFakeOverridePropertyImpl) {
        val signature = signaturer.composePublicIdSignature(declaration)

        symbolTable.declarePropertyFromLinker(WrappedPropertyDescriptor(), signature) {
            declaration.acquireSymbol(it)
            declaration
        }

        declaration.getter?.let {
            it.correspondingPropertySymbol = declaration.symbol
            linkFunctionFakeOverride(it as? IrFakeOverrideFunctionImpl
                ?: error("Unexpected fake override getter: $it")
            )
        }
        declaration.setter?.let {
            it.correspondingPropertySymbol = declaration.symbol
            linkFunctionFakeOverride(it as? IrFakeOverrideFunctionImpl
                ?: error("Unexpected fake override setter: $it")
            )
        }
    }

    fun provideFakeOverrides(module: IrModuleFragment) {
        module.acceptVoid(object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) {
                element.acceptChildrenVoid(this)
            }

            override fun visitClass(declaration: IrClass) {
                buildFakeOverrideChainsForClass(declaration)
                haveFakeOverrides.add(declaration)
                super.visitClass(declaration)
            }

            override fun visitFunction(declaration: IrFunction) {
                // Don't go for function local classes
            }
        })
    }
}
