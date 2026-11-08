package com.misgastos.app.notification

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationTemplatesTest {

    @Test
    fun `getReminderContent returns valid template and rotates without repeating consecutively`() {
        val first = NotificationTemplates.getReminderContent(lastIndex = -1)
        assertNotNull(first.title)
        assertNotNull(first.message)
        assertTrue(first.title.isNotBlank())
        assertTrue(first.message.isNotBlank())

        val second = NotificationTemplates.getReminderContent(lastIndex = first.templateIndex)
        assertNotEquals(first.templateIndex, second.templateIndex)
    }

    @Test
    fun `getSummaryContent formats currency and amounts correctly and rotates`() {
        val first = NotificationTemplates.getSummaryContent(
            currency = "C$",
            total = 120.50,
            count = 3,
            lastIndex = -1
        )
        assertNotNull(first.title)
        assertTrue(first.message.contains("C$"))
        assertTrue(first.message.contains("120.50") || first.message.contains("120,50"))
        assertTrue(first.message.contains("3"))

        val second = NotificationTemplates.getSummaryContent(
            currency = "₡",
            total = 5000.0,
            count = 1,
            lastIndex = first.templateIndex
        )
        assertNotEquals(first.templateIndex, second.templateIndex)
        assertTrue(second.message.contains("₡"))
        assertTrue(second.message.contains("1 gasto") || second.message.contains("1 registro") || second.message.contains("5,000") || second.message.contains("5.000"))
    }
}
