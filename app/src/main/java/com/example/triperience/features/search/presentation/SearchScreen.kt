package com.example.triperience.features.search.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.triperience.features.authentication.presentation.component.AuthWelcomeScreen
import com.example.triperience.features.destinations.AuthWelcomeScreenDestination
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(text = "Search Screen", modifier = Modifier.clickable{
            FirebaseAuth.getInstance().signOut()
            navigator.popBackStack()
            navigator.navigate(AuthWelcomeScreenDestination)
        }
        )
    }
}