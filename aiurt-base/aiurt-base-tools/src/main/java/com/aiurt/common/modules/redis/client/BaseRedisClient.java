package com.aiurt.common.modules.redis.client;

import com.aiurt.common.constant.GlobalConstants;
import com.aiurt.common.base.BaseMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
* @Description: redis客户端
* @author: scott
* @date: 2020/01/01 16:01
*/
@Configuration
public class BaseRedisClient {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 发送消息
     *
     * @param handlerName
     * @param params
     */
    public void sendMessage(String handlerName, BaseMap params) {
        params.put(GlobalConstants.HANDLER_NAME, handlerName);
        redisTemplate.convertAndSend(GlobalConstants.REDIS_TOPIC_NAME, params);
    }


}
