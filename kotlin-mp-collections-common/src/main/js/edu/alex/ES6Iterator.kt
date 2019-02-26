package edu.alex

internal interface ES6Iterator<T> {
    fun next(): IteratorValue<T>
}