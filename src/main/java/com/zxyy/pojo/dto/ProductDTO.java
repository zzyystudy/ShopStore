package com.zxyy.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    //主键 自增不传递
    private Long id;
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
    //图片
    private String image;
    //发货方式
    private Integer deliveryType;
    //商品价格
    private BigDecimal price;
    //购买限制
    private Integer purchaseLimit;
}
