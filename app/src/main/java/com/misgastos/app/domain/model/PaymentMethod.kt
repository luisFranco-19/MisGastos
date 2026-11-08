package com.misgastos.app.domain.model

enum class PaymentMethod(val displayName: String, val icon: String) {
    CASH("Efectivo", "💵"),
    DEBIT_CARD("Tarjeta de débito", "💳"),
    CREDIT_CARD("Tarjeta de crédito", "💳"),
    TRANSFER("Transferencia", "🏦"),
    OTHER("Otro", "💰");

    companion object {
        fun fromDisplayName(name: String): PaymentMethod {
            return entries.firstOrNull { it.displayName == name } ?: OTHER
        }
    }
}
