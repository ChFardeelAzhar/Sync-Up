package com.example.syncup.ui.status

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.syncup.R
import com.example.syncup.ui.navbar.BottomBarItemData
import com.example.syncup.ui.navbar.BottomNavigationBar
import com.example.syncup.utils.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StatusScreen(navController: NavController) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Sync Up",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            })
        },
        bottomBar = {


            val backStackEntry = navController.currentBackStackEntryAsState()
            val isHomeSelected =
                NavRoutes.Destination.ChatListScreen.route == backStackEntry.value?.destination?.route
            val isStatusSelected =
                NavRoutes.Destination.StatusScreen.route == backStackEntry.value?.destination?.route
            val isProfileSelected =
                NavRoutes.Destination.ProfileScreen.route == backStackEntry.value?.destination?.route


            val listItems = listOf<BottomBarItemData>(
                BottomBarItemData(
                    title = "Chats",
                    route = NavRoutes.Destination.ChatListScreen.route,
                    image = if (isHomeSelected) R.drawable.filled_chat else R.drawable.chat
                ),
                BottomBarItemData(
                    title = "Updates",
                    route = NavRoutes.Destination.StatusScreen.route,
                    image = if (isStatusSelected) R.drawable.filled_status else R.drawable.status
                ),
                BottomBarItemData(
                    title = "Profile",
                    route = NavRoutes.Destination.ProfileScreen.route,
                    image = if (isProfileSelected) R.drawable.filled_profile else R.drawable.filled_profile
                ),
            )


            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
            ) {

                BottomNavigationBar(
                    navController,
                    listItems,
                    onItemClick = { navController.navigate(it.route) }
                )

            }


        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(
                        context,
                        "You can't use this feature for yet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                Image(
                    imageVector = Icons.Filled.Upload,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), contentAlignment = Alignment.Center
        ) {
            Text(text = "Status Screen")
        }
    }
}