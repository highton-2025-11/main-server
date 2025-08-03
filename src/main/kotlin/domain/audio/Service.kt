package com.example.domain.audio

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.plugins.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import mu.KotlinLogging
import java.io.File
import java.util.UUID

/**
 * 음성 편지 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * 음성 파일 저장, 조회 및 관리 기능을 제공합니다.
 * 멀티파트 데이터에서 음성 파일을 추출하여 저장하고, 
 * 음성 편지 정보를 데이터베이스에 저장하는 기능을 담당합니다.
 *
 * @property audioRepository 음성 편지 정보 데이터 접근을 위한 리포지토리
 */
class AudioService(private val audioRepository: AudioRepository) {
    /**
     * 음성 파일이 저장될 디렉토리 경로
     */
    private val audioDir = File("src/main/resources/audio")

    /**
     * 로깅을 위한 로거 인스턴스
     */
    val logger = KotlinLogging.logger(name="Audio")

    /**
     * 멀티파트 데이터에서 음성 파일과 관련 정보를 추출하여 저장합니다.
     *
     * @param multipart 클라이언트로부터 전송된 멀티파트 데이터
     * @return 저장된 음성 편지 정보를 담은 DTO
     * @throws NotFoundException ID나 수신자 ID가 없는 경우
     */
    suspend fun saveAudio(multipart: MultiPartData): AudioDTO {
        logger.info { "saveAudio is reqeust" }
        var id:Int? = null
        var recieverId:Int? = null
        var filePath = ""
        var text = ""
        var title = ""
        var processText = ""



        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "id" -> id = part.value.toIntOrNull()
                        "recieverId" -> recieverId = part.value.toIntOrNull()
                        "text" -> text = part.value
                        "title" -> title = part.value
                        "processText" -> processText = part.value
                    }
                }
                is PartData.FileItem -> {
                    // 원본 파일명 또는 UUID로 고유 파일명 생성
                    val originalFileName = part.originalFileName
                    val fileExtension = originalFileName?.substringAfterLast('.', "")
                    val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"

                    // resources/audio 디렉토리 경로
                    if (!audioDir.exists()) {
                        audioDir.mkdirs()
                    }

                    // 파일 저장
                    val file = File(audioDir, uniqueFileName)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    filePath = uniqueFileName
                }
                else -> {}
            }
            part.dispose()
        }


        if (id != null && recieverId != null) {
            return id?.let {
                audioRepository.create(id!!, recieverId!!, title, filePath, text, processText)
            } ?: throw NotFoundException()
        } else {
            throw NotFoundException("id and recieverId is null")
        }

    }

    /**
     * ID로 음성 편지 정보를 조회합니다.
     *
     * @param id 조회할 음성 편지의 ID
     * @return 조회된 음성 편지 정보를 담은 DTO
     * @throws NotFoundException 해당 ID의 음성 편지가 존재하지 않는 경우
     */
    fun findById(id: Int) = audioRepository.findById(id)

    /**
     * ID로 음성 파일을 조회합니다.
     *
     * @param id 조회할 음성 편지의 ID
     * @return 음성 파일 객체
     * @throws NotFoundException 해당 ID의 음성 편지가 존재하지 않거나 파일이 존재하지 않는 경우
     */
    fun findFileById(id: Int): File {
        val audio = audioRepository.findById(id)

        val filePath = "$audioDir/${audio.audio}"

        val file = File(filePath)

        if (!file.exists()) {
            throw NotFoundException("File not found: $filePath")
        }

        return file
    }

    /**
     * 작성자(소유자) ID로 음성 편지 목록을 조회합니다.
     *
     * @param ownerId 작성자(소유자)의 ID
     * @return 해당 작성자가 작성한 음성 편지 목록
     */
    fun findByOwnerId(ownerId: Int): List<AudioDTO> = audioRepository.findByOwnerId(ownerId)

    /**
     * 수신자 ID로 음성 편지 목록을 조회합니다.
     *
     * @param recieverId 수신자의 ID
     * @return 해당 수신자가 받은 음성 편지 목록
     */
    fun findByRecieverId(recieverId: Int) = audioRepository.findByRecieverId(recieverId)
}