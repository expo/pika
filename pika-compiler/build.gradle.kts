import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.buildconfig)
  alias(libs.plugins.gradle.java.test.fixtures)
  alias(libs.plugins.gradle.idea)
  alias(libs.plugins.vanniktech.mavenPublish)
}

val kotlinVersionStr: String = libs.versions.kotlin.asProvider().get()
val kotlinMinorVersion = kotlinVersionStr.split(".").take(2).joinToString(".")

// Each compatibility axis picks the newest variant the current Kotlin release satisfies.

// Main sources: compiler APIs the plugin uses.
val mainSourceDir = when {
  kotlinVersionStr >= "2.3.20" -> "src-2.3.20+"
  kotlinVersionStr >= "2.3.0" -> "src-2.3"
  else -> "src-2.2"  // 2.2.x has DirectDeclarationsAccess but old getContainingClassSymbol location
}

// GenerateTests.kt: the JUnit5 generator DSL moved packages in 2.3.0.
val generatorSourceDir = if (kotlinMinorVersion >= "2.3") {
  "test-fixtures-2.3.0"
} else {
  "test-fixtures-2.2.0"
}

// AbstractJvmBoxTest.kt: 2.4.20 dropped AbstractFirBlackBoxCodegenTestBase.
val runnerSourceDir = if (kotlinVersionStr >= "2.4.20") {
  "test-fixtures-runner-2.4.20"
} else {
  "test-fixtures-runner-2.2.0"
}

// Golden dumps: the FIR/IR text changes between compiler releases. `testData` is the 2.3.x baseline.
val testDataDir = when {
  kotlinVersionStr >= "2.4.0" -> "testData-2.4.0"
  kotlinMinorVersion >= "2.3" -> "testData"
  kotlinVersionStr >= "2.2.20" -> "testData-2.2.20"
  else -> "testData-2.2.0"
}

sourceSets {
  main {
    java.setSrcDirs(listOf("src"))
    resources.setSrcDirs(listOf("resources"))
    kotlin.srcDir(mainSourceDir)
  }
  testFixtures {
    java.setSrcDirs(listOf("test-fixtures"))
    kotlin.srcDir(generatorSourceDir)
    kotlin.srcDir(runnerSourceDir)
  }
  test {
    java.setSrcDirs(listOf("test", "test-gen"))
    resources.setSrcDirs(listOf(testDataDir))
  }
}

idea {
  module.generatedSourceDirs.add(projectDir.resolve("test-gen"))
}

val annotationsRuntimeClasspath: Configuration by configurations.creating { isTransitive = false }
val testArtifacts: Configuration by configurations.creating

dependencies {
  compileOnly(libs.kotlin.compiler)

  testFixturesApi(libs.kotlin.test.junit5)
  testFixturesApi(libs.kotlin.test.framework)
  testFixturesApi(libs.kotlin.compiler)
  testFixturesRuntimeOnly(libs.junit)

  annotationsRuntimeClasspath("io.github.expo.pika:pika-api")

  // Dependencies required to run the internal test framework.
  testArtifacts(libs.kotlin.stdlib)
  testArtifacts(libs.kotlin.stdlib.jdk8)
  testArtifacts(libs.kotlin.reflect)
  testArtifacts(libs.kotlin.test)
  testArtifacts(libs.kotlin.script.runtime)
  testArtifacts(libs.kotlin.annotations.jvm)
}

buildConfig {
  useKotlinOutput {
    internalVisibility = true
  }

  packageName(group.toString())
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
}

tasks.test {
  val annotationsFiles = files(annotationsRuntimeClasspath)

  useJUnitPlatform()
  workingDir = rootDir
  inputs.files(annotationsFiles)

  jvmArgumentProviders.add(CommandLineArgumentProvider {
    listOf("-DannotationsRuntime.classpath=${annotationsFiles.asPath}")
  })

  // Properties required to run the internal test framework.
  setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
  setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
  setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
  setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
  setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
  setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")

  systemProperty("idea.ignore.disabled.plugins", "true")
  systemProperty("idea.home.path", rootDir)

  // Regenerate the golden .txt dumps in the active testData tier instead of asserting against them:
  //   ./gradlew :pika-compiler:test -PkotlinVersion=<version> -PupdateTestData
  if (providers.gradleProperty("updateTestData").isPresent) {
    systemProperty("kotlin.test.update.test.data", "true")
    outputs.upToDateWhen { false }
  }
}

kotlin {
  jvmToolchain(17)
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_11)
    optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    // DirectDeclarationsAccess is required for accessing FIR declarations directly in 2.2.x+
    // The explicit @OptIn annotations in the code use the local FirCompat annotation for source compatibility
    optIn.add("org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess")
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

val generateTests by tasks.registering(JavaExec::class) {
  inputs.dir(layout.projectDirectory.dir(testDataDir))
    .withPropertyName("testData")
    .withPathSensitivity(PathSensitivity.RELATIVE)
  outputs.dir(layout.projectDirectory.dir("test-gen"))
    .withPropertyName("generatedTests")

  classpath = sourceSets.testFixtures.get().runtimeClasspath
  mainClass.set("io.github.expo.pika.GenerateTestsKt")
  workingDir = rootDir
  args("pika-compiler/test-gen", "pika-compiler/$testDataDir")
}

tasks.compileTestKotlin {
  dependsOn(generateTests)
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
  val path = testArtifacts.files
    .find { """$jarName-\d.*""".toRegex().matches(it.name) }
    ?.absolutePath
    ?: return
  systemProperty(propName, path)
}
