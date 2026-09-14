package com.zxyy.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class InvalidStocksDTO {
    //库存编号
    private List<String> productNos;
    //状态字段 设置为无效的
    private Integer status;
    //无效的原因
    private String invalidReason;
}
