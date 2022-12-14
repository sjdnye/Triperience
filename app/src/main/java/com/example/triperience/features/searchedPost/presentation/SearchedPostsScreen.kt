package com.example.triperience.features.searchedPost.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.searchedPost.presentation.SearchedPostsViewModel
import com.example.triperience.features.destinations.CommentScreenDestination
import com.example.triperience.features.destinations.PostDetailScreenDestination
import com.example.triperience.features.destinations.UserProfileScreenDestination
import com.example.triperience.features.search.domain.model.SearchPostsOption
import com.example.triperience.utils.common.PostItem
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun SearchedPostsScreen(
    navigator: DestinationsNavigator,
    searchPostsOption: SearchPostsOption,
    searchedPostsViewModel: SearchedPostsViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {

        searchPostsOption.category?.let { searchedPostsViewModel.searchPostsByCategory(category = it) }
        searchPostsOption.city?.let { searchedPostsViewModel.searchPostsByCity(city = it) }
        searchPostsOption.latLng?.let {
            searchedPostsViewModel.searchPostsByCoordinates(
                latitude = it.latitude,
                longitude = it.longitude
            )
        }

        searchedPostsViewModel.searchEventFlow.collect { result ->
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
                    searchPostsOption.category?.let {
                        Text(text = it)
                    }
                    searchPostsOption.city?.let {
                        Text(text = it)
                    }
                    searchPostsOption.latLng?.let {
                        Text(text = "By coordinates")
                    }
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            searchedPostsViewModel.posts?.let { posts ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(posts) { post ->
                        PostItem(
                            onProfileClick = {
                                navigator.navigate(UserProfileScreenDestination(userid = it))
                            },
                            onImageClick = { latitude, longitude ->
                                navigator.navigate(
                                    PostDetailScreenDestination(
                                        latitude,
                                        longitude
                                    )
                                )
                            },
                            onCommentClick = {
                                navigator.navigate(CommentScreenDestination(postId = it.toString()))

                            },
                            getPostsPublisher = searchedPostsViewModel.getPostsPublisher,
                            post = post
                        )
                    }
                }
            }
            if (searchedPostsViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}