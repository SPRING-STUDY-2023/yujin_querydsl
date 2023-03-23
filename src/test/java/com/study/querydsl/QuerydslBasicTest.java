package com.study.querydsl;

import static com.querydsl.core.types.ExpressionUtils.isNull;
import static com.study.querydsl.Entity.QMember.member;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.querydsl.core.QueryResults;
import java.util.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.Entity.Member;
import com.study.querydsl.Entity.QMember;
import com.study.querydsl.Entity.Team;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        String qlString = "select m from Member m " + "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername(), is(equalTo("member1")));
    }

    @Test
    public void startQuerydsl() {
//member1을 찾아라.
        QMember m = new QMember("m");
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername(),is(equalTo("member1")));
    }

    @Test
    public void startQuerydsl2() {
        QMember m = new QMember("m");
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername(),is(equalTo("member1")));
    }

    @Test
    public void startQuerydsl3() {

        QMember qMember = new QMember("m"); //별칭 직접 지정 // 같은 테이블 조인할때
        QMember qMember1 = QMember.member; //기본 인스턴스 사용

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername(),is(equalTo("member1")));
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername(),is(equalTo("member1")));
    }

    @Test
    public void searchAndParam() {
        List<Member> result1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10))
                .fetch();
        assertThat(result1.size(), is(equalTo(1)));
    }
    @Test
    public void resultFetch() {
        //List
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();
//단 건
        Member findMember1 = queryFactory
                .selectFrom(member)
                .fetchOne();
//처음 한 건 조회
        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchFirst();
//페이징에서 사용
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
//count 쿼리로 변경
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
    }


    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */

    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();
        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername(), is(equalTo("member5")));
        assertThat(member6.getUsername(), is(equalTo("member6")));
    }
    @Test
    public void paging1() { // 조회 건수 제한
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc()) .offset(1) //0부터 시작(zero index) .limit(2) //최대 2건 조회
                .fetch();
        assertThat(result.size(), is(equalTo(2)));
    }

    @Test
    public void paging2() {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(queryResults.getTotal(), is(equalTo((4))));
        assertThat(queryResults.getLimit(), is(equalTo((2))));
        assertThat(queryResults.getOffset(), is(equalTo((1))));
        assertThat(queryResults.getResults().size(), is(equalTo((2))));
    }
}

