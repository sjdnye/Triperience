package com.example.triperience.features.profile.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.triperience.features.authentication.presentation.AuthScreenEvents
import com.example.triperience.features.authentication.presentation.component.CustomTextField2

@Composable
fun CustomAlertDialog(
    modifier: Modifier = Modifier,
    type: Int,
    onDismissRequest: () -> Unit,
    onConfirmResetPasswordRequest: (oldPass:String, newPass:String, confirmPass:String) -> Unit,
    onConfirmChangeEmail : (newEmail:String, oldPass:String) -> Unit

    ) {
    val focusManager = LocalFocusManager.current
    var oldPass by remember {
        mutableStateOf<String>("")
    }
    var newPass by remember {
        mutableStateOf<String>("")
    }
    var confirmPass by remember {
        mutableStateOf<String>("")
    }

    var newEmail by remember {
        mutableStateOf<String>("")
    }

    Dialog(
        onDismissRequest = {
            onDismissRequest()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Card(
            modifier = modifier,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                if (type == 1){
                    Text(
                        text = "Reset Password",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(CenterHorizontally),
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    CustomTextField2(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = oldPass,
                        onValueChange = {
                            oldPass = it
                        },
                        label = "Old password",
                        placeholder = "Enter your old password",
                        KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomTextField2(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = newPass,
                        onValueChange = {
                            newPass = it
                        },
                        label = "New password",
                        placeholder = "Enter your new password",
                        KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomTextField2(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = confirmPass,
                        onValueChange = {
                            confirmPass = it
                        },
                        label = "Confirm password",
                        placeholder = "Enter your new password again",
                        KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        onClick = {
                            onConfirmResetPasswordRequest(oldPass, newPass, confirmPass)
                        },
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(
                            text = "Reset",
                            color = MaterialTheme.colors.onPrimary
                        )

                    }
                }else{
                    Text(
                        text = "Change Email",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(CenterHorizontally),
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    CustomTextField2(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = oldPass,
                        onValueChange = {
                            oldPass = it
                        },
                        label = "password",
                        placeholder = "Enter your password",
                        KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomTextField2(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = newEmail,
                        onValueChange = {
                            newEmail = it
                        },
                        label = "New email",
                        placeholder = "Enter your new email",
                        KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        onClick = {
                            onConfirmChangeEmail(newEmail, oldPass)
                        },
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(
                            text = "Change",
                            color = MaterialTheme.colors.onPrimary
                        )

                    }
                }
            }

        }

    }

}