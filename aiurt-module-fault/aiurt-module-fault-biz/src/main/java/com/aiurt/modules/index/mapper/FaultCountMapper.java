package com.aiurt.modules.index.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelReq;
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
