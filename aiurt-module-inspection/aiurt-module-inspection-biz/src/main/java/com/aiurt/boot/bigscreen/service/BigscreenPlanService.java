package com.aiurt.boot.bigscreen.service;

import com.aiurt.boot.index.dto.InspectionDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1316:58
 */
@Service
public class BigscreenPlanService {

    /**
     * 获取大屏的检修概况数量
     *
     * @param lineCode 线路code
     * @param type   类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public PlanIndexDTO getOverviewInfo(String lineCode, Integer type) {
        return null;
    }

    /**
     * 功能：巡检修数据分析->检修数据统计
     *
     * @param lineCode 线路code
     * @param type   类型:1：本周，2：上周，3：本月， 4：上月
     * @param item   1计划数，2完成数，3漏检数，4今日检修数
     * @return
     */
    public List<InspectionDTO> getInspectionData(String lineCode, Integer type, Integer item) {
        return null;
    }
}
