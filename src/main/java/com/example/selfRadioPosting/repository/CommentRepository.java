package com.example.selfRadioPosting.repository;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value="select * from comment where article_id=:articleId", nativeQuery = true)
    List<Comment> findAllByArticleId(Long articleId);
}
