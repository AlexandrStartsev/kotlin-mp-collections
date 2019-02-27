package edu.alex

import kotlin.test.*

class TestList {

    @Test
    fun basicTests() {
        var list = RefList<String>()

        assertEquals(0, list.size)
        assertFalse { list.contains("1") }

        list.add("1")

        assertEquals(1, list.size)

        assertTrue { list.contains("1") }
        assertFalse { list.contains("2") }
        list.add("2")

        assertTrue { list.contains("2") }

        assertEquals("1", list[0])
        assertEquals("2", list[1])

        assertFailsWith(IndexOutOfBoundsException::class) {
            list[-1]
        }

        assertFailsWith(IndexOutOfBoundsException::class) {
            list[2]
        }

        assertFailsWith(IndexOutOfBoundsException::class) {
            list.add(3, "3")
        }

        list.add(2, "3")
        assertEquals("3", list[2])

        list.add(1, "1.5")
        assertEquals(4, list.size)

        assertEquals("1.5", list[1])
        assertEquals("2", list[2])
        assertEquals("3", list[3])

        assertFailsWith(IndexOutOfBoundsException::class) {
            list.add(5, "5")
        }
    }

    @Test
    fun testConstructor() {
        assertEquals(0, RefList<String>().size)
        assertEquals(1, RefList("1").size)
        assertEquals(2, RefList("1", "1").size)
        assertEquals(2, RefList(listOf("1", "1")).size)

        assertEquals("3", RefList("0", "1", "2", "3")[3])
        assertEquals("3", RefList(listOf("0", "1", "2", "3"))[3])
        assertEquals("3", RefList(RefList("0", "1", "2", "3"))[3])
    }

    @Test
    fun testContainsPrimitives() {
        val list = RefList("1", "2", "3")

        assertTrue { list.contains("1") }
        assertTrue { list.contains("2") }
        assertTrue { list.contains("3") }
        assertFalse { list.contains("4") }

        assertEquals(0, list.indexOf("1"))
        assertEquals(2, list.indexOf("3"))
        assertEquals(-1, list.indexOf("4"))

        assertTrue { list.containsAll(listOf("1", "3")) }
        assertFalse { list.containsAll(listOf("1", "4")) }

        assertTrue { list.containsAll(RefList("1", "3")) }
        assertFalse { list.containsAll(RefList("1", "4")) }
    }

    @Test
    fun testContainsNonPrimitives() {
        data class T(val a: Int)
        val t1 = T(1)
        val t2 = T(2)
        val t3 = T(3)
        val t4 = T(4)
        val t11 = T(1)
        val list = RefList(t1, t2, t3)

        assertTrue { list.contains(t1) }
        assertTrue { list.contains(t2) }
        assertTrue { list.contains(t3) }
        assertFalse { list.contains(t4) }
        assertFalse { list.contains(t11) }

        assertEquals(0, list.indexOf(t1))
        assertEquals(2, list.indexOf(t3))
        assertEquals(-1, list.indexOf(t4))
        assertEquals(-1, list.indexOf(t11))

        assertTrue { list.containsAll(listOf(t1, t3)) }
        assertFalse { list.containsAll(listOf(t3, t4)) }
        assertFalse { list.containsAll(listOf(t11)) }

        assertTrue { list.containsAll(RefList(t1, t2)) }
        assertFalse { list.containsAll(RefList(t1, t4)) }
        assertFalse { list.containsAll(RefList(t1, t11)) }
    }

    @Test
    fun testForEach() {
        val list = RefList("1", "2", "3", "4", "2")
        var s = ""
        list.forEach { s += it }
        assertEquals("12342", s)

        s = ""
        list.clear()

        list.forEach { s += it }
        assertEquals("", s)

    }

    @Test
    fun testSetGetAndRemoveAt() {
        val list = RefList("1", "x", "3", "4", "2")
        assertEquals("3", list[2])
        assertEquals("3", list.get(2))
        assertFailsWith(IndexOutOfBoundsException::class) {
            list.get(-1)
        }
        assertEquals("2", list.get(4))
        assertEquals("2", list.set(4, "5"))
        assertEquals("5", list.get(4))
        list[4] = "6"
        assertEquals("6", list.get(4))
        assertFailsWith(IndexOutOfBoundsException::class) {
            list.get(5)
        }
        assertFailsWith(IndexOutOfBoundsException::class) {
            list.get(612)
        }
        list.removeAt(2)
        assertEquals("4", list[2])
        assertFailsWith(IndexOutOfBoundsException::class) {
            list.get(4)
        }
        assertFailsWith(IndexOutOfBoundsException::class) {
            list[4] = "5"
        }
        list.removeAt(3)
        assertEquals(3, list.size)
        assertFailsWith(IndexOutOfBoundsException::class) {
            list.removeAt(3)
        }
        assertFailsWith(IndexOutOfBoundsException::class) {
            list.removeAt(-1)
        }
        list.removeAt(0)
        assertEquals("x", list[0])
        list.removeAt(0)
        list.removeAt(0)
        assertEquals(0, list.size)
        assertFailsWith(IndexOutOfBoundsException::class) {
            list.removeAt(0)
        }
    }

    @Test
    fun testInsert() {
        val list = RefList("1", "2", "3")
        list.add(0, "0")
        assertEquals("0", list[0])

        assertFailsWith(IndexOutOfBoundsException::class) {
            list.add(-1, "-1")
        }

        assertEquals(4, list.size)
        list.add(4, "4")
        assertEquals(5, list.size)
        assertEquals("4", list[4])

        assertFailsWith(IndexOutOfBoundsException::class) {
            list.add(6, "6")
        }

    }

    @Test
    fun testRetainRemoveAndRemoveAll() {
        val list = RefList("1", "2", "3")
        assertTrue { list.remove("2") }
        assertFalse { list.remove("2") }
        assertEquals(2, list.size)

        assertFalse { list.remove("21") }
        assertEquals(2, list.size)

        data class T(val a: Int)
        val t1 = T(1)
        val t2 = T(2)
        val t3 = T(3)
        val t4 = T(4)
        val list1 = RefList(t1, t2, t3)
        val list2 = RefList(t1, t2)
        val list3 = RefList(t2, t2, t3, t4)

        assertTrue { list1.containsAll(list2) }
        assertTrue { list1.retainAll(list3) } // list1 = t2 and t3
        assertFalse { list1.retainAll(list3) }
        assertFalse { list1.containsAll(list2) }
        assertFalse { list1.containsAll(list3) }

        assertTrue { list3.remove(t2) }
        assertTrue { list3.remove(t2) }
        assertFalse { list3.remove(t2) }
        assertTrue { list3.remove(t4) } // list3 = t3
        assertTrue { list1.containsAll(list3) }

        assertTrue { list1.removeAll(list2) } // list1 = t3
        assertFalse { list1.removeAll(list2) }

        assertEquals(1, list1.size)
        assertSame(t3, list1[0])
    }

    @Test
    fun testSlice() {
        val list = RefList("1", "2", "3")


    }

}