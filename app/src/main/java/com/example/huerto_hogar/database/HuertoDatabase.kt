package com.example.huerto_hogar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.huerto_hogar.database.converters.Converters
import com.example.huerto_hogar.database.dao.*
import com.example.huerto_hogar.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CategoryEntity::class,
        OrderEntity::class,
        OrderDetailEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HuertoDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun orderDao(): OrderDao
    abstract fun orderDetailDao(): OrderDetailDao
    
    companion object {
        @Volatile
        private var INSTANCE: HuertoDatabase? = null
        
        fun getDatabase(context: Context): HuertoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HuertoDatabase::class.java,
                    "huerto_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}