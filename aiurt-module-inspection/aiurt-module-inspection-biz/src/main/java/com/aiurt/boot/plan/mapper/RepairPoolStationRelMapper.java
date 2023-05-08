package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.index.dto.MapDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPoolStationRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
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

    /**
     * 根据检修计划code查询检修任务对应的站点编码
     *
     * @param planCodes
     * @return
     */
    List<MapDTO> selectStationToMapByPlanCode(List<String> planCodes);

    /**
     * 批量查询与给定的 repairPoolCodes 相关的站点信息
     * @param repairPoolCodes 修理池编码列表
     * @return 与给定的 repairPoolCodes 相关的站点信息列表
     */
    List<StationDTO> selectBatchStationList(@Param("repairPoolCodes") List<String> repairPoolCodes);
}
