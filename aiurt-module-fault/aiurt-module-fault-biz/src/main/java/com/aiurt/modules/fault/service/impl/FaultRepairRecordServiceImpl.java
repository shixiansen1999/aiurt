package com.aiurt.modules.fault.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.modules.fault.service.IFaultRepairParticipantsService;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultcauseusagerecords.entity.FaultCauseUsageRecords;
import com.aiurt.modules.faultcauseusagerecords.service.IFaultCauseUsageRecordsService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private FaultMapper faultMapper;

    @Autowired
    private IFaultRepairParticipantsService participantsService;

    @Autowired
    private IFaultKnowledgeBaseService knowledgeBaseService;

    @Autowired
    private IDeviceChangeSparePartService sparePartService;

    @Autowired
    private IFaultCauseUsageRecordsService faultCauseUsageRecordsService;

    @Override
    public RecordDetailDTO queryDetailByFaultCode(String faultCode) {

        RecordDetailDTO recordDetailDTO = new RecordDetailDTO();
        // 查询故障工单
        LambdaQueryWrapper<Fault> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Fault::getCode,faultCode);
        Fault fault1 = faultMapper.selectOne(wrapper);
        recordDetailDTO.setLineCode(fault1.getLineCode());
        recordDetailDTO.setLineName(sysBaseAPI.getLineNameByCode(fault1.getLineCode()));

        Fault fault = faultService.queryByCode(faultCode);

        Date happenTime = fault.getHappenTime();
        Date endTime = fault.getEndTime();
        if (Objects.nonNull(endTime) && Objects.nonNull(happenTime)) {
            recordDetailDTO.setEndTime(endTime);
            long between = DateUtil.between(happenTime, endTime, DateUnit.MS);
            recordDetailDTO.setRecoveryDuration(DateUtil.formatBetween(between, BetweenFormater.Level.SECOND));
        }
        // 故障历史
        recordDetailDTO.setStatus(fault.getStatus());
//        recordDetailDT
        List<RepairRecordDetailDTO> detailDTOList = baseMapper.queryRecordByFaultCode(faultCode);

        // 参与人
        detailDTOList.stream().forEach(repairRecordDetailDTO -> {
            List<FaultRepairParticipants> list = participantsService.queryParticipantsByRecordId(repairRecordDetailDTO.getId());
            repairRecordDetailDTO.setParticipantsList(list);
            if(repairRecordDetailDTO.getProcessing()!=null){
                List<DictModel> faultProcessing = sysBaseAPI.getDictItems("fault_processing");
                String faultProcessingName = faultProcessing.stream().filter(f -> f.getValue().equals(String.valueOf(repairRecordDetailDTO.getProcessing()))).map(DictModel::getLabel).collect(Collectors.joining());
                repairRecordDetailDTO.setProcessingName(faultProcessingName);
            }
            // 响应时长： 接收到任务，开始维修时长
            Date receviceTime = repairRecordDetailDTO.getReceviceTime();
            Date startTime = repairRecordDetailDTO.getStartTime();
            Date time = Optional.ofNullable(repairRecordDetailDTO.getEndTime()).orElse(new Date());
            if (Objects.nonNull(startTime) && Objects.nonNull(receviceTime)) {
                long between = DateUtil.between(receviceTime, startTime, DateUnit.MINUTE);
                between = between == 0 ? 1: between;
                repairRecordDetailDTO.setResponseDuration(between+"分钟");
            }
            if (Objects.nonNull(startTime) && Objects.nonNull(time)) {
                long between = DateUtil.between(time, startTime, DateUnit.MINUTE);
                between = between == 0 ? 1: between;
                repairRecordDetailDTO.setRepairDuration(between+"分钟");
            }
        });
        recordDetailDTO.setDetailList(detailDTOList);

        String knowledgeId = fault.getKnowledgeId();
        if (StrUtil.isNotBlank(knowledgeId)) {
            FaultKnowledgeBase knowledgeBase = knowledgeBaseService.getById(knowledgeId);
            if (Objects.nonNull(knowledgeBase)) {
                String deviceTypeCode = knowledgeBase.getDeviceTypeCode();
                if (StrUtil.isNotBlank(deviceTypeCode)) {
                    String deviceTypeName = baseMapper.queryDeviceTypeName(deviceTypeCode);
                    knowledgeBase.setDeviceTypeName(deviceTypeName);
                }
                // 设备类型
                recordDetailDTO.setFaultKnowledgeBase(knowledgeBase);
            }
        }
        return recordDetailDTO;
    }

    @Override
    public DeviceChangeRecordDTO queryDeviceChangeRecord(String faultCode) {
        Fault fault = faultService.queryByCode(faultCode);
        if (Objects.isNull(fault)) {
            return new DeviceChangeRecordDTO();
        }
        DeviceChangeRecordDTO deviceChangeRecordDTO = new DeviceChangeRecordDTO();
        List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.queryDeviceChangeByFaultCode(faultCode, null);
        List<SparePartStockDTO> deviceChangeList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("0", sparepart.getConsumables()))
                .map(sparepart -> {
                    SparePartStockDTO build = SparePartStockDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .name(sparepart.getNewSparePartName())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .oldSparePartCode(sparepart.getOldSparePartCode())
                            .oldSparePartName(sparepart.getOldSparePartName())
                            .deviceCode(sparepart.getDeviceCode())
                            .deviceName(sparepart.getDeviceName())
                            .specifications(sparepart.getSpecifications())
                            .newSparePartNum(sparepart.getNewSparePartNum())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .newSparePartSplitCode(sparepart.getNewSparePartSplitCode())
                            .lendOutOrderId(sparepart.getLendOutOrderId())
                            .build();
                    return build;
                }).collect(Collectors.toList());
        deviceChangeRecordDTO.setDeviceChangeList(deviceChangeList);
        List<SparePartStockDTO> consumableList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("1", sparepart.getConsumables()))
                .map(sparepart -> {
                    SparePartStockDTO build = SparePartStockDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .name(sparepart.getNewSparePartName())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .specifications(sparepart.getSpecifications())
                            .newSparePartNum(sparepart.getNewSparePartNum())
                            .newSparePartSplitCode(sparepart.getNewSparePartSplitCode())
                            .lendOutOrderId(sparepart.getLendOutOrderId())
                            .build();
                    return build;
                }).collect(Collectors.toList());
        deviceChangeRecordDTO.setConsumableList(consumableList);

        // 查询使用的解决原件
        LambdaQueryWrapper<FaultCauseUsageRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultCauseUsageRecords::getFaultCode, faultCode).eq(FaultCauseUsageRecords::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<FaultCauseUsageRecords> list = faultCauseUsageRecordsService.list(queryWrapper);

        if (CollUtil.isNotEmpty(list)) {
            deviceChangeRecordDTO.setFaultCauseSolutionId(list.stream().map(FaultCauseUsageRecords::getFaultCauseSolutionId).collect(Collectors.toList()));
        }

        // 判断是否异常
        deviceChangeRecordDTO.setIsException(fault.getException()==1);

        return deviceChangeRecordDTO;
    }
}
