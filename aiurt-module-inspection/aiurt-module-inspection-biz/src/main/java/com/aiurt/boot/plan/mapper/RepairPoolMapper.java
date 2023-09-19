package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.index.dto.InspectionDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.plan.dto.CodeManageDTO;
import com.aiurt.boot.plan.dto.StationPlanDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCode;
import com.aiurt.boot.plan.req.SelectPlanReq;
import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
public interface RepairPoolMapper extends BaseMapper<RepairPool> {

    /**
     * 检修计划池列表查询
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    List<RepairPool> queryList(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据检修标准原有的专业和专业子系统匹配对应关系
     *
     * @param majorCode
     * @param subSystemCode
     * @return
     */
    List<MajorDTO> queryMajorList(@Param("majorCode") Set<String> majorCode, @Param("subSystemCode") Set<String> subSystemCode);

    /**
     * 根据检修计划code查询检修标准
     *
     * @param planCode
     * @return
     */
    List<RepairPoolCode> queryStandardByCode(String planCode);

    /**
     * 根据检修计划code关联的组织机构
     *
     * @param planCode
     * @return
     */
    List<String> selectOrgByCode(String planCode);

    /**
     * 根据专业获取检修任务编码
     *
     * @param majorList
     * @return
     */
    Set<String> getCodeByMajor(List<String> majorList);

    /**
     * 检修计划总数和完成总数（带分页）
     *
     * @param page
     * @param orgCodes
     * @param item
     * @param beginDate
     * @param endDate
     * @return
     */
    List<InspectionDTO> getInspectionData(@Param("page") Page<InspectionDTO> page, @Param("orgCodes") List<String> orgCodes, @Param("item") Integer item, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,@Param("lineCode")String lineCode, @Param("stationCode")String stationCode, @Param("username")String username);

    /**
     * 今日检修（带分页）
     *
     * @param page
     * @param date
     * @param codeList
     * @return
     */
    List<InspectionDTO> getInspectionTodayData(@Param("page") Page<InspectionDTO> page, @Param("date") Date date, @Param("codeList") List<String> codeList,@Param("lineCode")String lineCode,@Param("status")Integer status,@Param("stationCode")String stationCode,@Param("username")String username);

    /**
     * 获取完成数量和未完成数量
     *
     * @param orgCode
     * @param beginDate
     * @param endDate
     * @return
     */
    PlanIndexDTO getNumByTimeAndOrgCode(@Param("orgCode") String orgCode, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,@Param("lineCode")String lineCode);

    /**
     * 检修计划总数和完成总数（不带分页）
     *
     * @param orgCodes
     * @param item
     * @param beginDate
     * @param endDate
     * @return
     */
    List<InspectionDTO> getInspectionDataNoPage(@Param("orgCodes") List<String> orgCodes, @Param("item") Integer item, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,@Param("lineCode")String lineCode);

    /**
     * 今日检修(不带分页)
     *
     * @param date
     * @param orgCodes
     * @return
     */
    List<InspectionDTO> getInspectionTodayDataNoPage(@Param("date") Date date, @Param("orgCodes") List<String> orgCodes,@Param("lineCode")String lineCode);

    /**
     * 根据检修任务code查询关联的组织机构
     *
     * @param taskCodes
     * @return
     */
    List<CodeManageDTO> selectOrgByCodes(List<String> taskCodes);

    /**
     * 根据检修任务code查询关联的站点
     *
     * @param taskCodes
     * @return
     */
    List<CodeManageDTO> selectStationList(List<String> taskCodes);
    @DataPermission({
            @DataColumn(key = "deptName",value = "t2.org_code"),
            @DataColumn(key = "stationName",value = "t3.station_code"),
            @DataColumn(key = "lineName",value = "t3.line_code"),
            @DataColumn(key = "majorName",value = "t5.major_code"),
            @DataColumn(key = "systemName",value = "t5.subsystem_code")
    })
    List<RepairPool> getList(@Param("startDate")Date startDate,@Param("endDate") Date endDate);

    /**
     * 获取符合条件的检修计划概览信息。
     *
     * @param startDate        查询的开始日期
     * @param endDate          查询的结束日期
     * @return 符合条件的检修计划列表
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "rpor.org_code"),
            @DataColumn(key = "stationName",value = "rpsr.station_code"),
            @DataColumn(key = "majorName",value = "rpc.major_code"),
            @DataColumn(key = "systemName",value = "rpc.subsystem_code")
    })
    List<RepairPool> getOverviewInfo(Date startDate, Date endDate);
    /**
     * 分页查询维修池中的维修任务。
     * @param page 分页对象，包含当前页数、每页显示数量等分页信息
     * @param selectPlanReq 查询条件对象，封装了查询所需的筛选参数，如起始时间、结束时间、状态等
     * @return 返回一个维修池任务列表，包含符合查询条件的维修任务
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "rpor.org_code"),
            @DataColumn(key = "stationName",value = "rpsr.station_code"),
            @DataColumn(key = "majorName",value = "rpc.major_code"),
            @DataColumn(key = "systemName",value = "rpc.subsystem_code")
    })
    List<RepairPool> selectRepairPool(@Param("page")Page<RepairPool> page, @Param("selectPlanReq") SelectPlanReq selectPlanReq);

    /**
     * 检修计划站点信息列表
     * @param page 分页对象
     * @param userId 用户id
     * @return 返回一个检修计划站点信息列表
     */
    List<StationPlanDTO> queryPlanStationList(@Param("page")Page<StationPlanDTO> page,@Param("userId")String userId);

    /**
     * 检修计划-统计部门站点的数（未完成、已完成、计划数）
     * @param selectPlanReq 查询条件
     * @return List<RepairPool> queryPlanOrgList
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "rpor.org_code")
    })
    List<RepairPool> queryPlanOrgList(@Param("selectPlanReq")SelectPlanReq selectPlanReq);

    /**
     * 检修计划站点信息列表(中心班组)
     * @param page 分页对象
     * @param stationCodeList 站点集合
     * @return  List<StationPlanDTO> queryCenterPlanStationList
     */
    List<StationPlanDTO> queryCenterPlanStationList(@Param("page")Page<StationPlanDTO> page, @Param("stationCodeList")String[] stationCodeList);

    /**
     * 根据计划编码查询关联的设备code集合
     * @param repairPoolCode
     * @return
     */
    String queryDeviceNameByPoolCode(@Param("repairPoolCode") String repairPoolCode);
}
