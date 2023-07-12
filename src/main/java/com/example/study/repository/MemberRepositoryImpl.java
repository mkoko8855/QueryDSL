package com.example.study.repository;

import com.example.study.entity.Member;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

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







    //0712
    //WHERE절에 BooleanExpression을 리턴하는 메서드를 사용한다.
    //nameEq, ageEq에서는 값이 전달되지 않으면 null을 리턴하고 그렇지 않을 경우에는 값을 반환한다.
    //또한, WHERE절에서는 null값인 경우에는 조건을 건너 뛴다.


    public List<Member> findUser(String nameParam, Integer ageParam){
        //쿼리문부터쓰자
        return queryFactory
                .selectFrom(member)
                .where(nameEq(nameParam), ageEq(ageParam)) //name과 age가 동시에 들어오니, 조건절 2개 다걸어줘. 하나면 하나만걸어줘. 근데 아무튼 뭘하든 객체가필요함. where절 괄호안에 nameEq와 ageEq를 하고 메서드 만들자
                .fetch();

    }


    //0712
    private BooleanExpression ageEq(Integer ageParam) { //객체 타입이면 int로 null비교가능하니까! int말고 객체타입인 integer로 하자.
        return ageParam != null ? member.age.eq(ageParam) : null;
    }


    //0712
    private BooleanExpression nameEq(String nameParam) {
        return nameParam != null ? member.userName.eq(nameParam) : null;
    }





}
