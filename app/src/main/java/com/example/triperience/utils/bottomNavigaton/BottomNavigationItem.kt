package com.example.triperience.utils.bottomNavigaton

import android.graphics.drawable.Icon
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.triperience.features.destinations.DirectionDestination
import com.example.triperience.features.destinations.HomeScreenDestination
import com.example.triperience.features.destinations.ProfileScreenDestination
import com.example.triperience.features.destinations.SearchScreenDestination

data class BottomNavigationItem(
    val icon : ImageVector,
    val destination: DirectionDestination
)

val bottomNavItems = listOf(
    BottomNavigationItem(
        icon = Icons.Default.Home,
        destination = HomeScreenDestination
    ),
    BottomNavigationItem(
        icon = Icons.Default.Search,
        destination = SearchScreenDestination
    ),
    BottomNavigationItem(
        icon = Icons.Default.Person,
        destination = ProfileScreenDestination
    )
)

@Composable
fun CustomBottomNavBar(
    navController: NavController,
    navItems: List<BottomNavigationItem> = bottomNavItems,
    modifier: Modifier = Modifier
){
    val darkTheme: Boolean = isSystemInDarkTheme()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    ) {
        navItems.forEach{ item ->
            val selectedNavItem = currentRoute?.contains(item.destination.route) == true
            BottomNavigationItem(
                selected = item.destination.route == currentRoute,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onBackground,
                icon = {
                      Icon(imageVector = item.icon, contentDescription = null)
                },
                onClick = {
                    if (!selectedNavItem) {
                        navController.popBackStack()
                        navController.navigate(item.destination.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}