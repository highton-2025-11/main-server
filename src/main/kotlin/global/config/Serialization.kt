package com.example.global.config

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.joda.JodaModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

/**
 * JSON 직렬화 및 역직렬화를 설정하는 확장 함수
 * 
 * 애플리케이션에 ContentNegotiation 플러그인을 설치하고 Jackson 직렬화 라이브러리를 구성합니다.
 * 들여쓰기된 출력을 활성화하고 Joda 시간 모듈을 등록하여 날짜/시간 타입의 직렬화를 지원합니다.
 * 이를 통해 API 요청 및 응답의 JSON 변환을 처리합니다.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
                registerModule(JodaModule())
            }
    }
}
