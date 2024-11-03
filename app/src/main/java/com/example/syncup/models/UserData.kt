package com.example.syncup.models

data class UserData(
    val id: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = "",
) {

    fun toMap() = mapOf(
        "id" to id,
        "name" to id,
        "number" to id,
        "imageUri" to id,
    )

}