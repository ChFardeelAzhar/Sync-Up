package com.example.syncup.ui.user_profile

import androidx.lifecycle.ViewModel
import com.example.syncup.constants.CHATS
import com.example.syncup.constants.User_Node
import com.example.syncup.models.ChatData
import com.example.syncup.models.UserProfileData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {


    private var _userProfileState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val userProfileState: StateFlow<ResultState<String>> = _userProfileState

    private var _currentChat: MutableStateFlow<ChatData?> = MutableStateFlow(null)
    val currentChat: StateFlow<ChatData?> = _currentChat

    private var _currentUserData = MutableStateFlow<UserProfileData?>(null)
    val currentUserData: StateFlow<UserProfileData?> = _currentUserData

    init {
        val id = auth.currentUser?.uid
        id?.let {
            getUserById(it)
        }
    }


    fun getChatById(chatId: String) {

        _userProfileState.value = ResultState.Loading

        db.collection(CHATS).document(chatId).addSnapshotListener { value, error ->

            if (value != null) {
                _currentChat.value = value.toObject<ChatData>()
            }

            if (error != null) {
                _userProfileState.value = ResultState.Failure(error)

            }


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


}