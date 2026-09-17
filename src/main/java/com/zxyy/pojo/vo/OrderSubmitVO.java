package com.zxyy.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderSubmitVO {
    // ===== 必须 =====
    /**
     * 业务订单号，支付/查询/取消的唯一凭证
     */
    private String orderNo;
    /**
     * 订单状态，新建恒为 10 待支付
     */
    private Integer orderStatus;
    /**
     * 后端重算的应付金额，前端展示以此为准
     */
    private BigDecimal payableAmount;
    /**
     * 支付截止时间（order.expire_time，建议创建 +15 分钟）
     */
    private LocalDateTime expireTime;
    /**
     * 支付宝收银台地址
     * 为了订单和支付的解耦 这里只返回订单的数据 支付需要另外的接口 来实现
     */
    //private String payUrl;
}
