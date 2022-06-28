package com.aiurt.boot.plan.service;

import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;

import java.util.Date;
import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IRepairPoolService extends IService<RepairPool> {
    /**
     * 检修计划池列表查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<RepairPool> queryList(Date startTime, Date endTime);

    /**
     * 获取时间范围和周数
     * @param year 年份
     * @return
     */
    Result getTimeInfo(Integer year);

    /**
     * 通过检修计划id查看检修标准详情
     * @param req
     * @return
     */
    RepairStrategyDTO queryStandardById(RepairStrategyReq req);

    /**
     * 通过检修计划id查看详情
     * @param id
     * @return
     */
    RepairPoolDetailsDTO queryById(String id);

    /**
     * 检修计划池-调整时间
     * @param ids
     * @param startTime
     * @param endTime
     * @return
     */
    Result updateTime(String ids, String startTime, String endTime);

    /**
     * 检修详情里的适用专业下拉列表
     * @param code
     * @return
     */
    List<MajorDTO> queryMajorList(String code);

    /**
     * 指派检修任务
     * @param assignDTO
     * @return
     */
    Result assigned(AssignDTO assignDTO);
    /**
     * 指派检修任务人员下拉列表
     *
     * @param
     * @return
     */
    List<LoginUser> queryUserList(String code);
    /**
     * 检修详情里的检修标准下拉列表
     *
     * @param code
     * @return
     */
    List<StandardDTO> queryStandardList(String code);

    /**
     * 分页查询手工下发任务列表
     * @param page
     * @param queryWrapper
     * @return
     */
    IPage<RepairPool> listPage(Page<RepairPool> page, QueryWrapper<RepairPool> queryWrapper);

    /**
     * 通过id查询手工下发检修任务信息
     * @param id
     * @return
     */
    RepairPoolDTO queryManualTaskById(String id);

    /**
     *修改手工下发检修任务信息
     * @param repairPoolDTO
     */
    void updateManualTaskById(RepairPoolDTO repairPoolDTO);

    /**
     * 根据任务id删除手工下发检修任务
     * @param id
     */
    void deleteManualTaskById(String id);

    /**
     * 添加手工下发检修任务
     * @param repairPoolDTO
     */
    void addManualTask(RepairPoolDTO repairPoolDTO);
}
