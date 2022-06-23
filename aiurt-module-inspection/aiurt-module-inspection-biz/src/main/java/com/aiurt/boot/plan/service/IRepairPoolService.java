package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.dto.RepairStrategyDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.Date;
import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IRepairPoolService extends IService<RepairPool> {
    /**
     * 检修计划池列表查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<RepairPool> queryList(Date startTime, Date endTime);

    /**
     * 获取时间范围和周数
     * @param year 年份
     * @return
     */
    Result getTimeInfo(Integer year);

    /**
     * 通过检修计划id查看检修标准详情
     * @param req
     * @return
     */
    List<RepairStrategyDTO> queryStandardById(RepairStrategyReq req);

    /**
     * 通过检修计划id查看详情
     * @param id
     * @return
     */
    RepairPoolDetailsDTO queryById(String id);

    /**
     * 检修计划池-调整时间
     * @param ids
     * @param startTime
     * @param endTime
     * @return
     */
    Result updateTime(String ids, String startTime, String endTime);
}
