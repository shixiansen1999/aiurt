package com.aiurt.modules.fault.service;

import com.aiurt.modules.fault.dto.FaultDeviceRepairDTO;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: fault_device
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface IFaultDeviceService extends IService<FaultDevice> {

    /**
     *  根据维修编号查询故障维修记录
     * @param faultCode
     * @return
     */
    public List<FaultDevice> queryByFaultCode(String faultCode);

    /**
     *
     * @param page
     * @param FaultDeviceRepairDTO
     * @return
     */
    IPage<FaultDeviceRepairDTO> queryRepairDeviceList(Page<FaultDeviceRepairDTO> page, FaultDeviceRepairDTO FaultDeviceRepairDTO);
}
