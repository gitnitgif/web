package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("city_resource")
public class CityResource {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String code;
    
    private String name;
    
    private String dialect;
    
    private String resource;
    
    private String description;
    
    private String audioUrl;
    
    private String downloadUrl;
    
    private String bgImage;
    
    private Integer enabled;
}
