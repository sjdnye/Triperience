package com.example.triperience.utils.common

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.home.presentation.HomeViewModel
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.presentation.component.CoilImage
import com.example.triperience.ui.theme.LightBlue300
import com.example.triperience.ui.theme.LightBlue500
import com.example.triperience.ui.theme.LightBlue700
import com.example.triperience.ui.theme.LightBlue900
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.random.Random

@Composable
fun PostItem(
    modifier: Modifier = Modifier,
    onProfileClick: (userId : String) -> Unit,
    onImageClick: (postId : String) -> Unit,
    getPublisherDetail: (publisherId: String) -> User?,
    homeViewModel: HomeViewModel,
    post: Post
) {
    var userProfile by remember {
        mutableStateOf<String?>(null)
    }
    var userName by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(key1 = true ){
//        val publisher = getPublisherDetail(post.publisher)
        val publisher = homeViewModel.getPostPublisherDetail(post.publisher)
        userProfile = publisher?.profileImage
        userName = publisher?.username

    }

    var expanded by remember {
        mutableStateOf(false)
    }
    val expandedIcon = if (!expanded) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward

    Card(
        modifier = modifier.padding(16.dp),
//        backgroundColor =,
        shape = MaterialTheme.shapes.large
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
                imageUrl = userProfile!!,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = userName!!)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Image(
            painter = rememberAsyncImagePainter(
                model = post.postImage
            ),
            contentDescription = null,
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .fillMaxWidth()
                .aspectRatio(3f / 2f)
                .clickable {
                    //go to tabRow Section to see location and photos like ths
                    onImageClick(post.postId)
                }
        )
        Spacer(modifier = Modifier.height(2.dp))
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
            Column(
                modifier = Modifier
            )
            {
                Text(
                    text = post.description,
                )
                if (post.score != "") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Score: ${post.score}/10")
                    //A row to show score length
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = LightBlue300,
                        color = if (post.score.toInt() <= 3) LightBlue500 else if (post.score.toInt() <= 6) LightBlue700 else LightBlue900,
                        progress = post.score.toFloat() / 10f
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                //
                /// convert post date to show
                Text(
                    text = SimpleConvert.convertLongToDate(post.dateTime!!),
                    fontSize = 10.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Comments",
            color = Color.Gray,
            fontSize = 10.sp,
            modifier = Modifier
                .clickable {
                    //go to comment section
                }
        )
    }
}