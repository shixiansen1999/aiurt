package com.aiurt.common.modules.redis.receiver;

import com.aiurt.common.modules.redis.listener.FlowRedisListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程
 */
public class FlowRedisReceiver {

    private static Map<String, FlowRedisListener> listenerMap = new HashMap<>();

    /**
     * 注册监听器
     */
    public void registerListener() {

    }
}
