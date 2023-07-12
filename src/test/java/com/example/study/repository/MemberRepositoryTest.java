package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.example.study.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.verification.TooFewActualInvocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.study.entity.QMember.member;
import static com.example.study.entity.QTeam.team;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    //필요한객체부터 주입받자
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em; //이건, 스프링데이터JPA에선 볼일이많지않았다. 굳이쓸일없으니. 즉, 얘는 JPA 관리 핵심 객체이다.
    JPAQueryFactory factory; //매개값으로는 엔터티매니저. 얘가 바로 쿼리문을 작성할 때의 핵심 객체이다. //지금은 오토와이드안할꺼임. 테스트클래스에서 직접생성해보고 쿼리DSL을 사용할 수 있는 다른 패턴을 소개하면서 얘도 자동으로 받아서 주입할꺼다.

    @BeforeEach
    void settingObject(){ //테스트 진행 전에 세팅될 수 있도록.
        factory = new JPAQueryFactory(em); //엔터티매니저를 넣어주자.
    }


    @Test
    @DisplayName("testJPA")
    void testJPA(){
        //given
        List<Member> members = memberRepository.findAll();
        //when
        members.forEach(System.out::println);
        //then


    }



    //더미데이터넣자
    @Test
    void testInsertData(){

       /* Team teamA = Team.builder()
                .name("teamA")
                .build();
        Team teamB = Team.builder()
                .name("teamB")
                .build();


        teamRepository.save(teamA);
        teamRepository.save(teamB);*/


        Member member5 = Member.builder()
                .userName("member5")
                .age(50)
                //.team(teamA)
                .build();

        Member member6 = Member.builder()
                .userName("member6")
                .age(60)
                //.team(teamA)
                .build();

        Member member7 = Member.builder()
                .userName("member7")
                .age(70)
                //.team(teamB)
                .build();

        Member member8 = Member.builder()
                .userName("member8")
                .age(80)
                //.team(teamB)
                .build();

        //memberRepository.save(member1);
        //memberRepository.save(member2);
        //memberRepository.save(member3);
        //memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
        memberRepository.save(member8);

    }







    //이제 JPQL테스트
    @Test
    @DisplayName("JPQL")
    void testJPQL() {
        //given
        String jpqlQuery = "SELECT m FROM Member m WHERE m.userName = : userName";  //Member는 테이블이름이아니다! 엔터티의 필드명이다.

        //when
        Member foundMember = em.createQuery(jpqlQuery, Member.class).setParameter("userName", "member2") //그리고 우리는 파라미터 주는거잖아(:userName) 이거를 채워넣자.
        .getSingleResult();

        //then
        assertEquals("teamB", foundMember.getTeam().getName());

        System.out.println("\n\n\n");
        System.out.println("foundMember = " + foundMember);
        System.out.println("\n\n\n");

    }


    //이제 쿼리DSL테스트
    @Test
    @DisplayName("QueryDSL")
    void testQueryDSL() {
        //given
        //QMember m = QMember.member; //QMember m = new QMember("m1"); 보단 이걸 권장함. 그러나주석.

        //when
        //쿼리DSL을 사용하기 위한 핵심 객체는 jpa쿼리팩토리(위에선언한변수)
        Member foundMember = factory
                .select(member)
                .from(member)
                .where(member.userName.eq("member3"))
                .fetchOne();
        //then
        assertNotNull(foundMember);
        assertEquals("teamA", foundMember.getTeam().getName());
    }






    @Test
    @DisplayName("search")
    void search() {
        //given
        String searchName = "member2";
        int searchAge = 20;
        //when
        Member foundMember = factory
                        .selectFrom(member) //selectFrom은 SELECT * FROM 과 같다.
                        .where(member.userName.eq(searchName)
                        .and(member.age.eq(searchAge)))//멤버에 유저네임이 같니? 기븐으로 주어진 서치네임과?, 그리고 멤버에 나이가 같니 서치네임과?
                        .fetchOne(); //조건이하나면 fetchOne, 여러개면 fetchAll

        //then
        assertNotNull(foundMember);
        assertEquals("teamA", foundMember.getTeam().getName());

         /*
                JPAQueryFactory(팩토리)를 이용해서 쿼리문을 조립한 후 반환 인자를 결정한다.
                - fetchOne(): 단일 건 조회. (하나인게 확실하면 fetchOne쓰자. 여러건 조회되면 예외발생함)
                즉, 작성한 쿼리문의 결과가 단일인지 복수인지 파악해~

                - fetchFirst(): 단일 건 조회. 여러 개가 조회돼도 첫 번째 값만 반환.
                - fetch() : 여러 건 조회 할 때 List 형태로 반환.






                JPQL이 제공하는 모든 검색 조건을 queryDsl에서도 사용이 가능하다.
                여러 가지가 있다.
                member.userName.eq("member1") //이건 userName = 'member1' 과 같다.
                member.userName.nq("member1") //이건 userName != 'member1' 과 같다.
                member.userName.eq("member1").not() //이건 userName != 'member1' 과 같다.
                member.userName.isNotNull() //이름이 is not Null 이다. -> 이름이 널이 아닌 애들 조회하는거임.
                member.age.in(10, 20) //age in(10, 20) 이라는 것과 같다.
                member.age.notIn(10, 20) //age not in(10, 20) 과 같음
                member.age.between(10, 30) // age between 10 and 30 과 같음
                member.age.goe(10, 30) // age >= 30 과 같음
                member.age.gt(10, 30) // age > 30 과 같음
                member.age.loe(10, 30) // age <= 30 과 같음
                member.age.lt(10, 30) // age < 30 과 같음
                member.userName.like("_김%") // userName LIKE '_김%' (두번째가 김으로 시작하는거)
                member.userName.contains("김") //userName LIKE '%김%'
                member.userName.startsWith("김") //userName LIKE '김%' (김으로 시작하면 조회해라)
                member.userName.endsWith("김") //userName LIKE '%김' (김으로 끝나면 조회해라)

         */

    }


    @Test
    @DisplayName("결과 반환하기")
    void testFetchResult() {
        //fetch(그냥 페치는 여러개라했다)
        List<Member> fetch1 = factory.selectFrom(member).fetch(); //SELECT * FROM tbl_member; 와 같음.
        //출력바로해보자
        System.out.println("\n\n ========== fetch1 ==========");
        fetch1.forEach(System.out::println);

        //fetchOne
        Member fetch2 = factory.selectFrom(member) //하나니까 조건걸어볼까
                .where(member.id.eq(3L))
                .fetchOne();
        System.out.println("\n\n ========== fetchOne ==========");
        System.out.println("fetch2 = " + fetch2); //반복문돌릴필요없지얘는. id는 pk니까 하나일꺼아니야.


        //fetchFirst
        Member fetch3 = factory.selectFrom(member)
                .fetchFirst();
        System.out.println("\n\n ========== fetchFirst ==========");
        //얘도 하나만오니까 반복필요없지
        System.out.println("fetch3 = " + fetch3);

    }



    //impl, Custom클래스 다 만들고 테스트해보자
    @Test
    @DisplayName("QueryDsl custom 설정 확인")
    void queryDslCustom() { //impl클래스에서 조립다해놨다.
        //given
        String name = "member4";
        //when
        List<Member> result = memberRepository.findByName(name);
        //then
        assertEquals(1, result.size());
        assertEquals("teamB", result.get(0).getTeam().getName());
    }



    @Test
    @DisplayName("회원 정렬 조회")
    void sort() {
        //given

        //when
        List<Member> result = factory.selectFrom(member)
                .orderBy(member.age.asc()) //asc또는 desc
                .fetch(); //여러개니까 페치

        //then
        assertEquals(result.size(), 8);
        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");

    }



    @Test
    @DisplayName("queryDsl paging")
    void paging() {
        //given

        //when
        List<Member> result = factory.selectFrom(member)
                .orderBy(member.userName.desc()) //내림차 먼저 정렬한 후 페이징 들어가자. 페이징을 mysql에서 했던 것처럼 해보자
                //일단 sql로 페이징문법은 다음과같다 -> SELECT * FROM tbl_member ORDER BY user_name DESC LIMIT 0, 10 (첫번째값부터10개가져와라). 2페이지로 넘어가면 LIMIT 10, 10.
                .offset(0) //오프셋 리밋을 순서대로.   1페이지면 0번부터니  0번부터 최대 3개까지 꺼낼수있음. 2페이지떈 0이아니라 3이겠지. 그거를 List로 받은것이다.
                .limit(3)
                .fetch();


        //then
        assertEquals(result.size(), 3);
        assertEquals(result.get(2).getUserName(), "member6"); //8 7 6
    }




    @Test
    @DisplayName("그룹 함수의 종류") //그룹함수도 쿼리DSL에서 사용가능하다는 것을 해보자.
    void aggregation() {
        //given

        //when
        List<Tuple> result = factory.select( //튜플이란리스트로받을수있음
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                ).from(member)
                .fetch();

        //then
        Tuple tuple = result.get(0);
        assertEquals(tuple.get(member.count()), 8); //뭘로꺼내냐면, 우리가 조회하는 컬럼명을쓰면됨. count를 꺼냈을때 8임을 단언한다!
        assertEquals(tuple.get(member.age.sum()), 360);
        assertEquals(tuple.get(member.age.avg()), 45);
        assertEquals(tuple.get(member.age.max()), 80);
        assertEquals(tuple.get(member.age.min()), 10);

        System.out.println("\n\n\n");
        System.out.println("tuple = " + tuple.toString());
        System.out.println("\n\n\n");

    }


    @Test
    @DisplayName("Group By, Having")
    void testGroup() {
        //given


        //when

        List<Long> result = factory.select(member.age.count()) //그룹바이안주면 그룹대상은 테이블 전체다. 근데줄꺼다.
                .from(member)
                .orderBy(member.age.asc())
                .groupBy(member.age)
                .having(member.age.count().goe(2)) //그룹의 카운터가 2 이상인 애들
                .fetch();

        //즉, 멤버테이블에서 조회하는데 그룹을짓는다. age별로. 그룹을 짓는 조건은 카운트가 2이상인애들만.


        //then
        assertEquals(result.size(), 2);

        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");
    }




    @Test
    @DisplayName("join 해보기")
    void join() {
        //given


        //when
        List<Member> result = factory.selectFrom(member)
                .join(member.team, team) //team은 알트엔터로 임포트고고(QTeam.team임)   조인할 상대 테이블은 멤버의 team이고 별칭은 team으로했다.     join말고 leftjoin이라고해도됨.
                .where(team.name.eq("teamA"))
                .fetch();

        //then
        System.out.println("\n\n\n");
        result.forEach(System.out::println); //List의 모든 Member에 대한 것을 println하겠다!
        System.out.println("\n\n\n");
    }







    /**
     * ex) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조회할꺼고, 회원은 모두 조회하겠다 라면?
     * 일단, 일반 문법과 JPQL을써보자
     * SQL: SELECT m.*, t.* FROM tbl_member m LEFT JOIN tbl_team t ON m.team_id = t.id AND t.name = 'teamA'
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'teamA'
     */
    @Test
    @DisplayName("left outer join 테스트") //SQL와 JPQL문법을 봤으니 쿼리DSL로해보자
    void leftJoinTest() {
        //given

        //when
        List<Tuple> result = factory.select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                //where말고 ON써도됨
                .on(team.name.eq("teamA"))
                .fetch();

        //then
        result.forEach(tuple -> System.out.println("tuple = " + tuple));

    }




    //0712
    @Test
    @DisplayName("sub query 사용하기(나이가 가장 많은 회원 조회)")
    void subQueryTest() {
        //given
        QMember memberSub = new QMember("subMember"); //-> 같은 테이블에서 서브쿼리를 적용하려면 별도로 QClass의 객체를 생성해야 한다.
        //when
        List<Member> result = factory.selectFrom(member)
                .where(member.age.eq( //eq괄호안엔 age의 max값이 들어가야 서브쿼리 완성이 된다.
                        JPAExpressions//서브쿼리를 사용할 수 있게 해주는 클래스
                                .select(memberSub.age.max()) //이 select절에 사용할 서브쿼리절은 따로만들어주자. given으로가자
                                .from(memberSub)
                )).fetch();//패치로 줬으니 리스트로오겠지 알트엔터로 지역변수GO
        //then
        System.out.println("\n\n\n");
        result.forEach(System.out::println); //리스트에 있는 내부 객체의 것들을 보자~
        System.out.println("\n\n\n");
    }




