package com.aiurt.modules.robot.service;


import com.aiurt.modules.robot.entity.PatrolPointInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据巡视点位ID获取设备编号,若参数为空获取全部，K:V(点位ID：设备编号)
     * @param points
     * @return
     */
    Map<String, String> getDeviceCodeByPointId(List<String> points);
}
