package io.github.expo.pika.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext

fun createSymbolFinder(context: IrPluginContext) = SymbolFinder(
  classResolver = { context.referenceClass(it) },
  functionResolver = { context.referenceFunctions(it) }
)
