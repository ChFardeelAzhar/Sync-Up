package com.example.syncup.ui.status

import androidx.lifecycle.ViewModel
import com.example.syncup.constants.User_Node
import com.example.syncup.models.Status
import com.example.syncup.models.UserProfileData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StatusScreenViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _statusState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val statusState: StateFlow<ResultState<String>> = _statusState

    private var _statuses = MutableStateFlow<List<Status>>(listOf())
    val statuses: StateFlow<List<Status>> = _statuses

    private var _currentUserData = MutableStateFlow<UserProfileData?>(null)
    val currentUserData: StateFlow<UserProfileData?> = _currentUserData


    init {

        val id = auth.currentUser?.uid
        id?.let {
            getUserById(it)
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