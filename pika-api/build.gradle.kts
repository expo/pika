import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.binary.compatibility.validator)
  alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.expo.pika"
version = libs.versions.pika.get()

kotlin {
  explicitApi()
  jvmToolchain(17)
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_11)
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)

  if (project.findProperty("signingInMemoryKey") != null) {
    signAllPublications()
  }

  configure(KotlinJvm(JavadocJar.Empty(), SourcesJar.Sources()))

  pom {
    name = "pika-api"
    description = "Pika - Kotlin compiler plugin for generating type information in the compile time"
    inceptionYear = "2025"
    url = "https://github.com/expo/pika"
    licenses {
      license {
        name = "The MIT License"
        url = "https://opensource.org/license/mit"
        distribution = "https://opensource.org/license/mit"
      }
    }
    developers {
      developer {
        id = "expo"
        name = "Expo"
        url = "https://github.com/expo"
      }
    }
    scm {
      url = "https://github.com/expo/pika"
      connection = "scm:git:git://github.com/expo/pika.git"
      developerConnection = "scm:git:ssh://github.com/expo/pika.git"
    }
  }
}
