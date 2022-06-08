package com.aiurt.handler;

import com.aiurt.common.base.BaseMap;
import com.aiurt.common.constant.GlobalConstants;
import com.aiurt.common.modules.redis.listener.JeecgRedisListener;
import com.aiurt.loader.DynamicRouteLoader;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 路由刷新监听（实现方式：redis监听handler）
 * @author zyf
 * @date: 2022/4/21 10:55
 */
@Slf4j
@Component(GlobalConstants.LODER_ROUDER_HANDLER)
public class LoderRouderHandler implements JeecgRedisListener {

    @Resource
    private DynamicRouteLoader dynamicRouteLoader;


    @Override
    public void onMessage(BaseMap message) {
        dynamicRouteLoader.refresh(message);
    }

}
