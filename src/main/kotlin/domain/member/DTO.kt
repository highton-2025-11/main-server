package com.example.domain.member

import io.ktor.server.plugins.*
import kotlinx.serialization.Serializable

/**
 * @property id 유저의 id
 * @property username 유저의 이름
 * @property password 유저의 비밀번호
 */
@Serializable
data class MemberDTO(
    val id: Int? = null,
    val username: String,
    val password: String,
)

/**
 * @property id 유저의 id
 * @property username 유저의 이름
 * @property following 내가 팔로우 하는 유저의 정보
 * @property followers 나를 팔로우 하는 유저의 정보
 */
@Serializable
data class MemberWithFollowersDTO(
    val id: Int,
    val username: String,
    val following: List<MemberFollowDTO>,
    val followers: List<MemberFollowDTO>,
)

/**
 * @property id 팔로우 유저 id
 * @property username 팔로우 유저의 이름
 */
@Serializable
data class MemberFollowDTO(
    val id: Int,
    val username: String,
)

/**
 * @property followerId 팔로우를 신청한 사람의 id
 * @property followeeId 팔로우를 받은 사람의 id
 */
@Serializable
data class MemberCreateFollowDTO(
    val followerId: Int,
    val followeeId: Int
)

fun MemberDTO.toResponseDTO(): MemberResponseDTO = this.id?.let{
    MemberResponseDTO(this.id, this.username)
} ?: throw NotFoundException("member dto is not found")

/**
 * @property id 유저의 id
 * @property username 유저의 이름
 */
@Serializable
data class MemberResponseDTO(
    val id: Int,
    val username: String
)

/**
 * @property id 로그인시 유저의 id
 * @property password 로그인시 유저의 비밀번호
 */
@Serializable
data class MemberLoginDTO(
    val id: Int,
    val password: String
)