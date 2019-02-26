package edu.alex

interface AbstractRefList<E>: RefCollection<E> { // List
    operator fun get(index: Int): E

    operator fun set(index: Int, element: E): E

    fun add(index: Int, element: E)

    fun removeAt(index: Int)

    fun indexOf(element: @UnsafeVariance E): Int
}