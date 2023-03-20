package com.aiurt.modules.index.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 首页故障概况
 *
 * @author: qkx
 * @date: 2022年09月05日 15:51
 */
@EnableDataPerm
public interface FaultCountMapper extends BaseMapper<FaultIndexDTO> {
    /**
     * 故障统计
     * @param startDate
     * @param endDate
     * @param ordList
     * @return
     */
    List<Fault> queryFaultCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("ordList") List<String> ordList, @Param("majorByUserId")List<String> majorByUserId,@Param("isDirector")boolean isDirector);


    /**
     *故障等级详情分页
     * @param level
     * @param page
     * @param faultTimeoutLevelReq
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "sys_org_code"),
            @DataColumn(key = "lineName",value = "line_code"),
            @DataColumn(key = "majorName",value = "major_code"),
            @DataColumn(key = "systemName",value = "system_code"),
            @DataColumn(key = "stationName",value = "station_code")
    })
    List<FaultTimeoutLevelDTO> getFaultData(@Param("level") Integer level, @Param("page") Page<FaultTimeoutLevelDTO> page, @Param("faultTimeoutLevelReq") FaultTimeoutLevelReq faultTimeoutLevelReq,@Param("majorByUserId")List<String> majorByUserId,@Param("stationCodeList")List<String> stationCodeList,@Param("lv1Hours") Integer lv1Hours,@Param("lv2Hours") Integer lv2Hours,@Param("lv3Hours") Integer lv3Hours,@Param("userNameByRealName")List<String> userNameByRealName);

    /**
     * 故障概况统计详情(总数和已解决)分页
     * @param type
     * @param page
     * @param faultCountInfoReq
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "sys_org_code"),
            @DataColumn(key = "lineName",value = "line_code"),
            @DataColumn(key = "majorName",value = "major_code"),
            @DataColumn(key = "systemName",value = "system_code"),
            @DataColumn(key = "stationName",value = "station_code")
    })
    List<FaultCountInfoDTO> getFaultCountInfo(@Param("type") Integer type, @Param("page") Page<FaultCountInfoDTO> page, @Param("faultCountInfoReq") FaultCountInfoReq faultCountInfoReq,/**@Param("ordList") List<String> ordList*/@Param("majorByUserId")List<String> majorByUserId,@Param("stationCodeList")List<String> stationCodeList,@Param("userNameByRealName")List<String> userNameByRealName);

    /**
     * 故障概况统计详情(未解决和挂起)分页
     * @param type
     * @param page
     * @param faultCountInfoReq
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "sys_org_code"),
            @DataColumn(key = "lineName",value = "line_code"),
            @DataColumn(key = "majorName",value = "major_code"),
            @DataColumn(key = "systemName",value = "system_code"),
            @DataColumn(key = "stationName",value = "station_code")
    })
    List<FaultCountInfosDTO> getFaultCountInfos(@Param("type") Integer type, @Param("page") Page<FaultCountInfosDTO> page, @Param("faultCountInfoReq") FaultCountInfoReq faultCountInfoReq,/**@Param("ordList") List<String> ordList*/@Param("majorByUserId")List<String> majorByUserId,@Param("stationCodeList")List<String> stationCodeList,@Param("userNameByRealName")List<String> userNameByRealName);





    /**
     * 获取首页日待办事项故障完成数量
     * @param dateTime
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "f.sys_org_code")
    })
    List<Fault> getDailyFaultNum(@Param("dateTime")DateTime dateTime);

    /**
     * 待办事项故障情况
     * @param page
     * @param startDate
     * @return
     */
    List<FaultTimeoutLevelDTO> getMainFaultCondition(@Param("page") Page<FaultTimeoutLevelDTO> page, @Param("startDate") Date startDate);

}
