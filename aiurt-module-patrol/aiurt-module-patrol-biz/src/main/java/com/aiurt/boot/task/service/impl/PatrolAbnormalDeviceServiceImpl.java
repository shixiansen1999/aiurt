package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.task.dto.PatrolAbnormalDeviceAddDTO;
import com.aiurt.boot.task.entity.PatrolAbnormalDevice;
import com.aiurt.boot.task.mapper.PatrolAbnormalDeviceMapper;
import com.aiurt.boot.task.service.IPatrolAbnormalDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sbx
 * @since 2023/10/17
 */
@Service
public class PatrolAbnormalDeviceServiceImpl extends ServiceImpl<PatrolAbnormalDeviceMapper, PatrolAbnormalDevice> implements IPatrolAbnormalDeviceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PatrolAbnormalDeviceAddDTO patrolAbnormalDeviceAddDTO) {
        // 删除之前保存的异常设备
        this.remove(new LambdaQueryWrapper<PatrolAbnormalDevice>().eq(PatrolAbnormalDevice::getResultId, patrolAbnormalDeviceAddDTO.getResultId()));
        List<String> abnormalDeviceCodeList = patrolAbnormalDeviceAddDTO.getAbnormalDeviceCodeList();
        if (CollUtil.isEmpty(abnormalDeviceCodeList)) {
            return;
        }
        // 保存异常设备
        ArrayList<PatrolAbnormalDevice> list = new ArrayList<>();
        abnormalDeviceCodeList.forEach(d -> {
            PatrolAbnormalDevice patrolAbnormalDevice = new PatrolAbnormalDevice();
            patrolAbnormalDevice.setResultId(patrolAbnormalDeviceAddDTO.getResultId());
            patrolAbnormalDevice.setDeviceCode(d);
            list.add(patrolAbnormalDevice);
        });
        this.saveBatch(list);
    }
}
