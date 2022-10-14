package com.example.triperience.features.comment.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.comment.domain.model.Comment

@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    comment: Comment,
    viewModel: CommentViewModel,
    onClick: (userid: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    var userProfileImage by remember {
        mutableStateOf<String?>(null)
    }
    var userUserName by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(key1 = true) {
        val user = viewModel.getCommentPublisherInfo(comment.publisher)
        userProfileImage = user?.profileImage
        userUserName = user?.username
    }


    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    onClick(comment.publisher)
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = userProfileImage!!),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(32.dp),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(text = userUserName!!, textAlign = TextAlign.Justify)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = comment.description, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(5.dp))
    }
}