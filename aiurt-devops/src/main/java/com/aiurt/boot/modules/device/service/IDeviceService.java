package com.aiurt.boot.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.statistical.vo.DeviceDataVo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IDeviceService extends IService<Device> {

    void addNeedInformation(List<Device> list);

    Result<Device> queryDetailById(String deviceId);

    /**
     * 大屏接口
     */
    Integer getDeviceNum(Map map);
    List<DeviceDataVo> getSystemDeviceData(Map map);
    List<DeviceDataVo> getDeviceNumByStation(Map map);

    List<Device> queryDeviceByStationCodeAndSystemCode(String stationCode, String systemCode);
}
