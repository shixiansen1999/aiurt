package com.aiurt.boot.task.service;

import com.aiurt.boot.task.entity.PatrolTaskStation;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_task_station
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
public interface IPatrolTaskStationService extends IService<PatrolTaskStation> {

    /**
     * 根据巡检任务编号查询站点信息
     *
     * @param taskCode
     * @return
     */
    List<PatrolTaskStationDTO> selectStationByTaskCode(String taskCode);
}
