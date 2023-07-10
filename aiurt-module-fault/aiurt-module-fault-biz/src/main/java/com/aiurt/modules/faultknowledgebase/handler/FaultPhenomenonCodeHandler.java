package com.aiurt.modules.faultknowledgebase.handler;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.aiurt.modules.handler.IRuleFillHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author:lkj
 * @create: 2023-07-03 11:50
 * @Description:
 */
@Component("FaultPhenomenonCodeHandler")
public class FaultPhenomenonCodeHandler implements IRuleFillHandler {
    @Override
    public Object execute(Map<String, String> params) {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String code = String.format("%s%s", "GZXX", snowflake.nextIdStr());
        return code;
    }
}
