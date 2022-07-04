package com.aiurt.boot.strategy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.plan.entity.RepairPoolStationRel;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.mapper.RepairPoolStationRelMapper;
import com.aiurt.boot.plan.req.RepairPoolCodeReq;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.strategy.entity.*;
import com.aiurt.boot.strategy.mapper.InspectionStrDeviceRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrOrgRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrStaRelMapper;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/3011:54
 */
@Component
public class StrategyService {

    @Resource
    private RepairPoolMapper repairPoolMapper;
    @Resource
    private RepairPoolStationRelMapper repairPoolStationRelMapper;
    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;
    @Resource
    private InspectionStrOrgRelMapper inspectionStrOrgRelMapper;
    @Resource
    private InspectionStrStaRelMapper inspectionStrStaRelMapper;
    @Resource
    private InspectionStrRelMapper inspectionStrRelMapper;
    @Resource
    private InspectionStrDeviceRelMapper inspectionStrDeviceRelMapper;
    @Resource
    private IRepairPoolService repairPoolService;

    /**
     * 周检
     *
     * @param ins            检修策略
     * @param inspectionCode 检修标准
     */
    public void weekPlan(InspectionStrategy ins, InspectionCode inspectionCode) {
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

        // 组织结构
        List<InspectionStrOrgRel> inspectionStrOrgRels = getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationRels = getInspectionStrStaRels(ins.getCode());

        // 检修标准项目
        List<InspectionCodeContent> inspectionCodeContentList = getInspectionCodeContentList(inspectionCode.getCode());

        // 判断是协作还是独立模式
        for (int i = 0; i < list.size(); i++) {
            addEveryWeekTask(InspectionConstant.WEEK, ins, inspectionCode, inspectionCodeContentList,
                    list.get(i)[0], list.get(i)[1], inspectionStrOrgRels, stationRels);
        }
    }


