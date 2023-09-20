package com.aiurt.boot.standard.handler;

import com.aiurt.boot.utils.PatrolCodeUtil;
import com.aiurt.modules.handler.IRuleFillHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-29 20:21
 * @Description:
 */
@Component("PatrolStandardCodeHandler")
public class PatrolStandardCodeHandler implements IRuleFillHandler {
    @Override
    public Object execute(Map<String, String> params) {
        return PatrolCodeUtil.getStandardCode();
    }
}

