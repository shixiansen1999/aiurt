package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.RepairDeviceDTO;
import com.aiurt.boot.task.entity.RepairDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
public interface RepairDeviceMapper extends BaseMapper<RepairDevice> {
    /**
     * 根据任务id、任务标准关联表id和工单的设备code查询设备
     * 工单的设备code不为空时只返回该设备
     * @param taskId 任务id
     * @param taskStandardId 任务标准id
     * @param deviceCode 工单的设备code
     * @return
     */
    List<RepairDeviceDTO> queryDevices(@Param("taskId") String taskId, @Param("taskStandardId") String taskStandardId, @Param("deviceCode") String deviceCode);
}
