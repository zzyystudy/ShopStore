package com.zxyy.controller.guest;

import com.zxyy.pojo.dto.OrderSubmitDTO;
import com.zxyy.pojo.vo.OrderSubmitVO;
import com.zxyy.result.Result;
import com.zxyy.service.impl.ShopOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/guest/shopOrder")
@Tag(name = "订单接口")
@Slf4j
public class ShopOrderController {
    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping
    @Operation(summary = "用户付款接口")
    public Result<String> alipay(String orderNo){
        log.info("支付订单:{}",orderNo);
        String content = shopOrderService.pay(orderNo);
        return Result.success(content);
    }



    @PostMapping("/submit")
    @Operation(summary = "用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO){
        log.info("用户下单:{}",orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = shopOrderService.submitOrder(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }
}
