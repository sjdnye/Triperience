package com.example.triperience.features.comment.domain.repository

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.comment.domain.model.Comment
import com.example.triperience.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

     fun getAllComments(postId: String) : Flow<Resource<List<Comment>?>>

     suspend fun getCommentPublisherInfo(publisherId : String) : User?

     suspend fun sendComment(comment: Comment) : Flow<Resource<Boolean>>
}