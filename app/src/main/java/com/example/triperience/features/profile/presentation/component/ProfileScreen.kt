package com.example.triperience.features.profile.presentation.component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
import com.example.triperience.features.destinations.*
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.example.triperience.utils.common.MyPostItem
import com.example.triperience.utils.common.PostItem
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
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
    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = profileViewModel.state.postsIsLoading)

    val lazyListState = rememberLazyListState()
    var mainTitle by remember {
        mutableStateOf<String>("")
    }

    val profileHeaderWeightAnimated by animateFloatAsState(
        targetValue = if (lazyListState.isScrolled) 1f else 2f,
        animationSpec = tween(durationMillis = 300)
    )
    val postSectionWeightAnimated by animateFloatAsState(
        targetValue = if (lazyListState.isScrolled) 9f else 8f,
        animationSpec = tween(durationMillis = 300)
    )


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
                title = {
                    Text(
                        text = mainTitle,
                        style = TextStyle(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    )

                },
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
                mainTitle = if (lazyListState.isScrolled) {
                    "${user.posts.size} post(s)"
                } else {
                    ""

                }
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //Header
                    ProfileHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(profileHeaderWeightAnimated),
                        user = user
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PostSection(
                        modifier = Modifier.weight(postSectionWeightAnimated),
                        swipeRefreshState = swipeRefreshState,
                        scope = scope,
                        profileViewModel = profileViewModel,
                        user = user,
                        navigator = navigator,
                        lazyListState = lazyListState
                    )

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
                    title = "Log out",
                    description = "Are you sure?",
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
            if (profileViewModel.showDeletePostDialog) {
                SimpleAlertDialog(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                    title = "Delete Post",
                    description = "This post will be deleted forever. Do you want to continue?",
                    onDismissRequest = {
                        profileViewModel.showDeletePostDialog = false
                    },
                    confirmButton = {
                        profileViewModel.showDeletePostDialog = false
                        profileViewModel.deletePost()
                    },
                    dismissButton = {
                        profileViewModel.showDeletePostDialog = false
                    }
                )
            }

//            if (profileViewModel.state.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            }

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

@Composable
fun PostSection(
    modifier: Modifier = Modifier,
    swipeRefreshState: SwipeRefreshState,
    scope: CoroutineScope,
    profileViewModel: ProfileViewModel,
    user: User,
    navigator: DestinationsNavigator,
    lazyListState: LazyListState
) {
    SwipeRefresh(
        modifier = modifier
            .fillMaxSize(),
        state = swipeRefreshState,
        onRefresh = {
            scope.launch {
                profileViewModel.getUserPosts(userId = user.userid)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            state = lazyListState

        ) {

            profileViewModel.state.posts?.let { posts ->
                items(posts) { post ->
                    MyPostItem(
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
                        onDeleteButton = {
                            profileViewModel.deletedPost = it
                            profileViewModel.showDeletePostDialog = true
                        },
                        post = post,
                        userProfileImage = user.profileImage,
                        userName = user.username,
                        showDeleteButton = user.userid == profileViewModel.meUser!!.userid
                    )
                }
            }
        }
    }
}


val LazyListState.isScrolled: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0


