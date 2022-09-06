package com.aiurt.boot.index.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 首页检修接口处理类
 * @date 2022/9/515:04
 */
@Service
public class IndexPlanService {
    @Resource
    private RepairPoolMapper repairPoolMapper;

    /**
     * 首页巡视概况
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public PlanIndexDTO getOverviewInfo(Date startDate, Date endDate) {
        PlanIndexDTO result = new PlanIndexDTO();
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return result;
        }

        // 将符合条件的检修计划查出
        LambdaQueryWrapper<RepairPool> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RepairPool::getStartTime, DateUtil.beginOfDay(startDate));
        queryWrapper.eq(RepairPool::getEndTime, DateUtil.endOfDay(endDate));
        queryWrapper.eq(RepairPool::getIsManual, InspectionConstant.NO_IS_MANUAL);
        List<RepairPool> repairPoolList = repairPoolMapper.selectList(queryWrapper);

        // 检修总数
        result.setSum(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.size() : 0L);
        // 已检修数
        result.setFinish(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.stream().filter(re -> InspectionConstant.COMPLETED.equals(re.getStatus())).count() : 0L);
        // 未检修数量
        result.setUnfinish(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.stream().filter(re -> !InspectionConstant.COMPLETED.equals(re.getStatus())).count() : 0L);
        // 已检修率
        if (result.getSum() <= 0 || result.getFinish() <= 0) {
            result.setFinishRate("0%");
        } else {
            double d = new BigDecimal((double) result.getFinish() * 100 / result.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            result.setFinishRate(d + "%");
        }
        // 漏检数量

        // 漏检率
        if (result.getSum() <= 0 || result.getOmit() <= 0) {
            result.setOmitRate("0%");
        } else {
            double d = new BigDecimal((double) result.getOmit() * 100 / result.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            result.setOmitRate(d + "%");
        }
        return result;
    }

    /**
     * @param pageNo         第几页
     * @param pageSize       每页显示多少数据
     * @param type           类型：1总数2已检修3未检修4漏检
     * @param taskDetailsReq 查询条件
     * @return
     */
    public IPage<TaskDetailsDTO> getOverviewInfoDetails(Integer pageNo,
                                                        Integer pageSize,
                                                        Integer type,
                                                        TaskDetailsReq taskDetailsReq) {
        IPage<TaskDetailsDTO> result = new Page<>();
        if (ObjectUtil.isEmpty(type)) {
            return result;
        }
        return null;
    }
}
