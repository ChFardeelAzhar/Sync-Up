package com.example.syncup.ui.single_chat

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.syncup.R
import com.example.syncup.models.ChatData
import com.example.syncup.models.Message
import com.example.syncup.ui.theme.SkyAppColor
import com.example.syncup.utils.NavRoutes
import com.example.syncup.utils.ResultState
import com.example.syncup.utils.getCurrentTime
import com.example.syncup.utils.getFormatedDate
import java.net.URL


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SingleChatScreen(
    navController: NavController,
    chatId: String,
    viewModel: SingleChatViewModel = hiltViewModel()
) {

    val messageText = remember { mutableStateOf("") }

    val currentChat by viewModel.currentChat.collectAsState()
    val currentUserData by viewModel.currentUserData.collectAsState()
    val chatMessages by viewModel.messageList.collectAsState()

    val state by viewModel.singleChatState.collectAsState()


    val context = LocalContext.current

    val chatUser =
        if (currentUserData?.id == currentChat?.user1?.id) currentChat?.user2 else currentChat?.user1


    val listState = rememberLazyListState()

    // Scroll to the bottom when the composable is first displayed
    LaunchedEffect(chatMessages) {
        listState.scrollToItem(chatMessages.size)
    }

    val groupedMessage =
        chatMessages.groupBy { getFormatedDate(it.timeStamp) }

    val selectedMessages = remember { mutableStateListOf<Message>() }
    var isSelectionMode by remember { mutableStateOf(false) }

    val currentChatId = chatUser?.id ?: ""
    val imageUrl = chatUser?.imageUrl ?: ""

    val showDeleteDialog = remember { mutableStateOf(false) }
    var showLoadingIndicator = remember { mutableStateOf(false) }

    LaunchedEffect(chatId) { // Unit for populate messages
        viewModel.getChatById(chatId)
        viewModel.populateMessages(chatId)
    }

    BackHandler {
        if (isSelectionMode) {
            isSelectionMode = false
        }
        navController.popBackStack()
        viewModel.depopulateMessages()
    }


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            if (isSelectionMode) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back Button",
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable {
                                isSelectionMode = false
                            }
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = "${selectedMessages.size} selected ",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(7f)

                    )

                    Image(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable {
                                showDeleteDialog.value = true
                            }
                    )


                }

            } else {
                CustomTopBar(
                    onBackClick = {
                        navController.popBackStack()
                        viewModel.depopulateMessages()
                    },
                    name = chatUser?.name ?: "",
                    imageUrl = chatUser?.imageUrl ?: "",
                    onImageClick = {
                        navController.navigate(
                            NavRoutes.Destination.DetailImageScreen.createImageRoute(it)
                        )
                    },
                    context = context,
                    onProfileClick = {
                        val route = NavRoutes.Destination.UserProfileScreen.createRoute(
                            id = chatId
                        )
                        navController.navigate(route)
                    }
                )
            }


            Box(
                modifier = Modifier
                    .weight(9f)
                    .fillMaxWidth()
            ) {

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedMessage.forEach { (date, message) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = date,
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .background(
                                            color = MaterialTheme.colorScheme.background.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                        .padding(5.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                        }
                        items(message) { msg ->

                            val color =
                                if (msg.sendBy == currentChatId) MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.1f
                                ) else SkyAppColor

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (selectedMessages.contains(msg)) Color.LightGray.copy(
                                            alpha = .5f
                                        ) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(1.dp)
                                    .clickable {
                                        if (isSelectionMode) {
                                            if (selectedMessages.contains(msg)) {
                                                selectedMessages.remove(msg)
                                                if (selectedMessages.isEmpty()) isSelectionMode =
                                                    false
                                            } else {
                                                selectedMessages.add(msg)
                                            }
                                        }
                                    }
                                    .padding(

                                        start = if (msg.sendBy == currentChatId) 5.dp else 65.dp,
                                        top = 5.dp,
                                        bottom = 5.dp,
                                        end = if (msg.sendBy == currentChatId) 65.dp else 5.dp,

                                        )
                                    .combinedClickable(
                                        onClick = {
                                            if (isSelectionMode) {
                                                if (selectedMessages.contains(msg)) {
                                                    selectedMessages.remove(msg)
                                                    if (selectedMessages.isEmpty()) isSelectionMode =
                                                        false
                                                } else {
                                                    selectedMessages.add(msg)
                                                }
                                            }
                                        },
                                        onLongClick = {

                                            isSelectionMode = true
                                            selectedMessages.add(msg)


                                        }
                                    ),
                                horizontalArrangement = if (msg.sendBy == currentChatId) Arrangement.Start else Arrangement.End,
                                verticalAlignment = Alignment.Top,
                            ) {

                                if (msg.sendBy == currentChatId) {

                                    if (imageUrl.isEmpty()) {
                                        Image(
                                            painter = painterResource(R.drawable.profile),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .clip(shape = CircleShape)
                                                .size(35.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .clip(shape = CircleShape)
                                                .size(35.dp),
                                            contentScale = ContentScale.Crop

                                        )
                                    }

                                }

                                Column(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .clip(shape = RoundedCornerShape(12.dp))
                                        .background(color = color)
                                        .padding(
                                            start = 8.dp,
                                            top = 8.dp,
                                            end = 8.dp,
                                            bottom = 5.dp
                                        ),
                                    verticalArrangement = Arrangement.Center,

                                    ) {

                                    Text(
                                        text = msg.message.toString(),
                                        color = Color.White,
                                        textAlign = TextAlign.Start
                                    )

                                    Text(
                                        text = getCurrentTime(msg.timeStamp!!),
                                        color = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.wrapContentWidth(),
                                        textAlign = TextAlign.End,
                                        style = MaterialTheme.typography.labelSmall
                                    )


                                }
                            }


                        }
                    }
                }
            }


            ReplyBox(
                onSendClick = {
                    viewModel.onSendChat(
                        chatId = chatId,
                        message = messageText.value
                    )
                    messageText.value = ""
                },
                messageText = messageText,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = 10.dp)
            )

        }

    }



    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        isSelectionMode = false
                        showDeleteDialog.value = false
                        viewModel.deleteMessages(chatId, selectedMessages)
                        selectedMessages.clear()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                    }
                ) {
                    Text("No")
                }
            },
            title = {
                Text(text = "Do you want to delete ${selectedMessages.size} message ?")
            }
        )

    }

    when (val result = state) {
        is ResultState.Success -> {
            Toast.makeText(
                context,
                result.data,
                Toast.LENGTH_SHORT
            ).show()
            showLoadingIndicator.value = false
            isSelectionMode = false

        }

        is ResultState.Failure -> {
            Toast.makeText(
                context,
                result.error.message,
                Toast.LENGTH_SHORT
            ).show()
            showLoadingIndicator.value = false
        }

        ResultState.Loading -> {
            showLoadingIndicator.value = true
        }

        ResultState.Idle -> {

        }

    }


}


