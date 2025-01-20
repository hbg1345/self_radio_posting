package com.example.selfRadioPosting.repository;

import com.example.selfRadioPosting.Entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(value="select * from article where category=:category", nativeQuery = true)
    List<Article> findAllByCategory(String category);

    @Modifying
    @Query(value="update article set views=views+1 where id=:id", nativeQuery=true)
    void updateView(Long id);

    @Modifying
    @Query(value="update article set recommend=recommend+1 where id=:id", nativeQuery = true)
    void recommend(Long id);
}