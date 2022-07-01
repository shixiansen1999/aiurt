package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPoolStationRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: repair_pool_station_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface RepairPoolStationRelMapper extends BaseMapper<RepairPoolStationRel> {

    List<StationDTO> selectStationList(String planCode);

    /**
     * 批量插入
     * @param repairPoolStationRel
     */
    void insertBatch(List<RepairPoolStationRel> repairPoolStationRel);
}
