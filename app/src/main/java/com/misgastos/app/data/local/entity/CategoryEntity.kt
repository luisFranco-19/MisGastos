package com.misgastos.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.misgastos.app.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val isDefault: Boolean = false
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        icon = icon,
        isDefault = isDefault
    )

    companion object {
        fun fromDomain(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            name = category.name,
            icon = category.icon,
            isDefault = category.isDefault
        )
    }
}
