package com.example.syncup.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.syncup.models.UserData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore

) : ViewModel() {


    private val _isUserLogIn: MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)
    val isUserSignIn: StateFlow<Boolean> = _isUserLogIn

    private val _currentUserData = MutableStateFlow<UserData?>(null)
    val currentUserData: StateFlow<UserData?> = _currentUserData

    init {
        checkUserLoginStatus()
    }


    private fun checkUserLoginStatus() {

        val user = auth.currentUser
        _isUserLogIn.value = user != null
        if (_isUserLogIn.value) {
            user?.let {
                getUserById(it.uid)
            }
        }

    }


    private fun getUserById(uid: String) {
        db.collection("user").document(uid).addSnapshotListener { value, error ->

            if (value != null) {
                val user = value.toObject<UserData>()
                _currentUserData.value = user
            }

            if (error != null) {
                ResultState.Failure(error) // _signUpState <- assign this failure to
            }

        }

    }


    fun logOut() {
        auth.signOut()
        _isUserLogIn.value = false
    }


}