package com.example.triperience.features.comment.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.comment.domain.model.Comment
import com.example.triperience.features.comment.domain.repository.CommentRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    private val _comments = MutableStateFlow<List<Comment>?>(emptyList())
    val comments = _comments.asStateFlow()

    var isLoading by mutableStateOf(false)

    private val _commentEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val commentEventFlow = _commentEventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>(Constants.POST_ID)?.let {
            getAllComments(it)
        }
    }


    private fun getAllComments(postId : String) {
        viewModelScope.launch {
            commentRepository.getAllComments(postId = postId).collect{result ->
                when(result){
                    is Resource.Success -> {
                        isLoading = false
                        _comments.value = result.data?.reversed()
                    }
                    is Resource.Error -> {
                        isLoading = false
                        sendCommentScreenUiEvent(
                            ScreenUiEvent.ShowMessage(message = result.message.toString())
                        )
                    }
                    is Resource.Loading -> {
                        isLoading = true
                    }
                }
            }
        }
    }

    suspend fun getCommentPublisherInfo(publisherId: String) : User?{
        val job = viewModelScope.async {
            return@async commentRepository.getCommentPublisherInfo(publisherId = publisherId)
        }
        return job.await()
    }

    private fun sendCommentScreenUiEvent(commentEvent: ScreenUiEvent){
        viewModelScope.launch {
            _commentEventFlow.emit(commentEvent)
        }
    }

}