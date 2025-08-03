package com.example.global.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.jackson.*
import io.ktor.client.plugins.contentnegotiation.*

object HttpClientProvider {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson {
                enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
                registerModule(com.fasterxml.jackson.datatype.joda.JodaModule())
            }
        }
    }
}
