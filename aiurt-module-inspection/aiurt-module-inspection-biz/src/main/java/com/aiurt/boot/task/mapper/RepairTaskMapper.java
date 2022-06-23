package com.aiurt.boot.task.mapper;

import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface RepairTaskMapper extends BaseMapper<RepairTask> {

    /**
     * 检修任务列表查询
     * @param pageList
     * @param condition
     * @return
     */
    List<RepairTask> selectables(@Param("pageList") Page<RepairTask> pageList, @Param("condition") RepairTask condition);


    /**
     * 检修任务清单查询
     * @param pageList
     * @param condition
     * @return
     */
    List<RepairTask> selectTasklet(@Param("pageList") Page<RepairTask> pageList, @Param("condition") RepairTask condition);


    /**
     * 查询站点信息
     * @param planCode
     * @return
     */
    List<StationDTO> selectStationList(String planCode);

}
