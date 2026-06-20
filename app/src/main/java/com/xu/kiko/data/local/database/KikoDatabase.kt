package com.xu.kiko.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xu.kiko.data.local.dao.FocusSessionDao
import com.xu.kiko.data.local.dao.TaskDao
import com.xu.kiko.data.local.dao.UserDao
import com.xu.kiko.data.local.entity.FocusSessionEntity
import com.xu.kiko.data.local.entity.TaskEntity
import com.xu.kiko.data.local.entity.UserEntity

@Database(
    entities = [
        TaskEntity::class,
        UserEntity::class,
        FocusSessionEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class KikoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        private const val DATABASE_NAME = "kiko.db"

        @Volatile
        private var instance: KikoDatabase? = null

        fun getDatabase(context: Context): KikoDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    KikoDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { database ->
                    instance = database
                }
            }
        }
    }
}
