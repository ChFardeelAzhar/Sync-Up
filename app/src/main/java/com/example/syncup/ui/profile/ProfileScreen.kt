package com.example.syncup.ui.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.syncup.R
import com.example.syncup.ui.theme.PurpleAppColor
import com.example.syncup.ui.theme.SkyAppColor
import com.example.syncup.utils.CustomCircularProgressBar
import com.example.syncup.utils.ResultState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileScreenViewModel = hiltViewModel(),
    onLogOutClick: () -> Unit
) {


    val state = viewModel.profileState.collectAsState()
    val userData by viewModel.currentUserData.collectAsState()
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            currentImageUri = uri
        }

    val name = remember { mutableStateOf("") }
    val number = remember { mutableStateOf("") }
    var profileBio = remember { mutableStateOf("") }

    val showNameDialog = remember { mutableStateOf(false) }
    val showNumberDialog = remember { mutableStateOf(false) }
    val showProfileDialog = remember { mutableStateOf(false) }
    val showLogOutDialog = remember { mutableStateOf(false) }

    LaunchedEffect(userData) {
        name.value = userData?.name ?: ""
        number.value = userData?.number ?: "" // in future we will disable it
        profileBio.value = userData?.profileBio ?: ""
    }

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
        }

    ) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
                .padding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(contentAlignment = Alignment.BottomEnd) {

                ElevatedCard(
                    modifier = Modifier
                        .padding(top = 25.dp)
                        .clip(
                            CircleShape
                        )
                        .size(150.dp)
                        .clickable {
                            launcher.launch("image/*")
                        },
                ) {

                    val painter: Painter =
                        rememberAsyncImagePainter(currentImageUri ?: userData?.imageUrl.toString())

                    if (userData?.imageUrl.isNullOrEmpty()) {

                        Image(
                            painter = painterResource(R.drawable.profile_bg),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(
                                    CircleShape
                                )
                                .clickable {
                                    launcher.launch("image/*")
                                },
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    } else {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(
                                    CircleShape
                                )
                                .clickable {
                                    launcher.launch("image/*")
                                },
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                }

                Image(
                    imageVector = Icons.Filled.Save,
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
//                            launcher.launch("image/*")
                            currentImageUri?.let {

                                viewModel.updateProfile(
                                    context = context,
                                    imageUrl = it.toString(),
                                    name = userData?.name,
                                    number = userData?.number,
                                    profileBio = userData?.profileBio
                                )
                            }
                        },
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.size(25.dp))

            SingleDescriptionItem(
                icon = Icons.Outlined.Person,
                onEditIconClick = {
                    showNameDialog.value = true
                },
                label = "${userData?.name}",
                type = "Name"
            )

            SingleDescriptionItem(
                icon = Icons.Outlined.Info,
                onEditIconClick = {
                    showProfileDialog.value = true
                },
                label = "${userData?.profileBio}",
                type = "About"
            )


            SingleDescriptionItem(
                icon = Icons.Outlined.Phone,
                onEditIconClick = {
//                    showNumberDialog.value = true
                },
                label = "${userData?.number}",
                type = "Phone"
            )

            Spacer(modifier = Modifier.size(25.dp))

            Button(
                onClick = {
//                    onLogOutClick()
                    showLogOutDialog.value = true

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

    when (val value = state.value) {
        is ResultState.Success -> {
            Toast.makeText(context, value.data, Toast.LENGTH_SHORT).show()
        }

        is ResultState.Failure -> {
            Toast.makeText(context, "${value.error.message}", Toast.LENGTH_SHORT).show()
        }

        ResultState.Loading -> {
            CustomCircularProgressBar()
        }

        ResultState.Idle -> {

        }
    }

    if (showNameDialog.value) {
        ShowDialogForInfoUpdate(
            userInfo = name,
            showDialog = showNameDialog,
            onSaveClick = {
                viewModel.updateProfile(
                    context = context,
                    name = name.value
                )
            }
        )
    }

    if (showProfileDialog.value) {
        ShowDialogForInfoUpdate(
            userInfo = profileBio,
            showDialog = showProfileDialog,
            onSaveClick = {
                viewModel.updateProfile(
                    context = context,
                    profileBio = profileBio.value
                )
            }
        )
    }

    if (showNumberDialog.value) {
        ShowDialogForInfoUpdate(
            userInfo = number,
            showDialog = showNumberDialog,
            onSaveClick = {
                viewModel.updateProfile(
                    context = context,
                    number = number.value
                )
            }
        )
    }

    if (showLogOutDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogOutDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogOutDialog.value = false
                        onLogOutClick()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogOutDialog.value = false
                    }
                ) {
                    Text("No")
                }
            },
            title = {
                Text(text = "Are you sure? want to Logout ?")
            }
        )

    }


}


@Composable
fun SingleDescriptionItem(
    icon: ImageVector,
    onEditIconClick: () -> Unit,
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
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .clickable {
                        onEditIconClick()
                    }
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
            modifier = Modifier
                .padding(top = 10.dp, end = 16.dp)
                .clickable {
                    onEditIconClick()
                }
        )
    }

}