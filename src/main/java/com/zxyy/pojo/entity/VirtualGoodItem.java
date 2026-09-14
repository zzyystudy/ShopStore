package com.zxyy.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("virtual_goods_item")
public class VirtualGoodItem implements Serializable {
    private static final long serialVersionUID = 1L;
    //主键id
    @TableId(type = IdType.AUTO)
    private Long id;
    //实际库存编号 全局唯一
    private String inventoryNo;
    //所属的商品id
    private Long productId;
    //对应的预占或已经交付的订单明细id
    private Long orderItemId;
    //Aes加密之后并Base64之后的发货json内容
    private String contentCiphertext;
    //加密版本号 只存版本号 不存密钥
    private String encryptionKeyVersion;
    //Aes加密并Base64之后的nonce 每条记录都不一样
    private String encryptionNonce;
    //内容 Sha256指纹 用于判重每条记录必然不同 查找也用这个
    private String contentFingerprint;
    //状态字段
    private Integer status;
    //支付成功并发货完成的时间
    private LocalDateTime deliveredTime;
    //第一次查看到内容的时间
    private LocalDateTime firstViewedTime;
    //完整内容被查询的次数
    private Integer viewCount;
    //商品作废原因 TODO 这个和使用了没有关系 后续再考虑这个
    private String invalidReason;
    //乐观锁版本号
    @Version
    private Integer version;
    //审计字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;

}
