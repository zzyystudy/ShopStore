package com.zxyy.listener;

import com.zxyy.constant.RabbitMqConstant;
import com.zxyy.pojo.entity.ShopOrder;
import com.zxyy.pojo.vo.ShopVirtualGoodVO;
import com.zxyy.pojo.vo.VirtualGoodsVO;
import com.zxyy.service.impl.ShopOrderService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryItemListener {
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Configuration freemarkerConfig;

    private final String subject = "雨云商城";
    private final String from = "zhaozhiyu6@163.com";


    /**
     * 异步发货消息监听器
     * @param orderNo 订单号 根据订单号来发货
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMqConstant.DELIVERY_ORDER_QUEUE,durable = "true"),
            exchange = @Exchange(value = RabbitMqConstant.DELIVERY_EXCHANGE,delayed = "true",type = ExchangeTypes.TOPIC),
            key = RabbitMqConstant.DELAY_ORDER_ROUTING_KEY
    ))
    public void listenDeliveryMessage(String orderNo){
        //邮箱发货 发货成功修改库存状态
        //根据订单号进行查询 解密封装
        ShopVirtualGoodVO shopVirtualGoodVO = shopOrderService.listVirtualItemByOrderNo(orderNo);
        try {
            sendHtmlEmail(from,subject,shopVirtualGoodVO);
            //TODO 重试？

        } catch (MessagingException | IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
        //发货成功修改 数据库状态
        shopOrderService.completeOrderByNo(orderNo);
    }


    public void sendHtmlEmail(String from, String subject,ShopVirtualGoodVO shopVirtualGoodVO) throws MessagingException, IOException, TemplateException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(from);
        helper.setTo(shopVirtualGoodVO.getEmail());
        helper.setSubject(subject);

        //加载freemarker 模板并且渲染
        Template template = freemarkerConfig.getTemplate("deliveryEmail.ftl");
        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template,shopVirtualGoodVO);

        helper.setText(htmlContent, true); // 第二个参数 true 表示 HTML
        mailSender.send(message);
    }
}
