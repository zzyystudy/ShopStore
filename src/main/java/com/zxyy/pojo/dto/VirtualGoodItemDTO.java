package com.zxyy.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class VirtualGoodItemDTO {
    //所属的商品id
    private Long productId;

    //批量新增的内容
    private List<String> goods;

}
