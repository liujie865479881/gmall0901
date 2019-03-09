package com.atguigu.gmall0901.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//使当前类RedisConfig变成配置文件  xxx.xml
@Configuration
public class RedisConfig {

    //读取配置文件中的redis的ip地址
    //使用 @Value的前提条件是当前类必须被spring容器管理
    //disabled 表示如果没有取得host，则给一个默认值disabled
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedisUtil getRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }
        RedisUtil redisUtil=new RedisUtil();
        redisUtil.initJedisPool(host,port,database);
        return redisUtil;
    }


}
