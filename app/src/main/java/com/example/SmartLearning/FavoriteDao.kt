@file:Suppress("PackageName")

package com.example.SmartLearning

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addData(example4: Example4)

    @get:Query("SELECT * FROM Example4")
    val favoriteData: List<Example4>

    @Query("SELECT * FROM Example4 WHERE isFavorite = 1")
    fun getAllFavorites(): List<Example4>

    @Query("SELECT * FROM Example4 WHERE id = :id")
    fun getItem(id: Int): Example4?

    @Delete
    fun delete(example4: Example4)

    /*
     @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addData(example4: Example4)

    @get:Query("SELECT * FROM Example4")
    val favoriteData: List<Example4>

    @Query("SELECT * FROM Example4 WHERE isFavorite = 1")
    fun getAllFavorites(): List<Example4>

    @Query("SELECT * FROM Example4 WHERE id = :id")
    fun getItem(id: Int): Example4?

    @Delete
    fun delete(example4: Example4)
     */
}