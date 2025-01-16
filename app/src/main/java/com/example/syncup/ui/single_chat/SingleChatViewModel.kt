package com.example.syncup.ui.single_chat

import androidx.lifecycle.ViewModel
import com.example.syncup.constants.CHATS
import com.example.syncup.constants.MESSAGE
import com.example.syncup.constants.User_Node
import com.example.syncup.models.ChatData
import com.example.syncup.models.Message
import com.example.syncup.models.UserProfileData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SingleChatViewModel @Inject constructor(

    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {


    private val _singleChatState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val singleChatState: StateFlow<ResultState<String>> = _singleChatState

    private var _currentUserData = MutableStateFlow<UserProfileData?>(null)
    val currentUserData: StateFlow<UserProfileData?> = _currentUserData

    private val _currentChat = MutableStateFlow<ChatData?>(null)
    val currentChat: StateFlow<ChatData?> = _currentChat

    private val _messageList = MutableStateFlow<List<Message>>(listOf())
    val messageList: StateFlow<List<Message>> = _messageList

    private var currentChatMessageListener: ListenerRegistration? = null

    init {
        val id = auth.currentUser?.uid
        id?.let {
            getUserById(id)
        }
    }

    private fun getUserById(uid: String) {

        db.collection(User_Node).document(uid).addSnapshotListener { value, error ->

            if (value != null) {
                val user = value.toObject<UserProfileData>()
                _currentUserData.value = user

            }

            if (error != null) {
                ResultState.Failure(error) // _profileState <- assign this failure to
            }

        }

    }

    fun onSendChat(chatId: String, message: String) {

        val time = System.currentTimeMillis()

        val messageRef = db.collection(CHATS).document(chatId).collection(MESSAGE).document()

        val msg = Message(
            id = messageRef.id,
            sendBy = currentUserData.value?.id,
            message = message,
            timeStamp = time,
        )

        messageRef.set(msg)
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

    fun deleteMessages(chatId: String, messages: List<Message>) {

        _singleChatState.value = ResultState.Loading

        if (messages.isNotEmpty()) {

            messages.forEach { message ->

                db.collection(CHATS)
                    .document(chatId)
                    .collection(MESSAGE)
                    .document(message.id)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            _singleChatState.value = ResultState.Success("Messages Deleted!")
                        }
                    }.addOnFailureListener {
                        _singleChatState.value = ResultState.Failure(it)

                    }

            }

        }


    }


}