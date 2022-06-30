package com.aiurt.modules.fault.service.impl;

import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.fault.mapper.DeviceChangeSparePartMapper;
import com.aiurt.modules.fault.service.IDeviceChangeSparePartService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 备件更换记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Service
public class DeviceChangeSparePartServiceImpl extends ServiceImpl<DeviceChangeSparePartMapper, DeviceChangeSparePart> implements IDeviceChangeSparePartService {

    /**
     * 根据故障编码以及维修记录id查询换件信息
     * @param faultCode 故障编码
     * @param recordId 维修记录id
     * @return
     */
    @Override
    public List<DeviceChangeSparePart> queryDeviceChangeByFaultCode(String faultCode, String recordId) {

        return baseMapper.queryDeviceChangeByFaultCode(faultCode, recordId);
    }
}
