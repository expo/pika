package io.github.expo.pika

import io.github.expo.pika.runners.AbstractJvmBoxTest
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5

fun main() {
  generateTestGroupSuiteWithJUnit5 {
    testGroup(testDataRoot = "pika-compiler/testData-2.2.20", testsRoot = "pika-compiler/test-gen") {
      testClass<AbstractJvmBoxTest> {
        model("box")
      }
    }
  }
}
