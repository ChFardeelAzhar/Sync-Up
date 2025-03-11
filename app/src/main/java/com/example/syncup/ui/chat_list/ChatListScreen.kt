package com.example.syncup.ui.chat_list

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.syncup.R
import com.example.syncup.models.SingleChatUserDate
import com.example.syncup.ui.navbar.BottomBarItemData
import com.example.syncup.ui.navbar.BottomNavigationBar
import com.example.syncup.ui.profile.ShowNumberDialog
import com.example.syncup.ui.theme.SkyAppColor
import com.example.syncup.utils.CustomCircularProgressBar
import com.example.syncup.utils.NavRoutes
import com.example.syncup.utils.ResultState


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatListViewModel = hiltViewModel()
) {


    val state = viewModel.chatListState.collectAsState()
    val chats = viewModel.chats.collectAsState()
    val currentUser = viewModel.currentUserData.collectAsState()

    val showAddContactDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Chats",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddContactDialog.value = true
                },
                containerColor = SkyAppColor,
                modifier = Modifier.padding(bottom = 5.dp, end = 7.dp)
            ) {
                Image(
                    imageVector = Icons.Filled.Chat,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(
                        Color.White
                    )
                )
            }
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


        }
    ) {

        if (chats.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = Icons.Filled.EmojiPeople,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.size(30.dp)
                )
                Text("No Chats Available", color = MaterialTheme.colorScheme.onBackground)

            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {

                items(chats.value) { chat ->

                    val chatUser = if (chat.user1.id == currentUser.value?.id) {
                        chat.user2
                    } else {
                        chat.user1
                    }

                    SingleChatUser(
                        user = chatUser
                    ) {

                        val route =
                            NavRoutes.Destination.SingleChatScreen.createRoute(chat.id.toString())
                        navController.navigate(route)

                    }


                }

            }

        }

    }



    if (showAddContactDialog.value) {
        ShowNumberDialog(
            onSaveClick = {
                viewModel.onAddChat(it)
            },
            showDialog = showAddContactDialog
        )
    }

    when (val value = state.value) {
        ResultState.Idle -> {

        }

        is ResultState.Success -> {
            Toast.makeText(context, value.data, Toast.LENGTH_SHORT).show()
        }

        ResultState.Loading -> {
            CustomCircularProgressBar()
        }

        is ResultState.Failure -> {
            Toast.makeText(context, value.error.message, Toast.LENGTH_SHORT).show()
        }
    }

}


@Composable
fun SingleChatUser(
    user: SingleChatUserDate,
    onChatClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onChatClick()
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape),
            shape = CircleShape,
//            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            if (user.imageUrl.isNullOrEmpty()) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = CircleShape)
//                    .background(color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
                        .clickable {

                        },
                    contentScale = ContentScale.Fit
                )
            } else {
                AsyncImage(
                    model = user.imageUrl, // we will write a function which will give us the image from supabase
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = CircleShape)
                        .clickable {

                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.size(5.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                user.name.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                user.number.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f)
            )
        }

    }

}