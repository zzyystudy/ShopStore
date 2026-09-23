package com.zxyy.constant;

public class RabbitMqConstant {
    //处理过期订单
    public static final String DELAY_EXCHANGE = "trade.delay.topic";
    public static final String DELAY_ORDER_QUEUE = "trade.order.delay.queue";
    public static final String DELAY_ORDER_ROUTING_KEY = "order.query";

    //发货
    public static final String DELIVERY_EXCHANGE = "trade.delivery.topic";
    public static final String DELIVERY_ORDER_QUEUE = "trade.delivery.queue";
    public static final String DELIVERY_ROUTING_KEY = "delivery.item";
}
