package com.example.triperience.utils.common

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.home.presentation.HomeViewModel
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.example.triperience.features.profile.presentation.component.CoilImage
import com.example.triperience.ui.theme.LightBlue300
import com.example.triperience.ui.theme.LightBlue500
import com.example.triperience.ui.theme.LightBlue700
import com.example.triperience.ui.theme.LightBlue900
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.random.Random

@Composable
fun MyPostItem(
    modifier: Modifier = Modifier,
    onImageClick: (latitude: Double, longitude: Double) -> Unit,
    onCommentClick: (postId: String) -> Unit,
    onDeleteButton: (postId:String) -> Unit,
    post: Post,
    userProfileImage: String?,
    userName: String?,
    showDeleteButton : Boolean = false
) {

    val context = LocalContext.current


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
       Box(modifier = Modifier.fillMaxWidth()){
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
           if(showDeleteButton){
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
//                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        //go to tabRow Section to see location and photos like ths
                        onImageClick(post.latitude!!, post.longitude!!)
                    },
            )
        }
//        Spacer(modifier = Modifier.height(2.dp))
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
                    Text(
                        text = "Score: ${post.score}/10",
                        fontSize = 10.sp
                    )
                    //A row to show score length
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = LightBlue300,
                        color = if (post.score.toInt() <= 3) LightBlue500 else if (post.score.toInt() <= 6) LightBlue700 else LightBlue900,
                        progress = post.score.toFloat() / 10f
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
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
                    onCommentClick(post.postId)
                }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}