package com.aiurt.boot.task.service;

import com.aiurt.boot.manager.dto.EquipmentOverhaulDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IRepairTaskService extends IService<RepairTask> {

    /**
     * 检修任务列表查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<RepairTask> selectables(Page<RepairTask> pageList, RepairTask condition);

    /**
     * 检修任务清单查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<RepairTaskDTO> selectTasklet(Page<RepairTaskDTO> pageList, RepairTaskDTO condition);



    /**
     * 查询专业，专业子系统的信息
     * @param id
     * @return
     */
    List<MajorDTO> selectMajorCodeList(String id);


    /**
     * 查询专业，专业子系统的信息
     * @param id
     * @return
     */
    EquipmentOverhaulDTO selectEquipmentOverhaulList(String id);
}
