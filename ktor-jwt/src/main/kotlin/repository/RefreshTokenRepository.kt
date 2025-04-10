package com.example.repository

class RefreshTokenRepository {

    private val tokens = mutableMapOf<String, String>()

    fun findUserIdEByToken(token: String): String? = tokens[token]

    fun save(token: String, userId: String) {
        tokens[token] = userId
    }
}