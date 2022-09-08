package com.aiurt.modules.fault.service;

import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.faultanalysisreport.dto.SpareConsumeDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * @Description: 备件更换记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
public interface IDeviceChangeSparePartService extends IService<DeviceChangeSparePart> {

    /**
     * 根据故障编码以及维修记录id查询换件信息
     * @param faultCode 故障编码
     * @param recordId 维修记录id
     * @return
     */
     List<DeviceChangeSparePart> queryDeviceChangeByFaultCode(String faultCode, String recordId);


    /**
     * 统计该时间段内的备件消耗品
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    List<SpareConsumeDTO> querySpareConsume(Date startDate, Date endDate);
}
