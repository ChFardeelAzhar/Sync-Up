package com.example.syncup.ui.single_chat

import androidx.lifecycle.ViewModel
import com.example.syncup.constants.CHATS
import com.example.syncup.constants.MESSAGE
import com.example.syncup.constants.User_Node
import com.example.syncup.models.ChatData
import com.example.syncup.models.Message
import com.example.syncup.models.UserData
import com.example.syncup.utils.ResultState
import com.example.syncup.utils.getCurrentTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SingleChatViewModel @Inject constructor(

    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {


    private val _singleChatState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val singleChatState: StateFlow<ResultState<String>> = _singleChatState

    private var _currentUserData = MutableStateFlow<UserData?>(null)
    val currentUserData: StateFlow<UserData?> = _currentUserData

    private val _currentChat = MutableStateFlow<ChatData?>(null)
    val currentChat: StateFlow<ChatData?> = _currentChat

    private val _messageList = MutableStateFlow<List<Message>>(listOf())
    val messageList: StateFlow<List<Message>> = _messageList

    var currentChatMessageListener: ListenerRegistration? = null

    init {
        val id = auth.currentUser?.uid
        id?.let {
            getUserById(id)
        }
    }

    private fun getUserById(uid: String) {

        db.collection("user").document(uid).addSnapshotListener { value, error ->

            if (value != null) {
                val user = value.toObject<UserData>()
                _currentUserData.value = user

            }

            if (error != null) {
                ResultState.Failure(error) // _profileState <- assign this failure to
            }

        }

    }

    fun onSendChat(chatId: String, message: String) {

        val time = getCurrentTime(Calendar.getInstance().time.time)

        val msg = Message(
            sendBy = currentUserData.value?.id,
            message = message,
            timeStamp = time,
        )

        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }

    fun getChatById(chatId: String) {

        db.collection(CHATS).document(chatId).addSnapshotListener { value, error ->
            if (value != null) {
                val chat = value.toObject<ChatData>()
                _currentChat.value = chat
            }
            if (error != null) {
                _singleChatState.value = ResultState.Failure(error)
            }
        }

    }

    fun populateMessages(chatId: String) {
        _singleChatState.value = ResultState.Loading

        currentChatMessageListener =
            db.collection(CHATS).document(chatId).collection(MESSAGE)
                .addSnapshotListener { value, error ->

                    if (value != null) {
                        _messageList.value = value.documents.mapNotNull {
                            it.toObject<Message>()
                        }.sortedBy { it.timeStamp }
                    }

                    if (error != null) {
                        _singleChatState.value = ResultState.Failure(error)
                    }
                }

    }

    fun depopulateMessages() {
        _messageList.value = listOf()
        currentChatMessageListener = null
    }

}