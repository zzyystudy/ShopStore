package com.zxyy.listener;

import com.zxyy.constant.MessageConstant;
import com.zxyy.constant.RabbitMqConstant;
import com.zxyy.pojo.common.DelayMessageProcessor;
import com.zxyy.pojo.common.MultiDelayMessage;
import com.zxyy.pojo.entity.OrderItem;
import com.zxyy.pojo.entity.ShopOrder;
import com.zxyy.pojo.entity.VirtualGoodItem;
import com.zxyy.service.impl.OrderItemService;
import com.zxyy.service.impl.ShopOrderService;
import com.zxyy.service.impl.VirtualGoodItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusCheckListener {

    private final ShopOrderService shopOrderService;
    private final RabbitTemplate rabbitTemplate;
    private final OrderItemService orderItemService;
    private final VirtualGoodItemService virtualGoodItemService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMqConstant.DELAY_ORDER_QUEUE,durable = "true"),
            exchange = @Exchange(value = RabbitMqConstant.DELAY_EXCHANGE,delayed = "true",type = ExchangeTypes.TOPIC),
            key = RabbitMqConstant.DELAY_ORDER_ROUTING_KEY
    ))
    public void listenOrderDelayMessage(MultiDelayMessage<Long> msg){
        //1.查询订单状态
        Long orderId = msg.getData();
        ShopOrder shopOrder = shopOrderService.getById(orderId);
        if(shopOrder == null || shopOrder.getPaymentStatus() == 1){
            //这里判断订单是否存在 是否已经支付了 如果都处理了 TODO这里直接处理了事务回滚但是发消息？？？
            return;
        }
        //未支付 不是真的未支付 TODO 微服务主动查询订单状态？？？
        //2.判断是否已经支付

        //2.1已支付 直接结束

        //2.2未支付 获取下一次订单延迟时间
        if(msg.hasNextDelay()){
            rabbitTemplate.convertAndSend(RabbitMqConstant.DELAY_EXCHANGE,
                    RabbitMqConstant.DELAY_ORDER_ROUTING_KEY, msg, new DelayMessageProcessor(msg.removeNextDelay()));
            return;
        }
        //不存在 取消订单 恢复库存
        shopOrderService.restoreStock(orderId);

        //3.判断是否存在下一次延时时间

        //3.1存在重发延时消息

        //3.2不存在取消订单

        //并且恢复订单库存
    }
}
