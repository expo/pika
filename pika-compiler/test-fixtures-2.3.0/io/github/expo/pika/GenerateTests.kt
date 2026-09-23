package io.github.expo.pika

import io.github.expo.pika.runners.AbstractJvmBoxTest
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5

fun main(args: Array<String>) {
  generateTestGroupSuiteWithJUnit5 {
    testGroup(testsRoot = args[0], testDataRoot = args[1]) {
      testClass<AbstractJvmBoxTest> {
        model("box")
      }
    }
  }
}
