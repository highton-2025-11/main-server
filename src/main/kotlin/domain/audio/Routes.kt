package com.example.domain.audio

import com.example.global.util.HttpClientProvider
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

val dotenv = dotenv()

/**
 * 음성 편지 관련 API 엔드포인트를 정의하는 라우팅 확장 함수
 *
 * 음성 편지 저장, 조회, 파일 다운로드, 텍스트 추출, 추천 기능 등의 API 엔드포인트를 제공합니다.
 * 외부 AI 서버와의 통신을 통해 음성 텍스트 변환 및 콘텐츠 추천 기능을 지원합니다.
 *
 * @param audioService 음성 편지 관련 비즈니스 로직을 처리하는 서비스
 */
fun Route.audioRoutes(audioService: AudioService) {
    val client = HttpClientProvider.client
    val aiServerUrl = dotenv["AI_SERVER_URL"]
    val logger = KotlinLogging.logger(name="Audio")
    route("/audio") {
        /**
         * 새로운 음성 편지를 저장하는 API 엔드포인트
         * POST /audio
         */
        post {
            val multipart = call.receiveMultipart()

            val response = audioService.saveAudio(multipart)

            call.respond(response)
        }
        
        /**
         * ID로 음성 편지 정보를 조회하는 API 엔드포인트
         * GET /audio/{id}
         */
        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw NotFoundException("id is not a number")

            val audio = audioService.findById(id)

            call.respond(audio)
        }
        
        /**
         * ID로 음성 파일을 다운로드하는 API 엔드포인트
         * GET /audio/file/{id}
         */
        get("/file/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw NotFoundException("id is not a number")

            val file = audioService.findFileById(id)
            val audioContentType = ContentType.parse("audio/m4a")

            val downloadFileName = file.name

            // 수동으로 Content-Type 설정
            call.response.header(HttpHeaders.ContentType, audioContentType.toString())

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, downloadFileName).toString()
            )

            // 파일 직접 응답
            call.respondOutputStream(status = HttpStatusCode.OK) {
                file.inputStream().use { it.copyTo(this) }
            }
        }
        
        /**
         * 오류 테스트를 위한 API 엔드포인트
         * GET /audio/error
         */
        get("/error") {
            throw NotFoundException("test")
        }
        
        /**
         * 작성자(소유자) ID로 음성 편지 목록을 조회하는 API 엔드포인트
         * GET /audio/owner/{id}
         */
        get("/owner/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw NotFoundException("id is not a number")

            call.respond(audioService.findByOwnerId(id))
        }
        
        /**
         * 수신자 ID로 음성 편지 목록을 조회하는 API 엔드포인트
         * GET /audio/reciever/{id}
         */
        get("/reciever/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw NotFoundException("id is not a number")

            val response = audioService.findByRecieverId(id)

            if (response.isNotEmpty()) {
                call.respond(response)
            } else {
                throw NotFoundException("해당 수신인이 받은 음성 일기가 없음")
            }
        }
        /**
         * 음성 편지 제목 추천을 요청하는 API 엔드포인트
         * POST /audio/recommend/title
         */
        post("/recommend/title") {
            val body = call.receive<AudioRecommendTitleDTO>()

            val response = client.post("$aiServerUrl/get-info") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<AudioRecommendTitleResponseDTO>()

            call.respond(response)
        }
        
        /**
         * 음성 편지 내용 추천을 요청하는 API 엔드포인트
         * POST /audio/recommend/text
         */
        post("/recommend/text") {
            val body = call.receive<AudioRecommendTextDTO>()

            val response = client.post("$aiServerUrl/process-content") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<AudioRecommendTextResponseDTO>()

            call.respond(response)
        }
        
        /**
         * 음성 파일을 텍스트로 변환하는 API 엔드포인트
         * POST /audio/text
         */
        post("/text") {
            val multipart = call.receiveMultipart()
            var audioBytes: ByteArray? = null
            var fileName: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem && part.name == "file") {
                    fileName = part.originalFileName
                    audioBytes = part.streamProvider().readBytes()
                }
                part.dispose()
            }

            if (audioBytes == null || fileName == null) {
                throw NotFoundException("오디오 파일이 안옴")
            }

            // 외부 서버로 요청
            val externalResponse = client.submitFormWithBinaryData(
                url = "$aiServerUrl/transcribe-audio", // 실제 URL로 변경
                formData = formData {
                    append("audio_file", audioBytes!!, Headers.build {
                        append(HttpHeaders.ContentType, "audio/x-m4a") // or "audio/mpeg" 등
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                }
            )

            val responseJson = externalResponse.body<AudioToTextResponseDTO>()
            call.respond(HttpStatusCode.OK, responseJson)
        }

    }
}