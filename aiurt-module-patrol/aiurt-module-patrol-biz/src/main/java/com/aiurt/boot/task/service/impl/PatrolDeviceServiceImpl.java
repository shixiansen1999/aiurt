package com.aiurt.boot.task.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.task.dto.PatrolDeviceDTO;
import com.aiurt.boot.task.entity.PatrolDevice;
import com.aiurt.boot.task.mapper.PatrolDeviceMapper;
import com.aiurt.boot.task.service.IPatrolDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Service
public class PatrolDeviceServiceImpl extends ServiceImpl<PatrolDeviceMapper, PatrolDevice> implements IPatrolDeviceService {

    @Autowired
    private PatrolDeviceMapper patrolDeviceMapper;

    @Override
    public List<PatrolDeviceDTO> queryDevices(String taskId, String taskStandardId, String deviceCode) {
        List<PatrolDeviceDTO> patrolDeviceDTOList = patrolDeviceMapper.queryDevices(taskId, taskStandardId);
        // 当标准与设备类型相关且不合并工单时，此时是按照设备来生成工单的，只返回该工单的设备
        if (StrUtil.isNotBlank(deviceCode)) {
            patrolDeviceDTOList = patrolDeviceDTOList.stream().filter(pd -> deviceCode.equals(pd.getDeviceCode())).collect(Collectors.toList());
        }
        return patrolDeviceDTOList;
    }
}
