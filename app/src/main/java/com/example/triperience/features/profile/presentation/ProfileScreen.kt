package com.example.triperience.features.profile.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.triperience.R
import com.example.triperience.features.authentication.presentation.AuthViewModel
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
import com.example.triperience.features.destinations.ProfileScreenDestination
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userId:String? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        profileViewModel.state.user?.let {
            Text(text = "User information received : ${it.toString()}", modifier = Modifier.align(
                Alignment.Center))
        }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                //Header
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .wrapContentHeight()
//                            .padding(5.dp)
//                    ) {
//                        CoilImage(
//                            imageUrl = profileViewModel.state.user!!.profileImage,
//                            modifier = Modifier
//                                .size(100.dp)
//                                .weight(3f)
//                                .padding(5.dp)
//                        )
//                        Divider(
//                            color = MaterialTheme.colors.primary,
//                            modifier = Modifier
//                                .weight(0.5f)
//                                .fillMaxHeight()
//                                .width(1.dp)
//                        )
//                        Spacer(modifier = Modifier.width(5.dp))
//                        Text(
//                            text = profileViewModel.state.user!!.bio,
//                            modifier = Modifier.weight(3.5f),
//                            maxLines = 4
//                        )
//                    }
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .wrapContentHeight()
//                    ) {
//                        Text(
//                            text ="${profileViewModel.state.user!!.following.size} following",
//                            style = TextStyle(
//                                color = MaterialTheme.colors.onBackground,
//                                fontWeight = FontWeight.Bold
//                            )
//                        )
//                        Spacer(modifier = Modifier.width(5.dp))
//
//                        Text(
//                            text ="${profileViewModel.state.user!!.followers.size} followers",
//                            style = TextStyle(
//                                color = MaterialTheme.colors.onBackground,
//                                fontWeight = FontWeight.Bold
//                            )
//                        )
//                    }
            }

        if (profileViewModel.state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}


@Composable
fun CoilImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true),
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                transformations(
                    CircleCropTransformation()
                )
            }
        )
        val painterState = painter.state
        AsyncImage(
            model = painter,
            contentDescription = "profile image",
        )
    }


}