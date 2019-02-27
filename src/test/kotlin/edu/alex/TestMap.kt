package edu.alex

import kotlin.test.*

class TestMap {

    @Test
    fun testMap() {
        val nm = RefMap<String, String>()

        nm.put("1", "2")

        assertEquals("2", nm["1"])

    }

    @Test
    fun testEntrySetAdd() {
        val map = RefMap<String, String>()

        assertFailsWith(UnsupportedOperationException::class) {
            map.entries.add(object: AbstractRefMap.RefEntry<String, String> {
                override val key = "1"
                override val value = "2"
                override fun setValue(newValue: String) = "-"
            })
        }

        assertFailsWith(UnsupportedOperationException::class) {
            map.keys.add("123")
        }

        assertFailsWith(UnsupportedOperationException::class) {
            map.values.add("123")
        }
    }

    @Test
    fun testGetOrPut() {
        val map = RefMap("1" to "a", "2" to "b")
        var callCount = 0
        val supplier = { ++callCount; "supplied" }

        assertEquals("a", map.getOrPut("1", supplier))
        assertEquals(0, callCount)
        assertEquals("supplied", map.getOrPut("3", supplier))
        assertEquals(1, callCount)
        assertEquals("supplied", map.getOrPut("3", supplier))
        assertEquals(1, callCount)
        assertEquals("supplied", map["3"])
    }

    @Test
    fun testGetOrDefault() {
        val map = RefMap("1" to "a", "2" to "b")
        assertEquals("a", map.getOrDefault("1", "c"))
        assertEquals("c", map.getOrDefault("3", "c"))
        assertEquals("c", map.getOrDefault(null, "c"))
    }


}