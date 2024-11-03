package com.example.syncup.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.syncup.models.UserData
import com.example.syncup.ui.theme.PurpleAppColor
import com.example.syncup.ui.theme.SkyAppColor
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        popUpTo(route)
    }
}


@Composable
fun LoadingIndicatorSimple(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = SkyAppColor
        )
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ), label = ""
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(40.dp)) {
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(PurpleAppColor, SkyAppColor),
//                    start = Offset.Zero,
//                    end = Offset(size.width, size.height)
                ),
                startAngle = angle,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}


fun createOrUpdateProfile(
    name: String? = null,
    number: String? = null,
    imageUrl: String? = null,
    db: FirebaseFirestore,
    auth: FirebaseAuth,
    currentUserData: MutableState<UserData>
) {

    val currentItemState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
//    val currentItemState: StateFlow<ResultState<String>> = _currentItemState

    val uid = auth.currentUser?.uid

    val user = UserData(
        id = uid,
        name = name ?: currentUserData.value.name,
        number = number ?: currentUserData.value.number,
        imageUrl = imageUrl ?: currentUserData.value.imageUrl
    )

    uid?.let {
        db.collection("user").document(uid).get().addOnSuccessListener {
            if (it.exists()) {

                // update here
                // db.collection("user").document(uid).update()

            } else {
                db.collection("user").document(uid).set(user)
                    .addOnCompleteListener {
                        currentItemState.value = ResultState.Success("Profile created")
                        getUserById(uid)
                    }
                    .addOnFailureListener { exception ->
                        currentItemState.value = ResultState.Failure(exception)
                    }
            }
        }.addOnFailureListener {
            currentItemState.value = ResultState.Failure(it)
        }

    }
}

fun getUserById(uid: String) {

    val currentUserData = mutableStateOf<UserData?>(null)

    Firebase.firestore.collection("user").document(uid).addSnapshotListener { value, error ->
        if (value != null) {
            val user = value.toObject<UserData>()
            currentUserData.value = user
        }

        if (error != null) {
            ResultState.Failure(error) // _signUpState <- assign this failure to
        }
    }
}
