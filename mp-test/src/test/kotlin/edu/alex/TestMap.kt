package edu.alex

import kotlin.test.Test
import kotlin.test.assertEquals

class TestMap {

    @Test
    fun testMap() {
        val nm = RefMap<String, String>()

        nm.put("1", "2")

        assertEquals("2", nm["1"])

    }
}