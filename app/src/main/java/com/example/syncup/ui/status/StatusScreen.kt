package com.example.syncup.ui.status

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.WifiProtectedSetup
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.syncup.R
import com.example.syncup.models.SingleChatUserDate
import com.example.syncup.ui.navbar.BottomBarItemData
import com.example.syncup.ui.navbar.BottomNavigationBar
import com.example.syncup.utils.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StatusScreen(navController: NavController, viewModel: StatusScreenViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val state = viewModel.statusState.collectAsState()
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val statuses = viewModel.statuses.collectAsState()
    val currentUser = viewModel.currentUserData.collectAsState()

    val myStatuses = statuses.value.filter { status ->
        status.user.id == currentUser.value?.id
    }

    val otherStatuses = statuses.value.filter { status ->
        status.user.id != currentUser.value?.id
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri.value = it

            // viewModel.uploadImageToSupabase(imageUri = it, context = context, name = name.value)


        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Status",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
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
                    // we will launch a gallery on click of upload status Fab button
                    launcher.launch("image/*")

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

        // here we will implement all our screen about Statuses

        if (statuses.value.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            launcher.launch("image/*")
                        }
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {


                    Image(
                        imageVector = Icons.Default.WifiProtectedSetup,
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        colorFilter = ColorFilter.tint(
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.5f
                            )
                        )

                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Text(
                        text = "Upload Status",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.titleMedium
                    )

                }

            }
        } else {

            if (!myStatuses.isNullOrEmpty()) {
                SingleStatusUser(
                    user = myStatuses[0].user,
                    onStatusClick = {

                        val statusId = myStatuses[0].user.id
                        statusId?.let {
                            navController.navigate(
                                NavRoutes.Destination.SingleStatusScreen.createStatusRoute(
                                    it
                                )
                            )
                        }

                    },
                    navController
                )
            }

        }


    }
}


@Composable
fun SingleStatusUser(
    user: SingleChatUserDate,
    onStatusClick: () -> Unit,
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onStatusClick()
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape),
            shape = CircleShape,
        ) {
            if (user.imageUrl.isNullOrEmpty()) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = CircleShape)
                        .clickable {

                        },
                    contentScale = ContentScale.Fit
                )
            } else {
                AsyncImage(
                    model = user.imageUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = CircleShape)
                        .clickable {
                            navController.navigate(
                                NavRoutes.Destination.DetailImageScreen.createImageRoute(
                                    user.imageUrl
                                )
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.size(7.dp))

        Text(
            user.name.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )


    }

}