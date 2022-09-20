package com.aiurt.boot.bigscreen.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.index.dto.InspectionDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.index.dto.TeamPortraitDTO;
import com.aiurt.boot.index.dto.TeamWorkingHourDTO;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.CodeManageDTO;
import com.aiurt.boot.plan.dto.StationDTO;
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
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
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
    public PlanIndexDTO getOverviewInfo(String lineCode, String type) {
        PlanIndexDTO result = new PlanIndexDTO();

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);

        if (time.length > 0) {
            // 根据自身管理专业和传入线路过滤出班组
            List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);

            // 筛选出来的班组为空，则直接返回
            if (CollUtil.isEmpty(orgCodes)) {
                result.setSum(0L);
                result.setFinish(0L);
                result.setOmit(0L);
                result.setTodayFinish(0L);
                return result;
            }

            // 根据传入的进行时间过滤
            List<InspectionDTO> inspectionDataNoPage = repairPoolMapper.getInspectionDataNoPage(orgCodes, null, time[0], time[1]);

            // 填充计划检修数
            result.setSum(CollUtil.isNotEmpty(inspectionDataNoPage) ? inspectionDataNoPage.size() : 0L);

            // 填充检修完成数
            result.setFinish(CollUtil.isNotEmpty(inspectionDataNoPage) ? inspectionDataNoPage.stream().filter(re -> InspectionConstant.COMPLETED.equals(re.getStatus())).count() : 0L);

            // 填充漏检数
            result.setOmit(0L);

            // 填充今日检修数
            List<InspectionDTO> todayInspectionNum = repairPoolMapper.getInspectionTodayDataNoPage(new Date(), orgCodes);
            result.setTodayFinish(CollUtil.isNotEmpty(todayInspectionNum) ? todayInspectionNum.size() : 0L);
        }
        return result;
    }


    /**
     * 功能：巡检修数据分析->检修数据统计(带分页)
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @param item     1计划数，2完成数，3漏检数，4今日检修数
     * @param page     分页参数
     * @return
     */
    public IPage<InspectionDTO> getInspectionDataPage(String lineCode, String type, Integer item, Page<InspectionDTO> page) {
        List<InspectionDTO> result = new ArrayList<>();

        // 校验,必填字段为空则直接返回
        if (StrUtil.isEmpty(type)) {
            return page;
        }

        // 默认查询的是计划总数
        if (ObjectUtil.isEmpty(item)) {
            item = InspectionConstant.PLAN_TOTAL_1;
        }

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);
        if (time.length > 0) {
            List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);
            // 通过传入的线路和自身管理的专业没有查询到班组，则直接返回
            if (CollUtil.isEmpty(orgCodes)) {
                return page;
            }

            // 查询计划数、完成数
            if (InspectionConstant.PLAN_TOTAL_1.equals(item) || InspectionConstant.PLAN_FINISH_2.equals(item)) {
                result = repairPoolMapper.getInspectionData(page, orgCodes, item, time[0], time[1]);
            }

            // TODO 漏检
            // 查询今日检修
            if (InspectionConstant.PLAN_TODAY_4.equals(item)) {
                result = repairPoolMapper.getInspectionTodayData(page, new Date(), orgCodes);
            }

            // 统一处理结果
            if (CollUtil.isNotEmpty(result)) {
                handleResult(result);
            }

        }
        return page.setRecords(result);
    }

    /**
     * 功能：巡检修数据分析->检修数据统计（不带分页）
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @param item     1计划数，2完成数，3漏检数，4今日检修数
     * @return
     */
    public List<InspectionDTO> getInspectionDataNoPage(String lineCode, String type, Integer item) {
        List<InspectionDTO> result = new ArrayList<>();

        // 校验,必填字段为空则直接返回
        if (StrUtil.isEmpty(type)) {
            return result;
        }

        // 默认查询的是计划总数
        if (ObjectUtil.isEmpty(item)) {
            item = InspectionConstant.PLAN_TOTAL_1;
        }

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);

        if (time.length > 0) {
            List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);
            if (CollUtil.isEmpty(orgCodes)) {
                return result;
            }

            // 填充计划数、完成数
            if (InspectionConstant.PLAN_TOTAL_1.equals(item) || InspectionConstant.PLAN_FINISH_2.equals(item)) {
                result = repairPoolMapper.getInspectionDataNoPage(orgCodes, item, time[0], time[1]);
            }

            // TODO 漏检
            // 填充今日检修
            if (InspectionConstant.PLAN_TODAY_4.equals(item)) {
                result = repairPoolMapper.getInspectionTodayDataNoPage(new Date(), orgCodes);
            }

            // 统一处理结果
            if (CollUtil.isNotEmpty(result)) {
                long start = System.currentTimeMillis();

                handleResult(result);
                long end = System.currentTimeMillis();
                System.out.println(end - start);
            }
        }
        return result;
    }

    /**
     * 处理检修数据统计结果
     *
     * @param result
     */
    public void handleResult(List<InspectionDTO> result) {
        if (CollUtil.isNotEmpty(result)) {
            // 任务状态字典
            Map<String, String> taskStateMap = Optional.ofNullable(sysBaseAPI.getDictItems(DictConstant.INSPECTION_TASK_STATE)).orElse(CollUtil.newArrayList()).stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));

            // 检修状态
            List<String> taskCodes = result.stream().map(InspectionDTO::getCode).collect(Collectors.toList());

            // 组织机构
            Map<String, List<String>> orgMap = new HashMap<>(64);
            List<CodeManageDTO> orgList = repairPoolMapper.selectOrgByCodes(taskCodes);
            if (CollUtil.isNotEmpty(orgList)) {
                orgMap = orgList.stream().collect(Collectors.toMap(CodeManageDTO::getCode, CodeManageDTO::getList));
            }
            Map<String, List<String>> finalOrgMap = orgMap;

            // 站点
            Map<String, List<StationDTO>> staMap = new HashMap<>(64);
            List<CodeManageDTO> staList = repairPoolMapper.selectStationList(taskCodes);
            if (CollUtil.isNotEmpty(orgList)) {
                staMap = staList.stream().collect(Collectors.toMap(CodeManageDTO::getCode, CodeManageDTO::getStationDTOS));
            }
            Map<String, List<StationDTO>> finalStaMap = staMap;

            // 检修人
            Map<String, List<RepairTaskUser>> userMap = new HashMap<>(64);
            List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskUser>()
                            .in(RepairTaskUser::getRepairTaskCode, taskCodes)
                            .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(repairTaskUsers)) {
                 userMap = repairTaskUsers.stream().collect(Collectors.groupingBy(RepairTaskUser::getRepairTaskCode));
            }
            Map<String, List<RepairTaskUser>> finalUserMap = userMap;

            // 并行流处理
            result.parallelStream().forEach(inspectionDTO -> {
                // 填充组织机构
                if (MapUtil.isNotEmpty(finalOrgMap)) {
                    inspectionDTO.setTeamName(manager.translateOrg(finalOrgMap.get(inspectionDTO.getCode())));
                }

                // 填充站点
                inspectionDTO.setStationName(manager.translateStation(finalStaMap.get(inspectionDTO.getCode())));

                // 翻译状态
                if (ObjectUtil.isNotEmpty(inspectionDTO.getStatus())) {
                    inspectionDTO.setStatusName(taskStateMap.get(String.valueOf(inspectionDTO.getStatus())));
                }

                // 填充检修任务
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

                    // 填充检修时间，无审核拿提交时间，有审核拿审核时间
                    List<Date> inspectionTime = repairTaskMapper.getTaskInspectionTime(inspectionDTO.getCode());

                    // 29日 12：23
                    inspectionDTO.setTime(CollUtil.isNotEmpty(inspectionTime) ? DateUtil.format(inspectionTime.get(0), "dd日 HH:mm") : "");

                    // 填充检修人
                    List<LoginUser> loginUsers = sysBaseAPI.queryAllUserByIds(Optional.ofNullable(finalUserMap.get(inspectionDTO.getCode())).orElse(CollUtil.newArrayList()).stream().map(RepairTaskUser::getUserId).toArray(String[]::new));
                    if (CollUtil.isNotEmpty(loginUsers)) {
                        inspectionDTO.setRealName(loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining("；")));
                    }
                }
            });
        }
    }

    /**
     * 功能：巡检修数据分析->检修任务完成情况
     * 检修任务完成情况默认只是查询的时间范围是本周
     *
     * @param lineCode 线路code
     * @return
     */
    public List<PlanIndexDTO> getTaskCompletion(String lineCode) {
        List<PlanIndexDTO> result = Collections.synchronizedList(new ArrayList<>());

        // 默认是本周的时间范围
        Date[] time = getTimeByType(InspectionConstant.THIS_WEEK_1);

        if (time.length > 0) {
            // 通过传入线路和自身专业过滤出班组详细信息
            List<SysDepartModel> teamBylineAndMajors = sysBaseAPI.getTeamBylineAndMajors(lineCode);

            if (CollUtil.isNotEmpty(teamBylineAndMajors)) {
                teamBylineAndMajors.parallelStream().forEach(teamBylineAndMajor -> {
                    PlanIndexDTO planIndexDTO = new PlanIndexDTO();

                    // 查询已完成数量、未完成数量
                    planIndexDTO = repairPoolMapper.getNumByTimeAndOrgCode(teamBylineAndMajor.getOrgCode(), time[0], time[1]);

                    // 填充班组名称
                    planIndexDTO.setTeamName(teamBylineAndMajor.getDepartName());

                    // 计算已检占比
                    if (planIndexDTO.getSum() <= 0 || planIndexDTO.getFinish() <= 0) {
                        planIndexDTO.setFinishRate("0%");
                    } else {
                        double d = new BigDecimal((double) planIndexDTO.getFinish() * 100 / planIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        planIndexDTO.setFinishRate(d + "%");
                    }

                    // 计算未检占比
                    if (planIndexDTO.getSum() <= 0 || planIndexDTO.getUnfinish() <= 0) {
                        planIndexDTO.setUnfinishRate("0%");
                    } else {
                        double d = new BigDecimal((double) planIndexDTO.getUnfinish() * 100 / planIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        planIndexDTO.setUnfinishRate(d + "%");
                    }

                    result.add(planIndexDTO);
                });
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
    private Date[] getTimeByType(String type) {
        if (StrUtil.isNotEmpty(type)) {

            // 本周
            if (InspectionConstant.THIS_WEEK_1.equals(type)) {
                Date date = new Date();
                DateTime beginDate = DateUtil.beginOfWeek(date);
                DateTime endDate = DateUtil.endOfWeek(date);
                return new Date[]{beginDate, endDate};
            }

            // 上周
            if (InspectionConstant.LAST_WEEK_2.equals(type)) {
                DateTime dateTime = DateUtil.lastWeek();
                DateTime beginDate = DateUtil.beginOfWeek(dateTime);
                DateTime endDate = DateUtil.endOfWeek(dateTime);
                return new Date[]{beginDate, endDate};
            }

            // 本月
            if (InspectionConstant.THIS_MONTH_3.equals(type)) {
                Date date = new Date();
                DateTime beginDate = DateUtil.beginOfMonth(date);
                DateTime endDate = DateUtil.endOfMonth(date);
                return new Date[]{beginDate, endDate};
            }

            // 上月
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
    public List<TeamPortraitDTO> getTeamPortrait(String type) {
        return Arrays.asList(new TeamPortraitDTO());
    }

    /**
     * 功能：班组画像-详情
     *
     * @param type 类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public Page<TeamWorkingHourDTO> getTeamPortraitDetails(String type, String teamId, Integer pageNo, Integer pageSize) {
        Page<TeamWorkingHourDTO> page = new Page<>(pageNo, pageSize);
        return page;
    }


}
