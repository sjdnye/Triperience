package com.example.triperience.features.authentication.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.authentication.presentation.AuthViewModel
import com.example.triperience.features.authentication.presentation.AuthenticationUiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = BringIntoViewRequester()

    var email by remember {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var passwordVisibility by remember {
        mutableStateOf(false)
    }
    val icon = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff

    LaunchedEffect(key1 = true) {
        viewModel.authEventFlow.collectLatest {
            when (it) {
                is AuthenticationUiEvent.NavigateToMainScreen -> {

                }
                is AuthenticationUiEvent.ShowMessage -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = it.message
                    )
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    backgroundColor = MaterialTheme.colors.primary,
                    snackbarData = data
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {},
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to main panel",
                        Modifier
                            .clickable {
                                navigator.popBackStack()
                            }
                    )
                },
                contentColor = MaterialTheme.colors.primary,
                elevation = 0.dp,
                modifier = Modifier.shadow(0.dp)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier
                        .align(Start),
                    text = "Login",
                    style = TextStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Enter your information to continue",
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    modifier = Modifier
                        .onFocusEvent { event ->
                            if (event.isFocused) {
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        }
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colors.primary,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    label = {
                        Text(text = "Email")
                    },
                    placeholder = {
                        Text(text = "Enter email")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "enter email")
                    },
                    keyboardOptions = KeyboardOptions(
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
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colors.primary,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    label = {
                        Text(text = "Password")
                    },
                    placeholder = {
                        Text(text = "Enter password")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "enter password"
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisibility = !passwordVisibility

                            }
                        ) {
                            Icon(imageVector = icon, contentDescription = "Visibility icon")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    modifier = Modifier
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    onClick = {
                        keyboardController?.hide()
                    },
                    shape = RoundedCornerShape(10.dp),

                    ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(CenterVertically),
                            color = MaterialTheme.colors.onPrimary
                        )
                    } else {
                        Text(
                            text = "Login",
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}
