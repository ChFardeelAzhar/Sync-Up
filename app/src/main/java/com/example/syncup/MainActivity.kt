package com.example.syncup

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.syncup.auth.AuthViewModel
import com.example.syncup.auth.login.LoginScreen
import com.example.syncup.auth.signup.SignUpScreen
import com.example.syncup.ui.chat_list.ChatListScreen
import com.example.syncup.ui.navbar.BottomBarItemData
import com.example.syncup.ui.navbar.BottomNavigationBar
import com.example.syncup.ui.profile.ProfileScreen
import com.example.syncup.ui.single_chat.SingleChatScreen
import com.example.syncup.ui.single_status.SingleStatusScreen
import com.example.syncup.ui.status.StatusScreen
import com.example.syncup.ui.theme.SyncUpTheme
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
fun MainApp(viewModel: AuthViewModel = hiltViewModel()) {

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
                ProfileScreen(navController)
            }
            composable(route = NavRoutes.Destination.ChatListScreen.route) {
                ChatListScreen(navController)
            }
            composable(route = NavRoutes.Destination.SingleChatScreen.route) {
                SingleChatScreen(navController)
            }
            composable(route = NavRoutes.Destination.StatusScreen.route) {
                StatusScreen(navController)
            }
            composable(route = NavRoutes.Destination.SingleStatusScreen.route) {
                SingleStatusScreen(navController)
            }
        }

    }


}