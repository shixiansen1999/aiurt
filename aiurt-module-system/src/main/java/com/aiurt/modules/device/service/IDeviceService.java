package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IDeviceService extends IService<Device> {

    /**
     * 查询详情
     * @param deviceId
     * @return
     */
    Result<Device> queryDetailById(String deviceId);

    /**
     * 翻译
     * @param device
     * @return
     */
    Device translate(Device device);

    /**
     * 编码分级
     * @param deviceTypeCodeCc
     * @return
     */
    String getCodeByCc(String deviceTypeCodeCc);

    QueryWrapper<Device> getQueryWrapper(String stationCode,String positionCodeCc, String temporary, String majorCode, String systemCode, String deviceTypeCode, String code, String name, String status);

}
