package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.task.dto.DeviceDTO;
import com.aiurt.boot.task.dto.PatrolDeviceDTO;
import com.aiurt.boot.task.entity.PatrolDevice;
import com.aiurt.boot.task.mapper.PatrolDeviceMapper;
import com.aiurt.boot.task.service.IPatrolDeviceService;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Service
public class PatrolDeviceServiceImpl extends ServiceImpl<PatrolDeviceMapper, PatrolDevice> implements IPatrolDeviceService {

    @Autowired
    private PatrolDeviceMapper patrolDeviceMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Override
    public List<PatrolDeviceDTO> queryDevices(String taskId, String taskStandardId, String deviceCode) {
        List<PatrolDeviceDTO> patrolDeviceDTOList = patrolDeviceMapper.queryDevices(taskId, taskStandardId);
        // 当标准与设备类型相关且不合并工单时，此时是按照设备来生成工单的，只返回该工单的设备
        if (StrUtil.isNotBlank(deviceCode)) {
            patrolDeviceDTOList = patrolDeviceDTOList.stream().filter(pd -> deviceCode.equals(pd.getDeviceCode())).collect(Collectors.toList());
        }
        return patrolDeviceDTOList;
    }

    @Override
    public List<DeviceDTO> queryDevicesDetail(String taskId, String taskStandardId) {
        List<DeviceDTO> deviceDTOList = patrolDeviceMapper.queryDevicesDetail(taskId, taskStandardId);
        if (CollUtil.isEmpty(deviceDTOList)) {
            return deviceDTOList;
        }
        Map<String, String> deviceStatusMap = sysBaseApi.queryDictItemsByCode("device_status").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        Map<String, String> deviceTemporaryMap = sysBaseApi.queryDictItemsByCode("device_temporary").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        deviceDTOList.forEach(d -> {
            d.setStatusName(deviceStatusMap.get(StrUtil.toString(d.getStatus())));
            d.setTemporaryName(deviceTemporaryMap.get(d.getTemporary()));
            String positionCodeName = d.getLineName();
            String stationName = d.getStationName();
            String positionName = d.getPositionName();
            if (StrUtil.isNotBlank(stationName)) {
                positionCodeName += CommonConstant.SYSTEM_SPLIT_STR + stationName;
            }
            if (StrUtil.isNotBlank(positionName)) {
                positionCodeName += CommonConstant.SYSTEM_SPLIT_STR + positionName;
            }
            d.setPositionCodeName(positionCodeName);
        });
        return deviceDTOList;
    }
}
