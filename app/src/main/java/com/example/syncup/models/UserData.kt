package com.example.syncup.models

data class UserData(
    val id: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = "",
    val profileBio: String? = "",
) {

    fun toMap() = mapOf(
        "id" to id,
        "name" to id,
        "number" to id,
        "imageUri" to id,
        "profileBio" to profileBio
    )

}

data class ChatData(
    val id: String? = "",
    val user1: SingleChatUserDate = SingleChatUserDate(),
    val user2: SingleChatUserDate = SingleChatUserDate(),

    )

data class SingleChatUserDate(
    val id: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = "",

    )

data class Message(
    val sendBy: String? = "",
    val message: String? = "",
    val timeStamp: Long? = null
)