package com.example.triperience.features.profile.presentation.component

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.R
import com.example.triperience.features.authentication.presentation.component.CustomTextField2
import com.example.triperience.features.profile.domain.model.CategoryItem
import com.example.triperience.features.profile.domain.model.PostCategory
import com.example.triperience.features.profile.presentation.UploadPostViewModel
import com.example.triperience.utils.Constants
import com.example.triperience.utils.common.ImagePickerPermissionChecker
import com.example.triperience.utils.common.PermissionRationaleDialog
import com.example.triperience.utils.common.ShowRationale
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class
)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun UploadPostScreen(
    navigator: DestinationsNavigator,
    viewModel: UploadPostViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val locationCoarsePermission =
        rememberPermissionState(permission = Manifest.permission.ACCESS_COARSE_LOCATION)
    val locationFinePermission =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val showLocationPermissionRationale = remember { mutableStateOf(ShowRationale()) }


    LaunchedEffect(key1 = true) {
        viewModel.uploadEventFlow.collect {
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
                title = { Text(text = "Upload a new post") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                contentColor = MaterialTheme.colors.primary,
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.background
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    contentAlignment = Center
                ) {
                    Image(
                        painter = if (viewModel.imageUri == null) {
                            painterResource(id = R.drawable.broken_image)
                        } else rememberAsyncImagePainter(model = Uri.parse(viewModel.imageUri.toString())),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Select a photo",
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .clickable {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        }
                )
                Spacer(modifier = Modifier.height(10.dp))
//                CustomTextField2(
//                    modifier = Modifier,
//                    value = viewModel.description,
//                    onValueChange = {
//                        viewModel.description = it
//                    },
//                    label = "Caption",
//                    placeholder = "Enter your description here",
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Text,
//                        imeAction = ImeAction.Done
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            focusManager.clearFocus()
//                            keyboardController?.hide()
//                        }
//                    ),
//                    maxLine = 4
//                )
//                Spacer(modifier = Modifier.height(5.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    //Location
//                    Text(
//                        text = "Auto",
//                        color = MaterialTheme.colors.primary,
//                        modifier = Modifier
//                            .weight(0.5f)
//                            .clickable {
//                                if (locationCoarsePermission.status.isGranted && locationFinePermission.status.isGranted) {
//                                    viewModel.getCurrentLocation()
//                                } else if (locationCoarsePermission.status.shouldShowRationale || locationFinePermission.status.shouldShowRationale) {
//                                    showLocationPermissionRationale.value =
//                                        showLocationPermissionRationale.value.copy(
//                                            showDialog = true,
//                                            message = "Triperience Requires this location permission to access coordinates of your situation.",
//                                            imageVector = Icons.Filled.MyLocation,
//                                            permission = Constants.LOCATION
//                                        )
//                                } else {
//                                    locationCoarsePermission.launchPermissionRequest()
//                                    locationFinePermission.launchPermissionRequest()
//                                }
//                            }
//                    )
//                    Text(
//                        text = viewModel.latitude.toString(),
//                        modifier = Modifier.weight(1f)
//                    )
//                    Text(
//                        text = viewModel.longitude.toString(),
//                        modifier = Modifier.weight(1f)
//                    )
                }
//                Spacer(modifier = Modifier.height(5.dp))
//                Divider(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(5.dp)
//                )
//                Text(
//                    text = "Category :",
//                    textAlign = TextAlign.Start,
//                    modifier = Modifier.padding(5.dp)
//                )
//                Spacer(modifier = Modifier.height(5.dp))

//                FlowRow(
//                    mainAxisSpacing = 10.dp,
//                    crossAxisSpacing = 10.dp,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    TripCategoryItem(
//                        modifier = Modifier,
//                        painter = R.drawable.sea,
//                        isSelected = viewModel.category == PostCategory.Sea,
//                        onClick = {
//                            viewModel.category = PostCategory.Sea
//
//                        }
//                    )
//                    TripCategoryItem(
//                        modifier = Modifier,
//                        painter = R.drawable.forest,
//                        isSelected = viewModel.category == PostCategory.Jungle,
//                        onClick = {
//                            viewModel.category = PostCategory.Jungle
//                        }
//                    )
//                    TripCategoryItem(
//                        modifier = Modifier,
//                        painter = R.drawable.mountain,
//                        isSelected = viewModel.category == PostCategory.Mountainous,
//                        onClick = {
//                            viewModel.category = PostCategory.Mountainous
//                        }
//                    )
//                    TripCategoryItem(
//                        modifier = Modifier,
//                        painter = R.drawable.desert,
//                        isSelected = viewModel.category == PostCategory.Desert,
//                        onClick = {
//                            viewModel.category = PostCategory.Desert
//                        }
//                    )
//                    TripCategoryItem(
//                        modifier = Modifier,
//                        painter = R.drawable.city,
//                        isSelected = viewModel.category == PostCategory.City,
//                        onClick = {
//                            viewModel.category = PostCategory.City
//                        }
//                    )
//                }

//                Spacer(modifier = Modifier.height(5.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ) {
//                    Text(text = "Score: ")
//                    Spacer(modifier = Modifier.width(10.dp))
//                    CustomExposedDropDownMenu(
//                        modifier = Modifier,
//                        options = (1..10).forEach { it.toString() } as MutableList<String>,
//                        onClick = {
//
//                        }
//                    )
//
//                }
//                Spacer(modifier = Modifier.height(10.dp))
//
//                Button(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    shape = RoundedCornerShape(1.dp),
//                    onClick = {
//                        keyboardController?.hide()
//                        viewModel.uploadPost()
//                    }
//                ) {
//                    if (viewModel.isLoading) {
//                        CircularProgressIndicator(
//                            modifier = Modifier
//                                .align(Alignment.CenterVertically)
//                                .wrapContentHeight(),
//                            color = MaterialTheme.colors.onPrimary
//                        )
//                    } else {
//                        Text(
//                            text = "Upload",
//                            color = MaterialTheme.colors.onPrimary
//                        )
//                    }
//                }
//            }
//            if (showLocationPermissionRationale.value.showDialog) {
//                PermissionRationaleDialog(
//                    message = showLocationPermissionRationale.value.message,
//                    imageVector = showLocationPermissionRationale.value.imageVector!!,
//                    onRequestPermission = {
//                        showLocationPermissionRationale.value =
//                            showLocationPermissionRationale.value.copy(showDialog = false)
//                        when (showLocationPermissionRationale.value.permission) {
//                            Constants.LOCATION -> {
//                                locationCoarsePermission.launchPermissionRequest()
//                                locationFinePermission.launchPermissionRequest()
//                            }
//                        }
//                    }
//                ) {
//                    showLocationPermissionRationale.value =
//                        showLocationPermissionRationale.value.copy(showDialog = false)
//                }
//            }
        }
    }

    ImagePickerPermissionChecker(
        coroutineScope,
        bottomSheetState,
        onCameraLaunchResult = { uri ->
            viewModel.imageUri = uri
        },
        onGalleryLaunchResult = { uri ->
            viewModel.imageUri = uri
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomExposedDropDownMenu(
    modifier: Modifier,
    options: List<String>,
    onClick: (score: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        TextField(
            value = selectedOptionText,
            onValueChange = {},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            label = { Text(text = "Score") },
            readOnly = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectedOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectedOption
                        onClick(selectedOptionText)
                        expanded = false
                    }
                ) {
                    Text(text = selectedOption)
                }
            }
        }
    }
}