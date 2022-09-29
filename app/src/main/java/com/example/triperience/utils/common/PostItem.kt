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
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.presentation.component.CoilImage
import kotlin.random.Random

@Composable
fun PostItem(
    modifier: Modifier = Modifier,
    post: Post
) {

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
                }
        ) {
            //publisherProfile
//            CoilImage(
//                imageUrl = ,
//                modifier = Modifier.size(64.dp)
//            )
//            Spacer(modifier = Modifier.width(10.dp))
            //publisher username
//            Text(text =)
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
                post.score?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Score: ${it}/10")
                    //A row to show score length
                    ///
                    ////
                }
                Spacer(modifier = Modifier.height(4.dp))
                //
                /// convert post date to show
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