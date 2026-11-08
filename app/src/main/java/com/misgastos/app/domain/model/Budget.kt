package com.misgastos.app.domain.model

data class Budget(
    val id: Long = 0,
    val monthlyAmount: Double,
    val year: Int,
    val month: Int
)
