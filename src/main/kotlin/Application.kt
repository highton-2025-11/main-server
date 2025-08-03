package com.example

import com.example.global.config.DatabaseConfig
import com.example.global.config.configureSerialization
import io.ktor.server.application.*

/**
 * 애플리케이션의 진입점 함수
 * 
 * Netty 엔진을 사용하여 Ktor 서버를 시작합니다.
 * 
 * @param args 명령줄 인수
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

/**
 * 애플리케이션 모듈 설정 함수
 * 
 * 애플리케이션의 주요 구성 요소를 초기화하고 설정합니다:
 * 1. 데이터베이스 연결 및 스키마 초기화
 * 2. JSON 직렬화 설정
 * 3. HTTP 관련 기능 설정 (CORS, 압축 등)
 * 4. 라우팅 설정
 */
fun Application.module() {
    DatabaseConfig.init()
    configureSerialization()
    configureHTTP()
    configureRouting()
}
