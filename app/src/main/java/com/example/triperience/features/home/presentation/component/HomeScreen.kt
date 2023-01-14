package com.example.triperience.features.home.presentation.component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.R
import com.example.triperience.features.destinations.CommentScreenDestination
import com.example.triperience.features.destinations.PostDetailScreenDestination
import com.example.triperience.features.destinations.SearchScreenDestination
import com.example.triperience.features.destinations.UserProfileScreenDestination
import com.example.triperience.features.home.presentation.HomeViewModel
import com.example.triperience.ui.theme.customFont
import com.example.triperience.utils.common.PostItem
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.core.GetPostsPublisher
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val postsState by homeViewModel.posts.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = homeViewModel.isLoading)
    val lazyListState = rememberLazyListState()
    val topBarHeight by animateDpAsState(
        targetValue = if (lazyListState.isScrolledHomeTitle) 0.dp else 56.dp,
        animationSpec = tween(durationMillis = 300)
    )


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
                    Image(
                        painter = painterResource(id = R.drawable.main_logo),
                        contentDescription = "Home screen Logo",
                        modifier = Modifier
//                            .fillMaxHeight()
                        ,
                        contentScale = ContentScale.Crop
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp,
                modifier = Modifier.height(topBarHeight)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = homeViewModel::getPosts
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp),
                    state = lazyListState
                ) {
                    items(postsState!!) { post ->
                        PostItem(
                            onProfileClick = {
                                navigator.navigate(UserProfileScreenDestination(userid = it))
                            },
                            onImageClick = { latitude, longitude ->
                                navigator.navigate(PostDetailScreenDestination(latitude, longitude))
                            },
                            onCommentClick = {
                                navigator.navigate(CommentScreenDestination(postId = it.toString()))
                            },
                            getPostsPublisher = homeViewModel.getPostsPublisher,
                            post = post
                        )
                    }
                }
            }
        }
    }
}
val LazyListState.isScrolledHomeTitle: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0