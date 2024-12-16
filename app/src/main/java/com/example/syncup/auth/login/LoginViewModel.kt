package com.example.syncup.auth.login

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
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {


    private var _signInState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val signInState: StateFlow<ResultState<String>> = _signInState

    private val currentUserData = mutableStateOf<UserData?>(null)

    fun login(
        email: String,
        password: String
    ) {
        _signInState.value = ResultState.Loading

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                _signInState.value = ResultState.Success("Sign In successfully")

                auth.currentUser?.uid?.let {
                    getUserById(it)
                }

            }
        }.addOnFailureListener {
            _signInState.value = ResultState.Failure(it)
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
                            _signInState.value = ResultState.Success("Profile created")
                            getUserById(uid)
                        }
                        .addOnFailureListener { exception ->
                            _signInState.value = ResultState.Failure(exception)
                        }
                }

            }.addOnFailureListener {
                _signInState.value = ResultState.Failure(it)
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


}
