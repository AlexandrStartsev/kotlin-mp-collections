package edu.alex

internal interface ObjectReference<T> {
    fun get(): T

    class PrimitiveReference<T> internal constructor(private val ref: T) : ObjectReference<T> {
        override fun get(): T {
            return this.ref
        }

        override fun hashCode(): Int {
            return this.ref.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return other is ObjectReference<*> && this.ref == other.get()
        }
    }

    class NonPrimitiveReference<T> internal constructor(private val ref: T) : ObjectReference<T> {

        override fun get(): T {
            return this.ref
        }

        override fun hashCode(): Int {
            return System.identityHashCode(this.ref)
        }

        override fun equals(other: Any?): Boolean {
            return other is ObjectReference<*> && this.ref === other.get()
        }
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <T> wrap(obj: T?): ObjectReference<T> {
            if (obj == null) {
                return nullReference as ObjectReference<T>
            }
            return if (obj is String || obj is Int || obj is Boolean) {
                PrimitiveReference(obj)
            } else NonPrimitiveReference(obj)

        }

        private val nullReference: ObjectReference<Any?> = object : ObjectReference<Any?> {
            override fun get(): Any? {
                return null
            }

            override fun hashCode(): Int {
                return 0
            }

            override fun equals(other: Any?): Boolean {
                return other == null || other is ObjectReference<*> && other.get() == null
            }
        }
    }
}