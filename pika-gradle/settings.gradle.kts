rootProject.name = "pika-gradle"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.toml"))
      // Pin to the oldest supported Kotlin version so the compiled metadata
      // is readable by all consumers (2.2.0+).
      version("kotlin", "2.2.0")
    }
  }
}

includeBuild("..") {
  dependencySubstitution {
    substitute(module("io.github.expo.pika:pika-compiler"))
      .using(project(":pika-compiler"))
  }
}

includeBuild("../pika-api") {
  dependencySubstitution {
    substitute(module("io.github.expo.pika:pika-api"))
      .using(project(":"))
  }
}
