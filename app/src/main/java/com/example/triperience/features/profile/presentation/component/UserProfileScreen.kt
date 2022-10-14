package com.example.triperience.features.profile.presentation.component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.example.triperience.utils.Constants
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun UserProfileScreen(
    navigator: DestinationsNavigator,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userId: String? = null
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
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
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.primary,
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

                if (profileViewModel.isShowFollowButton){
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(10.dp)),
                        onClick = {
                            if (profileViewModel.mainButtonText == Constants.FOLLOW_USER_TEXT) {
                                profileViewModel.followUser(userid = user.userid)
                            } else if (profileViewModel.mainButtonText == Constants.FOLLOWING_USER_TEXT) {
                                profileViewModel.unFollowUser(userid = user.userid)
                            }
                        }
                    ) {
                        if (profileViewModel.mainButtonIsLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(CenterVertically)
                                    .size(20.dp),
                                color = MaterialTheme.colors.onPrimary
                            )
                        } else {
                            Text(
                                modifier = Modifier.align(CenterVertically),
                                text = profileViewModel.mainButtonText,
                                color = MaterialTheme.colors.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

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
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (profileViewModel.state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}


