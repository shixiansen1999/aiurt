package com.aiurt.boot.strategy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
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
import com.aiurt.common.util.UpdateHelperUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private RepairPoolDeviceRelMapper repairPoolDeviceRel;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private RepairPoolCodeMapper repairPoolCodeMapper;
    @Resource
    private RepairPoolCodeContentMapper repairPoolCodeContentMapper;
    @Resource
    private RepairPoolRelMapper relMapper;

    /**
     * 周检
     *
     * @param ins         检修策略
     * @param newStaId    新的检修标准
     * @param orgList     组织机构
     * @param stationList 站点
     * @param deviceList  设备列表
     */
    public void weekPlan(InspectionStrategy ins,
                         String newStaId, List<InspectionStrOrgRel> orgList,
                         List<InspectionStrStaRel> stationList,
                         List<String> deviceList) {
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

        // 生成对应的周计划
        for (int i = 0; i < list.size(); i++) {
            addEveryWeekTask(InspectionConstant.WEEK, ins, newStaId, list.get(i)[0], list.get(i)[1], orgList, stationList, deviceList);
        }
    }


    /**
     * 月检
     *
     * @param ins         检修策略
     * @param newStaId    新的检修标准
     * @param orgList     组织机构
     * @param stationList 站点
     * @param deviceList  设备列表
     */
    public void monthPlan(InspectionStrategy ins,
                          String newStaId, List<InspectionStrOrgRel> orgList,
                          List<InspectionStrStaRel> stationList,
                          List<String> deviceList) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        // 保证最多有4周，获取当前周数
        int week = DateUtil.weekOfMonth(new Date()) == 1 ? 1 : DateUtil.weekOfMonth(new Date()) - 1;

        if (!thisYear) {
            month = 1;
        }

        for (int j = month; j <= InspectionConstant.MONTHAMOUNT; j++) {
            // 最多是4周
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
            addEveryWeekTask(InspectionConstant.MONTH, ins, newStaId, date[0], date[1], orgList, stationList, deviceList);
        }

    }


    /**
     * 双月检
     *
     * @param ins         检修策略
     * @param newStaId    新的检修标准
     * @param orgList     组织机构
     * @param stationList 站点
     * @param deviceList  设备列表
     */
    public void doubleMonthPlan(InspectionStrategy ins,
                                String newStaId, List<InspectionStrOrgRel> orgList,
                                List<InspectionStrStaRel> stationList,
                                List<String> deviceList) {
        boolean thisYear = isThisYear(ins.getYear());

        // 获取当前月
        int month = DateUtils.getMonth();

        // 获取当前周数
        int week = DateUtil.weekOfMonth(new Date()) == 1 ? 1 : DateUtil.weekOfMonth(new Date()) - 1;
        if (!thisYear) {
            month = 1;
        }

        // 两个月两个月来操作
        for (int j = month; j <= InspectionConstant.MONTHAMOUNT; j++) {

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

            addEveryWeekTask(InspectionConstant.DOUBLEMONTH, ins, newStaId, date[0], date[1], orgList, stationList, deviceList);
        }
    }

    /**
     * 季检
     *
     * @param ins         检修策略
     * @param newStaId    新的检修标准
     * @param orgList     组织机构
     * @param stationList 站点
     * @param deviceList  设备列表
     */
    public void quarterPlan(InspectionStrategy ins,
                            String newStaId, List<InspectionStrOrgRel> orgList,
                            List<InspectionStrStaRel> stationList,
                            List<String> deviceList) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        //获取当前周数
        int week = DateUtil.weekOfMonth(new Date()) == 1 ? 1 : DateUtil.weekOfMonth(new Date()) - 1;
        if (!thisYear) {
            month = 1;
        }

        // 根据月数 获取所在的季度
        int quarter = DateUtils.getQuarter(month);

        for (int y = quarter; y <= InspectionConstant.QUARTERAMOUNT; y++) {
            Integer tactics = ins.getTactics();

            // 计算这个策略是 第几月，最多有3个月(0、1、2)，12周
            int monthnum = (tactics - 1) / 4;

            // 计算这个策略相对月来说是第几周
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

            addEveryWeekTask(InspectionConstant.QUARTER, ins, newStaId, date[0], date[1], orgList, stationList, deviceList);
        }
    }

    /**
     * 半年检
     *
     * @param ins         检修策略
     * @param newStaId    新的检修标准
     * @param orgList     组织机构
     * @param stationList 站点
     * @param deviceList  设备列表
     */
    public void semiAnnualPlan(InspectionStrategy ins,
                               String newStaId, List<InspectionStrOrgRel> orgList,
                               List<InspectionStrStaRel> stationList,
                               List<String> deviceList) {
        boolean thisYear = isThisYear(ins.getYear());

        // 获取当前月
        int month = DateUtils.getMonth();

        // 获取当前周数
        int week = DateUtil.weekOfMonth(new Date()) == 1 ? 1 : DateUtil.weekOfMonth(new Date()) - 1;
        if (!thisYear) {
            month = 1;
        }

        // 代表上半年
        int isFirstHalfYear = 0;
        if (month > InspectionConstant.SEMIANNUALAMOUNT) {
            // 代表下半年
            isFirstHalfYear = 1;
        }

        for (int y = isFirstHalfYear; y < InspectionConstant.HALFYEARAMOUNT; y++) {
            Integer tactics = ins.getTactics();

            // 计算这个策略是 第几月，1开始，最多6个月，24周
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
            addEveryWeekTask(InspectionConstant.SEMIANNUAL, ins, newStaId, date[0], date[1], orgList, stationList, deviceList);
        }

    }

    /**
     * 年检
     *
     * @param ins         检修策略
     * @param newStaId    新的检修标准
     * @param orgList     组织机构
     * @param stationList 站点
     * @param deviceList  设备列表
     */
    public void annualPlan(InspectionStrategy ins,
                           String newStaId, List<InspectionStrOrgRel> orgList,
                           List<InspectionStrStaRel> stationList,
                           List<String> deviceList) {
        boolean thisYear = isThisYear(ins.getYear());

        //获取当前月
        int month = DateUtils.getMonth();

        //获取当前周数
        int week = DateUtil.weekOfMonth(new Date()) == 1 ? 1 : DateUtil.weekOfMonth(new Date()) - 1;
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

        //设置每个月的第几周新增任务
        addEveryWeekTask(InspectionConstant.ANNUAL, ins, newStaId, date[0], date[1], orgList, stationList, deviceList);

    }

    /**
     * @param type                 检修类型
     * @param ins                  检修策略
     * @param newStaId             新的标准id
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param inspectionStrOrgRels 组织机构集合
     * @param stationRels          站点集合
     * @param deviceCodes          设备列表
     */
    private void addEveryWeekTask(Integer type,
                                  InspectionStrategy ins,
                                  String newStaId,
                                  Date startTime,
                                  Date endTime,
                                  List<InspectionStrOrgRel> inspectionStrOrgRels,
                                  List<InspectionStrStaRel> stationRels,
                                  List<String> deviceCodes
    ) {

        // 检修计划基本信息
        RepairPool repairPool = new RepairPool();
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String jxCode = String.format("%s%s", "JX", snowflake.nextIdStr());
        repairPool.setCode(jxCode);
        repairPool.setName(String.format("%s%s", sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(type)), jxCode));
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

        // 保存检修计划
        repairPoolMapper.insert(repairPool);

        // 保存组织结构
        insertBatchOrg(inspectionStrOrgRels, jxCode);

        // 保存站点信息
        insertBatchSta(stationRels, jxCode);

        // 处理检修计划与检修标准的关联关系，并绑定设备
        handle(jxCode, newStaId, deviceCodes);
    }

    /**
     * 根据检修策略和检修标准查询对应的设备
     *
     * @param insCode
     * @param staCode
     * @return
     */
    public List<String> getDeviceList(String insCode, String staCode) {
        List<String> deviceCodes = new ArrayList<>();

        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStaCode, staCode)
                        .eq(InspectionStrRel::getInspectionStrCode, insCode).eq(InspectionStrRel::getDelFlag, 0));

        if (CollUtil.isNotEmpty(inspectionStrRels)) {
            List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                    new LambdaQueryWrapper<InspectionStrDeviceRel>()
                            .eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRels.get(0).getId()));

            if (CollUtil.isNotEmpty(inspectionStrDeviceRels)) {
                deviceCodes = inspectionStrDeviceRels.stream().map(InspectionStrDeviceRel::getDeviceCode).collect(Collectors.toList());
            }
        }

        return deviceCodes;
    }


    /**
     * 处理检修计划与检修标准的关联关系，并绑定设备
     *
     * @param jxCode
     * @param newStaId
     * @param deviceCodes
     */
    private void handle(String jxCode, String newStaId, List<String> deviceCodes) {
        // 保存检修计划关联标准信息
        RepairPoolRel repairPoolRel = RepairPoolRel.builder()
                .repairPoolCode(jxCode)
                .repairPoolStaId(newStaId)
                .build();
        relMapper.insert(repairPoolRel);
        String relId = repairPoolRel.getId();

        // 保存检修标准对应的设备
        if (CollUtil.isNotEmpty(deviceCodes)) {
            deviceCodes.forEach(red -> {
                RepairPoolDeviceRel poolDeviceRel = RepairPoolDeviceRel.builder()
                        .deviceCode(red)
                        .repairPoolRelId(relId)
                        .build();
                repairPoolDeviceRel.insert(poolDeviceRel);
            });
        }
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
                repairPoolStationRel.setStationCode(ins.getStationCode());
                repairPoolStationRel.setLineCode(ins.getLineCode());
                repairPoolStationRel.setPositionCode(ins.getPositionCode());
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


    /**
     * 获取站点信息
     *
     * @param code
     * @return
     */
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

    /**
     * 获取组织机构
     *
     * @param code
     * @return
     */
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

    /**
     * 保存检修标准与检修项
     *
     * @param inspectionCode
     */
    public String saveInspection(InspectionCode inspectionCode) {
        // 保存检修标准
        RepairPoolCode repairPoolCode = new RepairPoolCode();
        UpdateHelperUtils.copyNullProperties(inspectionCode, repairPoolCode);
        repairPoolCode.setId(null);
        repairPoolCodeMapper.insert(repairPoolCode);
        String staId = repairPoolCode.getId();

        // <K,V> K是旧的id,V新生成的id，用来维护复制检修项目时的pid更新
        HashMap<String, String> map = new HashMap<>(50);

        // 查询旧的检修标准对应的检修项
        List<InspectionCodeContent> inspectionCodeContentList = getInspectionCodeContentList(inspectionCode.getId());

        if (CollUtil.isNotEmpty(inspectionCodeContentList)) {
            inspectionCodeContentList.forEach(ins -> {
                RepairPoolCodeContent repairPoolCodeContent = new RepairPoolCodeContent();
                repairPoolCodeContent.setRepairPoolCodeId(staId);
                UpdateHelperUtils.copyNullProperties(ins, repairPoolCodeContent);
                repairPoolCodeContent.setId(null);

                // 保存检修检查项
                repairPoolCodeContentMapper.insert(repairPoolCodeContent);

                // 记录旧的检修检查项id和新的检修检查项id
                map.put(ins.getId(), repairPoolCodeContent.getId());
            });

            // 更新新检修检查项的pid
            List<RepairPoolCodeContent> repairPoolCodeContents = repairPoolCodeContentMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolCodeContent>()
                            .eq(RepairPoolCodeContent::getRepairPoolCodeId, staId)
                            .ne(RepairPoolCodeContent::getPid, 0)
                            .eq(RepairPoolCodeContent::getDelFlag, 0));
            if (CollUtil.isNotEmpty(repairPoolCodeContents)) {
                for (RepairPoolCodeContent repairPoolCodeContent : repairPoolCodeContents) {
                    repairPoolCodeContent.setPid(map.get(repairPoolCodeContent.getPid()));
                    repairPoolCodeContentMapper.updateById(repairPoolCodeContent);
                }
            }
        }
        return staId;
    }

    /**
     * 根据检修标准获取检修项目列表
     *
     * @param id
     * @return
     */
    public List<InspectionCodeContent> getInspectionCodeContentList(String id) {
        List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeContentMapper.selectList(
                new LambdaQueryWrapper<InspectionCodeContent>()
                        .eq(InspectionCodeContent::getInspectionCodeId, id)
                        .eq(InspectionCodeContent::getDelFlag, 0));
        if (CollUtil.isEmpty(inspectionCodeContentList)) {
            throw new AiurtBootException("检修标准项目为空");
        }
        return inspectionCodeContentList;
    }

}
