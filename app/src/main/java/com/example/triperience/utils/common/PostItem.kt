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
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.presentation.component.CoilImage
import com.example.triperience.ui.theme.*
import com.example.triperience.utils.core.GetPostsPublisher

@Composable
fun PostItem(
    modifier: Modifier = Modifier,
    onProfileClick: (userId: String) -> Unit,
    onImageClick: (latitude: Double, longitude: Double) -> Unit,
    onCommentClick: (postId: String) -> Unit,
    getPostsPublisher: GetPostsPublisher,
    post: Post
) {
    var userProfile by remember {
        mutableStateOf<String?>(null)
    }
    var userName by remember {
        mutableStateOf<String?>(null)
    }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        val publisher = getPostsPublisher.getPostPublisherDetail(post.publisher)
        userProfile = publisher?.profileImage
        userName = publisher?.username
    }

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
        PostPublisherSection(
            post = post,
            userProfile = userProfile,
            userName = userName,
            onProfileClick = {
                onProfileClick(it)
            }
        )
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

@Composable
fun PostImageSection(
    post: Post,
    onImageClick: (latitude: Double, longitude: Double) -> Unit
) {
    Box(modifier = Modifier) {
        Card(
            shape = MaterialTheme.shapes.large
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = post.postImage
                ),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clickable {
                        //go to tabRow Section to see location and photos like this
                        onImageClick(post.latitude!!, post.longitude!!)
                    },
            )
        }
        Text(
            text = post.city,
            fontSize = 25.sp,
            modifier = Modifier
                .align(BottomStart)
                .padding(start = 5.dp),
            fontFamily = customFont
        )
    }
}

@Composable
fun PostExpandedSection(
    post: Post
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    )
    {
        Column() {
            post.advantages.forEach {
                if (it != "") {
                    Row {
                        Text(text = "+ ", color = Color.Green)
                        Text(text = it, fontSize = 15.sp)

                    }
                }
            }
        }
        Column {
            post.disAdvantages.forEach {
                if (it != "") {
                    Row {
                        Text(text = "- ", color = Color.Red)
                        Text(text = it, fontSize = 15.sp)

                    }
                }
            }
        }

        if (post.description != "") {
            Column {
                Text(
                    text = "Caption :\n${post.description}",
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun PostPublisherSection(
    post: Post,
    userProfile: String?,
    userName: String?,
    onProfileClick: (postPublisher: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                //go to profile page of user
                onProfileClick(post.publisher)
            }
    ) {
        //publisherProfile
        CoilImage(
            imageUrl = if (userProfile != null) userProfile!! else "",
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = if (userName != null) userName!! else "",
            fontSize = 15.sp
        )
    }
}

@Composable
fun PostTimeAndCommentSection(
    post: Post,
    onCommentClick: (postId: String) -> Unit
) {
    Text(
        text = SimpleConvert.convertLongToDate(post.dateTime!!),
        fontSize = 10.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Comments",
        color = Color.Gray,
        fontSize = 10.sp,
        modifier = Modifier
            .clickable {
                //go to comment section
                onCommentClick(post.postId)
            }

    )
}

@Composable
fun PostDateAndTimeSection(
    post: Post
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (post.pickedDate != "") {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Date",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = post.pickedDate,
                    textAlign = TextAlign.Center
                )
            }
        }
        if (post.pickedTime != "") {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Time",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = post.pickedTime,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Composable
fun PostRateAndCitySection(
    post: Post
) {
    if (post.score != "") {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row {
                Text(
                    text = "Score: ",
                    fontSize = 15.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(CenterVertically).padding(start = 20.dp)
                )
                Text(
                    text = "${post.score}/10",
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(CenterVertically),
                    fontSize = 15.sp,
                    color = if (post.score.toInt() <= 3) Color.Red else if (post.score.toInt() <= 6) LightPink800 else if (post.score.toInt() <= 9) LightGreen800 else LightYellow500
                )
            }
            Row {
                val starCount = post.score.toInt() / 2
                val starCountHalf = post.score.toDouble() % 2
                if (starCount > 0) {
                    (0 until starCount).forEach {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "",
                            tint = Color.Yellow,
                            modifier = Modifier.align(CenterVertically),
                        )
                    }
                }
                if (starCountHalf > 0) {
                    Icon(
                        imageVector = Icons.Default.StarHalf,
                        contentDescription = "",
                        tint = Color.Yellow,
                        modifier = Modifier.align(CenterVertically),
                    )
                }
            }
        }
    }
}
