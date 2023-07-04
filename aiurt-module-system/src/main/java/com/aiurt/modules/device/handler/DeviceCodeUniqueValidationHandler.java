package com.aiurt.modules.device.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.mapper.DeviceMapper;
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
@Component("DeviceCodeUniqueValidationHandler")
public class DeviceCodeUniqueValidationHandler implements ValidationRule {
    @Resource
    private DeviceMapper deviceMapper;
    /**
     * 设备编号在数据库中唯一
     *
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(Column column) {
        // 获取当前列的值
        Object currentValue = column.getData();
        if (ObjectUtil.isNotEmpty(currentValue)) {
            Device device = deviceMapper.selectOne(new QueryWrapper<Device>().lambda().eq(Device::getCode, currentValue).eq(Device::getDelFlag, 0));
            if (ObjectUtil.isNotEmpty(device)) {
                return new ValidationResult(false, String.format("%s该字段值在数据库中已存在",  column.getName()));
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
