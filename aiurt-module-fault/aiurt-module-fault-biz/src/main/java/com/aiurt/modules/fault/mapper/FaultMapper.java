package com.aiurt.modules.fault.mapper;

import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.dto.FaultForSendMessageDTO;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.dto.FaultForSendMessageDTO;
import com.aiurt.modules.fault.dto.FaultFrequencyDTO;
import com.aiurt.modules.fault.dto.RecPersonListDTO;
import com.aiurt.modules.fault.dto.SparePartReplaceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultknowledgebase.dto.DeviceAssemblyDTO;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultsparepart.entity.FaultSparePart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.PortraitTaskModel;

import java.util.Date;
import java.util.List;

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
     * @param userId 用户id
     * @return
     */
    List<RecPersonListDTO> getManagedDepartmentUsers(String userId);

    /**
     * @param deviceCode
     * @param oldSparePartCode
     * @return
     */
    SparePartReplaceDTO querySparePart(@Param("deviceCode") String deviceCode, @Param("oldSparePartCode") String oldSparePartCode);

    /**
     * 查询同种
     *
     * @param materialCode
     * @return
     */
    Long countNumBymaterialCode(@Param("materialCode") String materialCode);

    /**
     * @param newSparePartCode
     * @return
     */
    Long existDeviceAssemblyCode(@Param("newSparePartCode") String newSparePartCode);

    /**
     * 查询FaultSparePar
     *
     * @param materialCode
     * @param faultCauseSolutionIdList
     * @return
     */
    List<FaultSparePart> queryFaultSparePart(@Param("materialCode") String materialCode, @Param("faultCauseSolutionIdList") List<String> faultCauseSolutionIdList);

    /**
     * 获取指定日期下，由指定用户管理的部门用户列表
     *
     * @param date   指定日期，用于筛选符合条件的用户列表
     * @param userId 指定用户的ID，用于确定管理者身份
     * @return 符合条件的用户列表
     */
    List<RecPersonListDTO> getManagedDepartmentUsers(@Param("date") Date date, @Param("userId") String userId);

    /**
     * 用户是否处理了相同的故障映射表
     *
     * @param userNames
     * @param knowledgeId
     * @return
     */
    List<SameFaultDTO> isSameFaultHandled(@Param("userNames") List<String> userNames, @Param("knowledgeId") String knowledgeId);

    /**
     * 查询人员的任务情况
     *
     * @param userNames
     * @return
     */
    List<SameFaultDTO> taskSituation(List<String> userNames);

    /**
     * 根据用户id获取故障处理总次数的最大值和最小值
     *
     * @param userNames
     * @return
     */
    CommonMaxMinNumDTO getFaultHandleNumberMaxMin(List<String> userNames);

    /**
     * 根据用户id获取用户故障处理的平均响应时间和平均解决时间
     *
     * @param userNames
     * @return
     */
    List<EfficiencyDTO> getEfficiencyList(List<String> userNames);

    /**
     * 根据用户id获取工龄最大值和最小值
     *
     *
     *
     * @param date
     * @param userIds
     * @return
     */
    CommonMaxMinNumDTO getTenureMaxMin(Date date, List<String> userIds);

    /**
     * 获取用户对应的故障处理总次数
     *
     * @param userNameList
     * @return
     */
    List<RadarNumberDTO> getHandleNumberList(List<String> userNameList);

    /**
     * 雷达图-获取用户的资质信息
     *
     * @param userIds
     * @return
     */
    List<RadarAptitudeDTO> getAptitude(List<String> userIds);

    /**
     * 获取用户的资质信息的最大值和最小值
     *
     * @param userIds
     * @return
     */
    CommonMaxMinNumDTO getAptitudeMaxMin(List<String> userIds);

    /**
     * 获取用户的绩效信息
     *
     * @param date
     * @param userIds
     * @return
     */
    List<RadarPerformanceDTO> getPerformanceList(@Param("date") Date date, @Param("userIds") List<String> userIds);

    /**
     * 获取同种故障现象的处理次数列表
     *
     * @param userNameList 用户名列表
     * @param knowledgeId  知识id
     * @return 同种故障现象的处理次数列表
     */
    List<RadarNumberDTO> getFaultHandCountListByFaultPhenomenon(@Param("userNameList") List<String> userNameList, @Param("knowledgeId") String knowledgeId);

    /**
     * 获取同种设备类型的处理次数列表
     *
     * @param userNameList   用户名列表
     * @param deviceTypeCode 设备类型编码
     * @return 同种设备类型的处理次数列表
     */
    List<RadarNumberDTO> getFaultHandCountListByDeviceType(@Param("userNameList") List<String> userNameList, String deviceTypeCode);

    /**
     * 根据用户id集合查询角色名称
     *
     * @param userIdList 用户id集合
     * @return
     */
    List<RoleNameDTO> getRoleNameByUserIdList(List<String> userIdList);

    /**
     * 获取用户资质列表
     *
     * @param userIdList 用户ID列表
     * @return 用户资质列表
     */
    List<AptitudeDTO> getAptitudeList(List<String> userIdList);

    /**
     * 根据用户账号查询历史维修任务
     *
     * @param userName       用户账号
     * @param symptoms       故障现象
     * @param deviceTypeCode 设备类型编码
     * @return
     */
    List<FaultRecDTO> getFaultRecList(@Param("userName") String userName, @Param("symptoms") String symptoms, @Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 补充用户当前所在站点
     *
     * @param userName      用户账号
     * @param tenMinutesAgo 10分钟前的时间
     * @return
     */
    String getUserStationName(@Param("userName") String userName, @Param("tenMinutesAgo") Date tenMinutesAgo);

    /**
     * 获取站点之间的关联关系
     *
     * @return
     */
    List<StationGraphDTO> getStationGraphData();

    /**
     * 根据用户ID列表获取用户当前所在的站点代码列表
     *
     * @param userNames     用户账号列表
     * @param tenMinutesAgo 当前时间-10分钟
     * @return 用户站点代码列表，以 UserStationCodeDTO 的列表形式返回
     */
    List<UserStationCodeDTO> getUserStationCodeList(@Param("userNames") List<String> userNames, @Param("tenMinutesAgo") Date tenMinutesAgo);

    /**
     * 查询换乘站点对应的换乘编码
     *
     * @return
     */
    List<ChangeCodeDTO> getStationChangeCodeList();


    /**
     * 查询组件
     *
     * @param deviceCode
     * @param faultCauseSolutionIdList
     * @return
     */
    List<DeviceAssemblyDTO> queryDeviceAssemblyByDeviceCode(@Param("deviceCode") String deviceCode, @Param("list") List<String> faultCauseSolutionIdList);
    /**
     * 根据故障编码故障及维修记录信息
     * @param code
     * @param status
     * @param updateTime
     * @return
     */
    FaultForSendMessageDTO queryForSendMessage(@Param("code") String code, @Param("status") Integer status, @Param("updateTime") Date updateTime);

    /**
     *
     * @param warehouseCode
     * @param materialCode
     * @return
     */
    SparePartReplaceDTO queryWarehouseCodeAndNum(@Param("warehouseCode")String warehouseCode, @Param("materialCode")String materialCode);
}

