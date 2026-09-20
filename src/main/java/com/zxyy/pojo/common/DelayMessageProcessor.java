package com.zxyy.pojo.common;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

@RequiredArgsConstructor
public class DelayMessageProcessor implements MessagePostProcessor {

    private final Long delay;

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        message.getMessageProperties().setDelayLong(delay);
        return message;
    }
}
