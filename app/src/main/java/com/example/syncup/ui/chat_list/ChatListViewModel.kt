package com.example.syncup.ui.chat_list


import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.syncup.constants.CHATS
import com.example.syncup.constants.User_Node
import com.example.syncup.models.ChatData
import com.example.syncup.models.SingleChatUserDate
import com.example.syncup.models.UserData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private var _currentUserData = MutableStateFlow<UserData?>(null)
    val currentUserData: StateFlow<UserData?> = _currentUserData

    private var _chatListState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val chatListState: StateFlow<ResultState<String>> = _chatListState

    private var _chats = MutableStateFlow<List<ChatData>>(listOf())
    val chats: StateFlow<List<ChatData>> = _chats


    init {

        val uid = auth.currentUser?.uid
        uid?.let {
            getUserById(it)
        }


    }


    fun onAddChat(number: String) {

        _chatListState.value = ResultState.Loading

        if (number.isEmpty() or !number.isDigitsOnly()) {
            _chatListState.value = ResultState.Success("Please enter the correct number")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", currentUserData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", currentUserData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {

                if (it.isEmpty) {

                    db.collection(User_Node).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                _chatListState.value = ResultState.Success("Invalid Number")
                            } else {
                                // here now i am ready to creat a chat room

                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id

                                val chat = ChatData(
                                    id = id,
                                    user1 = SingleChatUserDate(
                                        id = currentUserData.value?.id,
                                        name = currentUserData.value?.name,
                                        number = currentUserData.value?.number,
                                        imageUrl = currentUserData.value?.imageUrl
                                    ),
                                    user2 = SingleChatUserDate(
                                        id = chatPartner.id,
                                        name = chatPartner.name,
                                        number = chatPartner.number,
                                        imageUrl = chatPartner.imageUrl
                                    )
                                )

                                if (chatPartner.number == currentUserData.value?.number) {

                                    _chatListState.value = ResultState.Success("Invalid Number")

                                } else {

                                    db.collection(CHATS).document(id).set(chat)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                _chatListState.value =
                                                    ResultState.Success("Chat room created successfully")
                                            }
                                        }.addOnFailureListener {
                                            _chatListState.value = ResultState.Failure(it)
                                        }

                                }

                            }


                        }.addOnFailureListener {
                            _chatListState.value = ResultState.Failure(it)
                        }

                }

            }

        }


    }

    private fun getUserById(uid: String) {

        db.collection("user").document(uid).addSnapshotListener { value, error ->

            if (value != null) {
                val user = value.toObject<UserData>()
                _currentUserData.value = user
                populateChat()
            }

            if (error != null) {
                ResultState.Failure(error) // _profileState <- assign this failure to
            }

        }

    }

    private fun populateChat() {

        _chatListState.value = ResultState.Loading

        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.id", currentUserData.value?.id),
                Filter.equalTo("user2.id", currentUserData.value?.id),
            )
        ).addSnapshotListener { value, error ->
            if (value != null) {

                _chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                _chatListState.value = ResultState.Idle

                /*
                val currentChats = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                _chats.value = currentChats
                _chatListState.value = ResultState.Idle
                 */
            }

            if (error != null) {
                _chatListState.value = ResultState.Failure(error)
            }
        }

    }


}
