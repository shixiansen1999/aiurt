package com.aiurt.modules.index.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FaultCountMapper extends BaseMapper<FaultIndexDTO> {
    List<Fault> queryFaultCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    /**
     *故障等级详情分页
     * @param level
     * @param page
     * @param faultTimeoutLevelReq
     * @return
     */
    List<FaultTimeoutLevelDTO> getFaultData(@Param("level") Integer level, @Param("page") Page<FaultTimeoutLevelDTO> page, @Param("faultTimeoutLevelReq") FaultTimeoutLevelReq faultTimeoutLevelReq);

    /**
     *故障概况统计详情(总数和已解决)分页
     * @param type
     * @param page
     * @param faultCountInfoReq
     * @return
     */
    List<FaultCountInfoDTO> getFaultCountInfo(@Param("type") Integer type, @Param("page") Page<FaultCountInfoDTO> page, @Param("faultCountInfoReq") FaultCountInfoReq faultCountInfoReq);

    /**
     *故障概况统计详情(未解决和挂起)分页
     * @param type
     * @param page
     * @param faultCountInfoReq
     * @return
     */
    List<FaultCountInfosDTO> getFaultCountInfos(@Param("type") Integer type, @Param("page") Page<FaultCountInfosDTO> page, @Param("faultCountInfoReq") FaultCountInfoReq faultCountInfoReq);





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
