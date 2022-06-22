package com.aiurt.boot.plan.service.impl;

import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.entity.inspection.plan.RepairPool;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
            List<String> majorStr = new ArrayList<>();
            // 子系统

            // 组织机构

            // 站点

            // 周期类型
            repair.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repair.getType())));
            // 状态
            repair.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repair.getStatus())));
        });

        return repairPoolList;
    }
}
