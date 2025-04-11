package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.LoginResult
import com.example.repository.UserRepository
import com.example.routing.request.LoginRequest
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.Date
import java.util.UUID

class JwtService(
    private val application: Application,
    private val userRepository: UserRepository
) {
    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    fun createAccessToken(userId: String, expireIn: Int): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("type", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
            .sign(Algorithm.HMAC256(secret))
    }

    fun createRefreshToken(userId: String, expireIn: Int): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("type", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
            .sign(Algorithm.HMAC256(secret))
    }

    fun createJwtToken(loginRequest: LoginRequest, expireIn: Int = 0): LoginResult  {
        val foundUser = userRepository.findByEmail(loginRequest.email) ?: return LoginResult.UserNotFound
        if (foundUser.password != loginRequest.password) {
            return LoginResult.InvalidPassword
        }
        val userId = foundUser.id.toString()
        val accessToken = createAccessToken(userId, 1 * 30 * 1000) // 30초
        val refreshToken = createRefreshToken(userId, 2 * 60 * 1000) // 2분
        return LoginResult.Success(accessToken, refreshToken)
    }



    fun customValidator(credential: JWTCredential): JWTPrincipal? {
        val userId = extractUsername(credential)
        val foundUser = userId?.let {
            userRepository.findById(id = UUID.fromString(userId))
        }
        return foundUser?.let {
            if(audienceMatches(credential)) {
                JWTPrincipal(credential.payload)
            } else null
        }
    }

    fun audienceMatches(audience: String): Boolean = this.audience == audience
    private fun audienceMatches(credential: JWTCredential): Boolean = credential.payload.audience.contains(audience)

    private fun extractUsername(credential: JWTCredential): String? = credential.payload.getClaim("userId").asString()

    private fun getConfigProperty(path: String) = application.environment.config.property(path).getString()
}