package com.example.domain.audio

import com.example.domain.member.Member
import com.example.domain.member.MemberDTO
import com.example.domain.member.MemberEntity
import com.example.domain.member.toResponseDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime

/**
 * 음성 편지 정보를 저장하는 데이터베이스 테이블 정의
 *
 * @property ownerId 음성 편지를 작성한 회원의 ID (Member 테이블 참조)
 * @property recieverId 음성 편지를 받는 회원의 ID (Member 테이블 참조)
 * @property title 음성 편지의 제목 (최대 255자)
 * @property audio 음성 파일 경로 (최대 255자)
 * @property text 음성에서 추출한 원본 텍스트
 * @property processText 인공지능으로 처리된 텍스트
 * @property createdAt 음성 편지 생성 일시 (자동 생성)
 */
object Audio: IntIdTable(name="audio") {
    val ownerId = reference("ownerId", Member, onDelete = ReferenceOption.CASCADE) // ManyToOne 관계
    val recieverId = reference("recieverId", Member, onDelete = ReferenceOption.CASCADE)
    val title = varchar(name = "title", length = 255)
    val audio = varchar(name = "audio", length = 255)
    val text = text(name = "text")
    val processText = text(name = "processText")
    val createdAt = datetime("created").defaultExpression(CurrentDateTime)
}

/**
 * 음성 편지 정보를 표현하는 엔티티 클래스
 *
 * @property title 음성 편지의 제목
 * @property audio 음성 파일 경로
 * @property text 음성에서 추출한 원본 텍스트
 * @property processText 인공지능으로 처리된 텍스트
 * @property owner 음성 편지를 작성한 회원 엔티티
 * @property reciever 음성 편지를 받는 회원 엔티티
 * @property createdAt 음성 편지 생성 일시
 */
class AudioEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AudioEntity>(Audio)

    var title by Audio.title
    var audio by Audio.audio
    var text by Audio.text
    var processText by Audio.processText

    var owner by MemberEntity referencedOn Audio.ownerId
    var reciever by MemberEntity referencedOn Audio.recieverId

    var createdAt by Audio.createdAt
}

/**
 * AudioEntity를 AudioDTO로 변환하는 확장 함수
 * 
 * 엔티티의 정보를 DTO로 변환하여 API 응답에 사용할 수 있게 합니다.
 * 소유자와 수신자의 정보도 함께 변환합니다.
 *
 * @return 현재 엔티티의 정보를 담은 AudioDTO 객체
 */
fun AudioEntity.toDTO(): AudioDTO {
    val member = MemberDTO(this.owner.id.value, this.owner.username, this.owner.password).toResponseDTO()
    val recieverMember = MemberDTO(this.reciever.id.value, this.reciever.username, this.reciever.password).toResponseDTO()

    return AudioDTO(this.id.value, this.title, member, recieverMember, this.audio, this.text, this.processText, this.createdAt.toString())
}