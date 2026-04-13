package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjdialect.entity.Comment;
import com.fjdialect.mapper.CommentMapper;
import com.fjdialect.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> getCommentsByResource(String resourceType, Long resourceId) {
        return commentMapper.selectByResourceId(resourceType, resourceId);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentMapper.selectByUserId(userId);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public void addComment(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setLikeCount(0);
        commentMapper.insert(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    public void likeComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentMapper.updateById(comment);
        }
    }
}
