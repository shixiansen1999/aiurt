package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.modules.device.entity.DeviceType;
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
 * @create: 2023-06-28 14:54
 * @Description:
 */
@Component("InspectionCodeRowDeviceTypeValidationRuleHandler")
public class InspectionCodeRowDeviceTypeValidationRuleHandler implements RowValidationRule {
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        Column appointDeviceColumn = row.get("is_appoint_device");
        if (appointDeviceColumn != null) {
            if (InspectionConstant.SHI.equals(appointDeviceColumn.getData())) {
                return validateForAppointedDevice(row, column);
            } else if (InspectionConstant.FOU.equals(appointDeviceColumn.getData())) {
                return validateForNonAppointedDevice(column);
            }
        }
        return new ValidationResult(true, null);
    }

    /**
     * 针对特定的行和列验证设备是否被指定。
     *
     * @param row    正在被验证的数据集中的行。
     * @param column 正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     * <p>
     * 此方法首先检查设备数据是否为空。如果为空，它将返回带有错误消息的 ValidationResult。
     * 如果设备数据不为空，它将继续使用 `validateMajorAndSubsystem` 方法来验证主专业和子系统。
     */
    private ValidationResult validateForAppointedDevice(Map<String, Column> row, Column column) {
        if (ObjectUtil.isEmpty(column.getData())) {
            return new ValidationResult(false, String.format("当与设备类型相关字段为是时，%s该字段值不能为空", column.getName()));
        }
        return validateMajorAndSubsystem(row, column);
    }

    /**
     * 验证设备类型是否属于主专业和子系统。
     *
     * @param row    正在被验证的数据集中的行。
     * @param column 正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     */
    private ValidationResult validateMajorAndSubsystem(Map<String, Column> row, Column column) {
        Column majorCodeColumn = row.get("major_code");
        if (majorCodeColumn != null) {
            return checkMajorAndSubsystem(majorCodeColumn, row, column);
        }
        return new ValidationResult(true, null);
    }

    /**
     * 验证主专业和子系统的数据是否正确。
     *
     * @param row    正在被验证的数据集中的行。
     * @param column 正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     */
    private ValidationResult checkMajorAndSubsystem(Column majorCodeColumn, Map<String, Column> row, Column column) {
        Object majorName = majorCodeColumn.getData();
        Object subsystemName = getSubsystemName(row);
        JSONObject csMajorByName = sysBaseApi.getCsMajorByName((String) majorName);

        if (csMajorByName != null) {
            return validateDeviceType(csMajorByName, subsystemName, column);
        }
        return new ValidationResult(true, null);
    }

    /**
     * 获取子系统的名称。
     *
     * @param row 正在被验证的数据集中的行。
     * @return 子系统名称。
     */
    private Object getSubsystemName(Map<String, Column> row) {
        Column subsystemCodeColumn = row.get("subsystem_code");
        if (subsystemCodeColumn != null) {
            return subsystemCodeColumn.getData();
        }
        return null;
    }

    /**
     * 验证设备类型是否匹配主专业和子系统。
     *
     * @param csMajorByName 主专业的名称。
     * @param subsystemName 子系统名称。
     * @param column        正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     */
    private ValidationResult validateDeviceType(JSONObject csMajorByName, Object subsystemName, Column column) {
        JSONObject systemName = sysBaseApi.getSystemName(csMajorByName.getString("majorCode"), (String) subsystemName);

        String systemCode = "";
        if (systemName != null) {
            systemCode = systemName.getString("systemCode");
        }

        // 查询设备类型是否匹配专业和子系统
        DeviceType csMajorByCodeTypeName = sysBaseApi.getCsMajorByCodeTypeName(csMajorByName.getString("majorCode"), (String) column.getData(), systemCode);
        if (ObjectUtil.isEmpty(csMajorByCodeTypeName)) {
            return new ValidationResult(false, String.format("系统不存在该专业或该子系统的设备类型"));
        }
        return new ValidationResult(true, null);
    }

    /**
     * 不与设备类型相关时，不用填写设备类型字段，进行验证。
     *
     * @param column 正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     */
    private ValidationResult validateForNonAppointedDevice(Column column) {
        if (ObjectUtil.isNotEmpty(column.getData())) {
            return new ValidationResult(false, "不与设备类型相关时，不用填写设备类型字段");
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
