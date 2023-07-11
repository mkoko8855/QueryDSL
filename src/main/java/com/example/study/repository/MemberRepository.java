package com.example.study.repository;

import com.example.study.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


//0710
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

}