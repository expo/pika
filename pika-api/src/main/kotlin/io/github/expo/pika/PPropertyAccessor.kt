@file:Suppress("FunctionName")

package io.github.expo.pika

@PublishedApi
internal interface PPropertyAccessor {
  fun `__pika$PropertyGet`(instance: Any?, index: Int): Any?
  fun `__pika$PropertySet`(instance: Any?, index: Int, value: Any?)
}
