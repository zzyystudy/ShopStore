package com.zxyy.config;

import com.zxyy.properties.AliOssProperties;
import com.zxyy.util.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 * 这个类就是为了给我们的阿里云oss
 * 工具类初始化基本的属性，方便调用
 */


@Configuration
@Slf4j
public class OssConfiguration {
    //在配置类中通过方法参数注入将配置类注入进来
    //单独写这个方法 不会被执行到 所以我们要定义一个Bean注解
    //ConditionalOnMissingBean 表示这个方法返回的对象，如果容器中已经存在这个对象，则不执行这个方法
    //就是只创建一个这个工具类对象

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
