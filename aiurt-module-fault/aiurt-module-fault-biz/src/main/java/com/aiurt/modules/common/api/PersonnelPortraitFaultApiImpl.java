package com.aiurt.modules.common.api;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.FaultDeviceDTO;
import com.aiurt.modules.fault.dto.FaultHistoryDTO;
import com.aiurt.modules.fault.dto.FaultMaintenanceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.PortraitTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private FaultMapper faultMapper;

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

    @Override
    public List<FaultDeviceDTO> deviceInfo(String userId) {
        List<FaultDeviceDTO> deviceInfo = faultRepairRecordMapper.deviceInfo(userId);
        return deviceInfo;
    }

    @Override
    public Map<Integer, Long> getFaultTaskNumber(String userId, int flagYearAgo, int thisYear) {
        Map<Integer, Long> map = new HashMap<>(8);
        List<PortraitTaskModel> list = faultMapper.getFaultTaskNumber(userId, flagYearAgo, thisYear, FaultConstant.FAULT_STATUS);
        if (CollUtil.isNotEmpty(list)) {
            for (PortraitTaskModel portraitTask : list) {
                map.put(portraitTask.getYear(), portraitTask.getNumber());
            }
        }
        return map;
    }
}
