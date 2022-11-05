package com.example.triperience.features.comment.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    val comments by commentViewModel.comments.collectAsState()
    val context = LocalContext.current
    val bringIntoViewRequester = BringIntoViewRequester()
    val coroutineScope = rememberCoroutineScope()

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
                       .aspectRatio(4/7f)
               ) {
                   items(comments!!) { comment ->
                       CommentItem(
                           comment = comment,
                           viewModel = commentViewModel,
                           onClick = { userId ->
                               navigator.navigate(UserProfileScreenDestination(userId = userId))
                           }
                       )
                   }
               }
               Spacer(modifier = Modifier.height(4.dp))
               Box(
                   modifier = Modifier
                       .fillMaxSize()
                       .bringIntoViewRequester(bringIntoViewRequester)
               ){
                   CustomTextField2(
                       modifier = Modifier
                           .align(Alignment.BottomStart)
                           .onFocusEvent { event ->
                               if (event.isFocused) {
                                   coroutineScope.launch {
                                       bringIntoViewRequester.bringIntoView()
                                   }
                               }
                           },
                       value = myComment,
                       onValueChange = {
                           myComment = it
                       },
                       label = "Comment",
                       placeholder = "Type your opinion...",
                       keyboardOptions = KeyboardOptions(
                           keyboardType = KeyboardType.Text,
                           imeAction = ImeAction.Send
                       ),
                       keyboardActions = KeyboardActions(
                           onSend = {
                               commentViewModel.sendComment(myComment = myComment)
                               myComment = ""
                           }
                       ),
                       maxLine = 4
                   )
               }
           }
            if (commentViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}