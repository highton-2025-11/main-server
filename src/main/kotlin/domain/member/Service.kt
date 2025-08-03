package com.example.domain.member

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * 회원 조회, 로그인, 팔로우 관계 관리 등의 기능을 제공합니다.
 * 
 * @property memberRepository 회원 정보 데이터 접근을 위한 리포지토리
 */
class MemberService(
    private val memberRepository: MemberRepository,
) {
    /**
     * ID로 회원 정보를 조회합니다.
     *
     * @param id 조회할 회원의 ID
     * @return 조회된 회원 정보를 담은 DTO
     * @throws NoSuchElementException 해당 ID의 회원이 존재하지 않는 경우
     */
    fun findById(id: Int) = memberRepository.findById(id)

    /**
     * 회원 로그인을 처리합니다.
     *
     * @param dto 로그인 정보가 담긴 DTO (ID와 비밀번호)
     * @return 로그인한 회원 정보와 팔로워/팔로잉 정보를 담은 DTO
     * @throws NotFoundException 로그인 정보가 일치하는 회원이 없는 경우
     */
    fun login(dto: MemberLoginDTO) = memberRepository.login(dto)

    /**
     * 회원 간 팔로우 관계를 생성합니다.
     *
     * @param dto 팔로우 관계 생성 정보가 담긴 DTO
     * @throws NotFoundException 이미 팔로우 관계가 존재하는 경우
     * @throws NoSuchElementException 존재하지 않는 회원 ID가 제공된 경우
     */
    fun follow(dto: MemberCreateFollowDTO) = memberRepository.follow(dto.followerId, dto.followeeId)
}