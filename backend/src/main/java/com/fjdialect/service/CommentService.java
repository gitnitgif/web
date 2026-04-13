package com.fjdialect.service;

import com.fjdialect.entity.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByResource(String resourceType, Long resourceId);
    List<Comment> getCommentsByUserId(Long userId);
    Comment getCommentById(Long id);
    void addComment(Comment comment);
    void deleteComment(Long id);
    void likeComment(Long id);
}
