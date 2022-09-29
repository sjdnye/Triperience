package com.example.triperience.features.profile.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class CustomBottomSheetItem(
    val icon:ImageVector,
    val title:String,
    val onClickLabel:String
)

val bottomSheetItems = listOf<CustomBottomSheetItem>(
    CustomBottomSheetItem(
        icon = Icons.Default.AddToPhotos,
        title = "Upload a post",
        onClickLabel = "upload"
    ),
    CustomBottomSheetItem(
        icon = Icons.Default.Edit,
        title = "Edit profile",
        onClickLabel = "edit"
    ),
    CustomBottomSheetItem(
        icon = Icons.Default.Logout,
        title = "Log out",
        onClickLabel = "out"
    )

)

@Composable
fun CustomBottomSheet(
    modifier: Modifier = Modifier,
    onClick: (menuItem: String) -> Unit,
    listItems : List<CustomBottomSheetItem> = bottomSheetItems
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            listItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClick(item.onClickLabel)
                        },
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = item.title)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}