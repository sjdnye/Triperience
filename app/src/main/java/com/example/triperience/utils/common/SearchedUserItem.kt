package com.example.triperience.utils.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.triperience.features.authentication.domain.model.User

@Composable
fun SearchedUserItem(
    modifier: Modifier = Modifier,
    user: User,
    onClick: (userid:String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(user.userid)
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = user.profileImage),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(64.dp),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = user.username)
    }
}