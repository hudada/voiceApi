package com.example.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bean.LikeBean;
import com.example.bean.SongBean;
import com.example.bean.UserBean;

public interface LikeDao extends JpaRepository<LikeBean, Long> {

	@Query("from LikeBean b where b.uid=:uid and b.sid=:sid")
	LikeBean findByUid(@Param("uid") Long uid,
    		@Param("sid") Long sid);
	
	@Query("from LikeBean b where b.uid=:uid")
	List<LikeBean> findByUidAndLikeUid(@Param("uid") Long uid);
	
}
