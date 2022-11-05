package com.example.triperience.features.home.presentation.component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.destinations.CommentScreenDestination
import com.example.triperience.features.destinations.PostDetailScreenDestination
import com.example.triperience.features.destinations.SearchScreenDestination
import com.example.triperience.features.destinations.UserProfileScreenDestination
import com.example.triperience.features.home.presentation.HomeViewModel
import com.example.triperience.ui.theme.customFont
import com.example.triperience.utils.common.PostItem
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val postsState by homeViewModel.posts.collectAsState()

    LaunchedEffect(key1 = true) {
        homeViewModel.homeEventFLow.collect { result ->
            when (result) {
                is ScreenUiEvent.ShowMessage -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Triperience",
                        fontFamily = customFont,
                        fontSize = 25.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp)
            ) {
                items(postsState!!) { post ->
                    PostItem(
                        onProfileClick = {
                            navigator.navigate(UserProfileScreenDestination(userId = it))
                        },
                        onImageClick = {
                        },
                        onCommentClick = {
                            navigator.navigate(CommentScreenDestination(postId = it.toString()))
                        },
                        homeViewModel = homeViewModel,
                        post = post
                    )
                }
            }
            if (homeViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}