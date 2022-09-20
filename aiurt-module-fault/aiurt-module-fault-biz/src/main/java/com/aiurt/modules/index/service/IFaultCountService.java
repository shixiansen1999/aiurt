package com.aiurt.modules.index.service;

import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.modules.fault.dto.*;
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
     * 首页故障统计详情(故障总数和已解决)
     * @param faultCountInfoReq
     * @return
     */
    IPage<FaultCountInfoDTO> getFaultCountInfo(FaultCountInfoReq faultCountInfoReq);


    /**
     * 首页故障统计详情(未解决和挂起数)
     * @param faultCountInfoReq
     * @return
     */
    IPage<FaultCountInfosDTO> getFaultCountInfos(FaultCountInfoReq faultCountInfoReq);

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
