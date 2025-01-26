package com.example.selfRadioPosting.repository;

import com.example.selfRadioPosting.Entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(value="select * from article where category=:category", nativeQuery = true)
    List<Article> findAllByCategory(String category);

    @Modifying
    @Query(value="update article set views=views+1 where id=:id", nativeQuery=true)
    void updateView(Long id);

    @Modifying
    @Query(value="update article set recommend=recommend+1 where id=:id", nativeQuery = true)
    void recommend(Long id);

    @Modifying
    @Query(value="update article set content=:content, audio_url=:audioUrl, video_url=:videoUrl where id=:id", nativeQuery = true)
    void update(Long id, String content, String audioUrl, String videoUrl);

    Page<Article> findByCategory(String category, Pageable pageable);

    @Query(value="select * from article " +
            "where title like %:keyword% or content like %:keyword% or writer like %:keyword%", nativeQuery = true)
    Page<Article> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}