package com.aiurt.modules.faultknowledgebase.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.IRowDataConvertHandler;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lkj
 */
@Component("SpecificationConvertHandler")
public class SpecificationConvertHandler implements IRowDataConvertHandler {

    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public String convert(Column value, Map<String, Column> row) {
        if (ObjectUtil.isNotEmpty(value.getData())) {
            value.setData(sysBaseApi.getMaterialSpecificationByCode((String) value.getData()));
        }
        return null;
    }
}
