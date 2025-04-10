package com.example.routing

import com.example.model.BaseResponse
import com.example.model.LoginResult
import com.example.routing.request.LoginRequest
import com.example.routing.request.RefreshTokenRequest
import com.example.routing.response.TokenResponse
import com.example.service.JwtService
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(
    jwtService: JwtService,
    userService: UserService
) {
    post {
        val loginRequest = call.receive<LoginRequest>()
        when (val token = jwtService.createJwtToken(loginRequest = loginRequest)) {
            is LoginResult.Success -> call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    status = 200,
                    message = "로그인 성공",
                    data = TokenResponse(token.accessToken, token.refreshToken)
                )
            )

            LoginResult.UserNotFound -> call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(404, "사용자를 찾을 수 없습니다", null)
            )

            LoginResult.InvalidPassword -> call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(404, "비밀번호가 올바르지 않습니다", null)
            )
        }
    }

    post("/refresh") {
        val request = call.receive<RefreshTokenRequest>()
        println("refresh 요청 왔음")
        val decoded = try {
            jwtService.jwtVerifier.verify(request.refreshToken)
        } catch (e: Exception) {
            println("refresh 요청 exception 남")
            return@post call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(401, "RefreshToken이 유효하지 않습니다", null)
            )
        }

        val userId = decoded.getClaim("userId").asString()

        // 사용자 존재 확인
        val user = userService.findById(userId)
            ?: return@post call.respond(HttpStatusCode.NotFound, BaseResponse<Unit>(404, "사용자를 찾을 수 없습니다", null))

        val newAccessToken = jwtService.createAccessToken(userId, 1 * 30 * 1000) // 1분
        val newRefreshToken = jwtService.createRefreshToken(userId, 4 * 60 * 1000) //4 분
        call.respond(
            HttpStatusCode.OK,
            BaseResponse(
                status = 200,
                message = "AccessToken 재발급 성공",
                data = TokenResponse(newAccessToken, newRefreshToken)
            )
        )
    }
}