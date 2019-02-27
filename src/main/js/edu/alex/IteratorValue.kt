package edu.alex

internal interface IteratorValue<T> {
    val value: T
    val done: Boolean
}