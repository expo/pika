plugins {
  alias(libs.plugins.kotlin.jvm)
  id("io.github.expo.pika")
  application
}

application {
  mainClass.set("sample.MainKt")
}

pika {
  introspectableAnnotation("sample.OptimizedRecord")
}
