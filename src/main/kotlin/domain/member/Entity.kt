package com.example.domain.member

import com.example.domain.follow.Follow
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.select

/**
 * 회원 정보를 저장하는 데이터베이스 테이블 정의
 * 
 * @property username 사용자 이름 (최대 255자)
 * @property password 사용자 비밀번호 (최대 255자)
 */
object Member: IntIdTable(name = "member") {
    val username = varchar("username", 255)
    val password = varchar("password", 255)
}

/**
 * 회원 정보를 표현하는 엔티티 클래스
 * 
 * @property username 사용자 이름
 * @property password 사용자 비밀번호
 * @property following 현재 사용자가 팔로우하는 다른 사용자 목록
 * @property followers 현재 사용자를 팔로우하는 다른 사용자 목록
 */
class MemberEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MemberEntity>(Member)

    var username by Member.username
    var password by Member.password

    /**
     * 내가 팔로우한 사람들 (내가 follow 테이블에서 follower일 때의 followee 목록)
     * 
     * @return 현재 사용자가 팔로우하는 사용자 목록
     */
    val following: SizedIterable<MemberEntity>
        get() = Follow.select { Follow.follower eq id }
            .map { MemberEntity[it[Follow.followee]] }
            .let { SizedCollection(it) }

    /**
     * 나를 팔로우한 사람들 (내가 follow 테이블에서 followee일 때의 follower 목록)
     * 
     * @return 현재 사용자를 팔로우하는 사용자 목록
     */
    val followers: SizedIterable<MemberEntity>
        get() = Follow.select { Follow.followee eq id }
            .map { MemberEntity[it[Follow.follower]] }
            .let { SizedCollection(it) }
}

/**
 * MemberEntity를 MemberDTO로 변환하는 확장 함수
 * 
 * @return 현재 엔티티의 정보를 담은 MemberDTO 객체
 */
fun MemberEntity.toDTO() = MemberDTO(id.value, username, password)

/**
 * MemberEntity를 팔로워 정보가 포함된 MemberWithFollowersDTO로 변환하는 확장 함수
 * 
 * @return 현재 엔티티와 팔로워/팔로잉 정보를 담은 MemberWithFollowersDTO 객체
 */
fun MemberEntity.toWithFollowerDTO() = MemberWithFollowersDTO(id.value, username, this.toFollowerDTOList(following), this.toFollowerDTOList(followers))

/**
 * MemberEntity 목록을 MemberFollowDTO 목록으로 변환하는 확장 함수
 * 
 * @param list 변환할 MemberEntity 목록
 * @return 변환된 MemberFollowDTO 목록
 */
fun MemberEntity.toFollowerDTOList(list: SizedIterable<MemberEntity>) = list.map { MemberFollowDTO(it.id.value, it.username) }.toList()