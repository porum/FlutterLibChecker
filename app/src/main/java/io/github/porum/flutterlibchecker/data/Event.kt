package io.github.porum.flutterlibchecker.data

class Event<out T>(private val content: T) {
  private var hasBeenConsumed = false

  fun getContentIfNotConsumed(): T? {
    return if (hasBeenConsumed) {
      null
    } else {
      hasBeenConsumed = true
      content
    }
  }

  fun peekContent(): T = content
}