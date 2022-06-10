package com.aiurt.boot.modules.fault.service.impl;

import com.aiurt.common.result.FaultDeviceChangSpareResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.boot.modules.fault.mapper.DeviceChangeSparePartMapper;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;
import com.aiurt.boot.modules.fault.service.IFaultChangeSparePartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description: 故障更换备件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultChangeSparePartServiceImpl extends ServiceImpl<DeviceChangeSparePartMapper, DeviceChangeSparePart> implements IFaultChangeSparePartService {


    @Resource
    private IDeviceService deviceService;

    @Resource
    private DeviceChangeSparePartMapper deviceChangeSparePartMapper;

    /**
     * 根据设备编号查询故障更换备件记录
     * @param page
     * @param code
     * @param param
     * @return
     */
    @Override
    public IPage<FaultDeviceChangSpareResult> getFaultDeviceChangeSpare(IPage<FaultDeviceChangSpareResult> page, String code, FaultDeviceParam param) {

        Device device = deviceService.getOne(new QueryWrapper<Device>().eq(Device.CODE, code), false);
        IPage<FaultDeviceChangSpareResult> results = deviceChangeSparePartMapper.selectFaultDevice(page, device.getId(), param);
        return results;
    }
}
