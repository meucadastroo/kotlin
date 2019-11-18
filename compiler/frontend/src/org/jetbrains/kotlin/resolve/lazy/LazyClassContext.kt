/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

package org.jetbrains.kotlin.resolve.lazy

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProviderFactory
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.WrappedTypeFactory
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker

interface LazyClassContext {
    val declarationScopeProvider: DeclarationScopeProvider

    val storageManager: StorageManager
    val trace: BindingTrace
    val moduleDescriptor: ModuleDescriptor
    val descriptorResolver: DescriptorResolver
    val functionDescriptorResolver: FunctionDescriptorResolver
    val typeResolver: TypeResolver
    val declarationProviderFactory: DeclarationProviderFactory
    val annotationResolver: AnnotationResolver
    val lookupTracker: LookupTracker
    val supertypeLoopChecker: SupertypeLoopChecker
    val languageVersionSettings: LanguageVersionSettings
    val syntheticResolveExtension: SyntheticResolveExtension
    val delegationFilter: DelegationFilter
    val wrappedTypeFactory: WrappedTypeFactory
    val kotlinTypeChecker: NewKotlinTypeChecker
    val samConversionResolver: SamConversionResolver
}
