package com.example.triperience.features.profile.presentation.component

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.R
import com.example.triperience.features.authentication.presentation.component.CustomTextField2
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.example.triperience.utils.common.ImagePickerPermissionChecker
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun EditScreen(
    navigator: DestinationsNavigator,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse(viewModel.meUser?.profileImage))
    }
    var username by remember {
        mutableStateOf(viewModel.meUser?.username!!)
    }
    var bio by remember {
        mutableStateOf(viewModel.meUser?.bio!!)
    }

    LaunchedEffect(key1 = true) {
        viewModel.profileEventFlow.collect {
            when (it) {
                is ScreenUiEvent.ShowMessage -> {
                    if (it.isToast) {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        navigator.popBackStack()
                    } else {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = it.message
                        )
                    }
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
                title = {
                   Row(
                       modifier = Modifier.fillMaxWidth(),
                       horizontalArrangement = Arrangement.Center
                   ) {
                       Text(
                           text = "Edit Profile",
                           textAlign = TextAlign.Center,
                       )
                   }
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        },
                        modifier = Modifier.wrapContentHeight()

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cross_icon_18),
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.setUserInformation(username = username, bio = bio)

                        },
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.check_icon_18),
                            contentDescription = "Accept changes",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                },
                elevation = 0.dp

            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {

                Image(
                    painter = if (imageUri == null) {
                        painterResource(id = R.drawable.default_image_profile)
                    } else rememberAsyncImagePainter(model = Uri.parse(imageUri.toString())),
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .align(CenterHorizontally)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )


                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (imageUri != null) Arrangement.SpaceAround else Arrangement.Center
                ) {
                    Text(
                        text = "Change photo",
                        style = TextStyle(
                            color = MaterialTheme.colors.primary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch {
                                    bottomSheetState.show()
                                }
                            },
                    )
                    if (imageUri != null) {
                        Text(
                            text = "Save Profile image",
                            style = TextStyle(
                                color = MaterialTheme.colors.primary,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .clickable {
                                    viewModel.uploadImageProfile(imageUrl = imageUri!!)
                                },

                            )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField2(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    label = "Username",
                    placeholder = "Enter your new username",
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
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
                    value = bio,
                    onValueChange = {
                        bio = it
                    },
                    label = "Bio",
                    placeholder = "Enter your bio",
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                    keyboardActions = KeyboardActions(
                    ),
                    maxLine = 3
                )
            }
            if (viewModel.state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Center))
            }
        }
    }
    ImagePickerPermissionChecker(
        coroutineScope,
        bottomSheetState,
        onCameraLaunchResult = { uri ->
            imageUri = uri
        },
        onGalleryLaunchResult = { uri ->
            imageUri = uri
        }
    )
}