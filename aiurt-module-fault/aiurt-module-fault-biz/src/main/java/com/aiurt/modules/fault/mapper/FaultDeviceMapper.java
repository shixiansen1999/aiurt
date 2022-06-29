package com.aiurt.modules.fault.mapper;


import com.aiurt.modules.fault.entity.FaultDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: fault_device
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface FaultDeviceMapper extends BaseMapper<FaultDevice> {

    /**
     * 根据故障编码
     * @param faultCode
     * @return
     */
    List<FaultDevice> queryByFaultCode(String faultCode);
}
