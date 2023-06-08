package com.aiurt.modules.common.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.EfficiencyDTO;
import com.aiurt.modules.fault.dto.FaultDeviceDTO;
import com.aiurt.modules.fault.dto.FaultHistoryDTO;
import com.aiurt.modules.fault.dto.FaultMaintenanceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.PortraitTaskModel;
import org.jeecg.common.system.vo.RadarModel;
import org.jeecg.common.system.vo.RadarNumberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
    public RadarModel getHandleNumber(String userId, List<String> usernames) {
        LoginUser loginUser = iSysBaseApi.getUserById(userId);
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("未查询到用户信息！");
        }
        List<RadarNumberModel> handleNumber = faultRepairRecordMapper.getHandleNumber();
        if (CollUtil.isNotEmpty(usernames) && CollUtil.isNotEmpty(handleNumber)) {
            // 相同的班组
            handleNumber = handleNumber.stream().filter(l -> usernames.contains(l.getUsername())).collect(Collectors.toList());
        }
        List<Integer> values = handleNumber.stream().map(RadarNumberModel::getNumber).collect(Collectors.toList());

        RadarModel radarModel = new RadarModel();
        RadarNumberModel radarNumberModel = handleNumber.stream()
                .filter(l -> loginUser.getUsername().equals(l.getUsername()))
                .findFirst()
                .orElse(null);
        // 班组其他成员有数据，但是查询的用户不一定有数据
        if (ObjectUtil.isEmpty(radarNumberModel)) {
            return radarModel;
        }

        Integer currentValue = radarNumberModel.getNumber();
        radarModel.setCurrentValue(Double.valueOf(currentValue));
        if (CollUtil.isNotEmpty(values)) {
            Integer maxValue = Collections.max(values);
            Integer minValue = Collections.min(values);
            radarModel.setMaxValue(Double.valueOf(maxValue));
            radarModel.setMinValue(Double.valueOf(minValue));
        }
        return radarModel;
    }

    @Override
    public RadarModel getEfficiency(String username, List<String> usernames) {
        List<EfficiencyDTO> list = faultRepairRecordMapper.getEfficiency();
        // 同一所属班组的
        if (CollUtil.isNotEmpty(usernames) && CollUtil.isNotEmpty(list)) {
            list = list.stream().filter(l -> usernames.contains(l.getUsername())).collect(Collectors.toList());
        }
        RadarModel radarModel = new RadarModel();
        if (CollUtil.isNotEmpty(list)) {
            double currentValue = 0;
            List<Double> values = new ArrayList<>();
            for (EfficiencyDTO e : list) {
                double value = e.getResponseTime() + e.getResolveTime();
                values.add(value);
                if (username.equals(e.getUsername())) {
                    currentValue = value;
                }
            }
            // 班组其他成员有数据，但是查询的用户不一定有数据
            if (0 == currentValue) {
                return radarModel;
            }
            double maxValue = Collections.max(values);
            double minValue = Collections.min(values);
            radarModel.setCurrentValue(currentValue);
            radarModel.setMaxValue(maxValue);
            radarModel.setMinValue(minValue);
        }
        return radarModel;
    }
}
