package com.example.triperience.features.authentication.presentation.component

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.authentication.presentation.AuthViewModel
import com.example.triperience.features.destinations.LoginScreenDestination
import com.example.triperience.features.destinations.RegisterScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AuthWelcomeScreen(
    navigator: DestinationsNavigator,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            Text(
                text = "Triperience",
                modifier = Modifier.align(CenterHorizontally),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground,
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                              navigator.navigate(LoginScreenDestination)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(10.dp),

                    ) {
                        Text(
                            text = "Login",
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                              navigator.navigate(RegisterScreenDestination)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f),

                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(10.dp),

                    ) {
                        Text(
                            text = "Register",
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                }
            }
        }
    }
}