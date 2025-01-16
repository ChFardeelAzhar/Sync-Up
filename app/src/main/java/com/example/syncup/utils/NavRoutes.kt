package com.example.syncup.utils

object NavRoutes {
    sealed class Destination(val route: String) {
        data object Login : Destination("login_screen")
        data object SignUp : Destination("signUp_screen")
        data object ChatListScreen : Destination("chatList_screen")

        data object SingleChatScreen : Destination("singleChat_screen/{id}") {
            fun createRoute(id: String) = "singleChat_screen/$id"
        }

        data object StatusScreen : Destination("StatusScreen_screen")
        data object SingleStatusScreen : Destination("singleStatus_screen/{id}") {
            fun createStatusRoute(id: String) = "singleStatus_screen/$id"
        }

        data object ProfileScreen : Destination("profile_screen")

        data object UserProfileScreen : Destination("user_profile_screen/{id}") {
            fun createRoute(id: String) = "user_profile_screen/$id"
        }


        data object DetailImageScreen : Destination("image_screen/{image}") {
            fun createImageRoute(image: String) = "image_screen/$image"
        }
    }
}