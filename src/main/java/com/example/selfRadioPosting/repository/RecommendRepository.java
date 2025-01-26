package com.example.selfRadioPosting.repository;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.Entity.Recommend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    @Query(value="select distinct * from recommend "+
            "where article_id=:article_id and ip=:ip "+
            "order by recommend_date desc", nativeQuery = true)
    List<Recommend> findAllByIdAndIp(@Param("article_id") Long articleId,
                                     @Param("ip") String ip);

    @Modifying
    @Query(value="update recommend "+
            "set recommend_date=:date where id=:id",
            nativeQuery = true)
    void update(@Param("id") Long id, @Param("date") String date);
}