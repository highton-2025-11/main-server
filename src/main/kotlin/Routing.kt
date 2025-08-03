package com.example

import com.example.domain.audio.AudioRepository
import com.example.domain.audio.AudioService
import com.example.domain.audio.audioRoutes
import com.example.domain.member.MemberRepository
import com.example.domain.member.MemberService
import com.example.domain.member.memberRoutes
import com.fasterxml.jackson.databind.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}", status = HttpStatusCode.BadRequest)
        }

        exception<NotFoundException> { call, cause ->
            call.respondText("Not Found Exception: ${cause.message}", status = HttpStatusCode.NotFound)
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        audioRoutes(AudioService(AudioRepository()))
        memberRoutes(MemberService(MemberRepository()))
    }
}
