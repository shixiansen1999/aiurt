package com.aiurt.boot.index.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.index.dto.DayTodoDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.index.mapper.IndexPlanMapper;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.mapper.RepairPoolStationRelMapper;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskUser;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.boot.task.mapper.RepairTaskUserMapper;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private InspectionManager manager;
    @Resource
    private RepairPoolStationRelMapper repairPoolStationRelMapper;
    @Resource
    private IndexPlanMapper indexPlanMapper;
    @Resource
    private RepairTaskMapper repairTaskMapper;
    @Resource
    private RepairTaskUserMapper repairTaskUserMapper;

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
        queryWrapper.ge(RepairPool::getStartTime, DateUtil.beginOfDay(startDate));
        queryWrapper.le(RepairPool::getStartTime, DateUtil.endOfDay(endDate));
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
        result.setOmit(0L);
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
     * @param taskDetailsReq 查询条件
     * @return
     */
    public IPage<TaskDetailsDTO> getOverviewInfoDetails(TaskDetailsReq taskDetailsReq) {
        IPage<TaskDetailsDTO> result = new Page<>();
        if (ObjectUtil.isEmpty(taskDetailsReq)
                || ObjectUtil.isEmpty(taskDetailsReq.getType())
                || ObjectUtil.isEmpty(taskDetailsReq.getStartTime())
                || ObjectUtil.isEmpty(taskDetailsReq.getEndTime())) {
            return result;
        }

        // 分页聚合数据
        Page<TaskDetailsDTO> page = new Page<>(taskDetailsReq.getPageNo(), taskDetailsReq.getPageSize());
        List<TaskDetailsDTO> detailsDTOList = indexPlanMapper.getGropuByData(taskDetailsReq.getType(), page, taskDetailsReq);

        // 查询出符合条件的检修详情数据
        if (CollUtil.isNotEmpty(detailsDTOList)) {
            for (TaskDetailsDTO taskDetailsDTO : detailsDTOList) {
                // 查询改站点的所有检修任务
                List<RepairPoolDetailsDTO> repairPoolDetailsDTOList = indexPlanMapper.getMainDataByStationCodeNoPage(taskDetailsDTO.getStationCode(), taskDetailsReq);

                // 补充任务状态
                taskDetailsDTO.setStatusName("已完成");
                if (CollUtil.isNotEmpty(repairPoolDetailsDTOList)) {
                    for (RepairPoolDetailsDTO repairPoolDetailsDTO : repairPoolDetailsDTOList) {
                        if (ObjectUtil.isNotEmpty(repairPoolDetailsDTO)) {
                            if (!InspectionConstant.COMPLETED.equals(repairPoolDetailsDTO.getStatus())) {
                                taskDetailsDTO.setStatusName("未完成");
                                break;
                            }
                        }
                    }
                }

                // 补充提交时间
                if (CollUtil.isNotEmpty(repairPoolDetailsDTOList)) {
                    LambdaQueryWrapper<RepairTask> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.in(RepairTask::getCode, repairPoolDetailsDTOList.stream().map(RepairPoolDetailsDTO::getCode).collect(Collectors.toList()));
                    queryWrapper.isNotNull(RepairTask::getSubmitTime);
                    queryWrapper.apply("limit 1");
                    queryWrapper.orderByDesc(RepairTask::getAssignTime);
                    RepairTask repairTask = repairTaskMapper.selectOne(queryWrapper);
                    if (ObjectUtil.isNotEmpty(repairTask)) {
                        taskDetailsDTO.setSubmitTime(repairTask.getSubmitTime());
                    }
                }

                // 补充检修人员和所属班组
                if (CollUtil.isNotEmpty(repairPoolDetailsDTOList)) {
                    List<String> codeList = repairPoolDetailsDTOList.stream().map(RepairPoolDetailsDTO::getCode).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(codeList)) {
                        LambdaQueryWrapper<RepairTaskUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                        lambdaQueryWrapper.in(RepairTaskUser::getRepairTaskCode, codeList);
                        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(lambdaQueryWrapper);

                        if (CollUtil.isNotEmpty(repairTaskUsers)) {
                            Set<String> userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toSet());
                            if (CollUtil.isNotEmpty(userIds)) {
                                String userStr = StrUtil.join(",", userIds);
                                List<JSONObject> jsonObjects = sysBaseApi.queryUsersByIds(userStr);

                                if (CollUtil.isNotEmpty(jsonObjects)) {
                                    // 检修人员名称
                                    taskDetailsDTO.setRealName(jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；")));

                                    Set<String> orgId = jsonObjects.stream().map(js -> js.getString("orgId")).collect(Collectors.toSet());
                                    if (CollUtil.isNotEmpty(orgId)) {
                                        List<JSONObject> deptList = sysBaseApi.queryDepartsByIds(StrUtil.join(",", orgId));
                                        if (CollUtil.isNotEmpty(deptList)) {
                                            taskDetailsDTO.setTeamName(deptList.stream().map(dept -> dept.getString("departName")).collect(Collectors.joining("；")));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        page.setRecords(detailsDTOList);
        return result;
    }


    /**
     * 获取首页的日代办事项
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    public List<DayTodoDTO> getUserSchedule(Integer year, Integer month) {
        List<DayTodoDTO> result = new ArrayList<>();
        if (ObjectUtil.isEmpty(year) || ObjectUtil.isEmpty(month)) {
            return result;
        }

        // 获取某年某月的开始时间
        Date beginDate = beginOfMonth(year, month);

        // 计算某年某月一共有多少天
        int dayNum = getMonthDays(year, month);

        // 故障、检修、巡检、施工,key是日期，value是数量
        Map<String, Integer> inspectionMap = new HashMap<>(32);
        Map<String, Integer> patrolMap = new HashMap<>(32);
        Map<String, Integer> faultMap = new HashMap<>(32);
        Map<String, Integer> constructionMap = new HashMap<>(32);

        // 组装数据
        if (ObjectUtil.isNotEmpty(beginDate)) {
            for (int i = 0; i < dayNum; i++) {
                // 偏移日期
                String currDateStr = DateUtil.format(DateUtil.offsetDay(beginDate, i), "yyyy/MM/dd");
                DayTodoDTO dayTodoDTO = new DayTodoDTO();
                dayTodoDTO.setCurrDate(currDateStr);
                dayTodoDTO.setConstructionNum(ObjectUtil.isEmpty(constructionMap.get(currDateStr)) ? 0 : constructionMap.get(currDateStr));
                dayTodoDTO.setFaultNum(ObjectUtil.isEmpty(faultMap.get(currDateStr)) ? 0 : faultMap.get(currDateStr));
                dayTodoDTO.setInspectionNum(ObjectUtil.isEmpty(inspectionMap.get(currDateStr)) ? 0 : inspectionMap.get(currDateStr));
                dayTodoDTO.setPatrolNum(ObjectUtil.isEmpty(patrolMap.get(currDateStr)) ? 0 : patrolMap.get(currDateStr));
                result.add(dayTodoDTO);
            }
        }

        return result;
    }

    /**
     * 计算某年某月一共有多少天
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    private int getMonthDays(Integer year, Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 获取某年某月的开始时间
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    private Date beginOfMonth(Integer year, Integer month) {
        // 获取当前分区的日历信息(这里可以使用参数指定时区)
        Calendar calendar = Calendar.getInstance();
        // 设置年
        calendar.set(Calendar.YEAR, year);
        // 设置月，月份从0开始
        calendar.set(Calendar.MONTH, month - 1);
        // 设置为指定月的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 获取指定月第一天的时间
        Date start = calendar.getTime();
        return start;
    }

    /**
     * 点击站点获取检修数据
     *
     * @param taskDetailsReq 查询条件
     * @return
     */
    public IPage<RepairPoolDetailsDTO> getMaintenancDataByStationCode(TaskDetailsReq taskDetailsReq) {
        Page<RepairPoolDetailsDTO> page = new Page<>(taskDetailsReq.getPageNo(), taskDetailsReq.getPageSize());
        if (ObjectUtil.isEmpty(taskDetailsReq.getType())
                || ObjectUtil.isEmpty(taskDetailsReq)
                || StrUtil.isEmpty(taskDetailsReq.getStationCode())) {
            return new Page<>();
        }

        List<RepairPoolDetailsDTO> maintenancDataByStationCode = indexPlanMapper.getMaintenancDataByStationCode(page, taskDetailsReq.getType(), taskDetailsReq);
        if (CollUtil.isNotEmpty(maintenancDataByStationCode)) {
            for (RepairPoolDetailsDTO repairPool : maintenancDataByStationCode) {
                String planCode = repairPool.getCode();
                // 组织机构
                repairPool.setOrgName(manager.translateOrg(repairPoolMapper.selectOrgByCode(planCode)));
                // 站点
                repairPool.setStationName(manager.translateStation(repairPoolStationRelMapper.selectStationList(planCode)));
                // 周期类型
                repairPool.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPool.getType())));
                // 状态
                repairPool.setStatusName(sysBaseApi.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repairPool.getStatus())));
                if (ObjectUtil.isNotEmpty(repairPool.getStartTime()) && ObjectUtil.isNotEmpty(repairPool.getEndTime())) {
                    repairPool.setWeekName(String.format("第%d周(%s~%s)", repairPool.getWeeks(), DateUtil.format(repairPool.getStartTime(), "yyyy/MM/dd"), DateUtil.format(repairPool.getStartTime(), "yyyy/MM/dd")));
                }
            }
        }
        return page.setRecords(maintenancDataByStationCode);
    }
}
