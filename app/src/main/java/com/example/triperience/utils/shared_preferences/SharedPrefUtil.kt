package com.example.triperience.utils.shared_preferences

import android.content.SharedPreferences
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class SharedPrefUtil(
    private val sharedPreferences: SharedPreferences
) {

    fun saveCurrentUser(user: User?){
        val gson = Gson().toJson(user)
        sharedPreferences.edit().putString(Constants.CURRENT_USER, gson).apply()
    }

    fun getCurrentUser(): User?{
        val json = sharedPreferences.getString(Constants.CURRENT_USER, null)
        val type = object : TypeToken<User>() {}.type
        return Gson().fromJson(json, type)
    }

    fun saveString(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? = sharedPreferences.getString(key, null)
}