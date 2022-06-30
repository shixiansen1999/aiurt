package com.aiurt.boot.strategy.service.impl;

import com.aiurt.boot.strategy.entity.InspectionStrategy;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/2918:47
 */
@Service
public class AnnualPlanServiceImpl {
    /**
     * 生成年检计划
     *
     * @param ins
     * @return
     */

    public Result generateTask(InspectionStrategy ins) {

        Integer type = ins.getType();

//        //周检
//        if (type.equals(InspectionContant.WEEK)) {
//            weekPlan(inspectionCode, inspectionCodeContent);
//        }
//        //月检
//        if (type.equals(InspectionContant.MONTH)) {
//            monthPlan(inspectionCode, inspectionCodeContent);
//        }
//        //双月检
//        if (type.equals(InspectionContant.DOUBLEMONTH)) {
//            doubleMonthPlan(inspectionCode, inspectionCodeContent);
//        }
//        //季检
//        if (type.equals(InspectionContant.QUARTER)) {
//            quarterPlan(inspectionCode, inspectionCodeContent);
//        }
//        //半年检
//        if (type.equals(InspectionContant.SEMIANNUAL)) {
//            semiAnnualPlan(inspectionCode, inspectionCodeContent);
//        }
//        //年检
//        if (type.equals(InspectionContant.ANNUAL)) {
//            annualPlan(inspectionCode, inspectionCodeContent);
//        }
        return null;
    }

    /**
     * 重新生成年检计划
     *
     * @param ins
     * @return
     */
    public Result generateReNewTask(InspectionStrategy ins) {
        return null;
    }
}
