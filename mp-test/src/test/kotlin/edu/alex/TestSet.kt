package edu.alex

import kotlin.test.*

class TestSet {
/*
    fun rand(max: Int = Int.MAX_VALUE): Int = js("Math.floor(Math.random() * max)")

    fun makeRandomString(size: Int = 20): String {
        val all = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (0 until rand(size)).fold("") { a, b -> a + all[b] }
    }

   */
    data class C1(val a: String)

    @Test
    fun testNativeSet() {
        val refSet = RefSet<String>()
        refSet.add("a")
        refSet.add("b")
        refSet.add("" + "b")
        refSet.add("c")

        assertEquals(3, refSet.size)

        assertEquals("abc", refSet.fold("") { acc, s -> acc + s })

        assertTrue { refSet.retainAll(listOf("a", "b")) }
        assertFalse { refSet.retainAll(listOf("a", "b")) }

        assertEquals(2, refSet.size)
    }

    @Test
    fun testNonPrimitives() {
        val obj1 = C1("a")
        val obj2 = C1("a")

        assertEquals(obj1, obj2)

        val normalSet = setOf(obj1)

        assertTrue { normalSet.contains(obj2) }

        val refSet = RefSet<C1>()
        refSet.add(obj1)

        assertFalse { refSet.contains(obj2) }
        assertTrue { refSet.contains(obj1) }
    }

    @Test
    fun testStrings() {
        val obj1 = "5"
        val obj2 = 5.toString()

        assertEquals(obj1, obj2)

        val refSet = RefSet<String>()
        refSet.add(obj1)

        assertTrue { refSet.contains(obj2) }
        assertTrue { refSet.contains(obj1) }
    }

    @Test
    fun testIteratorRemove1() {
        val obj1 = C1("a")
        val obj2 = C1("b")
        val refSet = RefSet<C1>()
        refSet.add(obj1)

        assertEquals(1, refSet.size)
        var it = refSet.iterator()
        it.next()
        it.remove()

        assertEquals(0, refSet.size)

        try {
            it.remove()
            fail("Should have thrown")
        } catch(e: Throwable) {
            assertTrue { e is IllegalStateException }
        }

        refSet.add(obj1)
        it = refSet.iterator()
        it.next()
        try {
            it.next()
            fail("Should have thrown")
        } catch(e: Throwable) {
            assertTrue { e is NoSuchElementException }
        }
        it.remove()
        assertEquals(0, refSet.size)
        refSet.add(obj1)
        refSet.add(obj2)
        it = refSet.iterator()
        it.next()
        it.remove()
        try {
            it.remove()
            fail("Should have thrown")
        } catch(e: Throwable) {
            assertTrue { e is IllegalStateException }
        }
        it.hasNext()
        assertTrue { obj2 === it.next() }
        assertEquals(1, refSet.size)
        assertFalse { it.hasNext() }
        it.remove()
        assertEquals(0, refSet.size)
        try {
            it.remove()
            fail("Should have thrown")
        } catch(e: Throwable) {
            assertTrue { e is IllegalStateException }
        }
    }

}