package com.example.syncup.ui.profile

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.syncup.models.UserData
import com.example.syncup.utils.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.xml.transform.Result

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {


    private val _currentUserData = MutableStateFlow<UserData?>(null)
    val currentUserData: StateFlow<UserData?> = _currentUserData

    private var _profileState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val profileState: StateFlow<ResultState<String>> = _profileState


    init {
        val uid = auth.currentUser?.uid
        uid?.let {
            getUserById(uid)
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

        val imageRef = imageUrl?.let {
            copyUriToInternalStorage(
                context,
                uri = it.toUri(),
                "${name}-${System.currentTimeMillis()}.jpg"
            )
        }


        val user = UserData(
            id = uid,
            name = name ?: currentUserData.value?.name,
            number = number ?: currentUserData.value?.number,
            imageUrl = imageRef ?: currentUserData.value?.imageUrl,
            profileBio = profileBio ?: currentUserData.value?.profileBio
        )

        uid?.let {

            val map = hashMapOf<String, Any>()

            user.id?.let { map["id"] = it }
            user.name?.let { map["name"] = it.toString() }
            user.number?.let { map["number"] = it }
            user.imageUrl?.let { map["imageUrl"] = it }
            user.profileBio?.let { map["profileBio"] = it }

            db.collection("user").document(uid).update(map).addOnCompleteListener {
                if (it.isSuccessful) {
                    _profileState.value = ResultState.Success("Profile Updated Successfully")
                }
            }.addOnFailureListener {
                _profileState.value = ResultState.Failure(it)
            }
        }

    }

//    fun updateProfileWithImage(context: Context, uri: Uri, name: String?, number: String?) {
//        val imagePath = copyUriToInternalStorage(context, uri, "$name.jpg")
//        updateProfile(name = name, number = number, imageUrl = imagePath, context)
//    }

    private fun getUserById(uid: String) {

        db.collection("user").document(uid).addSnapshotListener { value, error ->

            if (value != null) {
                val user = value.toObject<UserData>()
                _currentUserData.value = user
            }

            if (error != null) {
                ResultState.Failure(error) // _profileState <- assign this failure to
            }

        }

    }

    private fun copyUriToInternalStorage(context: Context, uri: Uri, filename: String): String? {

        val file = File(context.filesDir, filename)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream: OutputStream = FileOutputStream(file)

        return try {
            // .use{} is a kotlin function which basic purpose is to close inputStream and OutputStream after the done
            inputStream?.use { streamInput ->
                outputStream.use { streamOutput ->
                    streamInput.copyTo(streamOutput)
                    _profileState.value = ResultState.Success("Image saved in Internal Storage")
                }
            }

            file.absolutePath

            /*
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
             */

        } catch (e: Exception) {
            _profileState.value = ResultState.Failure(e)
            null
        }
    }


}