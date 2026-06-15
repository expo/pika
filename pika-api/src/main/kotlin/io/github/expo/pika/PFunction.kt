package io.github.expo.pika

/**
 * Descriptor for a function for introspection.
 *
 * @property name The function name
 * @property visibility The visibility level of this function
 */
public class PFunction(
  public val name: String,
  public val visibility: PVisibility
)
