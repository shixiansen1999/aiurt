package com.aiurt.boot.strategy.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.boot.strategy.mapper.InspectionStrategyMapper;
import com.aiurt.boot.strategy.service.IInspectionStrategyService;
import com.aiurt.common.constant.InspectionContant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class InspectionStrategyServiceImpl extends ServiceImpl<InspectionStrategyMapper, InspectionStrategy> implements IInspectionStrategyService {


    @Resource
    private StrategyService strategyService;

    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    @Override
    public Result addAnnualPlan(String id) {
        // 校验
        InspectionStrategy ins = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(ins)) {
            return Result.error("非法操作");
        }
        if (ins.getYear() < DateUtil.year(new Date())) {
            return Result.error("只能生成当前往后年份的计划");
        }

        List<InspectionCode> arr = new ArrayList<>();
        String code = ins.getCode();

        // 根据检修类型查询调用不同的方法
        Integer type = ins.getType();
        arr.forEach(inspectionCode -> {
            //周检
            if (type.equals(InspectionContant.WEEK)) {
                strategyService.weekPlan(ins, inspectionCode);
            }

            //月检
            if (type.equals(InspectionContant.MONTH)) {
                strategyService.monthPlan(ins, inspectionCode);
            }

            //双月检
            if (type.equals(InspectionContant.DOUBLEMONTH)) {
                strategyService.doubleMonthPlan(ins, inspectionCode);
            }

            //季检
            if (type.equals(InspectionContant.QUARTER)) {
                strategyService.quarterPlan(ins, inspectionCode);
            }

            //半年检
            if (type.equals(InspectionContant.SEMIANNUAL)) {
                strategyService.semiAnnualPlan(ins, inspectionCode);
            }

            //年检
            if (type.equals(InspectionContant.ANNUAL)) {
                strategyService.annualPlan(ins, inspectionCode);
            }
        });

        // 更新是否生成年计划状态
        return Result.OK("年计划生成成功");
    }

    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    @Override
    public Result addAnnualNewPlan(String id) {
        InspectionStrategy inspectionStrategy = new InspectionStrategy();
        return null;
    }
}
