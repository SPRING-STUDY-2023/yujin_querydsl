package com.study.querydsl.repository;

import com.study.querydsl.Entity.MemberSearchCondition;
import com.study.querydsl.Entity.dto.MemberTeamDto;
import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);
}