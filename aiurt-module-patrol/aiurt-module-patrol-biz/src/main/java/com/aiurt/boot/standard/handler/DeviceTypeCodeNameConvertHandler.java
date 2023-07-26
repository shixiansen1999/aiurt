package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.IRowDataConvertHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lkj
 */
@Component("DeviceTypeCodeNameConvertHandler")
public class DeviceTypeCodeNameConvertHandler implements IRowDataConvertHandler {

    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public String convert(Column value, Map<String, Column> row) {
        if (value.getData() != null) {
            Column majorCodeColumn = row.get("profession_code");

            Column subsystemCodeColumn = row.get("subsystem_code");
            Object subsystem = subsystemCodeColumn.getData();

            LambdaQueryWrapper<DeviceType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeviceType::getMajorCode, (String) majorCodeColumn.getData()).eq(DeviceType::getSystemCode, (String) subsystem);

            // 查询设备类型是否匹配专业和子系统
            DeviceType csMajorByCodeTypeName = sysBaseApi.getDeviceTypeByCode((String) majorCodeColumn.getData(), (String) subsystem, (String) value.getData());
            if (ObjectUtil.isNotNull(csMajorByCodeTypeName)) {
                value.setData(csMajorByCodeTypeName.getCode());
            }
        }
        return null;
    }
}
