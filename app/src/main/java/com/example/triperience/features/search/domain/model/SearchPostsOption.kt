package com.example.triperience.features.search.domain.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchPostsOption(
    val category: String? = null,
    val latLng: LatLng? = null,
    val city:String? = null
) : Parcelable
