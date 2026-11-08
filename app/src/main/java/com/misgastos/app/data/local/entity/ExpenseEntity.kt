package com.misgastos.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.domain.model.PaymentMethod
import com.misgastos.app.domain.model.SyncStatus
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "expenses",
    indices = [
        androidx.room.Index(value = ["isDeleted", "date"]),
        androidx.room.Index(value = ["category"]),
        androidx.room.Index(value = ["date"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val paymentMethod: String,
    val category: String,
    val categoryIcon: String = "",
    val description: String? = null,
    val date: LocalDate,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncStatus: String = SyncStatus.PENDING.name,
    val remoteRowId: String? = null,
    val isDeleted: Boolean = false
) {
    fun toDomain(): Expense = Expense(
        id = id,
        amount = amount,
        paymentMethod = PaymentMethod.fromDisplayName(paymentMethod),
        category = category,
        categoryIcon = categoryIcon,
        description = description,
        date = date,
        createdAt = createdAt,
        updatedAt = updatedAt,
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }
            .getOrDefault(SyncStatus.ERROR),
        remoteRowId = remoteRowId,
        isDeleted = isDeleted
    )

    companion object {
        fun fromDomain(expense: Expense): ExpenseEntity = ExpenseEntity(
            id = expense.id,
            amount = expense.amount,
            paymentMethod = expense.paymentMethod.displayName,
            category = expense.category,
            categoryIcon = expense.categoryIcon,
            description = expense.description,
            date = expense.date,
            createdAt = expense.createdAt,
            updatedAt = expense.updatedAt,
            syncStatus = expense.syncStatus.name,
            remoteRowId = expense.remoteRowId,
            isDeleted = expense.isDeleted
        )
    }
}
