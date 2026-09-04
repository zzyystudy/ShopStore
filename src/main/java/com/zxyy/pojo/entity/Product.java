package com.zxyy.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    //自增主键
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
    //库存
    private Long AvailableStock;
    //已售
    private Long soldCount;
    //purchase limit 交易限购 0表示不限购
    private Integer purchaseLimit;
    //状态
    private Integer status;
    //乐观锁版本号
    private Integer version;
    //是否删除字段
    private Integer is_deleted;
    //审计字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;

}