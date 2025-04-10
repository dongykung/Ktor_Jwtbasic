package com.example.model

import com.example.routing.response.UserResponse
import com.example.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val email: String,
    val username: String,
    val password: String
) {
    fun toUserResponse(): UserResponse {
        return UserResponse(
            id = id,
            email = email,
            username = username
        )
    }
}
