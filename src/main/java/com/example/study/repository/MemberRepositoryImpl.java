package com.example.study.repository;

import com.example.study.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.study.entity.QMember.member;


//QueryDsl용 인터페이스의 구현체는 반드시 끝이 Impl로 끝나야 자동으로 인식되어서
//원본 인터페이스인 MemberRepository 타입의 객체로도 사용이 가능하다.
//우리가 클래스만들때 Impl붙여줬잖아. 그뜻임.



//0711
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {


    private final JPAQueryFactory queryFactory; //쿼리dslConfig클래스에서 이미 엔터티매니저 갖고있으니 바로 사용할 수 있음.



    @Override //위에 빨간줄떠서 알트엔터 추상메서드구현 고고.
    public List<Member> findByName(String name) {  //여기다가 쿼리DSL 문법 작성하면됨.
        //쿼리DSL쓰려면 JPA쿼리팩토리필요하지?
        
        return queryFactory //바로아래 member에 아트엔터로 static 임포트해야됨. 그러면 위에 import static com.example.study.entity.QMember.member 이게 생김
                .selectFrom(member)
                .where(member.userName.eq(name))
                .fetch();
    }
}
