package com.example.triperience.features.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.home.domain.repository.HomeRepository
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.utils.Resource
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sharedPrefUtil: SharedPrefUtil
) : ViewModel() {


    var meUser by mutableStateOf<User?>(null)

    private val _posts = MutableStateFlow<List<Post>?>(emptyList())
    val posts = _posts.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    private val _homeEventFLow = MutableSharedFlow<ScreenUiEvent>()
    val homeEventFLow = _homeEventFLow.asSharedFlow()

    init {
        meUser = sharedPrefUtil.getCurrentUser()
        getPosts()
    }

    fun getPosts() {
       viewModelScope.launch {
           homeRepository.getRelevantPost(followingList = meUser?.following!!).collect{result ->
               when(result){
                   is Resource.Success -> {
                       isLoading = false
                       _posts.value = result.data
                   }
                   is Resource.Error -> {
                       isLoading = false
                       sendHomeScreenUiEvent(
                           ScreenUiEvent.ShowMessage(message = result.message.toString(), isToast = true)
                       )
                   }
                   is Resource.Loading -> {
                       isLoading = true
                   }
               }
           }
       }

    }


    suspend fun getPostPublisherDetail(userId: String) : User? {
      val job = viewModelScope.async {
          return@async homeRepository.getPostPublisherDetail(userId)
      }
        return job.await()

    }

    private fun sendHomeScreenUiEvent(screenUiEvent: ScreenUiEvent){
        viewModelScope.launch {
            _homeEventFLow.emit(screenUiEvent)
        }
    }
}