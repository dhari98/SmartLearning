@file:Suppress("PackageName")

package com.example.SmartLearning

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * FavoriteDao
 *
 * Data Access Object for the Room Database responsible for:
 *  - Adding vocabulary items to favorites
 *  - Removing items from favorites
 *  - Reading all saved favorite items
 *  - Retrieving a single item by its ID
 *
 * This DAO works with the Example4 entity (German vocabulary model).
 * Room automatically generates the implementation at compile time.
 */
@Dao
interface FavoriteDao {

    /**
     * Inserts an item into the favorites table.
     * If the item already exists, it will be replaced.
     *
     * @param example4 vocabulary object to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addData(example4: Example4)

    /**
     * Returns the entire database content (all stored records)
     * regardless of the favorite state.
     *
     * Useful for debugging or exporting data.
     */
    @get:Query("SELECT * FROM Example4")
    val favoriteData: List<Example4>

    /**
     * Returns only items marked as favorites.
     *
     * @return List of Example4 items where isFavorite = 1
     */
    @Query("SELECT * FROM Example4 WHERE isFavorite = 1")
    fun getAllFavorites(): List<Example4>

    /**
     * Retrieves a single vocabulary record by ID.
     *
     * @param id primary key of the vocabulary item
     * @return Example4 item if found, otherwise null
     */
    @Query("SELECT * FROM Example4 WHERE id = :id")
    fun getItem(id: Int): Example4?

    /**
     * Removes an item from the Room database.
     *
     * @param example4 vocabulary item to remove
     */
    @Delete
    fun delete(example4: Example4)
}
