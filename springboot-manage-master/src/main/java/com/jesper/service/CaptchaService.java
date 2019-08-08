package com.jesper.service;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class CaptchaService {

    /**
     *  生成验证码样式的配置
     * @return  返回一个图像验证码生产者对象
     */
    @Bean
    public DefaultKaptcha captchaProducer(){
        DefaultKaptcha captchaProducer = new DefaultKaptcha();
        Properties properties  = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.goodsImage.width", "125");
        properties.setProperty("kaptcha.goodsImage.height", "45");
        properties.setProperty("kaptcha.textproducer.font.size", "45");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        captchaProducer.setConfig(config);
        return  captchaProducer;
    }
}
