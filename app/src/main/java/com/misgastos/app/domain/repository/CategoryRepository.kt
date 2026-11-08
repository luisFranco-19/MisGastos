package com.misgastos.app.domain.repository

import com.misgastos.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(id: Long)
    suspend fun getCategoryByName(name: String): Category?
    suspend fun initializeDefaultCategories()
    suspend fun deleteAllCategories()
}
