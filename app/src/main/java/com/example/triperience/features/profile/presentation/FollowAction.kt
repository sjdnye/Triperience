package com.example.triperience.features.profile.presentation

sealed class FollowAction{
    object Follow: FollowAction()
    object UnFollow: FollowAction()
}