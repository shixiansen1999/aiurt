package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordQueryDTO;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.entity.EmergencyPlanRecord;
import com.aiurt.boot.plan.vo.EmergencyPlanRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDeptUserModel;

import java.util.List;

/**
 * @Description: emergency_plan_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyPlanRecordService extends IService<EmergencyPlanRecord> {
    /**
     * 应急预案启动记录分页查询
     * @param page
     * @param emergencyPlanRecordQueryDto
     * @return
     */
    IPage<EmergencyPlanRecordVO> queryPageList(Page<EmergencyPlanRecordVO> page, EmergencyPlanRecordQueryDTO emergencyPlanRecordQueryDto);

    /**
     * 应急预案启动记录新增
     * @param emergencyPlanRecordDto
     * @return
     */
    String saveAndAdd(EmergencyPlanRecordDTO emergencyPlanRecordDto);

    /**
     * 应急预案启动记录编辑
     * @param emergencyPlanRecordDto
     * @return
     */
    String edit(EmergencyPlanRecordDTO emergencyPlanRecordDto);

    /**
     * 应急预案启动记录删除
     * @param id
     */
    void delete(String id);

    /**
     * 应急预案启动记录通过id查询
     * @param id
     * @return
     */
    EmergencyPlanRecordDTO queryById(String id);

    /**
     * 应急模块-责任部门和用户联动信息
     */
    List<SysDeptUserModel> getDeptUserGanged();

    /**
     * 应急模块-责任人信息
     */
    List<LoginUser> getDutyUser();

    /**
     * 应急预案启动记录-提交
     * @param id
     * @return
     */
    String submit(String id);
}
