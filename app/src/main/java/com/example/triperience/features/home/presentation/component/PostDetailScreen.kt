package com.example.triperience.features.home.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun PostDetailScreen(
    postId:String
) {

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    val tabRowItems = listOf<String>("Location", "Similar")

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.Transparent,
            ) {
                tabRowItems.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(text = title) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                        },
                        unselectedContentColor = MaterialTheme.colors.onBackground,
                        selectedContentColor = MaterialTheme.colors.primary,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                when(selectedTabIndex){
                    0 -> {
                        PostLocationScreen()
                    }
                    1 -> {
                        SimilarPostLocationScreen()
                    }
                }
            }
        }
    }
}