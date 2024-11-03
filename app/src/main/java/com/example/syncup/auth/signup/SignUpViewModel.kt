package com.example.syncup.auth.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.syncup.models.UserData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
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

    private val currentUserData = mutableStateOf<UserData?>(null)

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

                val user = auth.currentUser
                createOrUpdateProfile(name, number)
                val changeProfile = UserProfileChangeRequest.Builder().setDisplayName(name).build()

                user?.updateProfile(changeProfile)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        _signUpState.value = ResultState.Success("Profile updated")
                    }
                }?.addOnFailureListener {
                    _signUpState.value = ResultState.Failure(it)
                }

            }

        }.addOnFailureListener {
            _signUpState.value = ResultState.Failure(it)
        }

    }

    private fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid

        val user = UserData(
            id = uid,
            name = name ?: currentUserData.value?.name,
            number = number ?: currentUserData.value?.number,
            imageUrl = imageUrl ?: currentUserData.value?.imageUrl
        )

        uid?.let {
            db.collection("user").document(uid).get().addOnSuccessListener {
                if (it.exists()) {

                    // update here
                    // db.collection("user").document(uid).update()

                } else {
                    db.collection("user").document(uid).set(user)
                        .addOnCompleteListener {
                            _signUpState.value = ResultState.Success("Profile created")
                            getUserById(uid)
                        }
                        .addOnFailureListener { exception ->
                            _signUpState.value = ResultState.Failure(exception)
                        }
                }
            }.addOnFailureListener {
                _signUpState.value = ResultState.Failure(it)
            }

        }

    }

    private fun getUserById(uid: String) {

        db.collection("user").document(uid).addSnapshotListener { value, error ->
            if (value != null) {
                val user = value.toObject<UserData>()
                currentUserData.value = user
            }

            if (error != null) {
                ResultState.Failure(error) // _signUpState <- assign this failure to
            }
        }

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

    fun logOut() {
        auth.signOut()
        _isUserLogIn.value = false
    }

}