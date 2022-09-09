package com.aiurt.modules.index.service;

import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelReq;
import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public interface IFaultCountService{
    /**
     * 首页故障统计
     * @param startDate
     * @param endDate
     * @return
     */
    FaultIndexDTO queryFaultCount(Date startDate, Date endDate);

    /**
     * 故障超时等级详情分页
     * @param faultTimeoutLevelReq
     * @return
     */
    IPage<FaultTimeoutLevelDTO> getFaultLevelInfo(FaultTimeoutLevelReq faultTimeoutLevelReq);


    /**
     * 日待办故障详情
     * @param page
     * @param startDate
     * @return
     */
    IPage<FaultTimeoutLevelDTO> getMainFaultCondition(Page<FaultTimeoutLevelDTO> page, Date startDate);

}
