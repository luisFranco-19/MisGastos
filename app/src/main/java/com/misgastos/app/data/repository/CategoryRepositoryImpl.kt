package com.misgastos.app.data.repository

import com.misgastos.app.data.local.dao.CategoryDao
import com.misgastos.app.data.local.entity.CategoryEntity
import com.misgastos.app.domain.model.Category
import com.misgastos.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(CategoryEntity.fromDomain(category))
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.deleteCategory(id)
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)?.toDomain()
    }

    override suspend fun initializeDefaultCategories() {
        val count = categoryDao.getCategoryCount()
        if (count == 0) {
            categoryDao.insertCategories(
                Category.DEFAULT_CATEGORIES.map { CategoryEntity.fromDomain(it) }
            )
        }
    }

    override suspend fun deleteAllCategories() {
        categoryDao.deleteAllCategories()
    }
}
