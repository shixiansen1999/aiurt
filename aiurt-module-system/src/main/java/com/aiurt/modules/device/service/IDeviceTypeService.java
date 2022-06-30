package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.DeviceType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IDeviceTypeService extends IService<DeviceType> {


    /**
     * DeviceType树
     * @param typeList
     * @param id
     * @return
     */
    List<DeviceType> treeList(List<DeviceType> typeList, String id);
    /**
     * 添加
     *
     * @param deviceType
     * @return
     */
    Result<?> add(DeviceType deviceType);
    /**
     * 编辑
     *
     * @param deviceType
     * @return
     */
    Result<?> update(DeviceType deviceType);

    /**
     * 拼接cc字段
     * @param deviceType
     * @return
     */
    String getCcStr(DeviceType deviceType);
}
