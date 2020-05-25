package org.jetbrains.kotlin.ir.symbols

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.IdSignature

abstract class IrDelegatingSymbol<S : IrBindableSymbol<D, B>, B : IrSymbolOwner, D : DeclarationDescriptor>(var delegate: S) :
    IrBindableSymbol<D, B> {
    override val owner: B get() = delegate.owner
    override val descriptor: D get() = delegate.descriptor
    override val isBound: Boolean get() = delegate.isBound
    override val isPublicApi: Boolean
        get() = delegate.isPublicApi

    override val signature: IdSignature
        get() = delegate.signature

    override fun bind(owner: B) = delegate.bind(owner)
    override fun hashCode() = delegate.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (delegate === other) return true
        return false
    }
}

interface IrDelegatingClassSymbol : IrClassSymbol {
    var delegate: IrClassSymbol
}
class IrDelegatingClassSymbolImpl(delegate: IrClassSymbol) :
    IrDelegatingClassSymbol, IrDelegatingSymbol<IrClassSymbol, IrClass, ClassDescriptor>(delegate)

interface IrDelegatingEnumEntrySymbol : IrEnumEntrySymbol {
    var delegate: IrEnumEntrySymbol
}
class IrDelegatingEnumEntrySymbolImpl(delegate: IrEnumEntrySymbol) :
    IrDelegatingEnumEntrySymbol, IrDelegatingSymbol<IrEnumEntrySymbol, IrEnumEntry, ClassDescriptor>(delegate)

interface IrDelegatingSimpleFunctionSymbol : IrSimpleFunctionSymbol {
    var delegate: IrSimpleFunctionSymbol
}
class IrDelegatingSimpleFunctionSymbolImpl(delegate: IrSimpleFunctionSymbol) :
    IrDelegatingSimpleFunctionSymbol, IrDelegatingSymbol<IrSimpleFunctionSymbol, IrSimpleFunction, FunctionDescriptor>(delegate)

interface IrDelegatingConstructorSymbol : IrConstructorSymbol {
    var delegate: IrConstructorSymbol
}
class IrDelegatingConstructorSymbolImpl(delegate: IrConstructorSymbol) :
    IrDelegatingConstructorSymbol, IrDelegatingSymbol<IrConstructorSymbol, IrConstructor, ClassConstructorDescriptor>(delegate)

interface IrDelegatingPropertySymbol : IrPropertySymbol {
    var delegate: IrPropertySymbol
}
class IrDelegatingPropertySymbolImpl(delegate: IrPropertySymbol) :
    IrDelegatingPropertySymbol, IrDelegatingSymbol<IrPropertySymbol, IrProperty, PropertyDescriptor>(delegate)

interface IrDelegatingTypeAliasSymbol : IrTypeAliasSymbol {
    var delegate: IrTypeAliasSymbol
}
class IrDelegatingTypeAliasSymbolImpl(delegate: IrTypeAliasSymbol) :
    IrDelegatingTypeAliasSymbol, IrDelegatingSymbol<IrTypeAliasSymbol, IrTypeAlias, TypeAliasDescriptor>(delegate)

fun wrapInDelegatedSymbol(delegate: IrSymbol) = when(delegate) {
    is IrClassSymbol -> IrDelegatingClassSymbolImpl(delegate)
    is IrEnumEntrySymbol -> IrDelegatingEnumEntrySymbolImpl(delegate)
    is IrSimpleFunctionSymbol -> IrDelegatingSimpleFunctionSymbolImpl(delegate)
    is IrConstructorSymbol -> IrDelegatingConstructorSymbolImpl(delegate)
    is IrPropertySymbol -> IrDelegatingPropertySymbolImpl(delegate)
    is IrTypeAliasSymbol -> IrDelegatingTypeAliasSymbolImpl(delegate)
    else -> error("Unexpected symbol to delegate to: $delegate")
}