package com.misgastos.app.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val isDefault: Boolean = false
) {
    companion object {
        val DEFAULT_CATEGORIES = listOf(
            Category(name = "Comida", icon = "🍔", isDefault = true),
            Category(name = "Transporte", icon = "🚗", isDefault = true),
            Category(name = "Supermercado", icon = "🛒", isDefault = true),
            Category(name = "Entretenimiento", icon = "🎬", isDefault = true),
            Category(name = "Educación", icon = "📚", isDefault = true),
            Category(name = "Salud", icon = "🏥", isDefault = true),
            Category(name = "Hogar", icon = "🏠", isDefault = true),
            Category(name = "Ropa", icon = "👕", isDefault = true),
            Category(name = "Videojuegos", icon = "🎮", isDefault = true),
            Category(name = "Tecnología", icon = "💻", isDefault = true),
            Category(name = "Suscripciones", icon = "📱", isDefault = true),
            Category(name = "Otros", icon = "📦", isDefault = true)
        )
    }
}