//참고로 JPAExperssions는 from절을 제외하고 select절과 where절에서 사용이 가능하다.




    //0712
    @Test
    @DisplayName("나이가 평균 나이 이상인 회원을 조회해보자")
    void subQueryGoe() {
        //given
        QMember m2 = new QMember("m2");
        //when
        List<Member> result = factory.selectFrom(member)
                .where(member.age.goe( //초과는 gt겠지. 적다면 loe겠지. goe 괄호 안에는 서브쿼리사용해야하니 JPAExpreessions쓰자
                        JPAExpressions
                                .select(m2.age.avg())
                                .from(m2)
                )).fetch();

        //then
        assertEquals(result.size(), 5); //5명 조회가 되는게 맞니!
    }



    
    
    //0712
    @Test
    @DisplayName("동적 sql 테스트")
    void dynamicQueryTest() {
        //given
        String name = "member1"; //여기가 "member1"가 아니고 null이고 int age =60 으로 설정이 된다면 ,eq(name)은 null이고 where절에서 널이 나오는거니 검색 대상에서 제외된다고 할 수 있겠다!
        int age = 10;
        //when
        List<Member> result = memberRepository.findUser(name, age);
        //then
        assertEquals(result.size(), 1);

        System.out.println("\n\n\n");
        result.forEach((System.out::println));
        System.out.println("\n\n\n");
    }



    
    
    
    
    








}


