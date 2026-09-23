package com.zxyy.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class ShopVirtualGoodVO {
    private String shopOrderNo;
    private String email;
    private List<VirtualGoodsVO> virtualGoodsVOList;
}
