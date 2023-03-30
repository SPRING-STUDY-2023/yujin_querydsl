package com.study.querydsl.repository;

import static com.study.querydsl.Entity.QMember.member;
import static com.study.querydsl.Entity.QTeam.team;
import static org.springframework.util.StringUtils.isEmpty;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.Entity.MemberSearchCondition;
import com.study.querydsl.Entity.dto.MemberTeamDto;
import com.study.querydsl.Entity.dto.QMemberTeamDto;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.util.StringUtils;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
//회원명, 팀명, 나이(ageGoe, ageLoe)
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory.select(
                        new QMemberTeamDto(member.id, member.username, member.age, team.id, team.name))
                .from(member).leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()), teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()), ageLoe(condition.getAgeLoe())).fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.isEmpty(username) ? null : member.username.eq(username);
    }

    private BooleanExpression teamNameEq(String teamName) {
        return isEmpty(teamName) ? null : team.name.eq(teamName);
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe == null ? null : member.age.goe(ageGoe);
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }
}