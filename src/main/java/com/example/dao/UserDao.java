package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bean.SongBean;
import com.example.bean.UserBean;

public interface UserDao extends JpaRepository<UserBean, Long> {

    @Query("from UserBean b where b.userName=:userName")
    UserBean findUserByUserName(@Param("userName") String userName);
    
    @Query("from UserBean b where b.userName=:userName and b.pwd=:pwd")
    UserBean findUserByUserNameAndPwd(@Param("userName") String userName,
    		@Param("pwd") String pwd);
}
