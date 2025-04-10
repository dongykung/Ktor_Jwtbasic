package com.example.plugins

import com.example.routing.authRoute
import com.example.routing.userRoute
import com.example.service.JwtService
import com.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    jwtService: JwtService
) {
    routing {
        route("/api/auth") {
            authRoute(jwtService = jwtService, userService = userService)
        }

        route("/api/user") {
            userRoute(userService = userService)
        }
    }
}
