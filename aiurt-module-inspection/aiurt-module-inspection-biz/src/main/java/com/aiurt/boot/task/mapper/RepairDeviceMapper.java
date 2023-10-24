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
    List<RepairDeviceDTO> queryDevices(@Param("taskId") String taskId, @Param("taskStandardId") String taskStandardId);
}
