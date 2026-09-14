package com.zxyy.pojo.dto;

import lombok.Data;

@Data
public class InvalidStockDTO {
    //库存编号
    private String inventoryNo;
    //状态字段 设置为无效的
    private Integer status;
    //无效的原因
    private String invalidReason;
}
