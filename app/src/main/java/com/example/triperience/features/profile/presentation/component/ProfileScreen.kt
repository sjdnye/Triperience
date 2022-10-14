package com.example.triperience.features.profile.presentation.component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.authentication.presentation.AuthViewModel
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
import com.example.triperience.features.destinations.EditScreenDestination
import com.example.triperience.features.destinations.UploadPostScreenDestination
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userId: String? = null
) {
    val context = LocalContext.current
    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        profileViewModel.profileEventFlow.collect {
            when (it) {
                is ScreenUiEvent.ShowMessage -> {
                    if (it.isToast) {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    } else {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = it.message
                        )
                    }
                }

            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    backgroundColor = MaterialTheme.colors.primary,
                    snackbarData = data
                )
            }
        },
        sheetContent = {
            CustomBottomSheet(
                onClick = {
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                    when (it) {
                        "upload" -> {
                            navigator.navigate(UploadPostScreenDestination)
                        }
                        "edit" -> {
                            //go to EditProfileScreen
                            navigator.navigate(EditScreenDestination)

                        }
                        "out" -> {
                            profileViewModel.showDialog = true
                        }
                    }
                },
            )
        },
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (sheetState.isCollapsed) {
                                    sheetState.expand()
                                } else {
                                    sheetState.collapse()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu button"
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        },

        ) {
        Box(modifier = Modifier.fillMaxSize()) {
            profileViewModel.state.user?.let { user ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //Header
                    ProfileHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f),
                        user = user
                    )
                    Spacer(modifier = Modifier.height(5.dp))
//                    Text(
//                        text = "Posts: ${user.posts.size.toString()}",
//                        style = TextStyle(
//                            color = MaterialTheme.colors.primary,
//                            fontFamily = FontFamily.Default,
//                            fontSize = 30.sp,
//                        )
//                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(8f)
                    ) {

                    }
                }
                if (user.posts.isEmpty()) {
                    Text(
                        text = "There is nothing to show!!",
                        modifier = Modifier.align(Center)
                    )
                }
            }
            if (profileViewModel.showDialog) {
                SimpleAlertDialog(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                    onDismissRequest = {
                        profileViewModel.showDialog = false
                    },
                    confirmButton = {
                        profileViewModel.showDialog = false
                        FirebaseAuth.getInstance().signOut()
                        navigator.popBackStack()
                        navigator.navigate(AuthWelcomeScreenDestination)

                    },
                    dismissButton = {
                        profileViewModel.showDialog = false
                    }
                )
            }

            if (profileViewModel.state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

        }
    }

}

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    user: User
) {
    Column(
        modifier = modifier
            .padding(5.dp),
        horizontalAlignment = Alignment.Start,

        ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .weight(3.5f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CoilImage(
                    imageUrl = user.profileImage,
                    modifier = Modifier
                        .size(100.dp)
                        .weight(3f)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = user.username,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .weight(1f)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "${user.following.size} following",
                        style = TextStyle(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Bottom)
                    )
                    Text(
                        text = "${user.followers.size} followers",
                        style = TextStyle(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Bottom)
                    )
                }
            }
//            Spacer(modifier = Modifier.width(2.dp))
            Divider(
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(0.5.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (user.bio.isNotEmpty()) {
                Text(
                    text = user.bio,
                    modifier = Modifier
                        .weight(6f),
                    maxLines = 4,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(6f)
                        .fillMaxSize(),
                    contentAlignment = Center
                ) {
                    Text(
                        text = "Bio is empty!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Center)
                    )
                }
            }

        }
    }
}



