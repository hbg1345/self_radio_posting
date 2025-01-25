package com.example.selfRadioPosting.service;

import com.example.selfRadioPosting.Entity.Comment;
import com.example.selfRadioPosting.Entity.Reply;
import com.example.selfRadioPosting.dto.ReplyDto;
import com.example.selfRadioPosting.repository.CommentRepository;
import com.example.selfRadioPosting.repository.ReplyRepository;
import com.example.selfRadioPosting.util.IPManger;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.List;

@Service
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    @Autowired
    ReplyService(ReplyRepository replyRepository, CommentRepository commentRepository){
        this.replyRepository = replyRepository;
        this.commentRepository = commentRepository;
    }
    public List<ReplyDto> findAllByCommentId(Long id) {
        return replyRepository.findAllByCommentId(id).stream().map(ReplyDto::createDto).toList();
    }

    public ReplyDto post(Long commentId, ReplyDto replyDto, HttpServletRequest request)
            throws UnknownHostException {
        String ip = IPManger.getClientIp(request);
        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        Reply reply = replyDto.createEntity(comment, ip);
        Reply saved = replyRepository.save(reply);
        return ReplyDto.createDto(saved);
    }

    public ReplyDto delete(Long replyId) {
        Reply target = replyRepository.findById(replyId).orElseThrow(IllegalArgumentException::new);
        replyRepository.deleteById(replyId);
        return ReplyDto.createDto(target);
    }
}
