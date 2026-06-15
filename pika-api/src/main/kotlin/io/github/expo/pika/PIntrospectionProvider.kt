package io.github.expo.pika

public interface PIntrospectionProvider {
  public fun getIntrospectionData(): PIntrospectionData<*>
}
