package com.study.querydsl;

import static com.mysema.commons.lang.Assert.assertThat;

import com.study.querydsl.Entity.Member;
import com.study.querydsl.repository.MemberRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).get();
//        assertThat(findMember).isEqualTo(member);
        List<Member> result1 = memberRepository.findAll();
//        assertThat(result1).containsExactly(member);
        List<Member> result2 = memberRepository.findByUsername("member1");
//        assertThat(result2).containsExactly(member);
    }
}