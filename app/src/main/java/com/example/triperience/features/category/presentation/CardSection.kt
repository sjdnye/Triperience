package com.example.triperience.features.category.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.triperience.R
import com.example.triperience.features.destinations.SearchedPostsScreenDestination
import com.example.triperience.features.search.domain.model.SearchPostsOption
import com.example.triperience.ui.theme.*
import com.example.triperience.utils.Constants
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun CardSection(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(
                        SearchedPostsScreenDestination(
                            searchPostsOption = SearchPostsOption(
                                category = Constants.CATEGORY_SEA
                            )
                        )
                    )
                },
            painter = painterResource(id = R.drawable.sea),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightBlue800,
                    LightBlue100
                )
            ),
            title = Constants.CATEGORY_SEA
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(
                        SearchedPostsScreenDestination(
                            searchPostsOption = SearchPostsOption(
                                category = Constants.CATEGORY_JUNGLE
                            )
                        )
                    )
                },
            painter = painterResource(id = R.drawable.forest),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightGreen900,
                    LightGreen100
                )
            ),
            title = Constants.CATEGORY_JUNGLE
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(
                        SearchedPostsScreenDestination(
                            searchPostsOption = SearchPostsOption(
                                category = Constants.CATEGORY_MOUNTAIN
                            )
                        )
                    )
                },
            painter = painterResource(id = R.drawable.mountain),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    BlueGray800,
                    BlueGray100
                )
            ),
            title = Constants.CATEGORY_MOUNTAIN
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(
                        SearchedPostsScreenDestination(
                            searchPostsOption = SearchPostsOption(
                                category = Constants.CATEGORY_DESERT
                            )
                        )
                    )
                },
            painter = painterResource(id = R.drawable.desert),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightYellow800,
                    LightYellow100
                )
            ),
            title = Constants.CATEGORY_DESERT
        )
        CategoryCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(
                        SearchedPostsScreenDestination(
                            searchPostsOption = SearchPostsOption(
                                category = Constants.CATEGORY_CITY
                            )
                        )
                    )
                },
            painter = painterResource(id = R.drawable.city),
            gradiant = Brush.horizontalGradient(
                colors = listOf(
                    LightPink800,
                    LightPink100
                )
            ),
            title = Constants.CATEGORY_CITY
        )
    }
}