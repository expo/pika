![Pika](assets/banner.png)

A Kotlin compiler plugin that generates complete type information at compile time — generics, nullability, annotations, properties and more — with **zero reflection** at runtime.

Calls to `typeDescriptorOf<T>()`, `introspectionOf<T>()`, and `isIntrospectable<T>()` are resolved during compilation and replaced with pre-constructed data (or a constant). Classes annotated with `@Introspectable` get a synthetic companion carrying their full metadata.

## Installation

```kotlin
// settings.gradle.kts
pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}
```

```kotlin
// build.gradle.kts
plugins {
  id("io.github.expo.pika") version "0.3.2"
}
```

## License

MIT — see [LICENSE.txt](LICENSE.txt).
