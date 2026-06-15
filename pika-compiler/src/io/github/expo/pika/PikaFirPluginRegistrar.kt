package io.github.expo.pika

import io.github.expo.pika.fir.FirIntrospectablePredicateMatcher
import io.github.expo.pika.fir.IntrospectableDeclarationGenerator
import io.github.expo.pika.fir.IntrospectableSupertypeGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class PikaFirPluginRegistrar(
  private val extraAnnotationFqNames: List<String>,
) : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::IntrospectableDeclarationGenerator
    +::IntrospectableSupertypeGenerator
    +FirIntrospectablePredicateMatcher.getFactory(extraAnnotationFqNames)
  }
}
