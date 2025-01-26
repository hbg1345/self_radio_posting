package com.example.selfRadioPosting.dto;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.Entity.Comment;
import com.example.selfRadioPosting.util.IPManger;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@ToString
@Getter
@Setter
public class CommentDto {
    private Long id;
    private Long articleId;
    private String content;
    private String writer;
    private String ip;
    private String password;
    private String created;
    private List<ReplyDto> replyDtos;

    public static CommentDto createDto(Comment comment){
        return new CommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getContent(),
                comment.getWriter(),
                IPManger.sliceIp(comment.getIp()),
                comment.getPassword(),
                comment.getCreated(),
                null
        );
    }

    public Comment createEntity(Article article, String ip){
        return new Comment(
                id,
                article,
                content,
                writer,
                ip,
                password,
                created
        );
    }
}
