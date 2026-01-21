package com.harsh.playspot.dao

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val fullName: String = "",
    val userName: String = "",
    val email: String = "",
    val bio: String = "",
    val city: String = "",
    val playTime: List<String> = emptyList(),
    val skillLevel: String = "",
    val preferredSports: List<String> = emptyList(),
    val profilePictureUrl: String = ""
)
