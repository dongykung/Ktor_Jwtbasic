package com.example.routing

import com.example.model.BaseResponse
import com.example.routing.request.UserRequest
import com.example.routing.response.UserResponse
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute(
    userService: UserService
) {
    post {
        val userRequest = call.receive<UserRequest>()
        val createUser =
            userService.save(user = userRequest.toModel()) ?: return@post call.respond(HttpStatusCode.BadRequest)

        call.response.header(
            name = "id",
            value = createUser.id.toString()
        )
        call.respond(HttpStatusCode.Created, BaseResponse<Boolean>(201, "회원가입 성공", true))
    }
    authenticate("access-auth") {
        get {
            val users = userService.findAll()

            call.respond(message = BaseResponse<List<UserResponse>>(200, "success", users.map { it.toUserResponse() }))
        }
    }

    authenticate("access-auth") {
        get("/me") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asString() ?: return@get call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(404, "사용자를 찾을 수 없습니다.", null)
            )
            val foundUser = userService.findById(userId) ?: return@get call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(404, "사용자를 찾을 수 없습니다.", null)
            )
            call.respond(
                HttpStatusCode.OK,
                BaseResponse<UserResponse>(200, "success", foundUser.toUserResponse())
            )
        }
    }

    authenticate("access-auth") {
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val foundUser = userService.findById(id = id) ?: return@get call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(404, "사용자를 찾을 수 없습니다", null)
            )

            if (foundUser.id.toString() != extractPrincipalUserId(call)) {
                return@get call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(404, "사용자를 찾을 수 없습니다", null)
                )
            }

            call.respond(
                HttpStatusCode.OK,
                message =  BaseResponse<UserResponse>(200, "success", foundUser.toUserResponse())
            )
        }
    }
}

fun extractPrincipalUserId(call: RoutingCall): String? = call.principal<JWTPrincipal>()
    ?.payload
    ?.getClaim("userId")
    ?.asString()
