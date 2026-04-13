package com.fjdialect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fjdialect.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    List<Comment> selectByResourceId(
        @Param("resourceType") String resourceType,
        @Param("resourceId") Long resourceId
    );
    
    List<Comment> selectByUserId(Long userId);
    
    List<Comment> selectByParentId(Long parentId);
}
