package com.aiurt.boot.task.mapper;

import java.util.List;

import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.task.entity.PatrolTaskStation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: patrol_task_station
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
public interface PatrolTaskStationMapper extends BaseMapper<PatrolTaskStation> {

    List<PatrolTaskStationDTO> selectStationByTaskCode(@Param("taskCode") String taskCode);
}
