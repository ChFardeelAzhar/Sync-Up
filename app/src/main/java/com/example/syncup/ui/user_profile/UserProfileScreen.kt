package com.example.syncup.ui.user_profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.syncup.R
import com.example.syncup.ui.theme.SkyAppColor
import com.example.syncup.ui.theme.SkyAppColor

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(
    navController: NavController,
    chatId: String,
    viewModel: UserProfileViewModel = hiltViewModel()
) {


    LaunchedEffect(chatId) {
        viewModel.getChatById(chatId)
    }

    val currentChatPartners by viewModel.currentChat.collectAsState()
    val currentUser by viewModel.currentUserData.collectAsState()

    val chatUser =
        if (currentUser?.id == currentChatPartners?.user1?.id) currentChatPartners?.user2 else currentChatPartners?.user1


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = {},
                navigationIcon = {
                    Image(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .clickable {
                                navController.popBackStack()
                            }
                            .padding(vertical = 8.dp)
                            .background(shape = CircleShape, color = Color.Transparent)
                            .padding(5.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Transparent,
                    actionIconContentColor = Color.Transparent
                )
            )

        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Card(
                modifier = Modifier
                    .size(150.dp)
                    .clip(shape = CircleShape),
                elevation = CardDefaults.cardElevation(4.dp),

            ) {

                if (chatUser?.imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                } else {

                    AsyncImage(
                        model = chatUser?.imageUrl ?: "",
                        contentDescription = "",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(shape = CircleShape)
                            .padding(5.dp),
                        contentScale = ContentScale.Crop,
                    )

                }
            }


            Spacer(modifier = Modifier.size(5.dp))


            Text(
                text = chatUser?.name ?: "", // user name
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,

                )

            Text(
                text = chatUser?.number ?: "", // user number
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8f),
                style = MaterialTheme.typography.titleMedium

            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Image(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    colorFilter = ColorFilter.tint(color = SkyAppColor),
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp),

                    )

                Text(
                    text = chatUser?.email.toString() ?: "dummy", // user email
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8f),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 20.dp)

                )

            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Image(
                    imageVector = Icons.Default.Info,  // user profile bio
                    contentDescription = "Profile Bio",
                    colorFilter = ColorFilter.tint(color = SkyAppColor),
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp),

                    )

                Text(
                    text = chatUser?.profileBio ?: "",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8f),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 20.dp)

                )

            }


        }

    }


}

@Preview(showBackground = true)
@Composable
private fun Preview() {

//    UserProfileScreen()

}