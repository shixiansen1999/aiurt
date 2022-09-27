package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.entity.PatrolAreaInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_area_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
public interface IPatrolAreaInfoService extends IService<PatrolAreaInfo> {

    /**
     * 巡检区域和点位树形查询
     *
     * @param name 巡检区域名称或点位名称
     * @return 树形结构
     */
    List<AreaPointDTO> treelist(String name);

    /**
     * 同步巡检区域和点位
     *
     * @return
     */
    void synchronizeAreaAndPoint();
    /**
     * 同步巡检区域
     *
     * @return
     */
    void synchronizeArea();

    /**
     * 编辑巡检点位
     * @param patrolAreaInfo
     */
    void updatePoint(PatrolAreaInfo patrolAreaInfo);

    /**
     * 查询所有巡检区域
     * @return
     */
    List<AreaPointDTO> selectAreaList();

}
