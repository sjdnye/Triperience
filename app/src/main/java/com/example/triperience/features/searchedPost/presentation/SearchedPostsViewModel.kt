package com.example.triperience.features.searchedPost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.searchedPost.domain.repository.SearchedPostsRepository
import com.example.triperience.utils.Resource
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.core.GetPostsPublisher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchedPostsViewModel @Inject constructor(
    private val searchedPostsRepository: SearchedPostsRepository,
    val getPostsPublisher: GetPostsPublisher
) : ViewModel(){
    var posts by mutableStateOf<List<Post>?>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _searchEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val searchEventFlow: SharedFlow<ScreenUiEvent> = _searchEventFlow

    fun searchPostsByCategory(category: String){
        viewModelScope.launch {
            searchedPostsRepository.searchPostsByCategory(category = category).collect{ result ->
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

    fun searchPostsByCity(city: String){
        viewModelScope.launch {
            searchedPostsRepository.searchPostsByCity(city = city).collect{ result ->
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

    fun searchPostsByCoordinates(latitude: Double, longitude: Double){
        viewModelScope.launch {
            searchedPostsRepository.searchPostsByCoordinate(latitude = latitude, longitude = longitude).collect{ result ->
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