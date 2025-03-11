package com.example.syncup

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.syncup.auth.MainScreenViewModel
import com.example.syncup.auth.login.LoginScreen
import com.example.syncup.auth.signup.SignUpScreen
import com.example.syncup.ui.chat_list.ChatListScreen
import com.example.syncup.ui.profile.ProfileScreen
import com.example.syncup.ui.single_chat.SingleChatScreen
import com.example.syncup.ui.single_status.SingleStatusScreen
import com.example.syncup.ui.status.StatusScreen
import com.example.syncup.ui.theme.SyncUpTheme
import com.example.syncup.ui.user_profile.UserProfileScreen
import com.example.syncup.utils.ImageScreen
import com.example.syncup.utils.NavRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncUpTheme {
                MainApp()
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainApp(viewModel: MainScreenViewModel = hiltViewModel()) {

    val isUserLogin by viewModel.isUserSignIn.collectAsState()

    val destination = if (isUserLogin) {
        NavRoutes.Destination.ChatListScreen.route
    } else {
        NavRoutes.Destination.Login.route
    }

    val navController = rememberNavController()


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = destination
        ) {
            composable(route = NavRoutes.Destination.Login.route) {
                LoginScreen(navController)
            }
            composable(route = NavRoutes.Destination.SignUp.route) {
                SignUpScreen(navController)
            }

            composable(route = NavRoutes.Destination.ProfileScreen.route) {
                ProfileScreen(navController, onLogOutClick = {
                    viewModel.logOut()
                    navController.navigate(NavRoutes.Destination.Login.route) {
                        popUpTo(0)
                    }
                })
            }

            composable(route = NavRoutes.Destination.UserProfileScreen.route) {

                val id = it.arguments?.getString("id")
                id?.let { chatId ->
                    UserProfileScreen(
                        navController = navController,
                        chatId = chatId
                    )
                }
            }


            composable(route = NavRoutes.Destination.ChatListScreen.route) {
                ChatListScreen(navController)
            }

            composable(route = NavRoutes.Destination.SingleChatScreen.route) {
                val id = it.arguments?.getString("id")
                id?.let { newId ->
                    SingleChatScreen(navController = navController, chatId = newId)
                }
            }

            composable(route = NavRoutes.Destination.StatusScreen.route) {
                StatusScreen(navController)
            }
            composable(route = NavRoutes.Destination.SingleStatusScreen.route) {
                SingleStatusScreen(navController)
            }

            composable(route = NavRoutes.Destination.DetailImageScreen.route) {

                val image = it.arguments?.getString("image")?.let { Uri.decode(it) }

                image?.let { img ->
                    ImageScreen(img)
                }
            }

        }

    }


}