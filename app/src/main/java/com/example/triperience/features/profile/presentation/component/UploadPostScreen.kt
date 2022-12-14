package com.example.triperience.features.profile.presentation.component

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.MainActivity
import com.example.triperience.R
import com.example.triperience.features.authentication.presentation.component.CustomTextField2
import com.example.triperience.features.profile.domain.model.CategoryItem
import com.example.triperience.features.profile.domain.model.PostCategory
import com.example.triperience.features.profile.presentation.UploadPostViewModel
import com.example.triperience.ui.theme.LightGreen800
import com.example.triperience.ui.theme.LightPink800
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
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerColors
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

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
    val scoreOption = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map { it.toString() }

    val mYear: Int
    val mMonth: Int
    val mDay: Int
    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()
    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    mCalendar.time = Date()
    var formattedDate by remember {
        mutableStateOf(
            ""
        )
    }
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            formattedDate = "${mYear}.${mMonth + 1}.${mDayOfMonth}"
        }, mYear, mMonth, mDay
    )

    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]
    // Value for storing time as a string
    var formattedTime by remember {
        mutableStateOf(
            ""
        )
    }
    // Creating a TimePicker dialod
    val mTimePickerDialog = TimePickerDialog(
        context,
        { _, mHour: Int, mMinute: Int ->
            formattedTime = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )

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
                contentColor = MaterialTheme.colors.onBackground,
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
             Box(modifier = Modifier){
                 if (viewModel.imageUri != null){
                     Image(
                         painter = if (viewModel.imageUri == null) {
                             painterResource(id = R.drawable.default_icon_white)
                         } else rememberAsyncImagePainter(model = Uri.parse(viewModel.imageUri.toString())),
                         contentDescription = "",
                         contentScale = ContentScale.FillBounds,
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(400.dp)
                             .clip(RoundedCornerShape(10.dp)),
                         colorFilter = if (viewModel.imageUri == null) ColorFilter.tint(MaterialTheme.colors.onBackground) else null
                     )
                 }
             }
                Spacer(modifier = Modifier.height(10.dp))
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
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )

                Text(
                    text = "Location",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier.align(CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))
                CustomTextField2(
                    value = viewModel.city,
                    onValueChange = {
                        viewModel.city = it
                    },
                    label = "Place...",
                    placeholder = "Enter the name of city",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    //Location
                    Column(
                        modifier = Modifier
                            .weight(0.5f),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Auto",
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .clickable {
                                    if (locationCoarsePermission.status.isGranted && locationFinePermission.status.isGranted) {
                                        viewModel.getCurrentLocation()
                                    } else if (locationCoarsePermission.status.shouldShowRationale || locationFinePermission.status.shouldShowRationale) {
                                        showLocationPermissionRationale.value =
                                            showLocationPermissionRationale.value.copy(
                                                showDialog = true,
                                                message = "Triperience Requires this geoLocation permission to access your location.",
                                                imageVector = Icons.Filled.MyLocation,
                                                permission = Constants.LOCATION
                                            )
                                    } else {
                                        locationCoarsePermission.launchPermissionRequest()
                                        locationFinePermission.launchPermissionRequest()
                                    }
                                }
                        )
                        Divider(modifier = Modifier.fillMaxWidth())
                        Text(
                            text = "By city",
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .clickable {
                                    viewModel.getMapLocationByCity(context = context)
                                }
                        )
                    }
                    Row(
                        modifier = Modifier.weight(2f),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = if (viewModel.latitude == null) {
                                "latitude"
                            } else viewModel.latitude.toString(),
                            fontWeight = FontWeight.Thin

                        )
                        Text(
                            text = if (viewModel.longitude == null) {
                                "longitude"
                            } else viewModel.longitude.toString(),
                            fontWeight = FontWeight.Thin
                        )
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.align(CenterHorizontally),
                        text = "Time",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Thin
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = CenterHorizontally
                        ) {
                            Text(
                                text = "Pick date",
                                color = MaterialTheme.colors.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        mDatePickerDialog.show()
                                    }
                            )
                            Text(text = formattedDate)
                        }
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pick time",
                                color = MaterialTheme.colors.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        mTimePickerDialog.show()
                                    }
                            )
                            Text(text = formattedTime)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )

                Text(
                    modifier = Modifier.align(CenterHorizontally),
                    text = "Category",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Thin
                )
                CategorySection(viewModel = viewModel)

                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )
                AdvantagesSection(
                    viewModel = viewModel,
                    onDoneClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )

                DisAdvantagesSection(
                    viewModel = viewModel,
                    onDoneClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )
                CustomTextField2(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Start),
                    value = viewModel.description,
                    onValueChange = {
                        viewModel.description = it
                    },
                    label = "Caption...",
                    placeholder = "Enter your description here",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                    maxLine = 4,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )
                CustomExposedDropDownMenu(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .fillMaxWidth(0.5f),
                    options = scoreOption,
                    onClick = {
                        viewModel.score = it
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(CenterHorizontally)
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        keyboardController?.hide()
                        viewModel.uploadPost(pickedTime = formattedTime, pickedDate = formattedDate)
                    }
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .wrapContentHeight(),
                            color = MaterialTheme.colors.onPrimary
                        )
                    } else {
                        Text(
                            text = "Upload",
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }

            if (showLocationPermissionRationale.value.showDialog) {
                PermissionRationaleDialog(
                    message = showLocationPermissionRationale.value.message,
                    imageVector = showLocationPermissionRationale.value.imageVector!!,
                    onRequestPermission = {
                        showLocationPermissionRationale.value =
                            showLocationPermissionRationale.value.copy(showDialog = false)
                        when (showLocationPermissionRationale.value.permission) {
                            Constants.LOCATION -> {
                                locationCoarsePermission.launchPermissionRequest()
                                locationFinePermission.launchPermissionRequest()
                            }
                        }
                    }
                ) {
                    showLocationPermissionRationale.value =
                        showLocationPermissionRationale.value.copy(showDialog = false)
                }
            }
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
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        TextField(
            modifier = Modifier.fillMaxWidth(),
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

@Composable
fun CategorySection(
    viewModel: UploadPostViewModel
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        TripCategoryItem(
            modifier = Modifier,
            painter = R.drawable.sea,
            isSelected = viewModel.category == PostCategory.Sea,
            onClick = {
                viewModel.category = PostCategory.Sea

            }
        )
        TripCategoryItem(
            modifier = Modifier,
            painter = R.drawable.forest,
            isSelected = viewModel.category == PostCategory.Jungle,
            onClick = {
                viewModel.category = PostCategory.Jungle
            }
        )
        TripCategoryItem(
            modifier = Modifier,
            painter = R.drawable.mountain,
            isSelected = viewModel.category == PostCategory.Mountainous,
            onClick = {
                viewModel.category = PostCategory.Mountainous
            }
        )
        TripCategoryItem(
            modifier = Modifier,
            painter = R.drawable.desert,
            isSelected = viewModel.category == PostCategory.Desert,
            onClick = {
                viewModel.category = PostCategory.Desert
            }
        )
        TripCategoryItem(
            modifier = Modifier,
            painter = R.drawable.city,
            isSelected = viewModel.category == PostCategory.City,
            onClick = {
                viewModel.category = PostCategory.City
            }
        )
    }
}

@Composable
fun AdvantagesSection(
    viewModel: UploadPostViewModel,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = CenterHorizontally
    ) {

        Text(
            text = "Advantages ",
            fontWeight = FontWeight.Thin,
            modifier = Modifier.align(CenterHorizontally),
            color = LightGreen800,
            textAlign = TextAlign.Center,
        )
        CustomTextField2(
            modifier = Modifier
                .fillMaxWidth()
                .align(Start),
            value = viewModel.advantage_1,
            onValueChange = {
                viewModel.advantage_1 = it
            },
            label = "1.",
            placeholder = "1'st advantage",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDoneClick()
                }
            ),
            maxLine = 1,
        )
        CustomTextField2(
            modifier = Modifier
                .fillMaxWidth()
                .align(Start),
            value = viewModel.advantage_2,
            onValueChange = {
                viewModel.advantage_2 = it
            },
            label = "2.",
            placeholder = "2'nd advantage",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDoneClick()
                }
            ),
            maxLine = 1,
        )
        CustomTextField2(
            modifier = Modifier
                .fillMaxWidth()
                .align(Start),
            value = viewModel.advantage_3,
            onValueChange = {
                viewModel.advantage_3 = it
            },
            label = "3.",
            placeholder = "3'rd advantage",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDoneClick()
                }
            ),
            maxLine = 1,
        )
    }
}

