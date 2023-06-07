package com.aiurt.modules.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.modules.faultalarm.constant.FaultAlarmConstant;
import com.aiurt.modules.faultalarm.entity.AlmRecord;
import com.aiurt.modules.faultalarm.entity.OnAlmEquDevice;
import com.aiurt.modules.faultalarm.mapper.OnAlmEquDeviceMapper;
import com.aiurt.modules.faultalarm.service.IFaultAlarmService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author:wgp
 * @create: 2023-06-06 08:36
 * @Description:
 */
@Slf4j
public class OnAlmJob implements Job {
    @Resource
    private IFaultAlarmService faultAlarmService;
    @Resource
    private OnAlmEquDeviceMapper onAlmEquDeviceMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("OnAlmJob类定时任务同步当前告警数据开始，时间：{}", DateUtils.getTimestamp());

        // 获取sqlserver中的告警数据
        List<AlmRecord> onAlms = faultAlarmService.querySqlServerOnAlm();

        // 获取设备对应关系
        Map<String, String> deviceMap = getDeviceMap();

        List<AlmRecord> validOnAlms = filterAndTransformAlmRecords(onAlms, deviceMap);

        // 获取mysql中的告警数据
        List<String> oldIds = getOldIds();

        if (CollUtil.isEmpty(oldIds)) {
            faultAlarmService.saveBatch(validOnAlms, 1000);
        } else {
            // 需要删除的数据
            List<String> deleteIds = findDeleteIds(validOnAlms, oldIds);
            faultAlarmService.removeBatchByIds(deleteIds, 1000);
            faultAlarmService.saveOrUpdateBatch(validOnAlms, 1000);
        }

        log.info("OnAlmJob类定时任务同步当前告警数据结束，时间：{}", DateUtils.getTimestamp());
    }

    /**
     * 获取设备对应关系的映射表
     *
     * @return 设备对应关系的映射表，以集中告警设备ID为键，以系统设备ID为值
     */
    private Map<String, String> getDeviceMap() {
        return Optional.ofNullable(onAlmEquDeviceMapper.selectList(null)).orElse(CollUtil.newArrayList()).stream().filter(onAlmEquDevice -> onAlmEquDevice.getAlmEquipmentId() != null && onAlmEquDevice.getDeviceId() != null).collect(Collectors.toMap(OnAlmEquDevice::getAlmEquipmentId, OnAlmEquDevice::getDeviceId, (v1, v2) -> v1));
    }

    /**
     * 获取旧的告警记录的ID列表
     *
     * @return 旧的告警记录的ID列表
     */
    private List<String> getOldIds() {
        return Optional.ofNullable(faultAlarmService.list(new LambdaQueryWrapper<AlmRecord>().select(AlmRecord::getId))).orElse(CollUtil.newArrayList()).stream().map(AlmRecord::getId).collect(Collectors.toList());
    }

    /**
     * 过滤和转换后的有效告警记录列表
     *
     * @param onAlms    源告警记录列表，包含从SQL Server中查询的数据
     * @param deviceMap 设备对应关系的映射，将设备ID映射到设备编号
     * @return 设备对应关系的映射，将设备ID映射到设备编号
     */
    private List<AlmRecord> filterAndTransformAlmRecords(List<AlmRecord> onAlms, Map<String, String> deviceMap) {
        List<AlmRecord> validOnAlms = new ArrayList<>();
        Set<String> existingIds = new HashSet<>();

        for (AlmRecord almRecord : onAlms) {
            if (existingIds.contains(almRecord.getId())) {
                continue;
            }

            if (ObjectUtil.isNotEmpty(redisUtil.get(FaultAlarmConstant.FAULT_ALARM_ID + almRecord.getId()))) {
                continue;
            }

            existingIds.add(almRecord.getId());
            almRecord.setState(FaultAlarmConstant.ALM_DEAL_STATE_1);
            almRecord.setDeviceId(deviceMap.get(almRecord.getEquipmentId()));
            validOnAlms.add(almRecord);
        }

        return validOnAlms;
    }

    /**
     * @param validOnAlms 经过过滤和转换后的有效告警记录列表
     * @param oldIds      旧的告警记录ID列表
     * @return 需要删除的告警记录ID列表
     */
    private List<String> findDeleteIds(List<AlmRecord> validOnAlms, List<String> oldIds) {
        if (CollUtil.isEmpty(oldIds)) {
            return CollUtil.newArrayList();
        }
        List<String> newIds = Optional.ofNullable(validOnAlms).orElse(CollUtil.newArrayList())
                .stream()
                .map(AlmRecord::getId)
                .collect(Collectors.toList());
        return oldIds.stream().filter(oldId -> !newIds.contains(oldId)).collect(Collectors.toList());
    }
}
