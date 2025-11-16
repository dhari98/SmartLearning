@file:Suppress("PackageName")

package com.example.SmartLearning

import android.content.Context

object FavoritesManager {
    fun repo(context: Context): FavoritesRepository {
        return FavoritesRepository(FavoriteDatabase.getInstance(context).favoriteDao())
    }
}
