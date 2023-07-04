package com.aiurt.boot.standard.handler;

import com.aiurt.modules.handler.IRuleFillHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-28 11:43
 * @Description:
 */
@Component("InspectionCodeHandler")
public class InspectionCodeHandler implements IRuleFillHandler {
    @Override
    public Object execute(Map<String, String> params) {
        return "BZ"+System.currentTimeMillis();
    }
}

