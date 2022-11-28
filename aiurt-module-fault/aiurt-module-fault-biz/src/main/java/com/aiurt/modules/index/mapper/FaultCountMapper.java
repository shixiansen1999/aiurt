package com.aiurt.modules.index.mapper;

import cn.hutool.core.date.DateTime;
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
    List<FaultTimeoutLevelDTO> getFaultData(@Param("level") Integer level, @Param("page") Page<FaultTimeoutLevelDTO> page, @Param("faultTimeoutLevelReq") FaultTimeoutLevelReq faultTimeoutLevelReq,@Param("majorByUserId")List<String> majorByUserId,@Param("isDirector")boolean isDirector);

    /**
     * 故障概况统计详情(总数和已解决)分页
     * @param type
     * @param page
     * @param faultCountInfoReq
     * @param ordList
     * @return
     */
    List<FaultCountInfoDTO> getFaultCountInfo(@Param("type") Integer type, @Param("page") Page<FaultCountInfoDTO> page, @Param("faultCountInfoReq") FaultCountInfoReq faultCountInfoReq,@Param("ordList") List<String> ordList);

    /**
     * 故障概况统计详情(未解决和挂起)分页
     * @param type
     * @param page
     * @param faultCountInfoReq
     * @param ordList
     * @return
     */
    List<FaultCountInfosDTO> getFaultCountInfos(@Param("type") Integer type, @Param("page") Page<FaultCountInfosDTO> page, @Param("faultCountInfoReq") FaultCountInfoReq faultCountInfoReq,@Param("ordList") List<String> ordList);





    /**
     * 获取首页日待办事项故障完成数量
     * @param dateTime
     * @return
     */
    List<Fault> getDailyFaultNum(@Param("dateTime")DateTime dateTime);

    /**
     * 待办事项故障情况
     * @param page
     * @param startDate
     * @return
     */
    List<FaultTimeoutLevelDTO> getMainFaultCondition(@Param("page") Page<FaultTimeoutLevelDTO> page, @Param("startDate") Date startDate);

}
