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


}