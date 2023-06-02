package com.aiurt.modules.common.api;

import com.aiurt.modules.fault.dto.FaultHistoryDTO;
import com.aiurt.modules.fault.dto.FaultMaintenanceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private IFaultService faultService;

    @Override
    public List<FaultMaintenanceDTO> personnelPortraitStatic(List<String> userIds) {
        return faultRepairRecordMapper.personnelPortraitStatic(userIds);
    }

    @Override
    public List<FaultHistoryDTO> repairDeviceTopFive(String userId) {
        return faultRepairRecordMapper.repairDeviceTopFive(userId);
    }

    @Override
    public IPage<Fault> selectFaultRecordPageList(Fault fault, Integer pageNo, Integer pageSize, HttpServletRequest request) {
        return faultService.queryPageList(fault, pageNo, pageSize, request);
    }
}
