package com.aiurt.boot.modules.fault.service;

import com.aiurt.common.result.FaultDeviceChangSpareResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;

/**
 * @Description: 故障更换备件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IFaultChangeSparePartService extends IService<DeviceChangeSparePart> {

    /**
     * 根据设备编号查询故障更换备件记录
     * @param page
     * @param code
     * @param param
     * @return
     */
    IPage<FaultDeviceChangSpareResult> getFaultDeviceChangeSpare(IPage<FaultDeviceChangSpareResult> page, String code, FaultDeviceParam param);

}
