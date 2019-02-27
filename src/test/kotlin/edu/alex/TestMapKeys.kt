package edu.alex

import kotlin.test.*

class TestMapKeys {

    @Test
    fun testKeyCollections() {
        val map1 = RefMap("1" to "a", "2" to "b")
        val map2 = RefMap("3" to "c", "2" to "b")

        assertEquals(2, map1.keys.size)
        assertTrue { map1.keys.contains("1") }
        assertTrue { map1.keys.contains("2") }
        assertFalse { map1.keys.contains("3") }


        val it = map1.keys.iterator()

        assertTrue { it.hasNext() }
        assertFailsWith(IllegalStateException::class) {
            it.remove()
        }

        assertEquals("1", it.next())

        it.remove()
        assertEquals(1, map1.size)

        assertFailsWith(IllegalStateException::class) {
            it.remove()
        }

        assertEquals("2", it.next())
        assertFailsWith(NoSuchElementException::class) {
            it.next()
        }

        assertEquals(2, map2.keys.size)
        assertTrue { map2.keys.retainAll(map1.keys) }
        assertEquals(1, map2.keys.size)
        assertEquals(1, map2.size)

        assertFalse { map2.keys.retainAll(map1.keys) }

        assertEquals("b", map2["2"])


    }
}