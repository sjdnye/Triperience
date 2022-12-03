package com.example.triperience.features.search.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.search.domain.repository.SearchRepository
import com.example.triperience.utils.Resource
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.core.GetPostsPublisher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    val getPostsPublisher: GetPostsPublisher
) : ViewModel() {

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var posts by mutableStateOf<List<Post>?>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _searchEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val searchEventFlow: SharedFlow<ScreenUiEvent> = _searchEventFlow

    var job: Job? = null

    fun searchUsers(query: String) {
        if (query == "") {
            isLoading = false
            users = emptyList()
            job?.cancel()
        } else {
            job?.cancel()
            job = viewModelScope.launch {
                delay(500L)
                searchRepository.searchUsers(query = query).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            isLoading = false
                            users = result.data as MutableList<User>
                        }
                        is Resource.Error -> {
                            isLoading = false
                            sendScreenUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = result.message.toString(),
                                    isToast = false
                                )
                            )
                        }
                        is Resource.Loading -> {
                            isLoading = true
                        }
                    }
                }
            }
        }
    }

    fun searchPostsByCategory(category: String){
      viewModelScope.launch {
          searchRepository.searchPostsByCategory(category = category).collect{ result ->
              when(result){
                  is Resource.Success -> {
                      isLoading = false
                      posts = result.data
                  }
                  is Resource.Error -> {
                      isLoading = false
                      sendScreenUiEvent(
                          ScreenUiEvent.ShowMessage(
                              message = result.message.toString(),
                              isToast = false
                          )
                      )
                  }
                  is Resource.Loading -> {
                      isLoading = true
                  }

              }
          }
      }
    }

    private fun sendScreenUiEvent(searchUiEvent: ScreenUiEvent) {
        viewModelScope.launch {
            _searchEventFlow.emit(searchUiEvent)
        }
    }
}

