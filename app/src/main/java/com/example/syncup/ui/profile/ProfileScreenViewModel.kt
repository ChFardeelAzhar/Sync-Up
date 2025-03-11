package com.example.syncup.ui.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syncup.constants.User_Node
import com.example.syncup.models.UserProfileData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlin.jvm.Throws

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val supabaseStorage: Storage,
) : ViewModel() {


    private var _profileState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val profileState: StateFlow<ResultState<String>> = _profileState

    private val _currentUserData = MutableStateFlow<UserProfileData?>(null)
    val currentUserData: StateFlow<UserProfileData?> = _currentUserData


    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        val uid = auth.currentUser?.uid
        uid?.let {
            getUserById(it)
        }
    }


    fun updateProfile(
        context: Context,
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null,
        profileBio: String? = null
    ) {
        _profileState.value = ResultState.Loading

        val uid = auth.currentUser?.uid

        /*
        val url = imageUrl?.let {
            viewModelScope.launch {
                uploadImageToSupabase(it.toUri(), context = context, name = name ?: "")
            }
        }


         */

        val user = UserProfileData(
            id = uid,
            name = name ?: currentUserData.value?.name,
            number = number ?: currentUserData.value?.number,
            imageUrl = imageUrl ?: currentUserData.value?.imageUrl,
            profileBio = profileBio ?: currentUserData.value?.profileBio
        )

        uid?.let {

            val map = hashMapOf<String, Any>()

            user.id?.let { map["id"] = it }
            user.name?.let { map["name"] = it.toString() }
            user.number?.let { map["number"] = it }
            user.imageUrl?.let { map["imageUrl"] = it }
            user.profileBio?.let { map["profileBio"] = it }

            db.collection(User_Node).document(uid).update(map).addOnCompleteListener {
                if (it.isSuccessful) {
                    _profileState.value = ResultState.Success("Profile Updated on firebase")
                }
            }.addOnFailureListener {
                _profileState.value = ResultState.Failure(it)
            }
        }

    }


    /*
    fun uploadImageToSupabase(
        uri: Uri,
        name: String,
        context: Context
    ) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _profileState.value = ResultState.Loading
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val byteArray =
                        inputStream?.use { it.readBytes() }
                            ?: throw IllegalArgumentException("File not found")

                    val fileName = "$name-${System.currentTimeMillis()}.jpg"

                    // Upload image to Supabase
                    supabaseStorage.from("images").upload(fileName, byteArray)

                    // Get the public URL of the uploaded image
                    val fileUrl = supabaseStorage.from("images").publicUrl(fileName)

                    // Update Firestore with the new image URL
                    updateProfile(context, imageUrl = fileUrl)

                    _newImageUrl.value = fileUrl

                    _currentImageUri.value = fileUrl.toUri()
                    _profileState.value = ResultState.Success("Image Uploaded: $fileUrl")

                } catch (e: Exception) {
                    ResultState.Failure(e)
                }
            }
        }
    }



     */

    fun uploadImageToSupabase(imageUri: Uri?, context: Context, name: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Get the selected image URI
                val uri = imageUri ?: throw IllegalArgumentException("No image selected")

                // Convert the URI to a byte array
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val byteArray = inputStream?.use { it.readBytes() }
                    ?: throw IllegalArgumentException("Failed to read image")

                // Generate a unique file name
                val fileName = "$name-${System.currentTimeMillis()}.jpg"

                // Upload the image to Supabase
                supabaseStorage.from("images").upload(fileName, byteArray)

                // Get the public URL of the uploaded image
                val fileUrl = supabaseStorage.from("images").publicUrl(fileName)

                updateProfile(imageUrl = fileUrl, context = context)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e("ProfileScreenViewModel", "Error uploading image: ${e.message}")
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
                ResultState.Failure(error)

            }

        }

    }


}