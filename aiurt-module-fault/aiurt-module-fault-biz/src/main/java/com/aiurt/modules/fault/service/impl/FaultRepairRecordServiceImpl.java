package com.aiurt.modules.fault.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.fault.dto.DeviceChangeRecordDTO;
import com.aiurt.modules.fault.dto.RecordDetailDTO;
import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultRepairParticipantsService;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    /**
     * 避免循环依赖，代码解耦肯定是最优解
     */
    @Autowired
    @Lazy
    private IFaultService faultService;

    @Autowired
    private IFaultRepairParticipantsService participantsService;

    @Autowired
    private IFaultKnowledgeBaseService knowledgeBaseService;

    @Override
    public RecordDetailDTO queryDetailByFaultCode(String faultCode) {

        RecordDetailDTO recordDetailDTO = new RecordDetailDTO();
        // 查询故障工单

        Fault fault = faultService.queryByCode(faultCode);

        Date receiveTime = fault.getReceiveTime();
        Date endTime = fault.getEndTime();
        if (Objects.nonNull(endTime) && Objects.nonNull(receiveTime)) {
            recordDetailDTO.setEndTime(endTime);
            long between = DateUtil.between(receiveTime, endTime, DateUnit.MINUTE);
            between = between == 0? 1: between;
            long day = between / (24 * 60);
            long hours = between % (24 * 60) / 60;
            long min = between % (24 * 60) % 60;
            recordDetailDTO.setRecoveryDuration(day+"天"+hours+"小时"+min + "分");
        }
        // 故障历史
        recordDetailDTO.setStatus(fault.getStatus());
        List<RepairRecordDetailDTO> detailDTOList = baseMapper.queryRecordByFaultCode(faultCode);

        // 参与人
        detailDTOList.stream().forEach(repairRecordDetailDTO -> {
            List<FaultRepairParticipants> list = participantsService.queryParticipantsByRecordId(repairRecordDetailDTO.getId());
            repairRecordDetailDTO.setParticipantsList(list);

            // 响应时长： 接收到任务，开始维修时长
            Date receviceTime = repairRecordDetailDTO.getReceviceTime();
            Date startTime = repairRecordDetailDTO.getStartTime();
            Date time = repairRecordDetailDTO.getEndTime();
            if (Objects.nonNull(startTime) && Objects.nonNull(receviceTime)) {
                long between = DateUtil.between(receviceTime, startTime, DateUnit.MINUTE);
                between = between == 0? 1: between;
                repairRecordDetailDTO.setResponseDuration(between+"分钟");
            }
            if (Objects.nonNull(startTime) && Objects.nonNull(time)) {
                long between = DateUtil.between(time, startTime, DateUnit.MINUTE);
                between = between == 0? 1: between;
                repairRecordDetailDTO.setRepairDuration(between+"分钟");
            }
        });
        recordDetailDTO.setDetailList(detailDTOList);

        String knowledgeId = fault.getKnowledgeId();
        if (StrUtil.isNotBlank(knowledgeId)) {
            FaultKnowledgeBase knowledgeBase = knowledgeBaseService.getById(knowledgeId);
            recordDetailDTO.setFaultKnowledgeBase(knowledgeBase);
        }

        recordDetailDTO.setFaultKnowledgeBase(new FaultKnowledgeBase());
        return recordDetailDTO;
    }

    @Override
    public DeviceChangeRecordDTO queryDeviceChangeRecord(String faultCode) {
        // todo
        DeviceChangeRecordDTO deviceChangeRecordDTO = new DeviceChangeRecordDTO();
        deviceChangeRecordDTO.setDeviceChangeList(Collections.emptyList());
        deviceChangeRecordDTO.setConsumableList(Collections.emptyList());
        return deviceChangeRecordDTO;
    }
}
