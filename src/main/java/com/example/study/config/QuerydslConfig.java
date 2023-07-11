package com.example.study.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//0711



//이 클래스를 만든 이유는 Querydsl 문법을 사용하기 위한 필수 객체인 JPA쿼리팩토리의 빈 등록을 위한 클래스이다.
//그리고, 나중에 여러개의 레파지토리에서 쿼리DSL을 사용하기 위한 빈 등록이다.
@Configuration
public class QuerydslConfig {

    //여기다가 빈 등록하면된다. JPA쿼리팩토리빈을. JPA라이브러리했으면 등록할수있음
    //쿼리팩토리는 엔터티매니저필요하다했지?
    @PersistenceContext //JPA 라이브러리를 추가 했으면 객체 주입 가능
    private EntityManager entityManager;

    //이제 빈으로 등록하자. 우리가 테스트에서는 직접 작성했지만, 컨트롤라와 서비스를 통해 코드작성할꺼면 이렇게작성하면된다.
    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(entityManager);
    }


}
