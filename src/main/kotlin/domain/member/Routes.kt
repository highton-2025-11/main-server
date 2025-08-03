package com.example.domain.member

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import mu.KotlinLogging

/**
 * 회원 관련 API 엔드포인트를 정의하는 라우팅 확장 함수
 *
 * 회원 조회, 로그인, 팔로우 관계 생성 등의 API 엔드포인트를 제공합니다.
 *
 * @param memberService 회원 관련 비즈니스 로직을 처리하는 서비스
 */
fun Route.memberRoutes(memberService: MemberService) {

    val logger = KotlinLogging.logger(name="member")
    route("/member") {
        /**
         * 회원 ID로 회원 정보를 조회하는 API 엔드포인트
         * GET /member/{id}
         */
        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw NotFoundException("id is not a number")

            val member = memberService.findById(id).toResponseDTO()

            call.respond(member)
        }
        
        /**
         * 회원 로그인을 처리하는 API 엔드포인트
         * POST /member
         */
        post {
            logger.info { "member request" }
            val body = call.receive<MemberLoginDTO>()
            logger.info { body }

            val member = memberService.login(body)

            logger.info { "member is $member" }

            call.respond(member)
        }
        
        /**
         * 회원 간 팔로우 관계를 생성하는 API 엔드포인트
         * POST /member/follow
         */
        post("/follow") {
            val body = call.receive<MemberCreateFollowDTO>()

            logger.info { body }

            memberService.follow(body)

            call.respond(HttpStatusCode.Created)
        }
    }
}