package com.example.global.config

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * Koin 의존성 주입 프레임워크를 설정하는 확장 함수
 * 
 * 애플리케이션에 Koin 의존성 주입 프레임워크를 설치하고 구성합니다.
 * 로깅을 위한 SLF4J 로거를 설정하고 필요한 모듈을 등록합니다.
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules()
    }
}
