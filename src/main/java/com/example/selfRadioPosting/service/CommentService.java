package com.example.selfRadioPosting.service;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.Entity.Comment;
import com.example.selfRadioPosting.dto.CommentDto;
import com.example.selfRadioPosting.repository.ArticleRepository;
import com.example.selfRadioPosting.repository.CommentRepository;
import com.example.selfRadioPosting.util.IPManger;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    @Autowired
    CommentService(CommentRepository commentRepository,
                   ArticleRepository articleRepository){
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
    }

    public List<CommentDto> findAllComments(Long articleId){
        return commentRepository.findAllByArticleId(articleId).stream().map(CommentDto::createDto).toList();
    }

    public Comment post(Long articleId, CommentDto commentDto, HttpServletRequest request)
            throws UnknownHostException
    {
        String ip = IPManger.getClientIp(request);
        Article article = articleRepository.findById(articleId).orElseThrow(IllegalArgumentException::new);
        Comment target = commentDto.createEntity(article, ip);
        return commentRepository.save(target);
    }

    public Comment delete(Long commentId) {
        Comment target = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        commentRepository.deleteById(commentId);
        return target;
    }
}
