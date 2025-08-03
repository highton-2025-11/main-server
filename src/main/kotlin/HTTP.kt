package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

/**
 * HTTP 관련 기능을 설정하는 확장 함수
 * 
 * 다음과 같은 HTTP 관련 기능을 설정합니다:
 * 1. Swagger UI - API 문서화 인터페이스 제공
 * 2. CORS(Cross-Origin Resource Sharing) - 다른 도메인에서의 API 접근 허용
 * 
 * CORS 설정에서는 다양한 HTTP 메서드와 헤더를 허용하며,
 * 개발 환경에서는 모든 호스트의 접근을 허용합니다.
 * (참고: 프로덕션 환경에서는 보안을 위해 접근 가능한 호스트를 제한하는 것이 좋습니다)
 */
fun Application.configureHTTP() {
    routing {
        swaggerUI(path = "openapi")
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}
