package com.misgastos.app.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val category: String,
    val categoryIcon: String = "",
    val description: String? = null,
    val date: LocalDate,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val remoteRowId: String? = null,
    val isDeleted: Boolean = false
)
