package com.example.syncup.auth

import androidx.lifecycle.ViewModel
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
class MainScreenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {


    private val _isUserLogIn: MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)
    val isUserSignIn: StateFlow<Boolean> = _isUserLogIn


    init {
        checkUserLoginStatus()
    }


    private fun checkUserLoginStatus() {

        val user = auth.currentUser
        _isUserLogIn.value = user != null

    }


    fun logOut() {
        auth.signOut()
        _isUserLogIn.value = false
    }


}