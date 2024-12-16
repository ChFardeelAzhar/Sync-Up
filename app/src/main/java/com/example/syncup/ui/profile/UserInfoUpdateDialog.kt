package com.example.syncup.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.syncup.ui.theme.SkyAppColor


@Composable
fun ShowDialogForInfoUpdate(
    userInfo: MutableState<String>,
    onSaveClick: () -> Unit,
    showDialog: MutableState<Boolean>
) {
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = {
            showDialog.value = false
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                OutlinedTextField(
                    value = userInfo.value,
                    onValueChange = { userInfo.value = it },
                    placeholder = { Text("Enter your Info") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .focusRequester(focusRequester)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = .7f)),
                    textStyle = TextStyle(fontSize = 14.sp)
                )

                TextButton(
                    onClick = {
                        onSaveClick()
                        showDialog.value = false
                    }
                ) {
                    Text("Save")
                }

            }
        },
    )

}


@Composable
fun ShowNumberDialog(
    onSaveClick: (String) -> Unit,
    showDialog: MutableState<Boolean>
) {
    val focusRequester = remember {
        FocusRequester()
    }

    var number = remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = {
            showDialog.value = false
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    "Add Chat",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, top = 15.dp),
                    textAlign = TextAlign.Start
                )

//                Spacer(modifier = Modifier.size(10.dp))

                OutlinedTextField(
                    value = number.value,
                    onValueChange = {
                        if (it.length <= 11) {
                            number.value = it
                        }
                    },
                    placeholder = { Text("Add Number for Chat") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .focusRequester(focusRequester)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = .7f)),
                    textStyle = TextStyle(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                TextButton(
                    onClick = {
                        onSaveClick(number.value)
                        showDialog.value = false
                    }
                ) {
                    Text(
                        "Add",
                        style = MaterialTheme.typography.titleMedium,
                        color = SkyAppColor,
                        modifier = Modifier.padding(5.dp)
                    )
                }

            }
        },
    )

}