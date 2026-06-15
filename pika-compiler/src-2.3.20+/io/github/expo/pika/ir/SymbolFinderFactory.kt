package io.github.expo.pika.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext

fun createSymbolFinder(context: IrPluginContext): SymbolFinder {
  val finder = context.finderForBuiltins()
  return SymbolFinder(
    classResolver = { finder.findClass(it) },
    functionResolver = { finder.findFunctions(it) }
  )
}
