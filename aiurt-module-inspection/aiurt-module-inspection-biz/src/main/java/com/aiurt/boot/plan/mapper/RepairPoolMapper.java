package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.index.dto.InspectionDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCode;
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
     * @param majorList
     * @return
     */
    Set<String> getCodeByMajor(List<String> majorList);

    /**
     * 检修计划总数和完成总数
     * @param page
     * @param codeList
     * @param item
     * @param beginDate
     * @param endDate
     * @return
     */
    List<InspectionDTO> getInspectionData(@Param("page") Page<InspectionDTO> page,@Param("codeList") List<String> codeList,@Param("item") Integer item,@Param("beginDate") Date beginDate,@Param("endDate") Date endDate);

    /**
     * 今日检修
     * @param page
     * @param date
     * @param codeList
     * @return
     */
    List<InspectionDTO> getInspectionTodayData(@Param("page") Page<InspectionDTO> page, @Param("date") Date date, @Param("codeList") List<String> codeList);

    /**
     * 获取完成数量和未完成数量
     * @param orgCode
     * @param beginDate
     * @param endDate
     * @return
     */
    PlanIndexDTO getNumByTimeAndOrgCode(@Param("orgCode") String orgCode,@Param("beginDate") Date beginDate,@Param("endDate") Date endDate);
}
