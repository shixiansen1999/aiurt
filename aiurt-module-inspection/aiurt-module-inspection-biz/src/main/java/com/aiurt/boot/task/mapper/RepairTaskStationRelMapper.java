package com.aiurt.boot.task.mapper;

import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.task.entity.RepairTaskStationRel;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: repair_task_station_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
public interface RepairTaskStationRelMapper extends BaseMapper<RepairTaskStationRel> {

    /**
     * 根据检修任务code查询检修任务对应的站点编码
     * @param planCode
     * @return
     */
    List<StationDTO> selectStationList(String planCode);
}