    /**
     * 月检
     *
     * @param ins            检修策略
     * @param inspectionCode 检修标准
     */
    public void monthPlan(InspectionStrategy ins, InspectionCode inspectionCode) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }

        // 组织结构
        List<InspectionStrOrgRel> inspectionStrOrgRels = getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationRels = getInspectionStrStaRels(ins.getCode());

        // 检修标准项目
        List<InspectionCodeContent> inspectionCodeContentList = getInspectionCodeContentList(inspectionCode.getCode());

        for (int j = month; j <= InspectionConstant.MONTHAMOUNT; j++) {
            Integer tactics = ins.getTactics();

            //如果是今年,这个月的周数 大于 策略的周数 跳出当前循环
            if (thisYear && j == month && week > tactics) {
                continue;
            }

            //获取j月第tactics周的时间
            Date[] date = DateUtils.getDateByMonthAndWeek(ins.getYear(), j, tactics);
            if (date == null) {
                throw new AiurtBootException("无可生成计划");
            }

            // 设置每个月的第几周新增任务
            addEveryWeekTask(InspectionConstant.MONTH, ins, inspectionCode, inspectionCodeContentList, date[0], date[1], inspectionStrOrgRels, stationRels);
        }

    }


    /**
     * 双月检
     *
     * @param ins            检修策略
     * @param inspectionCode 检修标准
     */
    public void doubleMonthPlan(InspectionStrategy ins, InspectionCode inspectionCode) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }

        // 组织结构
        List<InspectionStrOrgRel> inspectionStrOrgRels = getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationRels = getInspectionStrStaRels(ins.getCode());

        // 检修标准项目
        List<InspectionCodeContent> inspectionCodeContentList = getInspectionCodeContentList(inspectionCode.getCode());

        // 两个月两个月来操作
        for (int j = month; j <= InspectionConstant.MONTHAMOUNT; j++) {

            // 隔两个月做一次
            if ((j & 1) == 0) {
                continue;
            }

            // 注意:他的策略 一个月是按4周来算的
            Integer tactics = ins.getTactics();

            // 计算这个策略是 第几月，对于双月来说，最多是8周，就是两个月
            int monthnum = (tactics - 1) / 4;

            // 计算这个策略相对月来说是第几周
            int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;

            // 如果是今年,这个月的周数 大于 策略的周数 跳出当前循环
            if (thisYear && j + monthnum == month && week > weeknum) {
                continue;
            }

            // 某年某月某周的开始时间和结束时间
            Date[] date = DateUtils.getDateByMonthAndWeek(ins.getYear(), j + monthnum, weeknum);
            if (date == null) {
                throw new AiurtBootException("无可生成计划");
            }

            addEveryWeekTask(InspectionConstant.DOUBLEMONTH, ins, inspectionCode, inspectionCodeContentList, date[0], date[1], inspectionStrOrgRels, stationRels);
        }
    }

    /**
     * 季检
     *
     * @param ins            检修策略
     * @param inspectionCode 检修标准
     */
    public void quarterPlan(InspectionStrategy ins, InspectionCode inspectionCode) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }

        // 根据月数 获取所在的季度
        int quarter = DateUtils.getQuarter(month);

        // 组织结构
        List<InspectionStrOrgRel> inspectionStrOrgRels = getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationRels = getInspectionStrStaRels(ins.getCode());

        // 检修标准项目
        List<InspectionCodeContent> inspectionCodeContentList = getInspectionCodeContentList(inspectionCode.getCode());

        for (int y = quarter; y <= InspectionConstant.QUARTERAMOUNT; y++) {
            Integer tactics = ins.getTactics();

            // 计算这个策略是 第几月，最多有3个月，12周
            int monthnum = (tactics - 1) / 4;

            // 计算这个策略是第几周
            int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;

            // 获取季度的开始月份
            int monthStart = (y - 1) * 3 + 1;
            if (thisYear) {
                if (monthStart + monthnum < month) {
                    continue;
                }
                if (monthStart + monthnum == month && week > weeknum) {
                    continue;
                }
            }

            // 某年某月某周的开始时间和结束时间
            Date[] date = DateUtils.getDateByMonthAndWeek(ins.getYear(), monthStart + monthnum, weeknum);
            if (date == null) {
                throw new AiurtBootException("无可生成计划");
            }

            addEveryWeekTask(InspectionConstant.QUARTER, ins, inspectionCode, inspectionCodeContentList, date[0], date[1], inspectionStrOrgRels, stationRels);
        }
    }


    /**
     * 半年检
     *
     * @param ins            检修策略
     * @param inspectionCode 检修标准
     */
    public void semiAnnualPlan(InspectionStrategy ins, InspectionCode inspectionCode) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }

        // 代表上半年
        int isFirstHalfYear = 0;
        if (month > InspectionConstant.SEMIANNUALAMOUNT) {
            // 代表下半年
            isFirstHalfYear = 1;
        }

        // 组织结构
        List<InspectionStrOrgRel> inspectionStrOrgRels = getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationRels = getInspectionStrStaRels(ins.getCode());

        // 检修标准项目
        List<InspectionCodeContent> inspectionCodeContentList = getInspectionCodeContentList(inspectionCode.getCode());

        for (int y = isFirstHalfYear; y < InspectionConstant.HALFYEARAMOUNT; y++) {
            Integer tactics = ins.getTactics();

            // 计算这个策略是 第几月，最多6个月，24周
            int monthnum = (tactics - 1) / 4 + 1;

            //计算这个策略是第几周
            int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;

            // 因为是半年检，偏移6个月
            if (y == 1) {
                monthnum = monthnum + 6;
            }

            //
            if (thisYear && monthnum < month) {
                continue;
            }
            if (thisYear && monthnum == month && week > weeknum) {
                continue;
            }

            // 某年某月某周的开始时间和结束时间
            Date[] date = DateUtils.getDateByMonthAndWeek(ins.getYear(), monthnum, weeknum);
            if (date == null) {
                throw new AiurtBootException("无可生成计划");
            }
            addEveryWeekTask(InspectionConstant.SEMIANNUAL, ins, inspectionCode, inspectionCodeContentList, date[0], date[1], inspectionStrOrgRels, stationRels);
        }

    }

    /**
     * 年检
     *
     * @param ins            检修策略
     * @param inspectionCode 检修标准
     */
    public void annualPlan(InspectionStrategy ins, InspectionCode inspectionCode) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        Integer tactics = ins.getTactics();

        //计算这个策略是第几月
        int monthnum = (tactics - 1) / 4 + 1;

        //计算这个策略是第几周
        int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;

        if (thisYear && monthnum < month) {
            throw new AiurtBootException("无可生成计划");
        }
        if (thisYear && monthnum == month && week > weeknum) {
            throw new AiurtBootException("无可生成计划");
        }

        // 某年某月某周的开始时间和结束时间
        Date[] date = DateUtils.getDateByMonthAndWeek(ins.getYear(), monthnum, weeknum);
        if (date == null) {
            throw new AiurtBootException("无可生成计划");
        }

        // 组织结构
        List<InspectionStrOrgRel> inspectionStrOrgRels = getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationRels = getInspectionStrStaRels(ins.getCode());

        // 检修标准项目
        List<InspectionCodeContent> inspectionCodeContentList = getInspectionCodeContentList(inspectionCode.getCode());

        //设置每个月的第几周新增任务
        addEveryWeekTask(InspectionConstant.ANNUAL, ins, inspectionCode, inspectionCodeContentList, date[0], date[1], inspectionStrOrgRels, stationRels);

    }


    /**
     * 创建每周的任务
     *
     * @param type                      检修类型
     * @param ins                       检修策略
     * @param inspectionCode            检修标准
     * @param inspectionCodeContentList 检修检查项集合
     * @param startTime                 开始时间
     * @param endTime                   结束时间
     * @param inspectionStrOrgRels      组织机构集合
     * @param stationRels               站点集合
     */
    private void addEveryWeekTask(Integer type,
                                  InspectionStrategy ins,
                                  InspectionCode inspectionCode,
                                  List<InspectionCodeContent> inspectionCodeContentList,
                                  Date startTime,
                                  Date endTime,
                                  List<InspectionStrOrgRel> inspectionStrOrgRels,
                                  List<InspectionStrStaRel> stationRels
    ) {

        // 检修计划基本信息
        RepairPool repairPool = new RepairPool();
        repairPool.setType(type);
        repairPool.setWeeks(DateUtils.getWeekOfYear(endTime));
        repairPool.setStartTime(startTime);
        repairPool.setEndTime(endTime);
        repairPool.setIsConfirm(ins.getIsConfirm());
        repairPool.setIsOutsource(ins.getIsOutsource());
        repairPool.setIsManual(InspectionConstant.NO_IS_MANUAL);
        repairPool.setInspectionStrCode(ins.getCode());
        repairPool.setWorkType(ins.getWorkType());
        repairPool.setStatus(InspectionConstant.TO_BE_ASSIGNED);
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String jxCode = String.format("%s%s", "JX", snowflake.nextIdStr());
        repairPool.setCode(jxCode);

        // 保存检修计划
        repairPoolMapper.insert(repairPool);

        // 保存组织结构
        insertBatchOrg(inspectionStrOrgRels, jxCode);

        // 保存站点信息
        insertBatchSta(stationRels, jxCode);

        // 查询检修策略对应的检修标准绑定的设备
        List<RepairPoolCodeReq> repairPoolCodes = new ArrayList<RepairPoolCodeReq>();
        RepairPoolCodeReq repairPoolCodeReq = new RepairPoolCodeReq();
        repairPoolCodeReq.setCode(inspectionCode.getCode());
        if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice())) {

            List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(
                    new LambdaQueryWrapper<InspectionStrRel>()
                            .eq(InspectionStrRel::getInspectionStaCode, inspectionCode.getCode())
                            .eq(InspectionStrRel::getInspectionStrCode, ins.getCode()).eq(InspectionStrRel::getDelFlag, 0));

            if (CollUtil.isNotEmpty(inspectionStrRels)) {

                List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                        new LambdaQueryWrapper<InspectionStrDeviceRel>()
                                .eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRels.get(0).getId()));

                if (CollUtil.isNotEmpty(inspectionStrDeviceRels)) {
                    repairPoolCodeReq.setDeviceCodes(inspectionStrDeviceRels.stream().map(InspectionStrDeviceRel::getDeviceCode).collect(Collectors.toList()));
                }
            }
        }

        repairPoolService.handle(jxCode, repairPoolCodes);
    }


    /**
     * 批量插入组织机构
     *
     * @param inspectionStrOrgRels 组织机构集合
     * @param jxCode               检修计划单号
     */
    public void insertBatchOrg(List<InspectionStrOrgRel> inspectionStrOrgRels, String jxCode) {
        if (CollUtil.isNotEmpty(inspectionStrOrgRels) && StrUtil.isNotEmpty(jxCode)) {
            List<RepairPoolOrgRel> repairPoolOrgRels = new ArrayList<>();
            inspectionStrOrgRels.forEach(ins -> {
                RepairPoolOrgRel repairPoolOrgRel = RepairPoolOrgRel.builder()
                        .repairPoolCode(jxCode)
                        .orgCode(ins.getOrgCode())
                        .build();
                repairPoolOrgRels.add(repairPoolOrgRel);
            });
            inspectionStrOrgRelMapper.insertBatch(repairPoolOrgRels);
        }
    }

    /**
     * 批量插入站点
     *
     * @param stationRels 站点集合
     * @param jxCode      检修计划单号
     */
    public void insertBatchSta(List<InspectionStrStaRel> stationRels, String jxCode) {
        if (CollUtil.isNotEmpty(stationRels) && StrUtil.isNotEmpty(jxCode)) {
            List<RepairPoolStationRel> re = new ArrayList<>();
            stationRels.forEach(ins -> {
                RepairPoolStationRel repairPoolStationRel = new RepairPoolStationRel();
                repairPoolStationRel.setRepairPoolCode(jxCode);
                re.add(repairPoolStationRel);
            });
            repairPoolStationRelMapper.insertBatch(re);
        }
    }

    /**
     * 判断是否是今年 true是，false否
     *
     * @param years
     * @return
     */
    private boolean isThisYear(Integer years) {
        return Integer.valueOf(DateUtils.getYear()).equals(years) ? true : false;
    }

    public List<InspectionCodeContent> getInspectionCodeContentList(String code) {
        List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeContentMapper.selectList(
                new LambdaQueryWrapper<InspectionCodeContent>()
                        .eq(InspectionCodeContent::getInspectionCodeId, code)
                        .eq(InspectionCodeContent::getDelFlag, 0));
        if (CollUtil.isEmpty(inspectionCodeContentList)) {
            throw new AiurtBootException("检修标准项目为空");
        }
        return inspectionCodeContentList;
    }

    @NotNull
    public List<InspectionStrStaRel> getInspectionStrStaRels(String code) {
        List<InspectionStrStaRel> stationRels = inspectionStrStaRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrStaRel>()
                        .eq(InspectionStrStaRel::getInspectionStrCode, code)
                        .eq(InspectionStrStaRel::getDelFlag, 0));
        if (CollUtil.isEmpty(stationRels)) {
            throw new AiurtBootException("请选择组织结构");
        }
        return stationRels;
    }

    @NotNull
    public List<InspectionStrOrgRel> getInspectionStrOrgRels(String code) {
        // 组织机构
        List<InspectionStrOrgRel> inspectionStrOrgRels = inspectionStrOrgRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrOrgRel>()
                        .eq(InspectionStrOrgRel::getInspectionStrCode, code)
                        .eq(InspectionStrOrgRel::getDelFlag, 0));
        if (CollUtil.isEmpty(inspectionStrOrgRels)) {
            throw new AiurtBootException("请选择组织结构");
        }
        return inspectionStrOrgRels;
    }

}
