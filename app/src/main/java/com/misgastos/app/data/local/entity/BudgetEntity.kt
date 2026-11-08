package com.misgastos.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.misgastos.app.domain.model.Budget

@Entity(
    tableName = "budgets",
    indices = [
        androidx.room.Index(value = ["year", "month"], unique = true)
    ]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monthlyAmount: Double,
    val year: Int,
    val month: Int
) {
    fun toDomain(): Budget = Budget(
        id = id,
        monthlyAmount = monthlyAmount,
        year = year,
        month = month
    )

    companion object {
        fun fromDomain(budget: Budget): BudgetEntity = BudgetEntity(
            id = budget.id,
            monthlyAmount = budget.monthlyAmount,
            year = budget.year,
            month = budget.month
        )
    }
}
