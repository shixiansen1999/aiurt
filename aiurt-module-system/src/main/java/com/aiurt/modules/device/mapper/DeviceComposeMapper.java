package com.aiurt.modules.device.mapper;

import java.util.List;

import com.aiurt.modules.device.entity.DeviceCompose;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @Description: device_compose
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Component
public interface DeviceComposeMapper extends BaseMapper<DeviceCompose> {

    /**
     * 根据deviceTypeCode查询设备组成
     * @param deviceTypeCode
     * @return
     */
    List<DeviceCompose> queryByDeviceTypeCode(@Param("deviceTypeCode") String deviceTypeCode);

}
