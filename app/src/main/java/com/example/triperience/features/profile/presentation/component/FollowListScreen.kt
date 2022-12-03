package com.example.triperience.features.profile.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.destinations.UserProfileScreenDestination
import com.example.triperience.features.profile.domain.model.FollowListItem
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.example.triperience.utils.common.SearchedUserItem
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun FollowListScreen(
    navigator: DestinationsNavigator,
    followListItem: FollowListItem,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        profileViewModel.getFollowListUsers(followList = followListItem.followList)

        profileViewModel.profileEventFlow.collect { result ->
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
        topBar = {
            TopAppBar(
                title = {
                    Text(text = followListItem.title)
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 0.dp
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    snackbarData = data,
                    backgroundColor = MaterialTheme.colors.primary
                )
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                profileViewModel.followListUser?.let { users ->
                    items(users) { user ->
                        SearchedUserItem(
                            user = user,
                            onClick = {
                                navigator.navigate(UserProfileScreenDestination(userid = user.userid))
                            }
                        )
                    }
                }
            }
            if (profileViewModel.followListIsLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

}