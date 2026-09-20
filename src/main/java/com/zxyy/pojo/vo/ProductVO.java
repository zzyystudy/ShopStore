package com.zxyy.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {
    //对外展示业务编号
    private String productNo;
    //商品分类id
    private Long categoryId;
    //商品名称
    private String name;
    //副标题 简短卖点
    private String subtitle;
    //描述信息
    private String description;
    //状态
    private Integer status;
    //图片
    private String image;
    //商品价格
    private BigDecimal price;
    //可用库存
    private Integer availableStock;
    //购买限制
    private Integer purchaseLimit;
}
