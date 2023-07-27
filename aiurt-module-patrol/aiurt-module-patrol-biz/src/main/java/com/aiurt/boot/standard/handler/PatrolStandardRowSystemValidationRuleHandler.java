package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
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
@Component("PatrolStandardRowSystemValidationRuleHandler")
public class PatrolStandardRowSystemValidationRuleHandler implements RowValidationRule {
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        //如果设备类型不为空，则子系统不能为空
        Column deviceTypeCode = row.get("device_type_code");
        if ( ObjectUtil.isNotEmpty(deviceTypeCode)&&ObjectUtil.isNotEmpty((String) deviceTypeCode.getData())) {
            return new ValidationResult(false, "设备类型不为空，子系统必填");
        }

        Object systemName = column.getData();
        Column majorName = row.get("profession_code");

        if (ObjectUtil.isNotEmpty(systemName) && ObjectUtil.isNotEmpty(majorName)) {
            JSONObject csMajorByName = sysBaseApi.getCsMajorByName((String) majorName.getData());

            if (ObjectUtil.isNotEmpty(csMajorByName)) {
                JSONObject subSystem = sysBaseApi.getSystemName(csMajorByName.getString("majorCode"), (String) systemName);
                if (ObjectUtil.isEmpty(subSystem)) {
                    return new ValidationResult(false, "系统不存在该专业下的子系统");
                }
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
