package edu.alex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestNativeSet {

    fun rand(max: Int = Int.MAX_VALUE): Int = js("Math.floor(Math.random() * max)")

    fun makeRandomString(size: Int = 20): String {
        val all = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (0 until rand(size)).fold("") { a, b -> a + all[b] }
    }

    @Test
    fun testEs6() {
        val set = ES6Set<String>()
        set.add("a")
        set.add("b")
        set.add("c")
        set.add("a" + "b")

        assertTrue { set.has("ab") }
        assertTrue { set.has("c") }

        set.add("b")
        assertEquals(4, set.size)
    }

    @Test
    fun testIntSpeed() {
        val arr = (0 until 100).map { rand(100) }.toTypedArray()

        val hashSet = HashSet<Int>()
        val nativeSet = RefSet<Int>()

        js("console.time('kotlin set (/10)')")
        (0 until 100000).forEach { hashSet.add(arr[ it%arr.size ]) }
        js("console.timeEnd('kotlin set (/10)')")

        js("console.time('native set')")
        (0 until 1000000).forEach { nativeSet.add(arr[ it%arr.size ]) }
        js("console.timeEnd('native set')")

        assertTrue { nativeSet.containsAll(hashSet) }
        assertTrue { hashSet.containsAll(nativeSet) }
    }

    @Test
    fun testStringSpeed() {


        val arr = (0 until 100).map { makeRandomString() }.toTypedArray()

        val hashSet = HashSet<String>()
        val nativeSet = RefSet<String>()

        js("console.time('kotlin set (/10)')")
        (0 until 100000).forEach { hashSet.add(arr[ it%arr.size ]) }
        js("console.timeEnd('kotlin set (/10)')")

        js("console.time('native set')")
        (0 until 1000000).forEach { nativeSet.add(arr[ it%arr.size ]) }
        js("console.timeEnd('native set')")

        assertTrue { nativeSet.containsAll(hashSet) }
        assertTrue { hashSet.containsAll(nativeSet) }
    }

    @Test
    fun testKotlinListVsNativeSet() {
        val element = makeRandomString()

        js("console.time('kotlin list')")
        (0 until 100000).forEach {
            val collection = ArrayList<String>()
            collection.add(element)
            (0 until 10).forEach {
                collection.contains("123")
            }
        }
        js("console.timeEnd('kotlin list')")

        js("console.time('native set')")
        (0 until 100000).forEach {
            val collection = RefSet<String>()
            collection.add(element)
            (0 until 10).forEach {
                collection.contains("123")
            }
        }
        js("console.timeEnd('native set')")

    }

    @Test
    fun testNativeSet() {
        val set = RefSet<String>()
        set.add("a")
        set.add("b")
        set.add("" + "b")
        set.add("c")

        assertEquals(3, set.size)

        assertEquals("abc", set.fold("") { acc, s -> acc + s })

        assertTrue { set.retainAll(listOf("a", "b")) }
        assertFalse { set.retainAll(listOf("a", "b")) }

        assertEquals(2, set.size)
    }
}