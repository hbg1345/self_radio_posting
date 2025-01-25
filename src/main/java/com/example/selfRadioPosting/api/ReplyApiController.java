package com.example.selfRadioPosting.api;

import com.example.selfRadioPosting.dto.ReplyDto;
import com.example.selfRadioPosting.service.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

@Slf4j
@RestController
public class ReplyApiController {
    ReplyService replyService;
    @Autowired
    ReplyApiController(ReplyService replyService){
        this.replyService = replyService;
    }

    @Transactional
    @PostMapping("/api/comments/{commentId}/replies")
    public ResponseEntity<ReplyDto> post(@PathVariable Long commentId,
                                         @RequestBody ReplyDto replyDto,
                                         HttpServletRequest request) throws UnknownHostException {
        ReplyDto ret = replyService.post(commentId, replyDto, request);
        return ResponseEntity.ok(ret);
    }

    @Transactional
    @DeleteMapping("/api/replies/{replyId}")
    public ResponseEntity<ReplyDto> delete(@PathVariable Long replyId){
        ReplyDto deleted = replyService.delete(replyId);
        return ResponseEntity.ok(deleted);
    }
}
