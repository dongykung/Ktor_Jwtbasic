package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity(
    jwtService: JwtService
) {

    authentication {
        jwt("access-auth"){
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate { credential ->
                println("인증된놈들요청옴")
                val tokenType = credential.payload.getClaim("type").asString()
                if (tokenType != "access") return@validate null
                jwtService.customValidator(credential)
            }
        }
    }
}
