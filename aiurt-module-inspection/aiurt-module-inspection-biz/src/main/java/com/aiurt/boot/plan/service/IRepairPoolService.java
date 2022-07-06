package com.aiurt.boot.plan.service;

import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCodeContent;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.aiurt.boot.plan.req.ManualTaskReq;
import com.aiurt.boot.plan.req.RepairPoolCodeReq;
import com.aiurt.boot.plan.req.RepairPoolReq;
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
 * @Date: 2022-06-22
 * @Version: V1.0
 */
public interface IRepairPoolService extends IService<RepairPool> {
    /**
     * 检修计划池列表查询
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    状态
     * @param workType  作业类型
     * @return
     */
    List<RepairPool> queryList(Date startTime, Date endTime, Integer status, Integer workType);

    /**
     * 获取时间范围和周数
     *
     * @param year 年份
     * @return
     */
    Result getTimeInfo(Integer year);

    /**
     * 通过检修计划id查看检修标准详情
     *
     * @param req
     * @return
     */
    RepairStrategyDTO queryStandardById(RepairStrategyReq req);

    /**
     * 通过检修计划id查看详情
     *
     * @param id
     * @return
     */
    RepairPoolDetailsDTO queryById(String id);

    /**
     * 检修计划池-调整时间
     *
     * @param ids
     * @param startTime
     * @param endTime
     * @return
     */
    Result updateTime(String ids, String startTime, String endTime);

    /**
     * 检修详情里的适用专业下拉列表
     *
     * @param code
     * @return
     */
    List<MajorDTO> queryMajorList(String code);

    /**
     * 指派检修任务
     *
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
    List<StandardDTO> queryStandardList(String code, String majorCode, String systemCode);

    /**
     * 分页查询手工下发任务列表
     *
     * @param page
     * @param manualTaskReq
     * @return
     */
    IPage<RepairPool> listPage(Page<RepairPool> page, ManualTaskReq manualTaskReq);

    /**
     * 通过id查询手工下发检修任务信息
     *
     * @param id
     * @return
     */
    RepairPoolDTO queryManualTaskById(String id);

    /**
     * 修改手工下发检修任务信息
     *
     * @param repairPoolReq
     */
    void updateManualTaskById(RepairPoolReq repairPoolReq);

    /**
     * 根据任务id删除手工下发检修任务
     *
     * @param id
     */
    void deleteManualTaskById(String id);

    /**
     * 添加手工下发检修任务
     *
     * @param repairPoolReq
     */
    void addManualTask(RepairPoolReq repairPoolReq);

    /**
     * 根据检修任务code和检修标准id查询检修标准对应的设备
     *
     * @param code 检修任务code
     * @param id   检修标准id
     * @return
     */
    IPage<RepairDeviceDTO> queryDeviceByCodeAndId(Page<RepairDeviceDTO> page, String code, String id);

    /**
     * 处理检修标准、检修项目、检修设备、检修计划与检修标准的关联关系
     *
     * @param jx
     * @param repairPoolCodes
     */
    void handle(String jx, List<RepairPoolCodeReq> repairPoolCodes);

    /**
     * 通过检修标准id查看检修项
     *
     * @param id 检修标准id
     * @return
     */
    List<RepairPoolCodeContent> selectCodeContentList(String id);

    /**
     * 生成检修标准关联、检修设备清单、检修结果信息
     *
     * @param repairPool
     * @param taskId
     * @param taskCode
     */
    void generate(RepairPool repairPool, String taskId, String taskCode);
}
