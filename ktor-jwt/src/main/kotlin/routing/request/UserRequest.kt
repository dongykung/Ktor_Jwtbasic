package com.example.routing.request

import com.example.model.User
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserRequest(
    val username: String,
    val email: String,
    val password: String,
) {
    fun toModel(): User {
        return User(
            id = UUID.randomUUID(),
            username = username,
            email = email,
            password = password
        )
    }
}