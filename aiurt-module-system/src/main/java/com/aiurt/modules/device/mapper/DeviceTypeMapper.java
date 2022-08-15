package com.aiurt.modules.device.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface DeviceTypeMapper extends BaseMapper<DeviceType> {
    List<DeviceType> readAll();
}
