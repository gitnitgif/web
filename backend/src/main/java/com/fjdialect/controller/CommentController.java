package com.fjdialect.controller;

import com.fjdialect.entity.Comment;
import com.fjdialect.service.CommentService;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public Result<List<Comment>> getComments(
        @RequestParam String resourceType,
        @RequestParam Long resourceId
    ) {
        List<Comment> comments = commentService.getCommentsByResource(resourceType, resourceId);
        return Result.success("查询成功", comments);
    }

    @GetMapping("/{id}")
    public Result<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment != null) {
            return Result.success("查询成功", comment);
        }
        return Result.error(404, "评论不存在");
    }

    @PostMapping("/add")
    public Result<String> addComment(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return Result.success("评论成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success("删除成功");
    }

    @PostMapping("/like/{id}")
    public Result<String> likeComment(@PathVariable Long id) {
        commentService.likeComment(id);
        return Result.success("点赞成功");
    }
}
