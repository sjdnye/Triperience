package com.example.triperience.features.comment.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.authentication.presentation.component.CustomTextField
import com.example.triperience.features.authentication.presentation.component.CustomTextField2
import com.example.triperience.features.destinations.UserProfileScreenDestination
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun CommentScreen(
    navigator: DestinationsNavigator,
    commentViewModel: CommentViewModel = hiltViewModel(),
    postId: String? = null
) {
    val scaffoldState = rememberScaffoldState()
    val state = rememberLazyListState()
    val comments by commentViewModel.comments.collectAsState()
    val context = LocalContext.current
    val bringIntoViewRequester = BringIntoViewRequester()
    val coroutineScope = rememberCoroutineScope()
    val showSendIcon by remember {
        mutableStateOf(false)
    }

    val commentField by remember {
        mutableStateOf(1f)
    }
    val animatedCommentWeight by animateFloatAsState(targetValue = commentField)

    var myComment by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = true) {
        commentViewModel.commentEventFlow.collect { result ->
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
                title = { Text(text = "Comments") },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    reverseLayout = true,
                    state = state
                ) {
                    items(comments!!) { comment ->
                        CommentItem(
                            comment = comment,
                            viewModel = commentViewModel,
                            onClick = { userId ->
                                navigator.navigate(UserProfileScreenDestination(userid = userId))
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .background(Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            modifier = Modifier
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                }
                                .clip(RoundedCornerShape(20.dp))
                                .weight(if (myComment.isNotBlank()) 9f else 1f),
                            value = myComment,
                            onValueChange = {
                                myComment = it.toString()
                            },
                            placeholder = {
                                Text(text = "Type your opinion...")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                            ),
                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            keyboardActions = KeyboardActions(),
                            maxLines = 15
                        )

                        if (myComment.isNotBlank()) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
//                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            commentViewModel.sendComment(myComment = myComment)
                                            myComment = ""
                                        },
                                    text = "Send",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colors.primary,

                                    )
                            }
                        }
                    }
                }
            }
            if (commentViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
