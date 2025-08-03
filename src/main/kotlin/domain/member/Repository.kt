package com.example.domain.member

import com.example.domain.follow.Follow
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 회원 정보에 대한 데이터베이스 작업을 처리하는 리포지토리 클래스
 * 
 * 회원 정보의 저장, 조회, 로그인 및 팔로우 관계 관리 기능을 제공합니다.
 */
class MemberRepository {
    /**
     * 새로운 회원 정보를 저장합니다.
     *
     * @param dto 저장할 회원 정보가 담긴 DTO
     * @return 저장된 회원 정보를 담은 DTO
     */
    fun save(dto: MemberDTO) = transaction {
        val entity = MemberEntity.new { this.username = dto.username; this.password = dto.password }

        entity.toDTO()
    }

    /**
     * ID로 회원 정보를 조회합니다.
     *
     * @param id 조회할 회원의 ID
     * @return 조회된 회원 정보를 담은 DTO
     * @throws NoSuchElementException 해당 ID의 회원이 존재하지 않는 경우
     */
    fun findById(id: Int) = transaction {
        MemberEntity[id].toDTO()
    }

    /**
     * 회원 로그인을 처리합니다.
     *
     * @param dto 로그인 정보가 담긴 DTO (ID와 비밀번호)
     * @return 로그인한 회원 정보와 팔로워/팔로잉 정보를 담은 DTO
     * @throws NotFoundException 로그인 정보가 일치하는 회원이 없는 경우
     */
    fun login(dto: MemberLoginDTO) = transaction {
        MemberEntity.find {
            (Member.id eq dto.id) and (Member.password eq dto.password)
        }.firstOrNull()?.toWithFollowerDTO() ?: throw NotFoundException("not found user")
    }

    /**
     * 회원 간 팔로우 관계를 생성합니다.
     * 이 구현에서는 양방향 팔로우 관계가 생성됩니다.
     *
     * @param followerId 팔로우를 요청하는 회원의 ID
     * @param followeeId 팔로우 대상 회원의 ID
     * @throws NotFoundException 이미 팔로우 관계가 존재하는 경우
     * @throws NoSuchElementException 존재하지 않는 회원 ID가 제공된 경우
     */
    fun follow(followerId: Int, followeeId: Int) = transaction {
        val follower = MemberEntity[followerId]
        val followee = MemberEntity[followeeId]

        val alreadyFollowed = Follow.select {
            (Follow.follower eq follower.id) and (Follow.followee eq followee.id)
        }.count() > 0

        if (!alreadyFollowed) {
            Follow.insert {
                it[Follow.follower] = follower.id
                it[Follow.followee] = followee.id
            }

            Follow.insert {
                it[Follow.follower] = followee.id
                it[Follow.followee] = follower.id
            }
        } else {
            throw NotFoundException("is already followed")
        }
    }
}