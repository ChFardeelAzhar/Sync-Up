package com.example.syncup.auth.login

import android.annotation.SuppressLint
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.syncup.R
import com.example.syncup.auth.signup.TopSection
import com.example.syncup.ui.theme.PurpleAppColor
import com.example.syncup.ui.theme.SkyAppColor
import com.example.syncup.utils.CustomCircularProgressBar
import com.example.syncup.utils.NavRoutes
import com.example.syncup.utils.ResultState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val state = viewModel.signInState.collectAsState()
    var showLoadingIndicator by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
                .padding(horizontal = 12.dp, vertical = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            TopSection(
                image = painterResource(R.drawable.sync_up),
                title = "Log In"
            )

            Spacer(Modifier.size(10.dp))

            Text(
                text = "Email", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(4.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
            )

            Text(
                text = "Password", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(4.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) {
                        Icons.Default.Visibility // Visible icon
                    } else {
                        Icons.Default.VisibilityOff // Hidden icon
                    }
                    if (password.isNotEmpty()){
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.size(20.dp))

            Button(
                onClick = {

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(
                            context,
                            "Please enter the correct email",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (password.length < 6) {
                        Toast.makeText(
                            context,
                            "Password should contain at least 6 letters",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.login(email, password)
                    }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty(),
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SkyAppColor,
                                PurpleAppColor
                            )
                        ),
                        shape = RoundedCornerShape(35.dp)
                    )
                    .padding(horizontal = 3.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(text = "Log In", color = Color.White)
            }

            Spacer(modifier = Modifier.size(20.dp))

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {

                TextButton(onClick = {
                    navController.navigate(NavRoutes.Destination.SignUp.route) {
                        launchSingleTop = true
                    }
                }, modifier = Modifier.padding(bottom = 15.dp)) {
                    Text(text = "Don't have account -> Go to Sign Up")

                }

            }


        }

        when (val result = state.value) {
            is ResultState.Success -> {
                Toast.makeText(
                    context,
                    result.data,
                    Toast.LENGTH_SHORT
                ).show()
                showLoadingIndicator = false

                navController.navigate(NavRoutes.Destination.ChatListScreen.route) {
                    popUpTo(0)
                }

            }

            is ResultState.Failure -> {
                Toast.makeText(
                    context,
                    result.error.message,
                    Toast.LENGTH_SHORT
                ).show()
                showLoadingIndicator = false
            }

            ResultState.Loading -> {
                showLoadingIndicator = true
            }

            ResultState.Idle -> {

            }

        }

    }



    if (showLoadingIndicator) {
        CustomCircularProgressBar()
    }

}