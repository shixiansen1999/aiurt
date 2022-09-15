package com.aiurt.boot.bigscreen.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.index.dto.InspectionDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.index.dto.TeamPortraitDTO;
import com.aiurt.boot.index.dto.TeamWorkingHourDTO;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolStationRel;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.mapper.RepairPoolStationRelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1316:58
 */
@Service
public class BigscreenPlanService {

    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private RepairPoolStationRelMapper repairPoolStationRelMapper;
    @Resource
    private RepairPoolMapper repairPoolMapper;
    @Resource
    private InspectionManager manager;

    /**
     * 获取大屏的检修概况数量
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public PlanIndexDTO getOverviewInfo(String lineCode, Integer type) {
        PlanIndexDTO result = new PlanIndexDTO();

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);

        if (time.length > 0) {
            // 专业和线路过滤
            Set<String> codeList = getCodeByLineAndMajor(lineCode);
            if (CollUtil.isEmpty(codeList)) {
                result.setSum(0L);
                result.setFinish(0L);
                result.setOmit(0L);
                result.setTodayFinish(0L);
                return result;
            }

            // 时间过滤
            LambdaQueryWrapper<RepairPool> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.ge(RepairPool::getStartTime, time[0]);
            lambdaQueryWrapper.le(RepairPool::getStartTime, time[1]);
            if (CollUtil.isNotEmpty(codeList)) {
                lambdaQueryWrapper.in(RepairPool::getCode, codeList);
            }
            List<RepairPool> repairPoolCodes = repairPoolMapper.selectList(lambdaQueryWrapper);

            // 计划检修数
            result.setSum(CollUtil.isNotEmpty(repairPoolCodes) ? repairPoolCodes.size() : 0L);
            // 检修完成数
            result.setFinish(CollUtil.isNotEmpty(repairPoolCodes) ? repairPoolCodes.stream().filter(re -> InspectionConstant.COMPLETED.equals(re.getStatus())).count() : 0L);
            // 漏检数
            result.setOmit(0L);
            // 今日检修数
            LambdaQueryWrapper<RepairPool> repairPoolLambdaQueryWrapper = new LambdaQueryWrapper<>();
            Date date = new Date();

            repairPoolLambdaQueryWrapper.le(RepairPool::getStartTime, date);
            repairPoolLambdaQueryWrapper.ge(RepairPool::getEndTime, date);
            if (CollUtil.isNotEmpty(codeList)) {
                lambdaQueryWrapper.in(RepairPool::getCode, codeList);
            }
            List<RepairPool> repairPoolList = repairPoolMapper.selectList(repairPoolLambdaQueryWrapper);
            result.setTodayFinish(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.size() : 0L);
        }
        return result;
    }

    @NotNull
    public Set<String> getCodeByLineAndMajor(String lineCode) {
        Set<String> codeList = new HashSet<>();

        if (StrUtil.isNotEmpty(lineCode)) {
            // 站点
            List<String> stationCodeByLineCode = sysBaseAPI.getStationCodeByLineCode(lineCode);
            if (CollUtil.isNotEmpty(stationCodeByLineCode)) {
                LambdaQueryWrapper<RepairPoolStationRel> repairPoolStationRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
                repairPoolStationRelLambdaQueryWrapper.in(RepairPoolStationRel::getStationCode, stationCodeByLineCode);
                List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(repairPoolStationRelLambdaQueryWrapper);
                codeList.addAll(Optional.ofNullable(repairPoolStationRels).orElse(new ArrayList<>()).stream().map(RepairPoolStationRel::getRepairPoolCode).collect(Collectors.toSet()));
            }
            if (CollUtil.isEmpty(stationCodeByLineCode) || CollUtil.isEmpty(codeList)) {
                return new HashSet<>();
            }
        }

        List<CsUserMajorModel> majorByUserId = sysBaseAPI.getMajorByUserId(manager.checkLogin().getId());
        if (CollUtil.isNotEmpty(majorByUserId)) {
            List<String> majorList = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
            codeList.addAll(repairPoolMapper.getCodeByMajor(majorList));
        }
        return codeList;
    }


    /**
     * 功能：巡检修数据分析->检修数据统计
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @param item     1计划数，2完成数，3漏检数，4今日检修数
     * @return
     */
    public IPage<InspectionDTO> getInspectionData(String lineCode, Integer type, Integer item, Page<InspectionDTO> page) {

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);
        if (time.length > 0) {
            List<InspectionDTO> result = new ArrayList<>();
            Set<String> codeList = getCodeByLineAndMajor(lineCode);
            if (CollUtil.isEmpty(codeList)) {
                return page;
            }
            // 计划数、完成数
            if (InspectionConstant.PLAN_TOTAL_1.equals(item) || InspectionConstant.PLAN_FINISH_2.equals(item)) {
                result = repairPoolMapper.getInspectionData(page, codeList, item, time[0], time[1]);
            }

            // 漏检
            // 今日检修
            if (InspectionConstant.PLAN_TODAY_4.equals(item)) {
                result = repairPoolMapper.getInspectionTodayData(page, new Date(), codeList);
            }
            // 统一处理
            if (CollUtil.isNotEmpty(result)) {
                for (InspectionDTO inspectionDTO : result) {
                    // 组织机构
                    inspectionDTO.setTeamName(manager.translateOrg(repairPoolMapper.selectOrgByCode(inspectionDTO.getCode())));
                    // 站点
                    inspectionDTO.setStationName(manager.translateStation(repairPoolStationRelMapper.selectStationList(inspectionDTO.getCode())));
                    // 状态，如果状态是已完成，存在检修单异常需要将状态改成结果异常

                    inspectionDTO.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(inspectionDTO.getStatus())));
                    if(InspectionConstant.COMPLETED.equals(inspectionDTO.getStatus())){
                        // 查询该任务是否有检修单存在异常项
                        inspectionDTO.setStatusName("结果异常");
                    }
                    // 检修时间，无审核拿提交时间，有审核拿审核时间

                    // 检修人

                }

