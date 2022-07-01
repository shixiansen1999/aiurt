package com.aiurt.boot.strategy.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolStationRel;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.strategy.entity.InspectionStrOrgRel;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.boot.strategy.mapper.*;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/3011:54
 */
@Component
public class StrategyService {

    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private InspectionManager manager;
    @Resource
    private RepairPoolRelMapper relMapper;
    @Resource
    private RepairPoolMapper repairPoolMapper;
    @Resource
    private RepairPoolCodeMapper repairPoolCodeMapper;
    @Resource
    private RepairPoolStationRelMapper repairPoolStationRelMapper;
    @Resource
    private RepairPoolOrgRelMapper orgRelMapper;
    @Resource
    private InspectionStrategyMapper inspectionStrategyMapper;
    @Resource
    private RepairPoolDeviceRelMapper repairPoolDeviceRel;
    @Resource
    private RepairPoolCodeContentMapper repairPoolCodeContentMapper;
    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;
    @Resource
    private InspectionCodeMapper inspectionCodeMapper;
    @Resource
    private InspectionStrDeviceRelMapper inspectionCodeContent;
    @Resource
    private InspectionStrOrgRelMapper inspectionStrOrgRelMapper;
    @Resource
    private InspectionStrStaRelMapper inspectionStrStaRelMapper;
    @Resource
    private InspectionStrRelMapper inspectionStrRelMapper;
    /**
     * 周检
     *
     * @param param
     * @return
     */
    public void weekPlan(InspectionStrategy ins, InspectionCode param) {
        //获取当前规范的年份,如果是今年就生成今年剩下的任务，如果是明年，则生成明年的所有任务
        Date date;
        if (Integer.valueOf(DateUtils.getYear()).equals(Integer.valueOf(ins.getYear()))) {
            date = DateUtils.getDate();
        } else {
            date = DateUtils.getNextYearFirstDay();
        }

        //生成时间限制
        List<Date[]> list = DateUtils.yearWeekList(date);
        if (CollUtil.isEmpty(list)) {
            throw new AiurtBootException("本年度最后一周无法生成周检");
        }
        // 站点信息


        // 组织机构
        List<InspectionStrOrgRel> inspectionStrOrgRels = inspectionStrOrgRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrOrgRel>()
                        .eq(InspectionStrOrgRel::getInspectionStrCode, ins.getCode())
                        .eq(InspectionStrOrgRel::getDelFlag, 0));
        if(CollUtil.isEmpty(inspectionStrOrgRels)){
            throw new AiurtBootException("请选择组织结构");
        }
        RepairPoolStationRel repairPoolStationRel = new RepairPoolStationRel();

        repairPoolStationRelMapper.insert(repairPoolStationRel);

        // 判断是协作还是独立模式

//        for (int i = 0; i < list.size(); i++) {
//            addEveryWeekTask(1,  inspectionCodeContent,
//                    list.get(i)[0], list.get(i)[1], inspectionCode.getOrganizationIds());
//        }
    }

    /**
     * 月检
     *
     * @param param
     * @return
     */
    public void monthPlan(InspectionStrategy ins, InspectionCode param) {

    }

    /**
     * 双月检
     *
     * @param param
     * @return
     */
    public void doubleMonthPlan(InspectionStrategy ins, InspectionCode param) {

    }

    /**
     * 季检
     *
     * @param param
     * @return
     */
    public void quarterPlan(InspectionStrategy ins, InspectionCode param) {

    }

    /**
     * 半年检
     *
     * @param param
     * @return
     */
    public void semiAnnualPlan(InspectionStrategy ins, InspectionCode param){
    }

    /**
     * 年检
     * d
     *
     * @param param
     * @return
     */
    public void annualPlan(InspectionStrategy ins, InspectionCode param) {

    }


    /**
     * 创建每周的任务
     */
    private void addEveryWeekTask(Integer type, InspectionCodeContent inspectionCodeContent, Date startTime, Date endTime, String organizationIds) {
        String[] split = organizationIds.split(",");
        for (String organizationId : split) {
            RepairPool repairPool = new RepairPool();
            repairPool.setType(type);
            repairPool.setWeeks(DateUtils.getWeekOfYear(endTime));
            repairPool.setStartTime(startTime);
            repairPool.setEndTime(endTime);

            repairPoolMapper.insert(repairPool);
        }
    }

}
