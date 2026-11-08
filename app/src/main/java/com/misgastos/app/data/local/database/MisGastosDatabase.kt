package com.misgastos.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.misgastos.app.data.local.dao.BudgetDao
import com.misgastos.app.data.local.dao.CategoryDao
import com.misgastos.app.data.local.dao.ExpenseDao
import com.misgastos.app.data.local.entity.BudgetEntity
import com.misgastos.app.data.local.entity.CategoryEntity
import com.misgastos.app.data.local.entity.ExpenseEntity
import com.misgastos.app.domain.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MisGastosDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: MisGastosDatabase? = null

        fun getInstance(context: Context): MisGastosDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): MisGastosDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MisGastosDatabase::class.java,
                "mis_gastos_database"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).categoryDao().insertCategories(
                                Category.DEFAULT_CATEGORIES.map { CategoryEntity.fromDomain(it) }
                            )
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