                return page.setRecords(result);
            }

        }
        return page;
    }

    /**
     * 功能：巡检修数据分析->检修任务完成情况
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public List<PlanIndexDTO> getTaskCompletion(String lineCode, Integer type) {
        return null;
    }


    /**
     * 根据类型计算开始时间和结束时间
     *
     * @param type
     * @return
     */
    private Date[] getTimeByType(Integer type) {
        if (ObjectUtil.isNotEmpty(type)) {

            if (InspectionConstant.THIS_WEEK_1.equals(type)) {
                Date date = new Date();
                DateTime beginDate = DateUtil.beginOfWeek(date);
                DateTime endDate = DateUtil.endOfWeek(date);
                return new Date[]{beginDate, endDate};
            }

            if (InspectionConstant.LAST_WEEK_2.equals(type)) {
                DateTime dateTime = DateUtil.lastWeek();
                DateTime beginDate = DateUtil.beginOfWeek(dateTime);
                DateTime endDate = DateUtil.endOfWeek(dateTime);
                return new Date[]{beginDate, endDate};
            }

            if (InspectionConstant.THIS_MONTH_3.equals(type)) {
                Date date = new Date();
                DateTime beginDate = DateUtil.beginOfMonth(date);
                DateTime endDate = DateUtil.endOfMonth(date);
                return new Date[]{beginDate, endDate};
            }

            if (InspectionConstant.LAST_MONTH_4.equals(type)) {
                DateTime dateTime = DateUtil.lastMonth();
                DateTime beginDate = DateUtil.beginOfMonth(dateTime);
                DateTime endDate = DateUtil.endOfMonth(dateTime);
                return new Date[]{beginDate, endDate};
            }
        }
        return new Date[0];
    }

    /**
     * 功能：班组画像
     *
     * @param type 类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public List<TeamPortraitDTO> getTeamPortrait(Integer type) {
        return null;
    }

    /**
     * 功能：班组画像-详情
     *
     * @param type 类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public TeamWorkingHourDTO getTeamPortraitDetails(Integer type, String teamId) {
        return null;
    }
}
