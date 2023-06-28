package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-28 15:23
 * @Description:
 */
@Component("InspectionCodeRowSystemValidationRuleHandler")
public class InspectionCodeRowSystemValidationRuleHandler implements RowValidationRule {
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        Object systemName = column.getData();
        if (ObjectUtil.isNotEmpty(systemName) && ObjectUtil.isNotEmpty(row.get("major_code").getData())) {
            JSONObject subSystem = sysBaseApi.getSystemName((String) row.get("major_code").getData(), (String) systemName);
            if (ObjectUtil.isEmpty(subSystem)) {
                return new ValidationResult(false, "系统不存在该专业下的子系统");
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
