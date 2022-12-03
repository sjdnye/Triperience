package com.example.triperience.features.profile.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowListItem(
    val title: String,
    val followList : List<String>
) : Parcelable
