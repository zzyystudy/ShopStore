package com.zxyy.properties;

import com.alipay.api.AlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shop.alipay")
@Data
public class AliPayProperties {
    //appid
    private String APP_ID;
    //应用私钥
    private  String APP_PRIVATE_KEY;
    //字符编码
    private  String CHARSET;
    // 支付宝公钥
    private  String ALIPAY_PUBLIC_KEY;
    //网关地址
    private  String GATEWAY_URL;
    private  String FORMAT;
    //签名方式
    private  String SIGN_TYPE;
    //支付宝异步通知路径，付通款完毕后会异步调用本项目的方法,必须为公网地址
    private  String NOTIFY_URL;
    //支付宝同步通知路径，付就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private  String RETURN_URL;

}
