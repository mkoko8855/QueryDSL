package com.example.study.repository;

import com.example.study.entity.Member;

import java.util.List;
//0711
public interface MemberRepositoryCustom {


    List<Member> findByName(String name);

    //0712
    List<Member> findUser(String nameParam, Integer ageParam);






}
