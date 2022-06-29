package com.aiurt.modules.fault.service.impl;

import com.aiurt.modules.fault.dto.RecordDetailDTO;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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


       // sysBaseAPI.querySysAttachmentByIdList();
        return null;
    }
}
