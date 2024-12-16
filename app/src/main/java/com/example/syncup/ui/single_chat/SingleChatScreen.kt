package com.example.syncup.ui.single_chat

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.syncup.R
import com.example.syncup.models.Message
import com.example.syncup.ui.theme.PurpleAppColor
import com.example.syncup.ui.theme.SkyAppColor
import com.example.syncup.utils.NavRoutes
import java.net.URL


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

    /*

    val chats by viewModel.chats.collectAsState()
    val currentChat = viewModel.chats.collectAsState().value.firstOrNull {
        it.id == chatId
    }

    val chatUser =
        if (currentUserData?.id == currentChat?.user1?.id) currentChat?.user2 else currentChat?.user1


     */

    LaunchedEffect(chatId) { // Unit for populate messages
        viewModel.getChatById(chatId)
        viewModel.populateMessages(chatId)
    }

    BackHandler {
        navController.popBackStack()
        viewModel.depopulateMessages()
    }

    val chatUser =
        if (currentUserData?.id == currentChat?.user1?.id) currentChat?.user2 else currentChat?.user1

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
                containerColor = MaterialTheme.colorScheme.background

            ) {
                ReplyBox(
                    onSendClick = {
                        viewModel.onSendChat(chatId = chatId, message = messageText.value)
                        messageText.value = ""
                    },
                    messageText = messageText
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
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
                }
            )

            MessagesBox(
                messages = chatMessages,
                currentChatId = chatUser?.id ?: "",
                imageUrl = chatUser?.imageUrl ?: ""
            )

            /*
            ReplyBox(
                onSendClick = {
                    viewModel.onSendChat(chatId = chatId, message = messageText.value)
                    messageText.value = ""
                },
                messageText = messageText
            )


             */

        }

    }
}


@Composable
fun CustomTopBar(
    onBackClick: () -> Unit,
    name: String,
    imageUrl: String,
    onImageClick: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp),
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

            AsyncImage(
                model = imageUrl,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(shape = CircleShape)
                    .clickable {
                        onImageClick(imageUrl)
                    },
                contentScale = ContentScale.Crop
            )

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
fun MessagesBox(messages: List<Message>, currentChatId: String, imageUrl: String) {

    val listState = rememberLazyListState()

    // Scroll to the bottom when the composable is first displayed
    LaunchedEffect(messages) {
        listState.scrollToItem(messages.size)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(messages) { msg ->

            val color =
                if (msg.sendBy == currentChatId) MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.1f
                ) else SkyAppColor

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(

                        start = if (msg.sendBy == currentChatId) 5.dp else 65.dp,
                        top = 5.dp,
                        bottom = 5.dp,
                        end = if (msg.sendBy == currentChatId) 65.dp else 5.dp,

                    )
                    .clickable {

                    },
                horizontalArrangement = if (msg.sendBy == currentChatId) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.Top,
            ) {

                if (msg.sendBy == currentChatId) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(shape = CircleShape)
                            .size(35.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(color = color)
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 5.dp),
                    verticalArrangement = Arrangement.Center,

                    ) {

                    Text(
                        text = msg.message.toString(),
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = msg.timeStamp.toString(),
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

@Composable
fun ReplyBox(
    onSendClick: () -> Unit,
    messageText: MutableState<String>,
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {

        OutlinedTextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },

            modifier = Modifier
                .fillMaxWidth()
                .weight(9f),
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
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color = SkyAppColor),
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clickable {

                                }
                        )

                        Image(
                            imageVector = Icons.Filled.KeyboardVoice,
                            contentDescription = null,
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color = SkyAppColor),
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
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color = SkyAppColor),
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