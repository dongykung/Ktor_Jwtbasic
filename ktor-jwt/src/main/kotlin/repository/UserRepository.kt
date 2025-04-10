package com.example.repository

import com.example.model.User
import java.util.UUID

class UserRepository {
    private val users = mutableListOf<User>(
        User(id = UUID.randomUUID(), username = "옥지", password = "1234", email = "옥지@naver.com"),
        User(id = UUID.randomUUID(), username = "빵빵이", password = "1234", email = "빵빵이@naver.com"),
        User(id = UUID.randomUUID(), username = "지방이", password = "1234", email = "지방이@naver.com"),
        User(id = UUID.randomUUID(), username = "동키", password = "1234", email = "dong@naver.com")

    )

    fun findAll(): List<User> = users

    fun findById(id: UUID): User? {
        return users.firstOrNull { it.id == id }
    }

    fun findByName(name: String): User? {
        return users.firstOrNull { it.username == name }
    }

    fun findByEmail(email: String): User? {
        return users.firstOrNull { it.email == email }
    }

    fun save(user: User): Boolean {
        return users.add(user)
    }
}