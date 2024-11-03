package com.example.syncup.ui.profile

import android.annotation.SuppressLint
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.syncup.R
import com.example.syncup.auth.AuthViewModel
import com.example.syncup.auth.login.LoginScreen
import com.example.syncup.ui.navbar.BottomBarItemData
import com.example.syncup.ui.navbar.BottomNavigationBar
import com.example.syncup.ui.theme.PurpleAppColor
import com.example.syncup.ui.theme.SkyAppColor
import com.example.syncup.utils.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val userData by viewModel.currentUserData.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Profile",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    },
                    navigationIcon = {
                        Image(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                                .clickable { navController.popBackStack() }
                                .padding(horizontal = 5.dp),
                        )
                    },
                )

                HorizontalDivider(
                    thickness = 0.1.dp,
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .9f),
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
                    image = if (isProfileSelected) R.drawable.filled_profile else R.drawable.profile
                ),
            )


            /*
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .height(100.dp),
            ) {

                BottomNavigationBar(
                    navController,
                    listItems,
                    onItemClick = { navController.navigate(it.route) }
                )

            }


             */

        }

    ) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
                .padding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {


            Box(contentAlignment = Alignment.BottomEnd) {

                ElevatedCard(
                    modifier = Modifier
                        .padding(top = 25.dp)
                        .clip(
                            CircleShape
                        )
                        .size(150.dp)
                        .clickable {

                        },
                ) {
                    Image(
                        painter = painterResource(R.drawable.app_icons),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .size(150.dp)
                            .clickable {

                            },
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )

                }

                Image(
                    imageVector = Icons.Filled.AddAPhoto,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .background(
                            shape = CircleShape, brush = Brush.verticalGradient(
                                colors = listOf(
                                    SkyAppColor, PurpleAppColor
                                )
                            )
                        )
                        .padding(10.dp)
                        .clickable {

                        },
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }


            Spacer(modifier = Modifier.size(25.dp))

            SingleDescriptionItem(
                icon = Icons.Outlined.Person,
                label = "${userData?.name}",
                type = "Name"
            )

            SingleDescriptionItem(
                icon = Icons.Outlined.Info,
                label = "Winners Never Quit Fk.",
                type = "About"
            )


            SingleDescriptionItem(
                icon = Icons.Outlined.Phone,
                label = "${userData?.number}",
                type = "Phone"
            )

            Spacer(modifier = Modifier.size(25.dp))

            Button(
                onClick = {
                    viewModel.logOut()
                    navController.navigate(NavRoutes.Destination.Login.route) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SkyAppColor,
                                PurpleAppColor
                            )
                        ),
                        shape = RoundedCornerShape(35.dp)
                    )
                    .padding(horizontal = 3.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(text = "Logout", color = Color.White)
            }

        }
    }


}


@Composable
fun SingleDescriptionItem(
    icon: ImageVector,
    label: String,
    type: String
) {
    Box(contentAlignment = Alignment.TopEnd) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),

            ) {

            Image(
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onBackground.copy(
                        alpha = .5f
                    )
                ),
                modifier = Modifier.padding(start = 8.dp, end = 16.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = type,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.size(10.dp))

                HorizontalDivider(
                    thickness = 0.1.dp,
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .9f),
                )


            }

        }

        Image(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
            colorFilter = ColorFilter.tint(SkyAppColor),
            modifier = Modifier.padding(top = 10.dp, end = 16.dp)
        )
    }

}