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
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.mapper.RepairPoolStationRelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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
        queryWrapper.le(RepairPool::getEndTime, DateUtil.endOfDay(endDate));
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
        if (ObjectUtil.isEmpty(type)
                || ObjectUtil.isEmpty(taskDetailsReq)
                || ObjectUtil.isEmpty(taskDetailsReq.getStartTime())
                || ObjectUtil.isEmpty(taskDetailsReq.getEndTime())) {
            return result;
        }

        // 分页聚合数据
        Page<TaskDetailsDTO> page = new Page<>(pageNo, pageSize);
        List<TaskDetailsDTO> detailsDTOList = indexPlanMapper.getGropuByData(type, page, taskDetailsReq);

        // 查询出符合条件的检修详情数据
        if (CollUtil.isNotEmpty(detailsDTOList)) {
            for (TaskDetailsDTO taskDetailsDTO : detailsDTOList) {
                // 补充所属班组和任务状态

                taskDetailsDTO.setTeamName(null);

                taskDetailsDTO.setStatusName("未完成");
                if (StrUtil.isNotEmpty(taskDetailsDTO.getStatusStr())) {
                    if (StrUtil.containsOnly(taskDetailsDTO.getStatusStr(), '8', ',')) {
                        taskDetailsDTO.setStatusName("已完成");
                    }
                }

                // 补充点击站点的数据列表
                LambdaQueryWrapper<RepairPool> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.ge(RepairPool::getStartTime, DateUtil.beginOfDay(taskDetailsReq.getStartTime()));
                queryWrapper.le(RepairPool::getEndTime, DateUtil.endOfDay(taskDetailsReq.getEndTime()));
                queryWrapper.eq(RepairPool::getIsManual, InspectionConstant.NO_IS_MANUAL);

                Page<RepairPool> repairPoolPage = repairPoolMapper.selectPage(new Page<>(taskDetailsReq.getPageNo(), taskDetailsReq.getPageSize()), queryWrapper);
                if (ObjectUtil.isNotEmpty(repairPoolPage) && CollUtil.isNotEmpty(repairPoolPage.getRecords())) {
                    List<RepairPool> records = repairPoolPage.getRecords();
                    for (RepairPool repairPool : records) {
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
}
