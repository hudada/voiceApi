package com.example.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bean.SongBean;

public interface SongDao extends JpaRepository<SongBean, Long> {

    @Query("from SongBean b where b.uid=:uid")
    List<SongBean> findByUid(@Param("uid") Long uid);
    
    @Query("from SongBean b where b.type=:type order by b.likeSum desc")
    List<SongBean> findByType(@Param("type") int type);
    
    @Query("from SongBean b where b.name like %:key%")
    List<SongBean> findByKey(@Param("key") String key);
}
