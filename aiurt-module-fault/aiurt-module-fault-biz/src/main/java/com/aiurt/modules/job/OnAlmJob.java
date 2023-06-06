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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("OnAlmJob类定时任务同步当前告警数据开始，时间：{}", DateUtils.getTimestamp());

        // sqlserver中的数据
        List<AlmRecord> onAlms = faultAlarmService.querySqlServerOnAlm();

        if (CollUtil.isNotEmpty(onAlms)) {
            // 获取设备对应关系
            Map<String, String> deviceMap = getDeviceMap();

            // 转换设备对应关系
            Iterator<AlmRecord> iterator = onAlms.iterator();
            while (iterator.hasNext()) {
                AlmRecord next = iterator.next();
                if (ObjectUtil.isNotEmpty(redisUtil.get(FaultAlarmConstant.FAULT_ALARM_ID + next.getId()))) {
                    iterator.remove();
                } else {
                    next.setDeviceId(deviceMap.get(next.getEquipmentId()));
                }
            }
        }

        // mysql中的数据
        List<String> oldIds = getOldIds();

        if (CollUtil.isEmpty(oldIds)) {
            faultAlarmService.saveBatch(onAlms, 1000);
        }

        // 需要删除的数据
        if (CollUtil.isNotEmpty(onAlms)) {
            List<String> newIds = onAlms.stream().map(AlmRecord::getId).collect(Collectors.toList());
            List<String> deleteIds = oldIds.stream().filter(oldId -> !newIds.contains(oldId)).collect(Collectors.toList());
            faultAlarmService.removeBatchByIds(deleteIds, 1000);
            faultAlarmService.saveOrUpdateBatch(onAlms, 1000);
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
}
