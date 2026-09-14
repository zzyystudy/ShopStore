package com.zxyy.pojo.vo;

import lombok.Data;

@Data
public class CategoryVO {
    private Long id;

    //分类名称
    private String name;

    //顺序
    private Integer sort;

    //分类状态 0标识禁用 1表示启用
    private Integer status;
}
