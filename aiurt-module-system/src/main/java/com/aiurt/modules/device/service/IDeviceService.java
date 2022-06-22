package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IDeviceService extends IService<Device> {

    Result<Device> queryDetailById(String deviceId);

}
