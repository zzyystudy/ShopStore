package com.zxyy.config;

import com.zxyy.properties.AliPayProperties;
import com.zxyy.util.PayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AlipayConfig {
    @Bean
    @ConditionalOnMissingBean
    public PayUtil payUtil(AliPayProperties aliPayProperties){
        log.info("创建ailipay工具类对象:{}",aliPayProperties);
        return new PayUtil(aliPayProperties.getAPP_ID(),
                aliPayProperties.getAPP_PRIVATE_KEY(),
                aliPayProperties.getCHARSET(),
                aliPayProperties.getALIPAY_PUBLIC_KEY(),
                aliPayProperties.getGATEWAY_URL(),
                aliPayProperties.getFORMAT(),
                aliPayProperties.getSIGN_TYPE(),
                aliPayProperties.getNOTIFY_URL(),
                aliPayProperties.getRETURN_URL(),
                null);
    }
}
