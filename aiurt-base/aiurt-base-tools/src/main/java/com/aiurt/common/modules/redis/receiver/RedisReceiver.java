package com.aiurt.common.modules.redis.receiver;


import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.GlobalConstants;
import lombok.Data;
import com.aiurt.common.base.BaseMap;
import com.aiurt.common.modules.redis.listener.RedisListener;
import com.aiurt.common.util.SpringContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author zyf
 */
@Component
@Data
public class RedisReceiver {


    /**
     * 接受消息并调用业务逻辑处理器
     *
     * @param params
     */
    public void onMessage(BaseMap params) {
        Object handlerName = params.get(GlobalConstants.HANDLER_NAME);
        RedisListener messageListener = SpringContextHolder.getHandler(handlerName.toString(), RedisListener.class);
        if (ObjectUtil.isNotEmpty(messageListener)) {
            messageListener.onMessage(params);
        }
    }

}
