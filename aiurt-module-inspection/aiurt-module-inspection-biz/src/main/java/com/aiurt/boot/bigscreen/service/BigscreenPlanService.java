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
import com.aiurt.boot.task.entity.RepairTaskUser;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.boot.task.mapper.RepairTaskUserMapper;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    @Resource
    private RepairTaskMapper repairTaskMapper;
    @Resource
    private RepairTaskUserMapper repairTaskUserMapper;

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
            // 专业和线路过滤出班组
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
        Set<String> codeLineList = new HashSet<>();
        Set<String> codeMajorList = new HashSet<>();


        if (StrUtil.isNotEmpty(lineCode)) {
            // 站点
            List<String> stationCodeByLineCode = sysBaseAPI.getStationCodeByLineCode(lineCode);
            if (CollUtil.isNotEmpty(stationCodeByLineCode)) {
                LambdaQueryWrapper<RepairPoolStationRel> repairPoolStationRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
                repairPoolStationRelLambdaQueryWrapper.in(RepairPoolStationRel::getStationCode, stationCodeByLineCode);
                List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(repairPoolStationRelLambdaQueryWrapper);
                codeLineList.addAll(Optional.ofNullable(repairPoolStationRels).orElse(new ArrayList<>()).stream().map(RepairPoolStationRel::getRepairPoolCode).collect(Collectors.toSet()));
            }
            if (CollUtil.isEmpty(stationCodeByLineCode) || CollUtil.isEmpty(codeLineList)) {
                return new HashSet<>();
            }
        }

        List<CsUserMajorModel> majorByUserId = sysBaseAPI.getMajorByUserId(manager.checkLogin().getId());
        if (CollUtil.isNotEmpty(majorByUserId)) {
            List<String> majorList = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
            codeMajorList.addAll(repairPoolMapper.getCodeByMajor(majorList));
        }

        return codeMajorList;
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
            List<String> codeList = sysBaseAPI.getTeamBylineAndMajor(lineCode);
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
                    // 翻译状态
                    inspectionDTO.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(inspectionDTO.getStatus())));
                    // 检修任务
                    if (ObjectUtil.isNotEmpty(inspectionDTO.getWeeks())) {
                        inspectionDTO.setInspectionTask(String.format("第%d周检修", inspectionDTO.getWeeks()));
                    }
                    // 如果状态是已完成，并且存在检修单异常需要将状态改成结果异常
                    if (StrUtil.isNotEmpty(inspectionDTO.getCode())) {
                        if (InspectionConstant.COMPLETED.equals(inspectionDTO.getStatus())) {
                            // 查询该任务是否有检修单存在异常项
                            Integer num = repairTaskMapper.getTaskExceptionItem(inspectionDTO.getCode());
                            if (num > 0) {
                                inspectionDTO.setStatusName("结果异常");
                            }
                        }
                        // 检修时间，无审核拿提交时间，有审核拿审核时间
                        List<Date> inspectionTime = repairTaskMapper.getTaskInspectionTime(inspectionDTO.getCode());
                        // 29日 12：23
                        inspectionDTO.setTime(CollUtil.isNotEmpty(inspectionTime) ? DateUtil.format(inspectionTime.get(0), "dd日 HH:mm") : "");
                        // 检修人
                        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                                new LambdaQueryWrapper<RepairTaskUser>()
                                        .eq(RepairTaskUser::getRepairTaskCode, inspectionDTO.getCode())
                                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
                        if (CollUtil.isNotEmpty(repairTaskUsers)) {
                            List<LoginUser> loginUsers = sysBaseAPI.queryAllUserByIds(repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new));
                            if (CollUtil.isNotEmpty(loginUsers)) {
                                inspectionDTO.setRealName(loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining("；")));
                            }
                        }
                    }
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
        List<PlanIndexDTO> result = new ArrayList<>();
        Date[] time = getTimeByType(type);
        if (time.length > 0) {
            List<SysDepartModel> teamBylineAndMajors = sysBaseAPI.getTeamBylineAndMajors(lineCode);
            if (CollUtil.isNotEmpty(teamBylineAndMajors)) {
                for (SysDepartModel teamBylineAndMajor : teamBylineAndMajors) {
                    PlanIndexDTO planIndexDTO = new PlanIndexDTO();
                    // 已完成，未完成
                    planIndexDTO = repairPoolMapper.getNumByTimeAndOrgCode(teamBylineAndMajor.getOrgCode(), time[0], time[1]);
                    planIndexDTO.setTeamName(teamBylineAndMajor.getDepartName());
                    // 计算比例
                    // 已检占比
                    if (planIndexDTO.getSum() <= 0 || planIndexDTO.getFinish() <= 0) {
                        planIndexDTO.setFinishRate("0%");
                    } else {
                        double d = new BigDecimal((double) planIndexDTO.getFinish() * 100 / planIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        planIndexDTO.setFinishRate(d + "%");
                    }
                    // 未检占比
                    if (planIndexDTO.getSum() <= 0 || planIndexDTO.getUnfinish() <= 0) {
                        planIndexDTO.setUnfinishRate("0%");
                    } else {
                        double d = new BigDecimal((double) planIndexDTO.getUnfinish() * 100 / planIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        planIndexDTO.setUnfinishRate(d + "%");
                    }
                    result.add(planIndexDTO);
                }

            }

        }
        return result;
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
        return  Arrays.asList(new TeamPortraitDTO());
    }

    /**
     * 功能：班组画像-详情
     *
     * @param type 类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public Page<TeamWorkingHourDTO> getTeamPortraitDetails(Integer type, String teamId, Integer pageNo, Integer pageSize) {
        Page<TeamWorkingHourDTO> page = new Page<>(pageNo,pageSize);
        return page;
    }
}
