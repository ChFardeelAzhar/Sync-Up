package com.example.syncup.utils

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.syncup.ui.theme.PurpleAppColor
import com.example.syncup.ui.theme.SkyAppColor
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun CustomCircularProgressBar(modifier: Modifier = Modifier) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing)
        ), label = ""
    )



    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.size(40.dp)) {
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(PurpleAppColor, SkyAppColor),
                ),
                startAngle = angle,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }


    }
}


fun getCurrentTime(date: Long): String {

    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("Asia/Karachi") // Set the time zone to Pakistan
    return format.format(date) // Format the current date and time

}

fun getFormatedDate(date: Long?): String {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return format.format(date ?: Date()) // Format the current date and time
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ImageScreen(imageUrl: String) {

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

        }
    }


}