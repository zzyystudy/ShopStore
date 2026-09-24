package com.zxyy.controller.notify;

import com.alibaba.fastjson.JSONObject;
import com.zxyy.constant.RabbitMqConstant;
import com.zxyy.result.Result;
import com.zxyy.service.impl.ShopOrderService;
import com.zxyy.util.PayUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/guest/notify")
@Tag(name = "回调接口")
@Slf4j
public class PayNotifyController {
    @Autowired
    private PayUtil payUtil;
    @Autowired
    private ShopOrderService shopOrderService;

    /**
     * 支付宝支付回调接口
     * @param out_trade_no 我们调用支付接口传入的第一个值 订单编号
     * @return
     */
    @PostMapping
    @Operation(summary = "支付宝回调接口")
    public Result<String> returns(String out_trade_no) {
        //查询支付包支付状态
        String query = payUtil.query(out_trade_no);
        log.info("支付回调id:{} 结果:{}", out_trade_no, query);

        //反序列化 支付宝返回的结果
        JSONObject jsonObject = (JSONObject) JSONObject.parse(query);
        Object o = jsonObject.get("alipay_trade_query_response");
        Map map = (Map) o;
        Object status = map.get("trade_status");
        if (status.equals("TRADE_SUCCESS")) {
            //支付成功做的事 TODO 发货 发消息 改订单和库存状态(乐观锁) 乐观锁条件！！！！！！！！！！！！！！！！！
            //1.修改订单和库存状态 同步操作
            shopOrderService.paySuccess(out_trade_no);
            return  Result.success("redirect:http://localhost:5173/user/cart");
        }else {
            //支付失败 不需要管 还能继续拉起支付 订单超时mq消息处理
            return Result.success("index");
        }
    }

}
