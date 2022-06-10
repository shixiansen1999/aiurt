package com.aiurt.boot.modules.repairManage.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.constant.InspectionContant;
import com.swsc.copsms.common.util.DateUtils;
import com.swsc.copsms.modules.patrol.utils.NumberGenerateUtils;
import com.swsc.copsms.modules.repairManage.entity.RepairPool;
import com.swsc.copsms.modules.repairManage.entity.RepairTask;
import com.swsc.copsms.modules.repairManage.mapper.RepairPoolMapper;
import com.swsc.copsms.modules.repairManage.mapper.RepairTaskMapper;
import com.swsc.copsms.modules.repairManage.service.IRepairPoolService;
import com.swsc.copsms.modules.standardManage.inspectionSpecification.entity.InspectionCode;
import com.swsc.copsms.modules.standardManage.inspectionSpecification.mapper.InspectionCodeMapper;
import com.swsc.copsms.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import com.swsc.copsms.modules.standardManage.inspectionStrategy.mapper.InspectionCodeContentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 检修计划池
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Service
public class RepairPoolServiceImpl extends ServiceImpl<RepairPoolMapper, RepairPool> implements IRepairPoolService {

    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;

    @Resource
    private InspectionCodeMapper inspectionCodeMapper;

    @Resource
    private RepairTaskMapper repairTaskMapper;

    @Resource
    private NumberGenerateUtils numberGenerateUtils;

    /**
     * 生成检修计划池任务
     *
     * @param inspectionCode
     * @return
     */
    public Result generateTask(InspectionCode inspectionCode) throws Exception {
        List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeContentMapper.selectListById(inspectionCode.getId().toString());
        if (inspectionCodeContentList.size() == 0) {
            return Result.error("请先设置策略");
        }
        Integer type = inspectionCodeContentList.get(0).getType();
        //周检
        if (type.equals(InspectionContant.WEEK)) {
            return weekPlan(inspectionCode, inspectionCodeContentList);
        }
        //如果不是周检，按策略来设置某周的记录
        //月检
        if (type.equals(InspectionContant.MONTH)) {
            return monthPlan(inspectionCode, inspectionCodeContentList);
        }
        //双月检
        if (type.equals(InspectionContant.DOUBLEMONTH)) {
            return doubleMonthPlan(inspectionCode, inspectionCodeContentList);
        }
        //季检
        if (type.equals(InspectionContant.QUARTER)) {
            return quarterPlan(inspectionCode, inspectionCodeContentList);
        }
        //半年检
        if (type.equals(InspectionContant.SEMIANNUAL)) {
            return semiAnnualPlan(inspectionCode, inspectionCodeContentList);
        }
        //年检
        if (type.equals(InspectionContant.ANNUAL)) {
            return annualPlan(inspectionCode, inspectionCodeContentList);
        }
        return Result.ok();
    }

    /**
     * 重新生成检修计划
     *
     * @param inspectionCode
     * @return
     */
    public Result generateReNewTask(InspectionCode inspectionCode) throws Exception {
        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
        wrapper.eq("inspection_code_id", inspectionCode.getId()).eq("del_flag", 0).ge("start_time", DateUtil.now());
        List<RepairPool> list = this.baseMapper.selectList(wrapper);
        if (list.size() == 0) {
            return Result.error("检修池任务都已完成,无法重新生成新任务");
        }
        list.forEach(x -> x.setDelFlag(1));
        this.updateBatchById(list);
        return this.generateTask(inspectionCode);
    }

    /**
     * 判断是否是今年 true是，false否
     *
     * @param years
     * @return
     */
    private boolean isThisYear(String years) {
        if (Integer.valueOf(DateUtils.getYear()).equals(Integer.valueOf(years))) {
            return true;
        }
        return false;
    }

    /**
     * 周检任务
     *
     * @param inspectionCode
     * @param inspectionCodeContentList
     * @return
     */
    private Result weekPlan(InspectionCode inspectionCode, List<InspectionCodeContent> inspectionCodeContentList) {
        //获取当前规范的年份,如果是今年就生成今年剩下的任务，如果是明年，则生成明年的所有任务
        Date date;
        if (Integer.valueOf(DateUtils.getYear()).equals(Integer.valueOf(inspectionCode.getYears()))) {
            date = DateUtils.getDate();
        } else {
            date = DateUtils.getNextYearFirstDay();
        }
        List<Date[]> list = DateUtils.yearWeekList(date);
        if (list.size() == 0) {
            return Result.error("本年度最后一周无法生成周检");
        }
        for (int x = 0; x < inspectionCodeContentList.size(); x++) {
            for (int i = 0; i < list.size(); i++) {
                addEveryWeekTask(1, inspectionCode.getType(), inspectionCodeContentList.get(x),
                        list.get(i)[0], list.get(i)[1], inspectionCode.getOrganizationIds());
            }
        }
        return Result.ok();
    }

