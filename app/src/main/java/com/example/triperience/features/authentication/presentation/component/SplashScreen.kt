package com.example.triperience.features.authentication.presentation.component

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.R
import com.example.triperience.features.authentication.presentation.AuthViewModel
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@RootNavGraph(true)
@Destination
@Composable
fun SplashScreen(
    navigator: DestinationsNavigator,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scale = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {

        viewModel.getUser()
        scale.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(1000)

        if (viewModel.isUserAuthenticated) {
//            navigator.popBackStack()
//            navigator.navigate()
        } else {
            navigator.popBackStack()
            navigator.navigate(AuthWelcomeScreenDestination)
        }
    }
//    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
//        Image(painter = painterResource(id = R.drawable.ic_instagram_logo),
//            contentDescription = "Splash Screen Logo", modifier = Modifier.scale(scale.value))
//    }

}