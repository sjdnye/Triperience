package com.example.triperience.features.search.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.R
import com.example.triperience.features.authentication.presentation.component.AuthWelcomeScreen
import com.example.triperience.features.authentication.presentation.component.CustomTextField
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
import com.example.triperience.features.destinations.UserProfileScreenDestination
import com.example.triperience.ui.theme.*
import com.example.triperience.utils.common.SearchedUserItem
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current



    var searchQuery by remember {
        mutableStateOf("")
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        searchScreenViewModel.searchUsers(searchQuery)
                    },
                    label = "Search",
                    placeholder = "",
                    leadingIcon = Icons.Default.Search,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (searchQuery != "") {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(searchScreenViewModel.users) { user ->
                            SearchedUserItem(
                                user = user,
                                onClick = {
                                    navigator.navigate(UserProfileScreenDestination(userId = user.userid))
                                }
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                } else {
                        CardSection()
                }
            }
            if (searchScreenViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun CategoryCardItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    gradiant: Brush,
    title: String
) {
    Card(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .clip(RoundedCornerShape(20.dp))
            .wrapContentHeight(),
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .clip(RoundedCornerShape(20.dp))
                .background(gradiant),

            ) {
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(5.dp),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 20.sp,
                fontFamily = customFont
            )
            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(128.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }

    }
}

@Composable
fun CardSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 20.dp)
            .verticalScroll(
                rememberScrollState()
            ),
//            .clip(RoundedCornerShape(20.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            painter = painterResource(id = R.drawable.sea),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightBlue800,
                    LightBlue100
                )
            ),
            title = "Sea"
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            painter = painterResource(id = R.drawable.forest),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightGreen900,
                    LightGreen100
                )
            ),
            title = "Jungle"
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            painter = painterResource(id = R.drawable.mountain),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    BlueGray800,
                    BlueGray100
                )
            ),
            title = "Mountain"
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            painter = painterResource(id = R.drawable.desert),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightYellow800,
                    LightYellow100
                )
            ),
            title = "Desert"
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            painter = painterResource(id = R.drawable.city),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightPink800,
                    LightPink100
                )
            ),
            title = "City"
        )
    }
}
