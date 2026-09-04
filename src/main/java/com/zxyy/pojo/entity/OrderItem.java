package com.zxyy.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
@TableName("order_item")
public class OrderItem implements Serializable {

    //序列化版本号
    private static final long serialVersionUID = 1L;

    //主键id
    private Long id;
    //关联的订单id TODO 这里关联的订单id是主键id还是展示id 为什么
    private Long orderId;
    //单个商品的行号 从一开始
    private Integer lineNo;
    //对应的商品id
    private Long productId;
    //下单时对应的商品编号快照
    private String productNoSnapshot;
    //商品名称
    private String productName;
    //商品图片url
    private String productImageUrl;
    //下单时发货方式快照
    private Integer deliveryType;
    //下单时 单品价值 人民币
    private BigDecimal unitPrice;
    //单个商品购买数量
    private Integer quantity;
    //原始总金额
    private BigDecimal goodsAmount;
    //分摊到此商品的优惠金额明细
    private BigDecimal discountAmount;
    //明细应付金额
    private BigDecimal payableAmount;
    //明细发货状态
    private Integer fulfillStatus;
    //已经成功交付的数量
    private Integer deliveredQuantity;
    //已经退款的数量
    private Integer refundedQuantity;
    //该明细全部发货完成的时间
    private LocalDateTime deliveredTime;
    //乐观锁版本号
    private Integer version;
    //审计字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
