package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolTaskDeviceDTO;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskDeviceMapper extends BaseMapper<PatrolTaskDevice> {

    List<PatrolTaskDeviceParam> selectBillInfo(PatrolTaskDeviceParam patrolTaskDeviceParam);

    /**
     * app-获取巡检清单列表
     * @param pageList
     * @param patrolTaskDeviceDTO
     * @return
     */
    List<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(@Param("pageList") Page<PatrolTaskDeviceDTO> pageList, @Param("patrolTaskDeviceDTO") PatrolTaskDeviceDTO patrolTaskDeviceDTO);
}
