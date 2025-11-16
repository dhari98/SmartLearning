@file:Suppress("PackageName")

package com.example.SmartLearning

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Example4(


    @ColumnInfo val imageViewLike: Int,

    @ColumnInfo val imageViewSound: Int,

    @ColumnInfo val text1: String,

    @ColumnInfo val text2: String,



    @PrimaryKey
    val id: Int = -1,

    @ColumnInfo val isFavorite: Boolean = false

)
