package com.aiurt.modules.common.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
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
    @Autowired
    private ISysBaseAPI iSysBaseApi;

    @Override
    public List<FaultMaintenanceDTO> personnelPortraitStatic(List<String> usernames) {
        return faultRepairRecordMapper.personnelPortraitStatic(usernames);
    }

    @Override
    public List<FaultHistoryDTO> repairDeviceTopFive(String userId) {
        LoginUser loginUser = iSysBaseApi.getUserById(userId);
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("未查询到用户信息！");
        }
        return faultRepairRecordMapper.repairDeviceTopFive(loginUser.getUsername());
    }

    @Override
    public IPage<Fault> selectFaultRecordPageList(Fault fault, Integer pageNo, Integer pageSize, HttpServletRequest request) {
        return faultService.queryPageList(fault, pageNo, pageSize, request);
    }

    @Override
    public List<FaultDeviceDTO> deviceInfo(String userId) {
        LoginUser loginUser = iSysBaseApi.getUserById(userId);
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("未查询到用户信息！");
        }
        List<FaultDeviceDTO> deviceInfo = faultRepairRecordMapper.deviceInfo(loginUser.getUsername());
        return deviceInfo;
    }

    @Override
    public Map<Integer, Long> getFaultTaskNumber(String userId, int flagYearAgo, int thisYear) {
        Map<Integer, Long> map = new HashMap<>(8);
        LoginUser loginUser = iSysBaseApi.getUserById(userId);
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("未查询到用户信息！");
        }
        List<PortraitTaskModel> list = faultMapper.getFaultTaskNumber(loginUser.getUsername(), flagYearAgo, thisYear, FaultConstant.FAULT_STATUS);
        if (CollUtil.isNotEmpty(list)) {
            for (PortraitTaskModel portraitTask : list) {
                map.put(portraitTask.getYear(), portraitTask.getNumber());
            }
        }
        return map;
    }

    @Override
    public List<RadarNumberModelDTO> getHandleNumber() {
        return faultRepairRecordMapper.getHandleNumber();
    }

    @Override
    public List<EfficiencyDTO> getEfficiency() {
        return faultRepairRecordMapper.getEfficiency();
    }
}
