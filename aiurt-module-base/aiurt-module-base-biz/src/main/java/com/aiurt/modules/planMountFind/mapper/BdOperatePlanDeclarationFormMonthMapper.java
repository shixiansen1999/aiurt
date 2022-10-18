package com.aiurt.modules.planMountFind.mapper;

import com.aiurt.modules.planMountFind.dto.*;
import com.aiurt.modules.planMountFind.entity.BdOperatePlanDeclarationFormMonth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Description: bd_operate_plan_declaration_form_month
 * @Author: jeecg-boot
 * @Date: 2021-05-18
 * @Version: V1.0
 */
public interface BdOperatePlanDeclarationFormMonthMapper extends BaseMapper<BdOperatePlanDeclarationFormMonth> {

    /**
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param page       页面信息
     * @param roleType   角色类型
     * @param staffID    登陆人id
     * @return Page<getAllByDateDTO>
     */
    Page<getAllByDateDTO> getAllByDate(@Param("start_time") String start_time,@Param("end_time") String end_time,@Param("line_id") String line_id, @Param("page")Page page,@Param("roleType") String roleType, @Param("staffID")String staffID);


    /**
     * 生产计划管理-月施工计划定制-添加下月任务-作业类别-下拉框接口
     *
     * @return List<BdWorkTypeDTO>
     */
    List<BdWorkTypeDTO> queryAllContruction();

    /**
     * 生产计划管理-月施工计划定制-添加下月任务-施工负责人-下拉框接口
     *
     * @param team_id 团队id
     * @return List<queryByTeamIdStaffDTO>
     */
    List<queryByTeamIdStaffDTO> queryByTeamIdStaff(String team_id);

    /**
     * 生产计划管理-月施工计划定制-生产经理 与 线路负责人-下拉框接口
     *
     * @param roleType 角色类型
     * @param deptId   ?
     * @return List<queryStaffsByRoleTypeDTO>
     */
    List<queryStaffsByRoleTypeDTO> queryStaffsByRoleType(@Param("roleType") String roleType,@Param("deptId") String deptId);

    /**
     * 站点信息查询
     *
     * @return List<BdStationCopyDTO>
     */
    List<BdStationCopyDTO> queryAllStationInfo();

    /**
     * 获取线路信息,返回线路id和name
     *
     * @return List<BdLineInfoDTO>
     */
    List<BdLineInfoDTO> getLineInfo();//


    /**
     * 获取用户信息.
     *
     * @return List<BdStaffDTO>
     */
    List<BdStaffDTO> getAllInfoByStaffTable();

    /**
     * excel导出
     *
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param roleType   角色类型
     * @param staffID    用户id
     * @return List<ExcelExportDTO>
     */
    List<ExcelExportDTO> exportExcel(String start_time, String end_time, String line_id, String roleType, String staffID);

    /**
     * @param id 数据行id
     * @return List<BdqueryByID>
     */
    List<BdqueryByID> queryByID(String id);

    /**
     * @param id 数据行id
     * @return List<BdOperatePlanDeclarationFormMonth>
     */
    List<BdOperatePlanDeclarationFormMonth> queryAllInfoByID(String id);

    /**
     * @param roleName 角色类型
     * @return String
     */
    String getRoleType(String roleName);

    /**
     * @param roleName 角色类型
     * @return Integer
     */
    Integer getRoleId(String roleName);

    /**
     * @param id                 数据行id
     * @param remark             ?
     * @param dispatchStaffId    负责人
     * @param changeReason       意见
     * @param lineFormStatus     线路负责人状态
     * @param dispatchFormStatus 负责人状态
     * @param formStatus         审核状态
     * @return Integer
     */
    Integer insertOperate_plan_state_change_monthByID(int id, String remark, int dispatchStaffId, String changeReason, Integer lineFormStatus, Integer dispatchFormStatus, int formStatus);

    /**
     * @param LineFormStatus     线路负责人状态
     * @param DispatchFormStatus 负责人状态
     * @param Voice              录音
     * @param Picture            图片
     * @param PlanChange         计划变更(月计划中不存在)
     * @param id                 线路id
     * @param changeReason       意见
     * @return Integer
     */
    Integer updateBd_operate_plan_declaration_form_monthByID(int LineFormStatus, int DispatchFormStatus, String Voice, String Picture, int PlanChange, String id, String changeReason);

    /**
     * 根据id查询角色类型(sys_user表中可能此功能不可用)
     *
     * @param id id
     * @return List<queryRoleTypeByIDDTO>
     */
    List<queryRoleTypeByIDDTO> queryRoleTypeByID(String id);

    /**
     * 审核按钮
     *
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param page       页面信息
     * @param roleType   角色类型
     * @param staffID    员工信息
     * @return Page<getAllByDateDTO>
     */
    Page<getAllByDateDTO> ApproveQuery(String start_time, String end_time, String line_id, Page page, String roleType, String staffID, String busId);

    /**
     * 取消按钮
     *
     * @param formStatus 审核状态
     * @param id         id
     * @param rejectedReason 取消意见
     * @return Integer
     */
    Integer cancelButton(@Param("formStatus") int formStatus, @Param("id") String id, @Param("rejectedReason") String rejectedReason);
}
