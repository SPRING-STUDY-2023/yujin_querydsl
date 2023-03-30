package com.study.querydsl.repository;


import com.study.querydsl.Entity.Member;
import com.study.querydsl.Entity.MemberSearchCondition;
import com.study.querydsl.Entity.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsername(String username);

    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
