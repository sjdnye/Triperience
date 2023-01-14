package com.example.triperience.features.search.presentation

import android.annotation.SuppressLint
import android.location.Address
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.R
import com.example.triperience.features.authentication.presentation.component.AuthWelcomeScreen
import com.example.triperience.features.authentication.presentation.component.CustomTextField
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
import com.example.triperience.features.destinations.PostDetailScreenDestination
import com.example.triperience.features.destinations.SearchedPostsScreenDestination
import com.example.triperience.features.destinations.UserProfileScreenDestination
import com.example.triperience.features.home.presentation.component.PostWeatherInfoScreen
import com.example.triperience.features.search.domain.model.SearchPostsOption
import com.example.triperience.ui.theme.*
import com.example.triperience.utils.Constants
import com.example.triperience.utils.common.SearchedUserItem
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Destination
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = BringIntoViewRequester()


    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    val tabRowItems = listOf<String>("User", "Place")

    var searchQuery by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = true) {
        searchScreenViewModel.searchEventFlow.collect { result ->
            when (result) {
                is ScreenUiEvent.ShowMessage -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = result.message
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
                    snackbarData = data,
                    backgroundColor = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(bottom = 50.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        when (selectedTabIndex) {
                            0 -> {
                                searchScreenViewModel.searchUsers(searchQuery)
                            }
                            1 -> {
                                searchScreenViewModel.getMapLocation(
                                    location = searchQuery,
                                    context = context
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(text = "Search...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search user/place"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    when (selectedTabIndex) {
                                        0 -> {
                                            searchScreenViewModel.searchUsers(searchQuery)
                                        }
                                        1 -> {
                                            searchScreenViewModel.getMapLocation(
                                                location = searchQuery,
                                                context = context
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.cross_icon_18),
                                    contentDescription = "Cancel",
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
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
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                TabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = Color.Transparent,
                ) {
                    tabRowItems.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                Text(
                                    text = title,
                                    textAlign = TextAlign.Center
                                )
                            },
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                            },
                            unselectedContentColor = MaterialTheme.colors.onBackground,
                            selectedContentColor = MaterialTheme.colors.primary,

                            )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                when (selectedTabIndex) {
                    0 -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(searchScreenViewModel.users) { user ->
                                SearchedUserItem(
                                    user = user,
                                    onClick = {
                                        navigator.navigate(UserProfileScreenDestination(userid = user.userid))
                                    }
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                    1 -> {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                        searchScreenViewModel.addressList?.let {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(it) { address ->
                                    MapAddressItem(
                                        address = address,
                                        currentAddress = searchScreenViewModel.currentAddress,
                                        onAddressClick = {
                                            searchScreenViewModel.currentAddress = address
                                            searchScreenViewModel.latLng =
                                                LatLng(address.latitude, address.longitude)
                                        },
                                        onTypeClick = {
                                            searchScreenViewModel.addressList = null
                                            if (it == "city") {
                                                navigator.navigate(
                                                    SearchedPostsScreenDestination(
                                                        searchPostsOption = SearchPostsOption(
                                                            city = searchQuery
                                                        )
                                                    )
                                                )
                                            } else {
                                                navigator.navigate(
                                                    SearchedPostsScreenDestination(
                                                        searchPostsOption = SearchPostsOption(
                                                            latLng = searchScreenViewModel.latLng
                                                        )
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .padding(bottom = 50.dp, end = 10.dp),
            ) {
                AnimatedVisibility(
                    modifier = Modifier,
                    visible = selectedTabIndex == 1,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                searchScreenViewModel.latLng?.let {
                                    navigator.navigate(
                                        PostDetailScreenDestination(
                                            latitude = it.latitude,
                                            longitude = it.longitude
                                        )
                                    )
                                }
                            },
                        painter = painterResource(id = R.drawable.weather),
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        colorFilter = (if (searchScreenViewModel.latLng != null) null else Color.LightGray)?.let {
                            ColorFilter.tint(
                                color = it
                            )
                        }
                    )
                }
            }
            if (searchScreenViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MapAddressItem(
    modifier: Modifier = Modifier,
    address: Address,
    currentAddress: Address?,
    onAddressClick: (address: Address) -> Unit,
    onTypeClick: (type: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp)),

        ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    onAddressClick(address)
                },
            verticalAlignment = CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(32.dp),
                painter = painterResource(id = R.drawable.pin),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = address.countryName,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(CenterVertically)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        if (address == currentAddress) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Search by city",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(CenterVertically)
                        .clickable {
                            onTypeClick("city")
                        },
                )
                Text(
                    text = "Search by coordinate",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(CenterVertically)
                        .clickable {
                            onTypeClick("coordinate")
                        },
                )
            }
        }
    }
}