@Composable
fun CustomTopBar(
    onBackClick: () -> Unit,
    name: String,
    imageUrl: String,
    onImageClick: (String) -> Unit,
    context: Context,
    onProfileClick: () -> Unit
) {


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp)
                .clickable {
                    onProfileClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        onBackClick()
                    }
            )


            Card(
                modifier = Modifier
                    .size(45.dp)
                    .clip(shape = CircleShape),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                if (imageUrl.isEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = CircleShape)
                            .clickable {
                                Toast
                                    .makeText(context, "No Profile Image", Toast.LENGTH_SHORT)
                                    .show()
                            },


                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = CircleShape)
                            .clickable {
                                onImageClick(imageUrl)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))
            Text(
                name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )


        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        )

    }


}

@Composable
fun ReplyBox(
    onSendClick: () -> Unit,
    messageText: MutableState<String>,
    modifier: Modifier
) {


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {

        OutlinedTextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },

            modifier = Modifier
                .fillMaxWidth()
                .weight(9f)
                .clip(shape = RoundedCornerShape(50.dp)),
            shape = RoundedCornerShape(50.dp),
            placeholder = {
                Text("Message")
            },
            trailingIcon = {

                if (messageText.value.isEmpty()) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,

                        ) {

                        Image(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = SkyAppColor),
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clickable {

                                }
                        )

                        Image(
                            imageVector = Icons.Filled.KeyboardVoice,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = SkyAppColor),
                            modifier = Modifier
                                .padding(horizontal = 7.dp)
                                .clickable {
                                }
                        )
                    }
                } else {

                }
            }
        )

        Image(
            imageVector = Icons.Filled.Send,
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = SkyAppColor),
            modifier = Modifier
                .clickable {
                    if (messageText.value.isNotEmpty()) {
                        onSendClick()
                    }
                }
                .padding(start = 5.dp)
                .weight(1f)
                .size(30.dp)
        )

    }


}

