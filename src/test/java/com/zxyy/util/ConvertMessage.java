package com.zxyy.util;

import com.zxyy.constant.RabbitMqConstant;
import com.zxyy.pojo.common.DelayMessageProcessor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConvertMessage {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void convertMessage(){
        String orderNo = "01M38JRRQP939XYATSF5MM6Z77";
        rabbitTemplate.convertAndSend(RabbitMqConstant.DELIVERY_EXCHANGE,
                RabbitMqConstant.DELIVERY_ROUTING_KEY,orderNo);
    }
}
