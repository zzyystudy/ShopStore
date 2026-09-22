package com.zxyy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.constant.MessageConstant;
import com.zxyy.constant.RabbitMqConstant;
import com.zxyy.exception.OrderException;
import com.zxyy.mapper.OrderItemMapper;
import com.zxyy.mapper.ProductMapper;
import com.zxyy.mapper.ShopOrderMapper;
import com.zxyy.mapper.VirtualGoodItemMapper;
import com.zxyy.pojo.common.DelayMessageProcessor;
import com.zxyy.pojo.common.MultiDelayMessage;
import com.zxyy.pojo.dto.BuyProductDTO;
import com.zxyy.pojo.dto.OrderSubmitDTO;
import com.zxyy.pojo.entity.OrderItem;
import com.zxyy.pojo.entity.Product;
import com.zxyy.pojo.entity.ShopOrder;
import com.zxyy.pojo.entity.VirtualGoodItem;
import com.zxyy.pojo.vo.OrderSubmitVO;
import com.zxyy.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShopOrderService extends ServiceImpl<ShopOrderMapper, ShopOrder> {
    @Autowired
    private AESGCMUtil aesgcmUtil;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private VirtualGoodItemMapper virtualGoodItemMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PayUtil payUtil;
    /**
     * 创建订单
     *
     * @param orderSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {
        String clientRequestNo = orderSubmitDTO.getClientRequestNo();
        String email = orderSubmitDTO.getEmail();
        String queryPassword = orderSubmitDTO.getQueryPassword();
        String buyerRemark = orderSubmitDTO.getBuyerRemark();
        List<BuyProductDTO> items = orderSubmitDTO.getItems();
        StringBuilder payload = new StringBuilder(email + "|" + queryPassword + "|" +buyerRemark +"|");
        for(BuyProductDTO buyProductDTO : items){
            payload.append(buyProductDTO.getProductNo()).append("|").append(buyProductDTO.getQuantity()).append("|");
        }
        //计算请求指纹
        String requestFingerprint = HmacSha256Util.sign(payload.toString());
        //校验全局唯一的下单幂等号 防止重复下单
        ShopOrder shopOrder = query().eq("client_request_no", clientRequestNo).one();
        if(shopOrder != null){
            //如果重复下单 校验订单指纹
            if(!shopOrder.getRequestFingerprint().equals(requestFingerprint)){
                throw new OrderException(MessageConstant.IDEMPOTENCY_CHECK_FAILED);
            }
            //订单指纹一致 直接返回原订单
            return BeanUtil.copyProperties(shopOrder, OrderSubmitVO.class);
        }
        //规范化邮箱计算邮箱的HMAC指纹并加密 有相同的直接返回已存在的订单
        //1.将邮箱中的字母全部转为小写
        String lowEmail = email.toLowerCase();
        //2.对邮箱进行加密 并计算指纹
        AESGCMUtil.EncryptedPayload encryptedPayload = aesgcmUtil.encrypt(lowEmail);
        String buyerEmailFingerprint = HmacSha256Util.sign(lowEmail);
        //查询商品是否上架 是否逻辑删除 以及购买数量是否满足限购要求
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .in(Product::getProductNo, items.stream().map(BuyProductDTO::getProductNo).toList())
                .eq(Product::getStatus, 1);
        List<Product> products = productMapper.selectList(wrapper);
        if(products.size() != items.size()){
            throw new OrderException(MessageConstant.PRODUCT_NOT_SOLD);
        }
        Map<String, Product> collect = products.stream()
                .collect(Collectors.toMap(Product::getProductNo, p -> p));
        //使用数据库中的商品价格计算订单金额
        BigDecimal goodsAmount = new BigDecimal(0);
        int itemQuantity = 0;
        for(BuyProductDTO buyProductDTO : items){
            BigDecimal price = collect.get(buyProductDTO.getProductNo()).getPrice();
            BigDecimal quantity = BigDecimal.valueOf(buyProductDTO.getQuantity());
            goodsAmount = goodsAmount.add(price.multiply(quantity));
            itemQuantity += buyProductDTO.getQuantity();
        }
        //2.创建订单主表
        ShopOrder newShopOrder = new ShopOrder();
        newShopOrder.setOrderNo(ULIDUtil.generateULID());
        newShopOrder.setClientRequestNo(clientRequestNo);
        newShopOrder.setRequestFingerprint(requestFingerprint);
        newShopOrder.setBuyerEmailCiphertext(encryptedPayload.ciphertext());
        newShopOrder.setBuyerEmailNonce(encryptedPayload.nonce());
        newShopOrder.setBuyerEmailKeyVersion("2026"); //TODO 固定密钥
        newShopOrder.setBuyerEmailFingerprint(buyerEmailFingerprint);
        newShopOrder.setQueryPasswordHash(PasswordUtil.encrypt(queryPassword));
        newShopOrder.setGoodsAmount(goodsAmount);
        newShopOrder.setPayableAmount(goodsAmount);
        newShopOrder.setItemQuantity(itemQuantity);
        newShopOrder.setBuyerRemark(buyerRemark);
        save(newShopOrder);
        //创建完成订单主表后立即发送消息到消息队列

        //创建订单明细表
        int i = 0;
        //存放回显的订单明细id
        List<Long> orderItemIds = new ArrayList<>();
        //创建订单明细表
        for(BuyProductDTO buyProductDTO : items){
            String productNo = buyProductDTO.getProductNo();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(newShopOrder.getId());
            orderItem.setLineNo(i++);
            orderItem.setProductId(collect.get(productNo).getId());
            orderItem.setProductNoSnapshot(productNo);
            orderItem.setProductName(collect.get(productNo).getName());
            orderItem.setProductImageUrl(collect.get(productNo).getImage());
            orderItem.setDeliveryType(collect.get(productNo).getDeliveryType());
            orderItem.setUnitPrice(collect.get(productNo).getPrice());
            orderItem.setQuantity(buyProductDTO.getQuantity());
            orderItem.setGoodsAmount(collect.get(productNo).getPrice().multiply(BigDecimal.valueOf(buyProductDTO.getQuantity())));
            orderItem.setPayableAmount(orderItem.getGoodsAmount()); //TODO 真正的优惠 设置 优惠引擎？？
            orderItemMapper.insert(orderItem);
            orderItemIds.add(orderItem.getId());
        }
        List<List<VirtualGoodItem>> everyProductInventory = new ArrayList<>();
        //对每张订单明细表使用 for update skip locked 选出足量的可用库存
        for(BuyProductDTO buyProductDTO : items){
            Long productId = collect.get(buyProductDTO.getProductNo()).getId();
            List<VirtualGoodItem> virtualGoodItems = new ArrayList<>();
            virtualGoodItems = virtualGoodItemMapper.selectAvailableForUpdate(productId,buyProductDTO.getQuantity());
            //如果数量小于购买数量抛出异常
            if(virtualGoodItems==null || virtualGoodItems.size()<buyProductDTO.getQuantity()){
                throw new OrderException(MessageConstant.OUT_OF_STOCK);
            }
            everyProductInventory.add(virtualGoodItems);
        }
        //更新库存状态 更改status 并且更改order_item_id
        for(List<VirtualGoodItem> virtualGoodItemList : everyProductInventory){
            int k = 0;
            for (VirtualGoodItem virtualGoodItem : virtualGoodItemList){
                virtualGoodItem.setStatus(1);//TODO 常量定义
                virtualGoodItem.setOrderItemId(orderItemIds.get(k++));
                virtualGoodItemMapper.updateById(virtualGoodItem);
            }
        }
        //原子减少商品库存 不增加soldCount
        for(BuyProductDTO buyProductDTO : items){
            Product product = collect.get(buyProductDTO.getProductNo());
            product.setAvailableStock(product.getAvailableStock()-buyProductDTO.getQuantity());
            //原子性扣减库存 乐观锁
            productMapper.updateById(product);
        }
        //发送mq消息 TODO 消息事务 outbox

        try{
            MultiDelayMessage<Long> msg = new MultiDelayMessage<>(newShopOrder.getId());
            rabbitTemplate.convertAndSend(RabbitMqConstant.DELAY_EXCHANGE,
                    RabbitMqConstant.DELAY_ORDER_ROUTING_KEY, msg, new DelayMessageProcessor(msg.removeNextDelay()));
            log.info("延时消息发送成功:{}",msg);
        }catch (Exception e2){
            log.error("延时消息发送异常",e2);
        }
        return BeanUtil.copyProperties(newShopOrder,OrderSubmitVO.class);
    }

    /**
     * 调用支付宝支付接口
     * @param orderNo
     * @return
     */
    public String pay(String orderNo) throws AlipayApiException {
        //查询金额 发送请求
        ShopOrder shopOrder = lambdaQuery()
                .eq(ShopOrder::getOrderNo, orderNo)
                .one();
        //判断订单状态 待支付才可以进行支付 TODO springSecurity 统一处理？？
        if(shopOrder.getOrderStatus() != 10){
            throw new OrderException("订单正在处理");
        }
        return payUtil.sendRequestToAlipay(orderNo, shopOrder.getPayableAmount(), "用户支付");
    }

    /**
     * 订单超时恢复库存
     * @param orderId
     */
    @Transactional
    public void restoreStock(Long orderId) {
        lambdaUpdate()
                .set(ShopOrder::getOrderStatus,40)
                .set(ShopOrder::getClosedTime, LocalDateTime.now())
                .set(ShopOrder::getCloseReason, MessageConstant.ORDER_OUTOFTIME)
                .eq(ShopOrder::getId,orderId)
                .update();

        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        List<Long> orderItemIds= orderItemList.stream().map(OrderItem::getId).toList();
        virtualGoodItemMapper.update(new LambdaUpdateWrapper<VirtualGoodItem>()
                .in(VirtualGoodItem::getOrderItemId,orderItemIds)
                .set(VirtualGoodItem::getStatus,0));

        //product增加可用库存
        for(OrderItem orderItem :orderItemList){
            Product product = productMapper.selectOne(new LambdaQueryWrapper<Product>().eq(Product::getId, orderItem.getProductId()));
            product.setAvailableStock(product.getAvailableStock() + orderItem.getQuantity());
            productMapper.updateById(product);
        }
    }
}
