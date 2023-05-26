package com.aiurt.boot.plan.service;

import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.OrgDTO;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCodeContent;
import com.aiurt.boot.plan.req.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
public interface IRepairPoolService extends IService<RepairPool> {
    /**
     * 分页查询检修计划池中的检修任务。
     * @param selectPlanReq 查询条件对象，封装了查询所需的筛选参数，如起始时间、结束时间、状态等
     * @return 返回一个检修计划池任务列表，包含符合查询条件的检修任务
     */
    IPage<RepairPool> queryList(SelectPlanReq selectPlanReq);

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
    List<StandardNewDTO> queryStandardList(String code, String majorCode, String systemCode);

    /**
     * 分页查询手工下发的检修任务列表
     *
     * @param page          分页对象，包含当前页码和每页显示的记录数
     * @param manualTaskReq 查询条件对象，包含检修任务单号、检修类型、状态等筛选条件
     * @return IPage<RepairPool> 结果对象，包含分页后的检修任务列表及分页信息
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

    /**
     * app指派任务下拉接口
     * @param id 检修计划id
     */
    List<OrgDTO> queryUserDownList(String id);

    /**
     * app-检修计划站所信息
     * @param page 分页
     * @param selectPlanReq 传参
     * @return IPage<StationPlanDTO> 结果对象，包含分页后的检修任务列表及分页信息
     */
    IPage<StationPlanDTO> queryPlanStationList(Page<StationPlanDTO> page,SelectPlanReq selectPlanReq);
}
