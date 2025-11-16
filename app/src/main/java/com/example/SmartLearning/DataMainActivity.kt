@file:Suppress("PackageName")

package com.example.SmartLearning

data class DataMainActivity(
    val title: String,
    val image: Int? = null,
    val id: Int,
    val customType: Int = -1 // -1 يعني لا يوجد نوع مخصص
) {
    val type: Int
        get() = when {
            customType != -1 -> customType // لو تم تحديده يدويًا
            image == null || image == 0 -> 2
            else -> 1
        }
}


/*
data class DataMainActivity(val title: String, val image: Int? = null, val id: Int) {
    val type: Int
        get() = if (image == null || image == 0) 2 else 1
}
 */
