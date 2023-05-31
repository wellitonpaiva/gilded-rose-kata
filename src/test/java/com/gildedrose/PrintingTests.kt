package com.gildedrose

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PrintingTests {

    private val now = LocalDate.parse("2021-10-29")

    @Test
    fun `print empty stock list`() {
        val stock = listOf<Item>()
        val expected = listOf("29 October 2021")

        Assertions.assertEquals(expected, stock.printout(now))
    }

    @Test
    fun `print non empty stock list`() {
        val stock = listOf(
            Item("banana", now.minusDays(1), 42u), Item("kumquat", now.plusDays(1), 101u)
        )
        val expected = listOf(
            "29 October 2021", "banana, -1, 42", "kumquat, 1, 101"
        )

        Assertions.assertEquals(expected, stock.printout(now))
    }
}

