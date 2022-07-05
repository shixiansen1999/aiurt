package com.aiurt.modules.fault.service.impl;

import com.aiurt.modules.fault.dto.DeviceChangeRecordDTO;
import com.aiurt.modules.fault.dto.RecordDetailDTO;
import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 维修记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Service
public class FaultRepairRecordServiceImpl extends ServiceImpl<FaultRepairRecordMapper, FaultRepairRecord> implements IFaultRepairRecordService {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public RecordDetailDTO queryDetailByFaultCode(String faultCode) {
        RecordDetailDTO recordDetailDTO = new RecordDetailDTO();
        // 查询故障工单

        List<RepairRecordDetailDTO> detailDTOList = baseMapper.queryRecordByFaultCode(faultCode);
        recordDetailDTO.setDetailList(detailDTOList);


        return recordDetailDTO;
    }

    @Override
    public DeviceChangeRecordDTO queryDeviceChangeRecord(String faultCode) {
        DeviceChangeRecordDTO deviceChangeRecordDTO = new DeviceChangeRecordDTO();
        deviceChangeRecordDTO.setDeviceChangeList(Collections.emptyList());
        deviceChangeRecordDTO.setConsumableList(Collections.emptyList());
        return deviceChangeRecordDTO;
    }
}
