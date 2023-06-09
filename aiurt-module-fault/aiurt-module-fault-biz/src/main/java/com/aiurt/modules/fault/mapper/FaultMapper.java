package com.aiurt.modules.fault.mapper;

import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.PortraitTaskModel;
import org.jeecg.common.system.vo.RadarAptitudeModel;
import org.jeecg.common.system.vo.RadarNumberModel;
import org.jeecg.common.system.vo.RadarPerformanceModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
public interface FaultMapper extends BaseMapper<Fault> {

    /**
     * 根据编码查询故障工单
     *
     * @param code
     * @return
     */
    Fault selectByCode(@Param("code") String code);

    /**
     * 根据专业编码查询作业类型
     *
     * @param majorCode
     * @return
     */
    List<CsWork> queryCsWorkByMajorCode(@Param("majorCode") String majorCode);


    /**
     * 查询匹配的 故障解决方案
     *
     * @param faultKnowledgeBase
     * @return
     */
    List<String> queryKnowledge(FaultKnowledgeBase faultKnowledgeBase);

    /**
     * 分页查询
     *
     * @param page
     * @param knowledgeBase
     * @return
     */
    List<FaultKnowledgeBase> pageList(Page<FaultKnowledgeBase> page, @Param("condition") FaultKnowledgeBase knowledgeBase);


    /**
     * 故障发生次数列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "f.sys_org_code"),
            @DataColumn(key = "majorName", value = "f.major_code"),
            @DataColumn(key = "systemName", value = "f.sub_system_code"),
            @DataColumn(key = "lineName", value = "f.line_code"),
            @DataColumn(key = "stationName", value = "f.station_code")
    })
    List<FaultFrequencyDTO> selectBySubSystemCode(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 翻译专业信息
     *
     * @param codeList
     * @return
     */
    List<String> translateMajors(List<String> codeList);


    /**
     * 翻译专业子系统信息
     *
     * @param codeList
     * @return
     */
    List<String> translateSubsystems(List<String> codeList);

    /**
     * 获取线路名
     *
     * @param stationCode
     * @return
     */
    String getStationName(String stationCode);

    /**
     * 获取维修状态名
     *
     * @param status
     * @return
     */
    String getStatusName(Integer status);

    /**
     * 查询人员账号信息
     *
     * @param roleCodeList
     * @return
     */
    List<String> selectUserNameByComplex(@Param("roleCodeList") List<String> roleCodeList, @Param("majorCode") String majorCode,
                                         @Param("subSystemCode") String subSystemCode, @Param("stationCode") String stationCode,
                                         @Param("sysOrgCode") String sysOrgCode);


    /**
     * 故障选择查询
     *
     * @param page
     * @param condition
     * @param faultCodes
     * @return List<Fault>
     */
    List<FaultDTO> getFault(@Param("page") Page<FaultDTO> page, @Param("condition") FaultDTO condition, @Param("faultCodes") List<String> faultCodes);

    /**
     * 获取设备名称
     *
     * @param code
     * @return
     */
    List<String> getDeviceName(@Param("code") String code);

    /**
     * 获取近五年的故障任务数据
     *
     * @param username
     * @param fiveYearsAgo
     * @param thisYear
     * @param faultStatus
     * @return
     */
    List<PortraitTaskModel> getFaultTaskNumber(@Param("username") String username,
                                               @Param("fiveYearsAgo") int fiveYearsAgo,
                                               @Param("thisYear") int thisYear,
                                               @Param("faultStatus") Integer faultStatus);


    /**
     * 查询管理的部门的所有人员
     *
     * @param date 当前时间
     * @param userId 用户id
     * @return
     */
    List<RecPersonListDTO> getManagedDepartmentUsers(@Param("date") Date date,@Param("userId")String userId);

    /**
     * 用户是否处理了相同的故障映射表
     * @param userNames
     * @param knowledgeId
     * @return
     */
    List<SameFaultDTO> isSameFaultHandled(@Param("userNames") List<String> userNames,@Param("knowledgeId")String knowledgeId);

    /**
     * 查询人员的任务情况
     * @param userNames
     * @return
     */
    List<SameFaultDTO> taskSituation(List<String> userNames);

    /**
     * 根据用户id获取故障处理总次数的最大值和最小值
     * @param userNames
     * @return
     */
    CommonMaxMinNumDTO getHandleNumberMaxAndMin(List<String> userNames);

    /**
     * 根据用户id获取用户故障处理的平均响应时间和平均解决时间
     * @param userNames
     * @return
     */
    List<EfficiencyDTO> getEfficiency(List<String> userNames);

    /**
     * 根据用户id获取工龄最大值和最小值
     * @param date
     * @param userIds
     * @return
     */
    CommonMaxMinNumDTO getUserExperienceRange(Date date,List<String> userIds);

    /**
     * 获取用户对应的故障处理总次数
     * @param userNames
     * @return
     */
    List<RadarNumberModel> getHandleNumber(List<String> userNames);

    /**
     * 获取用户对应的工龄
     * @param userIds
     * @return
     */
    List<RadarNumberModel> getUserExperienceList(List<String> userIds);

    /**
     * 雷达图-获取用户的资质信息
     *
     * @param userIds
     * @return
     */
    List<RadarAptitudeModel> getAptitude(List<String> userIds);
    /**
     * 获取用户的资质信息的最大值和最小值
     *
     * @param userIds
     * @return
     */
    CommonMaxMinNumDTO getAptitudeMaxAndMin(List<String> userIds);

    /**
     * 获取用户的绩效信息
     * @param date
     * @param userIds
     * @return
     */
    List<RadarPerformanceModel> getPerformance(@Param("date") Date date,@Param("userIds") List<String> userIds);
}
