package com.example.selfRadioPosting.dto;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.Entity.Comment;
import com.example.selfRadioPosting.Entity.Reply;
import com.example.selfRadioPosting.util.IPManger;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@ToString
@Getter
@Setter
public class ReplyDto {
    private Long id;
    private Long commentId;
    private String content;
    private String writer;
    private String ip;
    private String password;
    private String created;

    public static ReplyDto createDto(Reply reply){
        return new ReplyDto(
                reply.getId(),
                reply.getComment().getId(),
                reply.getContent(),
                reply.getWriter(),
                IPManger.sliceIp(reply.getIp()),
                reply.getPassword(),
                reply.getCreated()
        );
    }

    public Reply createEntity(Comment comment, String ip){
        return new Reply(
                id,
                comment,
                content,
                writer,
                ip,
                password,
                created
        );
    }
}
