package com.example.domain.audio

import com.example.domain.member.MemberResponseDTO
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @property id 해당 음성 편지의 오디오
 * @property title 해당 음성 편지의 제목
 * @property owner 해당 음성 편지를 작성한 사람의 정보
 * @property reciever 해당 음성 편지를 받는 사람의 정보
 * @property audio 음성 편지의 음성 파일 경로
 * @property text 음성에서 추출한 텍스트
 * @property processText 인공지능에서 처리된 텍스트
 * @property createdAt 해당 음성 편지가 작성된 날짜
 */
@Serializable
data class AudioDTO(
    val id: Int,
    val title: String,
    val owner: MemberResponseDTO,
    val reciever: MemberResponseDTO,
    val audio: String,
    val text: String,
    val processText: String,
    @Contextual val createdAt: String? = null
)

/**
 * @property text 음성 편지의 음성 파일에서 추출한 텍스트
 * @property target 음성 편지의 대상
 */
@Serializable
data class AudioRecommendTitleDTO(
    val text: String,
    val target: String,
)

/**
 * @property title 인공지능이 추천하는 제목
 * @property rating 해당 글의 긍정도(낮을수록 부정)
 */
@Serializable
data class AudioRecommendTitleResponseDTO(
    val title: String,
    val rating: Int
)

/**
 * @property text 음성 편지의 음성 파일에서 추출한 텍스트
 * @property target 음성 편지의 대상
 * @property instruction 사용자가 인공지능에게 부탁하는 프롬포트
 */
@Serializable
data class AudioRecommendTextDTO(
    val text: String,
    val target: String,
    val instruction: String
)

/**
 * @property processed_content 인공지능이 처리하여 추천하는 전달 내용
 */
@Serializable
data class AudioRecommendTextResponseDTO(
    val processed_content: String
)

@Serializable
data class AudioToTextResponseDTO(
    val result: String
)