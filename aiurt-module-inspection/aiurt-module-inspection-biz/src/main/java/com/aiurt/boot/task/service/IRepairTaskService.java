package com.aiurt.boot.task.service;

import com.aiurt.boot.manager.dto.EquipmentOverhaulDTO;
import com.aiurt.boot.manager.dto.ExamineDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.task.dto.WriteMonadDTO;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.entity.RepairTaskEnclosure;
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
     * 设备台账-检修履历
     * @param pageList
     * @param condition
     * @return
     */
    Page<RepairTaskDTO> repairSelectTaskletForDevice(Page<RepairTaskDTO> pageList, RepairTaskDTO condition);



    /**
     * 查询专业，专业子系统的信息
     * @param id
     * @return
     */
    List<MajorDTO> selectMajorCodeList(String id);


    /**
     * 查询专业，专业子系统的信息
     * @param id
     * @param majorCode
     * @param subsystemCode
     * @return
     */
    EquipmentOverhaulDTO selectEquipmentOverhaulList(String id,String majorCode,String subsystemCode);

    /**
     * 查询检修单信息
     * @param id
     * @param code
     * @return
     */
    CheckListDTO selectCheckList(String id,String code);

    /**
     * 审核
     * @param examineDTO
     */
    void toExamine(ExamineDTO examineDTO);

    /**
     * 待执行-执行
     * @param examineDTO
     */
    void toBeImplement(ExamineDTO examineDTO);

    /**
     * 执行中-执行
     * @param examineDTO
     */
    void inExecution(ExamineDTO examineDTO);

    /**
     * 验收
     * @param examineDTO
     */
    void acceptance(ExamineDTO examineDTO);

    /**
     * 查询附件息
     * @param resultId
     * @return
     */
    List<RepairTaskEnclosure> selectEnclosure(String resultId);

    /**
     * 待确认退回任务
     * @param examineDTO
     */
    void confirmedDelete(ExamineDTO examineDTO);
    /**
     *  领取检修任务
     *
     * @param id
     * @return
     */
    void receiveTask(String id);
    /**
     * 填写检修工单
     * @param monadDTO
     */
    void writeMonad(WriteMonadDTO monadDTO);
}
