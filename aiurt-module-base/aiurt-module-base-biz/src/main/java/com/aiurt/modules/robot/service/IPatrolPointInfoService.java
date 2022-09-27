package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.entity.PatrolPointInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: patrol_point_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
public interface IPatrolPointInfoService extends IService<PatrolPointInfo> {

    /**
     * 同步巡检点位
     */
    void synchronizePoint();
}
