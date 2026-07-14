package com.example.catsadoption_shop.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.catsadoption_shop.data.User
import com.example.catsadoption_shop.data.UserDao


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    companion object {
        @Volatile // Volatile makes it so any changes are instantly shown across all threads
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Checks if the database exists or not, if it exists it returns the instance
            // If it doesn't exist, the code enters a synchronized block, meaning only one thread can enter it at a time,
            // this prevents two separate threads from creating two separate databases
            return INSTANCE ?: synchronized(this) {
                // This is where the database is created
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cat_adoption_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}