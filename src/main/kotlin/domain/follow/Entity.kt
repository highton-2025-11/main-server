package com.example.domain.follow

import com.example.domain.member.Member
import org.jetbrains.exposed.sql.Table

/**
 * 회원 간 팔로우 관계를 저장하는 데이터베이스 테이블 정의
 *
 * 팔로워와 팔로이 간의 관계를 나타내는 테이블입니다.
 * 복합 기본 키(follower, followee)를 사용하여 중복 팔로우를 방지합니다.
 *
 * @property follower 팔로우를 요청한 회원의 ID (Member 테이블 참조)
 * @property followee 팔로우 대상 회원의 ID (Member 테이블 참조)
 */
object Follow: Table("follow") {
    val follower = reference("follower", Member)
    val followee = reference("followee", Member)

    override val primaryKey = PrimaryKey(follower, followee)
}

