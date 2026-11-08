package com.misgastos.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppCurrencyTest {

    @Test
    fun `default currency is NIO`() {
        assertEquals(AppCurrency.NIO, AppCurrency.DEFAULT)
        assertEquals("NIO", AppCurrency.DEFAULT.code)
        assertEquals("C$", AppCurrency.DEFAULT.symbol)
    }

    @Test
    fun `fromCode returns expected currency`() {
        assertEquals(AppCurrency.NIO, AppCurrency.fromCode("NIO"))
        assertEquals(AppCurrency.NIO, AppCurrency.fromCode("nio"))
        assertEquals(AppCurrency.CRC, AppCurrency.fromCode("CRC"))
        assertEquals(AppCurrency.CRC, AppCurrency.fromCode("crc"))
        assertEquals(AppCurrency.NIO, AppCurrency.fromCode("UNKNOWN"))
        assertEquals(AppCurrency.NIO, AppCurrency.fromCode(null))
    }

    @Test
    fun `fromSymbol returns expected currency`() {
        assertEquals(AppCurrency.NIO, AppCurrency.fromSymbol("C$"))
        assertEquals(AppCurrency.CRC, AppCurrency.fromSymbol("₡"))
        assertEquals(AppCurrency.NIO, AppCurrency.fromSymbol(null))
    }

    @Test
    fun `only NIO and CRC are available`() {
        assertEquals(2, AppCurrency.entries.size)
        assertEquals(listOf(AppCurrency.NIO, AppCurrency.CRC), AppCurrency.entries)
    }
}
