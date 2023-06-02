package com.aiurt.modules.common.api;

import com.aiurt.modules.fault.dto.FaultHistoryDTO;
import com.aiurt.modules.fault.dto.FaultMaintenanceDTO;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author
 * @description
 */

@Slf4j
@Service
public class PersonnelPortraitFaultApiImpl implements PersonnelPortraitFaultApi {
    @Autowired
    private FaultRepairRecordMapper faultRepairRecordMapper;

    @Override
    public List<FaultMaintenanceDTO> personnelPortraitStatic(List<String> userIds) {
        return faultRepairRecordMapper.personnelPortraitStatic(userIds);
    }

    @Override
    public List<FaultHistoryDTO> repairDeviceTopFive(String userId) {
        return faultRepairRecordMapper.repairDeviceTopFive(userId);
    }
}
