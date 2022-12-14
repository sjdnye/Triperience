package com.example.triperience.utils.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.presentation.component.CoilImage
import com.example.triperience.ui.theme.customFont

@Composable
fun MyPostItem(
    modifier: Modifier = Modifier,
    onImageClick: (latitude: Double, longitude: Double) -> Unit,
    onCommentClick: (postId: String) -> Unit,
    onDeleteButton: (postId: String) -> Unit,
    post: Post,
    userProfileImage: String?,
    userName: String?,
    showDeleteButton: Boolean = false
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val expandedIcon = if (!expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color.Transparent),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //publisherProfile
                CoilImage(
                    imageUrl = if (userProfileImage != null) userProfileImage!! else "",
                    modifier = Modifier
                        .size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (userName != null) userName!! else "",
                    fontSize = 15.sp
                )
            }
            if (showDeleteButton) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        onDeleteButton(post.postId)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete post")
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        PostImageSection(
            post = post,
            onImageClick = { latitude, longitude ->
                onImageClick(latitude, longitude)
            }
        )
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PostRateAndCitySection(post = post)
            PostDateAndTimeSection(post = post)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    expanded = !expanded
                }
            ) {
                Icon(
                    imageVector = expandedIcon,
                    contentDescription = null,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (expanded) {
            PostExpandedSection(
                post = post
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        PostTimeAndCommentSection(
            post = post,
            onCommentClick = {
                onCommentClick(it)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}