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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.authentication.presentation.component.AuthWelcomeScreen
import com.example.triperience.features.authentication.presentation.component.CustomTextField
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
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
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        searchScreenViewModel.searchUsers(searchQuery)
                    },
                    label = "",
                    placeholder = "Search...",
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
                if (searchQuery.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(searchScreenViewModel.users) { user ->
                            SearchedUserItem(
                                user = user,
                                onClick = {
                                    TODO("navigate to UserProfileScreen")
                                }
                            )
                        }
                    }

                } else {
                    TODO("Show different categories")

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
    imageVector: ImageVector,
) {
    Box(
        modifier = modifier,
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
        )
    }
}
