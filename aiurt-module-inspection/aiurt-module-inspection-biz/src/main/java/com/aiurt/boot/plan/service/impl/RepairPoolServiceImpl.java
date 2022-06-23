package com.aiurt.boot.plan.service.impl;

import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.dto.RepairStrategyDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairPoolServiceImpl extends ServiceImpl<RepairPoolMapper, RepairPool> implements IRepairPoolService {

    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private InspectionManager manager;

    /**
     * 检修计划池列表查询
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public List<RepairPool> queryList(Date startTime, Date endTime) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.ge("start_time", startTime);
        queryWrapper.le("end_time", endTime);
        queryWrapper.orderByAsc("type");
        List<RepairPool> repairPoolList = baseMapper.selectList(queryWrapper);

        repairPoolList.forEach(repair -> {
            // 专业
            repair.setMajorName(manager.translateMajor(new ArrayList<>(), InspectionConstant.MAJOR));
            // 子系统
            repair.setSubsystemName(manager.translateMajor(new ArrayList<>(), InspectionConstant.SUBSYSTEM));
            // 组织机构

            // 站点

            // 周期类型
            repair.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repair.getType())));
            // 状态
            repair.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repair.getStatus())));
        });

        return repairPoolList;
    }


    /**
     * 获取时间范围和周数
     *
     * @param year 年份
     * @return
     */
    @Override
    @SneakyThrows
    public Result getTimeInfo(Integer year) {
        LocalDateTime yearFirst = DateUtils.getYearFirst(year);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = yearFirst.atZone(zoneId);
        Date date = Date.from(zonedDateTime.toInstant());
        ArrayList<Object> list = DateUtils.getWeekAndTime(date);
        return Result.ok(list);
    }

    /**
     * 通过检修计划id查看检修标准详情
     *
     * @param req
     * @return
     */
    @Override
    public List<RepairStrategyDTO> queryStandardById(RepairStrategyReq req) {

        return null;
    }

    /**
     * 通过检修计划id查看详情
     *
     * @param id
     * @return
     */
    @Override
    public RepairPoolDetailsDTO queryById(String id) {
        return null;
    }
}