    /**
     * 月检
     *
     * @param inspectionCode
     * @param list
     * @return
     */
    private Result monthPlan(InspectionCode inspectionCode, List<InspectionCodeContent> list) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeek();
        if (!thisYear) {
            month = 1;
        }
        for (int j = month; j <= InspectionContant.MONTHAMOUNT; j++) {
            for (int i = 0; i < list.size(); i++) {
                Integer tactics = list.get(i).getTactics();
                //如果是今年,这个月的周数 大于 策略的周数 跳出当前循环
                if (thisYear && j == month && week >= tactics) {
                    continue;
                }
                //获取j月第tactics周的时间
                Date[] date = DateUtils.getDateByMonthAndWeek(j, tactics);
                if (date == null) {
                    return Result.error("无可生成计划");
                }
                //设置每个月的第几周新增任务
                addEveryWeekTask(2, inspectionCode.getType(),list.get(i), date[0], date[1], inspectionCode.getOrganizationIds());
            }
        }
        return Result.ok();
    }

    /**
     * 双月检
     *
     * @param inspectionCode
     * @param list
     * @return
     */
    private Result doubleMonthPlan(InspectionCode inspectionCode, List<InspectionCodeContent> list) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeek();
        if (!thisYear) {
            month = 1;
        }
        //两个月两个月来操作
        for (int j = month; j <= InspectionContant.MONTHAMOUNT; j++) {
            if ((j & 1) == 0) {
                continue;
            }
            for (int i = 0; i < list.size(); i++) {
                //注意:他的策略 一个月是按4周来算的
                Integer tactics = list.get(i).getTactics();
                //计算这个策略是 第几月（-1）
                int monthnum = tactics / 5;
                //计算这个策略是 第几周
                int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;
                if (thisYear && j + monthnum == month && week >= weeknum) {
                    continue;
                }
                Date[] date = DateUtils.getDateByMonthAndWeek(j + monthnum, weeknum);
                if (date == null) {
                    return Result.error("无可生成计划");
                }
                addEveryWeekTask(3, inspectionCode.getType(), list.get(i), date[0], date[1], inspectionCode.getOrganizationIds());
            }
        }
        return Result.ok();
    }

    /**
     * 季检
     *
     * @param inspectionCode
     * @param list
     * @return
     */
    private Result quarterPlan(InspectionCode inspectionCode, List<InspectionCodeContent> list) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeek();
        if (!thisYear) {
            month = 1;
        }
        int quarter = DateUtils.getQuarter(month);
        for (int y = quarter; y < 5; y++) {
            for (int i = 0; i < list.size(); i++) {
                Integer tactics = list.get(i).getTactics();
                //计算这个策略是第几月
                int monthnum = (tactics - 1) / 4 + 1;
                //计算这个策略是第几周
                int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;

                //获取季度的开始月份
                int monthStart = (y - 1) * 3 + 1;
                if (thisYear) {
                    if (monthStart + monthnum < month) {
                        continue;
                    }
                    if (monthStart + monthnum == month && week > weeknum) {
                        continue;
                    }
                }
                Date[] date = DateUtils.getDateByMonthAndWeek(monthStart + monthnum, weeknum);
                if (date == null) {
                    return Result.error("无可生成计划");
                }
                addEveryWeekTask(4, inspectionCode.getType(), list.get(i), date[0], date[1], inspectionCode.getOrganizationIds());
            }
        }
        return Result.ok();
    }

    /**
     * 半年检
     *
     * @param inspectionCode
     * @param list
     * @return
     */
    private Result semiAnnualPlan(InspectionCode inspectionCode, List<InspectionCodeContent> list) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeek();
        if (!thisYear) {
            month = 1;
        }
        int isFirstHalfYear = 0;
        if (month > 6) {
            isFirstHalfYear = 1;
        }

        for (int y = isFirstHalfYear; y < 2; y++) {
            for (int i = 0; i < list.size(); i++) {
                Integer tactics = list.get(i).getTactics();
                //计算这个策略是第几月
                int monthnum = (tactics - 1) / 4 + 1;
                //计算这个策略是第几周
                int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;
                if (y == 1) {
                    monthnum = monthnum + 6;
                }
                if (thisYear && monthnum < month) {
                    continue;
                }
                if (thisYear && monthnum == month && week >= weeknum) {
                    continue;
                }
                Date[] date = DateUtils.getDateByMonthAndWeek(monthnum, weeknum);
                if (date == null) {
                    return Result.error("无可生成计划");
                }
                addEveryWeekTask(5, inspectionCode.getType(), list.get(i), date[0], date[1], inspectionCode.getOrganizationIds());
            }
        }
        return Result.ok();
    }

    /**
     * 年检
     *
     * @param inspectionCode
     * @param list
     * @return
     */
    private Result annualPlan(InspectionCode inspectionCode, List<InspectionCodeContent> list) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeek();
        if (!thisYear) {
            month = 1;
        }
        for (int i = 0; i < list.size(); i++) {
            Integer tactics = list.get(i).getTactics();
            //计算这个策略是第几月
            int monthnum = tactics / 5;
            //计算这个策略是第几周
            int weeknum = tactics % 4;
            if (thisYear && monthnum < month) {
                continue;
            }
            if (thisYear && monthnum == month && week >= weeknum) {
                continue;
            }
            Date[] date = DateUtils.getDateByMonthAndWeek(monthnum, weeknum);
            if (date == null) {
                return Result.error("无可生成计划");
            }
            //设置每个月的第几周新增任务
            addEveryWeekTask(6, inspectionCode.getType(), list.get(i), date[0], date[1], inspectionCode.getOrganizationIds());
        }
        return Result.ok();
    }


    /**
     * 创建每周的任务
     */
    private void addEveryWeekTask(Integer type, Integer icType, InspectionCodeContent repairPoolContent, Date startTime, Date endTime, String organizationIds) {
        String[] split = organizationIds.split(",");
        for (String organizationId : split) {
            RepairPool repairPool = new RepairPool();
            repairPool.setType(type);
            repairPool.setIcType(icType);
            repairPool.setWeeks(DateUtils.getWeekOfYear(startTime));
            repairPool.setInspectionCodeId(repairPoolContent.getInspectionCodeId());
            repairPool.setRepairPoolContent(repairPoolContent.getContent());
            repairPool.setStartTime(startTime);
            repairPool.setEndTime(endTime);
            repairPool.setStatus(0);
            repairPool.setOrganizationId(organizationId);
            this.baseMapper.insert(repairPool);
        }
    }

    @Override
    public Result assigned(String ids, String userIds, String userNames) {
        String[] split = ids.split(",");
        for (String id : split) {
            RepairPool repairPool = this.baseMapper.selectById(id);
            if (repairPool == null){
                return Result.error("非法参数");
            }
            repairPool.setRepairUserIds(userIds);
            repairPool.setStatus(1);
            this.baseMapper.updateById(repairPool);
        }

        RepairPool repairPool = this.baseMapper.selectById(split[0]);

        String codeNo = "G123.123.123";
//        try {
//            codeNo = numberGenerateUtils.getCodeNo("G213");
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }

        //添加检修单记录
        RepairTask repairTask = new RepairTask();
        repairTask.setRepairPoolIds(ids);
        repairTask.setCode(codeNo);
        repairTask.setWeeks(repairPool.getWeeks());
        repairTask.setStartTime(repairPool.getStartTime());
        repairTask.setEndTime(repairPool.getEndTime());
        repairTask.setIcType(repairPool.getIcType());
        repairTask.setStatus(0);
        repairTask.setStaffIds(userIds);
        repairTask.setStaffNames(userNames);
        repairTaskMapper.insert(repairTask);
        return Result.ok();
    }

    @Override
    public Result updateTime(String ids, String startTime, String endTime) {
        String[] split = ids.split(",");
        for (String id : split) {
            int week = DateUtils.getWeekOfYear(DateUtil.parse(startTime));
            RepairPool repairPool = this.baseMapper.selectById(id);
            repairPool.setWeeks(week);
            repairPool.setStartTime(DateUtil.parse(startTime.concat(" 00:00:00")));
            repairPool.setEndTime(DateUtil.parse(endTime.concat(" 23:59:59")));
            this.baseMapper.updateById(repairPool);
        }
        return Result.ok();
    }


    @Override
    public Result getRepairTask(String userId, String startTime, String endTime) {
        if (StrUtil.isBlank(startTime) ||StrUtil.isBlank(endTime) ){
            //获取当前周
            Date date = new Date();
            Date st = DateUtils.getWeekStartTime(date);
            Date et = DateUtils.getWeekEndTime(date);
            startTime = DateUtil.formatTime(st);
            endTime = DateUtil.formatTime(et);
        }
        //根据当前用户获取部门id
//        List<SysUserDepart> list = sysUserDepartMapper.getUserDepartByUid(userId);

        String organizationId="99999";
        //查询检修池任务
        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",0).eq("organization_id",organizationId)
                .ge("start_time",startTime).le("end_time", endTime);
        List<RepairPool> list = this.baseMapper.selectList(wrapper);
        return Result.ok(list);
    }
}
