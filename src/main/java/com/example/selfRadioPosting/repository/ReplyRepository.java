package com.example.selfRadioPosting.repository;

import com.example.selfRadioPosting.Entity.Comment;
import com.example.selfRadioPosting.Entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @Query(value="select * from reply where comment_id=:commentId", nativeQuery = true)
    List<Reply> findAllByCommentId(Long commentId);
}
