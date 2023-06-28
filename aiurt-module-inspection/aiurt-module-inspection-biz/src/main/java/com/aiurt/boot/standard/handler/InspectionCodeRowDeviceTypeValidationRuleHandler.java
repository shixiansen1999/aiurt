package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-28 14:54
 * @Description:
 */
@Component("InspectionCodeRowDeviceTypeValidationRuleHandler")
public class InspectionCodeRowDeviceTypeValidationRuleHandler implements RowValidationRule {
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        if (InspectionConstant.SHI.equals(row.get("is_appoint_device").getData())) {
            Object deviceTypeName = column.getData();
            if (ObjectUtil.isEmpty(deviceTypeName)) {
                return new ValidationResult(false, String.format("当与设备类型相关字段为是时，%s该字段值不能为空",column.getName()));
            }
            Object majorCode = row.get("major_code").getData();
            Object subsystemCode = row.get("subsystem_code").getData();

            // 查询设备类型是否匹配专业和子系统
            DeviceType csMajorByCodeTypeName = sysBaseApi.getCsMajorByCodeTypeName((String) majorCode, (String) deviceTypeName, (String) subsystemCode);
            if (ObjectUtil.isEmpty(csMajorByCodeTypeName)) {
                return new ValidationResult(false, String.format("系统不存在该专业或该子系统的设备类型"));
            }

        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
