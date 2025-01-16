package com.example.syncup.auth.signup

import androidx.lifecycle.ViewModel
import com.example.syncup.constants.User_Node
import com.example.syncup.models.UserProfileData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore

) : ViewModel() {


    private var _signUpState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val signUpState: StateFlow<ResultState<String>> = _signUpState

    private val _isUserLogIn: MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)
    val isUserSignIn: StateFlow<Boolean> = _isUserLogIn


    init {
        checkUserLoginStatus()
    }

    fun signUp(
        name: String,
        number: String,
        email: String,
        password: String
    ) {

        _signUpState.value = ResultState.Loading

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                _signUpState.value = ResultState.Success("Sign Up successfully")

                val user = auth.currentUser?.uid
//                createOrUpdateProfile(name, number)

                val userProfileData = UserProfileData(
                    id = user,
                    name = name,
                    number = number,
                    email = email,
                    imageUrl = null,
                    profileBio = "Hey i am synced Up!"
                )

                user?.let {
                    db.collection(User_Node).document(user).set(userProfileData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                _signUpState.value = ResultState.Success("Profile Created")
                            }
                        }.addOnFailureListener {
                            _signUpState.value = ResultState.Failure(it)
                        }
                }

            }

        }.addOnFailureListener {
            _signUpState.value = ResultState.Failure(it)
        }

    }


    private fun checkUserLoginStatus() {
        val user = auth.currentUser
        _isUserLogIn.value = user != null
    }

    /*
    fun logOut() {
        auth.signOut()
        _isUserLogIn.value = false
    }
     */

}