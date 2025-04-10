package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.repository.UserRepository
import com.example.service.JwtService
import com.example.service.UserService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val userRepository = UserRepository()
    val userService = UserService(userRepository = userRepository)
    val jwtService = JwtService(application = this, userRepository = userRepository)
    configureSecurity(jwtService = jwtService)
    configureSerialization()
    configureRouting(userService = userService, jwtService = jwtService)
}
