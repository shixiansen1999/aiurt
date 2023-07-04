package com.aiurt.modules.device.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.ValidationRule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:lkj
 * @create: 2023-07-03 11:50
 * @Description:
 */
@Component("DeviceAssemblyCodeUniqueValidationHandler")
public class DeviceAssemblyCodeUniqueValidationHandler implements ValidationRule {
    @Resource
    private DeviceAssemblyMapper deviceAssemblyMapper;
    @Override
    public ValidationResult validate(Column column) {

        // 获取当前列的值
        Object currentValue = column.getData();
        if (ObjectUtil.isNotEmpty(currentValue)) {
            DeviceAssembly deviceAssembly = deviceAssemblyMapper.selectOne(new QueryWrapper<DeviceAssembly>().lambda()
                    .eq(DeviceAssembly::getCode, currentValue)
                    .eq(DeviceAssembly::getCode, currentValue)
                    .eq(DeviceAssembly::getDelFlag, 0));
            if (ObjectUtil.isNotEmpty(deviceAssembly)) {
                return new ValidationResult(false, String.format("%s该字段值在数据库中已存在", column.getName()));
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
