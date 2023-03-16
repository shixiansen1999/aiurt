package com.aiurt.modules.fault.service.impl;


import com.aiurt.modules.fault.dto.FaultDeviceRepairDTO;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.mapper.FaultDeviceMapper;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: fault_device
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class FaultDeviceServiceImpl extends ServiceImpl<FaultDeviceMapper, FaultDevice> implements IFaultDeviceService {

    @Override
    public List<FaultDevice> queryByFaultCode(String faultCode) {
        List<FaultDevice> faultDeviceList = baseMapper.queryByFaultCode(faultCode);
        return faultDeviceList;
    }

    @Override
    public IPage<FaultDeviceRepairDTO> queryRepairDeviceList(Page<FaultDeviceRepairDTO> page, FaultDeviceRepairDTO FaultDeviceRepairDTO) {
        IPage<FaultDeviceRepairDTO> faultDeviceRepairDtoList = baseMapper.queryRepairDeviceList(page, FaultDeviceRepairDTO);
        return faultDeviceRepairDtoList;
    }


}
