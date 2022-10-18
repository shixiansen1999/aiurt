package com.aiurt.modules.robot.mapper;


import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.entity.PatrolAreaInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: patrol_area_info
 * @Author: aiurt
 * @Date:   2022-09-26
 * @Version: V1.0
 */
public interface PatrolAreaInfoMapper extends BaseMapper<PatrolAreaInfo> {

    /**
     * 查询巡检区域和点位列表
     * @param name  巡检区域名称或点位名称
     * @return
     */
    List<AreaPointDTO> treelist(String name);

    /**
     * 根据设备编码查询巡检区域
     * @param deviceCode
     * @return
     */
    int queryAreaByDeviceCode(String deviceCode);

    /**
     * 查询所有的巡检区域数据
     * @return
     */
    List<AreaPointDTO> selectAreaList();

}
