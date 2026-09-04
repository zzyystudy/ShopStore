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
@TableName("shop_order")
public class ShopOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id 不对外
    private Long id;
    //业务编号 对外展示
    private String orderNo;
    //客户端下单幂等号 防止重复下单
    private String clientRequestNo;
    //Aes-Gcm加密之后Base64后的预留邮箱
    private String buyerEmailCiphertext;
    //邮箱加密使用的随机nonce的base64
    private String buyerEmailNonce;
    //邮箱加密版本号 只存版本号
    private String buyerEmailKeyVersion;
    //规范化后邮箱的 Hmac-Sha256指纹 用于精确比较
    private String buyerEmailFingerprint;
    //安全查询密码的bcrypt
    private String queryPasswordHash;
    //订单状态
    private Integer orderStatus;
    //支付状态
    private Integer paymentStatus;
    //发货状态
    private Integer fulfillmentStatus;
    //商品原始总金额
    private BigDecimal goodsAmount;
    //优惠总金额
    private BigDecimal discountAmount;
    //应付金额
    private BigDecimal payableAmount;
    //实际付款金额
    private BigDecimal paidAmount;
    //累计退款金额
    private BigDecimal refundAmount;
    //订单购买总件数
    private Long itemQuantity;
    //买家备注
    private String buyerRemark;
    //订单关闭取消原因
    private String closeReason;
    //订单过期事件
    private LocalDateTime expireTime;
    //订单支付时间
    private LocalDateTime paidTime;
    //订单完成时间
    private LocalDateTime completedTime;
    //订单取消时间
    private LocalDateTime cancelledTime;
    //订单关闭时间
    private LocalDateTime closedTime;
    //乐观锁版本号
    private Integer version;
    //审计字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;

}
