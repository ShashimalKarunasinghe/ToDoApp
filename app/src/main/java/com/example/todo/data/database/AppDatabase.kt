package com.example.todo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todo.data.dao.TaskDao
import com.example.todo.data.model.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
                    // Auto-reset database on schema mismatch or corruption
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback()) // adds safe prepopulation & error handling
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Optional: you can insert default tasks here for first run
            CoroutineScope(Dispatchers.IO).launch {
                // Example pre-population (optional)
                // INSTANCE?.taskDao()?.insert(TaskEntity(title="Welcome Task", description="Your first task!", priority="Medium"))
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            // Ensures clean rebuild instead of crash on version mismatch
        }
    }
}
