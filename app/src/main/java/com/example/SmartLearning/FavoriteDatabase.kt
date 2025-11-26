@file:Suppress("PackageName")

package com.example.SmartLearning


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * FavoriteDatabase
 *
 * Room Database implementation used for storing and managing
 * the user's favorite vocabulary items (Example4 entity).
 *
 * This database is a Singleton to avoid multiple instances in memory,
 * ensuring better performance and preventing memory leaks.
 */
@Database(entities = [Example4::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {

    /**
     * Provides the DAO interface required to perform CRUD
     * (Create, Read, Update, Delete) operations on favorites.
     */
    abstract fun favoriteDao(): FavoriteDao

    companion object {

        /**
         * Holds the single instance of the Room database.
         * @Volatile guarantees visibility between threads.
         */
        @Volatile
        private var faveDB: FavoriteDatabase? = null

        /**
         * Returns a singleton instance of the database.
         * If it doesn't exist, a new one will be created.
         *
         * @param context application context
         * @return FavoriteDatabase (singleton)
         */
        fun getInstance(context: Context): FavoriteDatabase {
            return faveDB ?: synchronized(this) {
                faveDB ?: buildDatabaseInstance(context).also { faveDB = it }
            }
        }

        /**
         * Builds and configures the Room database.
         *
         * fallbackToDestructiveMigration():
         *     If the database schema changes without migration rules,
         *     this option will recreate the database instead of crashing.
         */
        private fun buildDatabaseInstance(context: Context): FavoriteDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FavoriteDatabase::class.java,
                "FAV"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        /**
         * Clears the cached database reference.
         * Useful when logging out or closing the app completely.
         */
        fun cleanUp() {
            faveDB = null
        }
    }
}
