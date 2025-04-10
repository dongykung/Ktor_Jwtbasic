package com.example.service

import com.example.model.User
import com.example.repository.UserRepository
import java.util.UUID

class UserService(
    private val userRepository: UserRepository
) {
    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: String): User? = userRepository.findById(id = UUID.fromString(id))

    fun findByName(name: String): User? = userRepository.findByName(name = name)

    fun findByEmail(email: String): User? = userRepository.findByEmail(email = email)

    fun save(user: User): User? {
        val foundUser = findByName(name = user.username)
        return if(foundUser == null) {
            userRepository.save(user = user)
            user
        } else null
    }
}