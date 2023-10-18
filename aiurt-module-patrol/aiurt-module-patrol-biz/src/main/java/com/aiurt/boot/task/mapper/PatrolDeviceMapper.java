package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolDeviceDTO;
import com.aiurt.boot.task.entity.PatrolDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
public interface PatrolDeviceMapper extends BaseMapper<PatrolDevice> {
    List<PatrolDeviceDTO> queryDevices(@Param("taskId") String taskId, @Param("taskStandardId") String taskStandardId);
}
