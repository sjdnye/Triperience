package com.example.triperience

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.triperience.features.authentication.presentation.component.NavGraphs
import com.example.triperience.features.authentication.presentation.component.destinations.SplashScreenDestination
import com.example.triperience.ui.theme.TriperienceTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navHostEngine = rememberNavHostEngine()
            val newBackStackEntry by navController.currentBackStackEntryAsState()
            val route = newBackStackEntry?.destination?.route


//            val showBottomBar = route in listOf(
//                HomeScreenDestination.route,
//                ReelsScreenDestination.route,
//                ProfileScreenDestination.route,
//                SearchScreenDestination.route,
//                ActivityScreenDestination.route,
//
//            )

            TriperienceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        backgroundColor = MaterialTheme.colors.background,
                        bottomBar = {
//                            if (showBottomBar) {
//                                CustomBottomNavBar(
//                                    profileImage = sharedPrefUtil.getString(Constants.PROFILE_IMAGE_URL) ?: "",
//                                    navController = navController
//                                )
//                            }
                        }
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine
                        )
                    }
                }
            }
        }
    }
}


