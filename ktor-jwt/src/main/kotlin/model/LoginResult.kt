package com.example.model

sealed class LoginResult {
    data class Success(val accessToken: String, val refreshToken: String) : LoginResult()
    data object UserNotFound : LoginResult()
    data object InvalidPassword : LoginResult()
}