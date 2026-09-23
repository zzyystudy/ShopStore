package com.zxyy.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VirtualGoodsVO {
    //所属的商品id
    private String productName;
    //发货内容
    private String context;
}
