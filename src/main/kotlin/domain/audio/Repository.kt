package com.example.domain.audio

import com.example.domain.member.MemberDTO
import com.example.domain.member.MemberEntity
import com.example.domain.member.toDTO
import com.example.domain.member.toResponseDTO
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 음성 편지 정보에 대한 데이터베이스 작업을 처리하는 리포지토리 클래스
 *
 * 음성 편지의 생성, 조회 기능을 제공합니다.
 * 소유자 ID나 수신자 ID로 음성 편지를 조회하는 기능도 지원합니다.
 */
class AudioRepository {
    /**
     * 새로운 음성 편지를 생성합니다.
     *
     * @param id 음성 편지 작성자(소유자)의 ID
     * @param recieverId 음성 편지 수신자의 ID
     * @param titleValue 음성 편지의 제목
     * @param audioPath 음성 파일 경로
     * @param textValue 음성에서 추출한 원본 텍스트
     * @param processTextValue 인공지능으로 처리된 텍스트
     * @return 생성된 음성 편지 정보를 담은 DTO
     * @throws NotFoundException 작성자 ID가 존재하지 않는 경우
     */
    fun create(id: Int, recieverId: Int, titleValue: String, audioPath: String, textValue: String, processTextValue: String): AudioDTO = transaction {
        return@transaction MemberEntity.findById(id)?.let {
            val member = MemberDTO(it.id.value, it.username, it.password).toResponseDTO()
            val recieverMember = MemberEntity[recieverId]
            val audio = AudioEntity.new{
                owner = it
                reciever = recieverMember
                audio = audioPath
                text = textValue
                title = titleValue
                processText = processTextValue
            }

            AudioDTO(audio.id.value, audio.title, member, recieverMember.toDTO().toResponseDTO(), audio.audio, audio.text, audio.processText, audio.createdAt.toString())
        } ?: throw NotFoundException()
    }

    /**
     * ID로 음성 편지 정보를 조회합니다.
     *
     * @param id 조회할 음성 편지의 ID
     * @return 조회된 음성 편지 정보를 담은 DTO
     * @throws NotFoundException 해당 ID의 음성 편지가 존재하지 않는 경우
     */
    fun findById(id: Int): AudioDTO = transaction {
        return@transaction AudioEntity.findById(id)?.toDTO() ?: throw NotFoundException()
    }

    /**
     * 작성자(소유자) ID로 음성 편지 목록을 조회합니다.
     *
     * @param id 작성자(소유자)의 ID
     * @return 해당 작성자가 작성한 음성 편지 목록
     */
    fun findByOwnerId(id: Int): List<AudioDTO> = transaction {
        return@transaction AudioEntity.find { Audio.ownerId eq id}.map {
            it.toDTO()
        }.toList()
    }

    /**
     * 수신자 ID로 음성 편지 목록을 조회합니다.
     *
     * @param id 수신자의 ID
     * @return 해당 수신자가 받은 음성 편지 목록
     */
    fun findByRecieverId(id: Int): List<AudioDTO> = transaction {
        return@transaction AudioEntity.find {Audio.recieverId eq id}.map {
            it.toDTO()
        }.toList()
    }
}