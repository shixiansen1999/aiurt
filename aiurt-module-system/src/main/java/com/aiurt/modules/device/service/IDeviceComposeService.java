package com.aiurt.modules.device.service;

import com.aiurt.modules.device.dto.DeviceComposeTreeDTO;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: device_compose
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IDeviceComposeService extends IService<DeviceCompose> {

    /**
     * 组件、部位树
     * @param deviceTypeCode
     * @return
     */
    List<DeviceComposeTreeDTO> queryComposeTree(String deviceTypeCode);
}
