package com.example.selfRadioPosting.api;

import com.example.selfRadioPosting.Entity.Comment;
import com.example.selfRadioPosting.dto.CommentDto;
import com.example.selfRadioPosting.service.CommentService;
import com.example.selfRadioPosting.util.IPManger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

@Slf4j
@RestController
public class CommentApiController {
    private final CommentService commentService;
    @Autowired
    CommentApiController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<CommentDto> postComment(@PathVariable Long articleId,
                                                  @RequestBody CommentDto commentDto,
                                                  HttpServletRequest request) throws UnknownHostException {
        Comment posted = commentService.post(articleId, commentDto, request);
        log.info(posted.toString());
        return ResponseEntity.ok(CommentDto.createDto(posted));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable Long commentId){
        Comment deleted = commentService.delete(commentId);
        log.info(deleted.toString());
        return ResponseEntity.ok(CommentDto.createDto(deleted));
    }
}
