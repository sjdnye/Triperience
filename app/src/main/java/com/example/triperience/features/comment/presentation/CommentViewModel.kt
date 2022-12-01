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
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    sharedPrefUtil: SharedPrefUtil,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var meUser by mutableStateOf<User?>(null)
    private var postId: String? = null

    private val _comments = MutableStateFlow<List<Comment>?>(emptyList())
    val comments = _comments.asStateFlow()

    var isLoading by mutableStateOf(false)

    private val _commentEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val commentEventFlow = _commentEventFlow.asSharedFlow()

    init {
        meUser = sharedPrefUtil.getCurrentUser()
        savedStateHandle.get<String>(Constants.POST_ID)?.let {
            postId = it
            getAllComments(it)
        }
    }


    private fun getAllComments(postId: String) {
        viewModelScope.launch {
            commentRepository.getAllComments(postId = postId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        isLoading = false
                        _comments.value = result.data!!.sortedByDescending { it.dateTime }
                    }
                    is Resource.Error -> {
                        isLoading = false
                        sendCommentScreenUiEvent(
                            ScreenUiEvent.ShowMessage(
                                message = result.message.toString(),
                                isToast = true
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

    suspend fun getCommentPublisherInfo(publisherId: String): User? {
        val job = viewModelScope.async {
            return@async commentRepository.getCommentPublisherInfo(publisherId = publisherId)
        }
        return job.await()
    }

    fun sendComment(myComment: String) {
        if(myComment.trim().isEmpty()){
            sendCommentScreenUiEvent(
                ScreenUiEvent.ShowMessage(
                    message = "Your comment can not be empty!",
                    isToast = true
                )
            )
        }else{
            viewModelScope.launch {
                commentRepository.sendComment(
                    Comment(
                        postId = postId!!,
                        publisher = meUser?.userid!!,
                        description = myComment,
                        dateTime = System.currentTimeMillis()
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            sendCommentScreenUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = "Comment sent successfully",
                                    isToast = true
                                )
                            )
                        }
                        is Resource.Error -> {
                            sendCommentScreenUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = result.message.toString(),
                                    isToast = true
                                )
                            )
                        }
                        is Resource.Loading -> {
                            sendCommentScreenUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = "Please wait..",
                                    isToast = true
                                )
                            )
                        }
                    }
                }
            }

        }
    }

    private fun sendCommentScreenUiEvent(commentEvent: ScreenUiEvent) {
        viewModelScope.launch {
            _commentEventFlow.emit(commentEvent)
        }
    }

}