@file:Suppress("PackageName")

package com.example.SmartLearning


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Example4::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var faveDB: FavoriteDatabase? = null

        fun getInstance(context: Context): FavoriteDatabase {
            return faveDB ?: synchronized(this) {
                faveDB ?: buildDatabaseInstance(context).also { faveDB = it }
            }
        }

        private fun buildDatabaseInstance(context: Context): FavoriteDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FavoriteDatabase::class.java,
                "FAV"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        fun cleanUp() {
            faveDB = null
        }
    }
}

