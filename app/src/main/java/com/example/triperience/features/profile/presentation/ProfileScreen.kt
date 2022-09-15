package com.example.triperience.features.profile.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.triperience.R
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.authentication.presentation.AuthViewModel
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
import com.example.triperience.features.destinations.ProfileScreenDestination
import com.example.triperience.features.profile.presentation.ProfileViewModel
import com.example.triperience.ui.theme.LightBlue900
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userId: String? = null
) {
    val context = LocalContext.current
    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            CustomBottomSheet(
                onClick = {
                    when (it) {
                        0 -> {
                            Toast.makeText(context, "upload a post", Toast.LENGTH_LONG).show()
                        }
                        1 -> {
                            Toast.makeText(context, "Edit profile", Toast.LENGTH_LONG).show()
                        }
                    }
                },
            )

        },
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetPeekHeight = 0.dp,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if(sheetState.isCollapsed) {
                                    sheetState.expand()
                                } else {
                                    sheetState.collapse()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu button"
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            profileViewModel.state.user?.let { user ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //Header
                    ProfileHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f),
                        user = user
                    )
                    Spacer(modifier = Modifier.height(5.dp))
//                    Text(
//                        text = "Posts: ${user.posts.size.toString()}",
//                        style = TextStyle(
//                            color = MaterialTheme.colors.primary,
//                            fontFamily = FontFamily.Default,
//                            fontSize = 30.sp,
//                        )
//                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(8f)
                    ) {

                    }
                }
                if (user.posts.isEmpty()) {
                    Text(
                        text = "There is nothing to show!!",
                        modifier = Modifier.align(Center)
                    )
                }
            }



            if (profileViewModel.state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

}

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    user: User
) {
    Column(
        modifier = modifier
            .padding(5.dp),
        horizontalAlignment = Alignment.Start,

        ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .weight(3.5f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CoilImage(
                    imageUrl = user.profileImage,
                    modifier = Modifier
                        .size(100.dp)
                        .weight(3f)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = user.username,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .weight(1f)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "${user.following.size.toString()} following",
                        style = TextStyle(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Bottom)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${user.followers.size.toString()} followers",
                        style = TextStyle(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Bottom)
                    )
                }
            }
            Divider(
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(0.5.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            if (user.bio.isNotEmpty()){
                Text(
                    text =user.bio,
                    modifier = Modifier
                        .weight(6f),
                    maxLines = 4,
                    textAlign = TextAlign.Start,
                )
            }else{
                Box(
                    modifier = Modifier.weight(6f).fillMaxSize(),
                    contentAlignment = Center
                ) {
                    Text(
                        text = "Bio is empty!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Center)
                    )
                }
            }

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
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                transformations(
                    CircleCropTransformation(),
                )
                placeholder(R.drawable.default_image_profile)
            },
        )
        val painterState = painter.state
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
            ,
            painter = painter,
            contentDescription = "profile image",

        )
    }
}

@Composable
fun CustomBottomSheet(
    modifier: Modifier = Modifier,
    onClick: (menuItem: Int) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(0)
                    },
            ) {
                Icon(
                    imageVector = Icons.Default.AddToPhotos,
                    contentDescription = "add photo"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Upload a post")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(1)
                    },
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Edit Profile")
            }
        }

    }
}