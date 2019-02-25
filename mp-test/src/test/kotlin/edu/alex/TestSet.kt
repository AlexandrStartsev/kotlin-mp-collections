package edu.alex

import kotlin.test.*

class TestSet {

    fun makeRandomString(size: Int = 20): String {
        val all = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (0 until size).map { (0 until all.length).random() }.fold("") { a, b -> a + all[b] }
    }


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
        val refSet = RefSet(obj1)

        assertEquals(obj1, obj2)


        assertFalse { refSet.contains(obj2) }
        assertTrue { refSet.contains(obj1) }
        assertTrue { refSet.add(obj2) }

        assertTrue { refSet.contains(obj2) }

        assertEquals(1, RefSet(obj1, obj1).size )
        assertEquals(2, RefSet(obj1, obj2).size )
    }

    @Test
    fun testStrings() {
        val obj1 = "5"
        val obj2 = 5.toString()

        assertEquals(obj1, obj2)

        val refSet = RefSet(obj1)
        assertFalse { refSet.add(obj2) }

        assertTrue { refSet.contains(obj2) }
        assertTrue { refSet.contains(obj1) }
    }

    @Test
    fun testInt() {
        assertEquals(2, RefSet(2, 3, 2, 3).size)
        assertTrue { RefSet(2, 3).add(1) }
        assertFalse { RefSet(2, 3).add(2) }
    }

    @Test
    fun testVarArgs() {
        val set = setOf("6", "1", "2", "4", "5", "8")
        val set1 = RefSet("6", "1", "2", "4", "5", "8")
        val set2 = RefSet(listOf("6", "1", "2", "4", "5", "8"))

        assertEquals(set, set.intersect(set1))
        assertEquals(set, set.intersect(set2))
    }

    @Test
    fun toMutableSet() {
        assertTrue { RefSet("a").toMutableSet() is RefSet }

        val obj1 = C1("a")
        val obj2 = C1("a")
        val set = RefSet(obj1)
        assertTrue { set.toMutableSet().add(obj2) }
        assertEquals(1, set.size)
    }

    @Test
    fun testIterator() {
        val refSet = RefSet("6", "1", "2", "4", "5", "8")

        var it = refSet.iterator()
        var s = ""

        while(it.hasNext()) s += it.next()

        assertEquals("612458", s)

        assertFailsWith(NoSuchElementException::class) {
            it.next()
        }

        it = refSet.iterator()
        it.next()
        it.next() // 1

        s = ""
        while(it.hasNext()) s += it.next()
        assertEquals("2458", s)
    }

    @Test
    fun testIteratorRemove() {
        val obj1 = C1("a")
        val obj2 = C1("b")
        val refSet = RefSet<C1>()
        refSet.add(obj1)

        assertEquals(1, refSet.size)
        var it = refSet.iterator()
        it.next()
        it.remove()

        assertEquals(0, refSet.size)

        assertFailsWith(IllegalStateException::class) {
            it.remove()
        }

        refSet.add(obj1)
        it = refSet.iterator()
        it.next()
        assertFailsWith(NoSuchElementException::class) {
            it.next()
        }
        it.remove()
        assertEquals(0, refSet.size)
        refSet.add(obj1)
        refSet.add(obj2)
        it = refSet.iterator()
        it.next()
        it.remove()
        assertFailsWith(IllegalStateException::class) {
            it.remove()
        }
        it.hasNext()
        assertTrue { obj2 === it.next() }
        assertEquals(1, refSet.size)
        assertFalse { it.hasNext() }
        it.remove()
        assertEquals(0, refSet.size)
        assertFailsWith(IllegalStateException::class) {
            it.remove()
        }
    }

    @Test
    fun testNullKey() {
        val refSet = RefSet<String?>()
        assertTrue { refSet.add(null) }

        assertEquals(1, refSet.size)
        assertTrue { refSet.contains(null) }

        assertFalse { refSet.add(null) }
        assertTrue { refSet.contains(null) }

        assertTrue { refSet.remove(null) }
        assertFalse { refSet.contains(null) }
        assertFalse { refSet.remove(null) }
        assertEquals(0, refSet.size)

    }

    @Test
    fun testRemoveAll() {
        val refSet = RefSet("6", "1", "2", "4", "5", "8")
        refSet.removeAll(listOf("2", "5", null, "8"))

        assertEquals("614", refSet.joinToString(separator = ""))

    }

    @Test
    fun testAddAll() {
        // Notice how order of present element is not changes in linked set
        val refSet = RefSet<String?>("6", "1", "2")
        assertTrue { refSet.addAll(listOf("6", "5", null, "8")) }
        assertTrue { refSet.addAll(listOf("6", "5", null, "8", "9")) }
        assertFalse { refSet.addAll(listOf("6", "5", null, "8")) }

        assertEquals("6125null89", refSet.joinToString(separator = ""))
    }

    @Test
    fun testContains() {
        val refSet = RefSet("6", "1", "2")
        assertTrue { refSet.contains("6") }
        assertFalse { refSet.contains("7") }

        val obj1 = C1("a")
        val obj2 = C1("a")
        val obj3 = C1("b")

        val refSetObj = RefSet(obj1, obj3)
        assertTrue { refSetObj.contains(obj1) }
        assertFalse { refSetObj.contains(obj2) }
        assertFalse { refSetObj.containsAll(listOf(obj1, obj2, obj3)) }

        assertTrue { refSetObj.add(obj2) }
        assertTrue { refSetObj.contains(obj2) }
        assertTrue { refSetObj.containsAll(listOf(obj1, obj2, obj3)) }

        assertEquals(3, refSetObj.size)

    }

    @Test
    fun testRemove() {
        val obj1 = C1("a")
        val obj2 = C1("a")
        val obj3 = C1("b")

        val refSetObj = RefSet(obj1, obj2, obj3)

        assertTrue { refSetObj.remove(obj2) }
        assertFalse { refSetObj.remove(obj2) }

        assertTrue { refSetObj.removeAll(listOf(obj2, obj3)) }
        assertFalse { refSetObj.removeAll(listOf(obj2, obj3)) }

        assertEquals(1, refSetObj.size)
        assertTrue { refSetObj.removeAll(listOf(obj1, obj2, obj3)) }
        assertEquals(0, refSetObj.size)
    }


    @Test
    fun testToString() {
        val refSet = RefSet("1", "2", "3")
        assertEquals("[1, 2, 3]", refSet.toString())

        val refSetObj = RefSet(C1("a"), C1("b"), C1("b"))
        assertEquals("[C1(a=a), C1(a=b), C1(a=b)]", refSetObj.toString())
    }

    @Test
    fun testRetainAll() {
        val obj1 = C1("a")
        val obj2 = C1("a")
        val obj3 = C1("a")
        val obj4 = C1("a")
        val obj5 = C1("a")

        val refSet = RefSet(obj1, obj2, obj3, obj4)

        assertTrue { refSet.retainAll(listOf(obj2, obj3, obj4, obj5)) }
        assertFalse { refSet.retainAll(listOf(obj2, obj3, obj4, obj5)) }

        assertEquals(3, refSet.size)
        assertFalse { refSet.contains(obj1) }
        assertTrue { refSet.contains(obj2) }
        assertTrue { refSet.contains(obj3) }
        assertTrue { refSet.contains(obj4) }
        assertFalse { refSet.contains(obj5) }

    }

    @Test
    fun testIntersect() {
        val obj1 = C1("a")
        val obj2 = C1("a")
        val obj3 = C1("a")
        val obj4 = C1("a")
        val obj5 = C1("a")
        val other = listOf(obj2, obj3, obj5)
        val me = RefSet(obj1, obj2, obj3, obj4)
        val set = me.intersect(other)
        assertTrue { set is RefSet }
        assertEquals(2, set.size)
    }

    @Test
    fun testSpeed() {

        fun doAdd(createSet: () -> MutableSet<String>, times: Int = 10000) {
            val arr = (0 until 100).map { makeRandomString() }

            (0 until times).forEach {
                val set = createSet()
                set.addAll (arr)
            }
        }

        println("HashSet 1M insertions " + timeIt { doAdd({ HashSet() }) } )
        println("RefSet 1M insertions " + timeIt { doAdd({ RefSet() }) } )

        fun doSearch(set: MutableSet<String>, times: Int = 1000000) {
            val arr = (0 until 100).map { makeRandomString() }
            set.addAll( arr )

            (0 until times).forEach {
                set.contains(arr[it%100] )
            }
        }

        println("HashSet 1M contains " + timeIt { doSearch(HashSet()) } )
        println("RefSet 1M contains " + timeIt { doSearch(RefSet()) } )

        fun <T: MutableSet<String>> doIntersect(set: T, times: Int = 1000) {
            val arr = (0 until 100).map { makeRandomString() }
            val arr2 = arr.subList(20, 70) + (0 until 50).map { makeRandomString() }
            set.addAll( arr )

            (0 until times).forEach {
                set.intersect(arr2)
            }
        }

        println("HashSet 1K intersect " + timeIt { doIntersect(HashSet()) } )
        println("RefSet 1K intersect " + timeIt { doIntersect(RefSet()) } )
    }

}