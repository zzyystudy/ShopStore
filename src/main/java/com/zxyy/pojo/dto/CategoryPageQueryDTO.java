package com.zxyy.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CategoryPageQueryDTO implements Serializable {
    private int page;

    private int pageSize;

    private String name;
    private Integer isDeleted;
    //状态 0表示禁用 1表示启用
    private Integer status;
    //排序字段 按哪个时间 还是价格排序
    private String sortField;
    //升序还是降序
    private boolean isAsc;
    //开始时间
    private LocalDateTime startTime;
    //结束时间
    private LocalDateTime endTime;
}
