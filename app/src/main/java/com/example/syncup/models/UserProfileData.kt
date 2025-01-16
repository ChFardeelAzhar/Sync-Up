package com.example.syncup.models

data class UserProfileData(
    val id: String? = "",
    val name: String? = "",
    val number: String? = "",
    val email: String? = "",
    val imageUrl: String? = "",
    val profileBio: String? = "",
) {

    fun toMap() = mapOf(
        "id" to id,
        "name" to id,
        "number" to id,
        "email" to email,
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
    val email: String? = "",
    val profileBio: String? = "",

    )

data class Message(
    val id: String = "",
    val sendBy: String? = "",
    val message: String? = "",
    val timeStamp: Long? = null
)