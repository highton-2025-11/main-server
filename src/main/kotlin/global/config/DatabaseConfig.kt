package com.example.global.config

import com.example.domain.audio.Audio
import com.example.domain.follow.Follow
import com.example.domain.member.Member
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 환경 변수를 로드하기 위한 dotenv 인스턴스
 */
val dotenv = dotenv()

/**
 * 데이터베이스 연결 및 스키마 초기화를 담당하는 설정 객체
 * 
 * 애플리케이션 시작 시 데이터베이스 연결을 설정하고 필요한 테이블을 생성합니다.
 */
object DatabaseConfig {
    /**
     * 데이터베이스 연결을 초기화하고 필요한 테이블 스키마를 생성합니다.
     * 
     * 환경 변수에서 데이터베이스 연결 정보를 가져와 연결을 설정하고,
     * Audio, Member, Follow 테이블을 생성합니다.
     */
    fun init() {
        Database.connect(
            url = dotenv["DB_URL"],
            user = dotenv["DB_USER"],
            driver = "com.mysql.cj.jdbc.Driver",
            password = dotenv["DB_PASSWORD"],
        )

        transaction {
            SchemaUtils.create(Audio, Member, Follow)
            addLogger(StdOutSqlLogger)
        }
    }

}
