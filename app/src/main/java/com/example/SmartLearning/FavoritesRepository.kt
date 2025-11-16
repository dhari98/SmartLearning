@file:Suppress("PackageName")

package com.example.SmartLearning

class FavoritesRepository(private val dao: FavoriteDao) {

    fun add(item: Example4) = dao.addData(item)

     fun remove(item: Example4) = dao.delete(item)

     fun getById(id: Int): Example4? = dao.getItem(id)

}
