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
    public List<Comment> getCommentsByResource(String resourceType, Long resourceId) {      //根据类型+id检索
        return commentMapper.selectByResourceId(resourceType, resourceId);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {         //根据用户id检索
        return commentMapper.selectByUserId(userId);
    }

    @Override
    public Comment getCommentById(Long id) {            //根据评论id检索
        return commentMapper.selectById(id);
    }

    @Override
    public void addComment(Comment comment) {       //新增评论
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setLikeCount(0);
        commentMapper.insert(comment);
    }

    @Override
    public void deleteComment(Long id) {        //删除评论
        commentMapper.deleteById(id);
    }

    @Override
    public void likeComment(Long id) {          //累计点赞数
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentMapper.updateById(comment);
        }
    }
}