@Composable
fun DisAdvantagesSection(
    viewModel: UploadPostViewModel,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Disadvantages ",
            fontWeight = FontWeight.Thin,
            modifier = Modifier.align(CenterHorizontally),
            color = LightPink800,
            textAlign = TextAlign.Center
        )

        CustomTextField2(
            modifier = Modifier
                .fillMaxWidth()
                .align(Start),
            value = viewModel.disAdvantage_1,
            onValueChange = {
                viewModel.disAdvantage_1 = it
            },
            label = "1.",
            placeholder = "1'st disadvantage",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDoneClick()
                }
            ),
            maxLine = 1,
        )
        CustomTextField2(
            modifier = Modifier
                .fillMaxWidth()
                .align(Start),
            value = viewModel.disAdvantage_2,
            onValueChange = {
                viewModel.disAdvantage_2 = it
            },
            label = "2.",
            placeholder = "2'nd disadvantage",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDoneClick()
                }
            ),
            maxLine = 1,
        )
        CustomTextField2(
            modifier = Modifier
                .fillMaxWidth()
                .align(Start),
            value = viewModel.disAdvantage_3,
            onValueChange = {
                viewModel.disAdvantage_3 = it
            },
            label = "3.",
            placeholder = "3'rd disadvantage",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDoneClick()
                }
            ),
            maxLine = 1,
        )
    }
}