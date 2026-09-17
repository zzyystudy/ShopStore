package com.zxyy.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderSubmitDTO {
    //客户下单幂等号 防止重复下单
    private String clientRequestNo;
    //用户备注
    private String buyerRemark;
    //用户邮箱
    private String email;
    //用户订单查询密码
    private String queryPassword;
    //选择下单的商品
    List<BuyProductDTO> items;
}
