@file:Suppress("PackageName")

package com.example.SmartLearning

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Example4
 *
 * Entity model used for Room Database to store German vocabulary items
 * including image references, text values, and favorite status.
 *
 * Fields:
 *  - imageViewLike  → drawable resource for the "favorite" icon shown in UI
 *  - imageViewSound → drawable resource for the "speaker" / pronunciation icon
 *  - text1          → German word
 *  - text2          → Translation (Arabic or other selected language)
 *  - id             → Unique primary key (required by Room)
 *  - isFavorite     → Boolean flag indicating whether the user has added
 *                     this word to the Favorites list
 *
 * Storing vocabulary inside Room DB ensures:
 *  - offline access to saved words
 *  - fast rendering in RecyclerView
 *  - persistence even after app restart
 */
@Entity
data class Example4(

    @ColumnInfo val imageViewLike: Int,      // Resource ID of "favorite" icon
    @ColumnInfo val imageViewSound: Int,     // Resource ID of "sound/speaker" icon
    @ColumnInfo val text1: String,           // German word
    @ColumnInfo val text2: String,           // Translation of the word

    @PrimaryKey
    val id: Int = -1,                        // Unique identifier for each word item

    @ColumnInfo val isFavorite: Boolean = false // Tracks if user marked this item as favorite
)
