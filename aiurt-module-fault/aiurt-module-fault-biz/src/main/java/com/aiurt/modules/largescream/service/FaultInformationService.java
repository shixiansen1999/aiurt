package com.aiurt.modules.largescream.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.constants.FaultDictCodeConstant;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.largescream.dto.LargeFaultDataDatailDTO;
import com.aiurt.modules.largescream.mapper.FaultInformationMapper;
import com.aiurt.modules.largescream.model.FaultScreenModule;
import com.aiurt.modules.largescream.model.ReliabilityWorkTime;
import com.aiurt.modules.largescream.util.FaultLargeDateUtil;
import com.aiurt.modules.position.entity.CsLine;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 大屏故障信息统计
 *
 * @author: qkx
 * @date: 2022-09-13 14:39
 */
@Service
public class FaultInformationService {
    @Resource
    private FaultInformationMapper faultInformationMapper;

    @Resource
    private IFaultDeviceService faultDeviceService;

    @Resource
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Value("${fault.lv1}")
    private Integer lv1Hours;

    @Value("${fault.lv2}")
    private Integer lv2Hours;

    @Value("${fault.lv3}")
    private Integer lv3Hours;
    @Autowired
    private ISysParamAPI sysParamApi;

    /**
     * 综合大屏-故障信息统计数量
     *
     * @param lineCode
     * @param startDate
     * @param endDate
     * @return
     */
    public FaultLargeCountDTO queryLargeFaultInformation(String lineCode,String startDate,String endDate) {
        FaultLargeCountDTO result = new FaultLargeCountDTO();
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        int count = 0;

        List<Fault> faultList = faultInformationMapper.queryLargeFaultInformation(startTime, endTime, lineCode, majors);

        List<Fault> faultList1 = faultInformationMapper.queryLargeFaultInformation(getTime(0), getTime(1), lineCode, majors);

        //总故障数
        if (CollUtil.isNotEmpty(faultList)) {
            result.setSum(faultList.size());
        } else {
            result.setSum(0);
        }
        //未解决数
        if (CollUtil.isNotEmpty(faultList)) {
            for (Fault fault : faultList) {
                if (!FaultStatusEnum.Close.getStatus().equals(fault.getStatus())) {
                    count++;
                }
                result.setUnSolve(count);
            }
        } else {
            result.setUnSolve(0);
        }
        Date todayStartDate = DateUtil.beginOfDay(new Date());
        Date todayEndDate = DateUtil.endOfDay(new Date());
        //当天已解决数
        List<Fault> faultInformationTodaySolve = faultInformationMapper.queryLargeFaultInformationTodaySolve(todayStartDate, todayEndDate, lineCode, majors);
        if (CollUtil.isNotEmpty(faultInformationTodaySolve)) {
            result.setSolve(faultInformationTodaySolve.size());
        } else {
            result.setSolve(0);
        }
        //当天新增
        List<Fault> faults = faultInformationMapper.queryLargeFaultInformationTodayAdd(todayStartDate, todayEndDate, lineCode, majors);
        if (CollUtil.isNotEmpty(faults)) {
            result.setNewAddNumber(faults.size());
        } else {
            result.setNewAddNumber(0);
        }
        return result;
    }

    private Date getTime(Integer integer){
        Date time = null;
        String dateTime1 = FaultLargeDateUtil.getDateTime(CommonConstant.BOARD_TIME_TYPE_3);
        String[] split1 = dateTime1.split("~");
        time = DateUtil.parse(split1[integer]);
        return time;
    }
    /**
     * 综合大屏-故障信息统计详情
     *

     * @param lineCode
     * @return
     */
    public List<FaultLargeInfoDTO> getLargeFaultDatails(String startDate,String endDate, Integer faultModule, String lineCode) {
        FaultScreenModule faultScreenModule = new FaultScreenModule();
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();
        faultScreenModule.setLineCode(lineCode);
        faultScreenModule.setMajors(majors);
        faultScreenModule.setStartDate(startTime);
        faultScreenModule.setEndDate(endTime);
        switch (faultModule) {
            // 未解决故障
            case 2:
                faultScreenModule.setUnSo(1);
                break;
            // 当日新增
            case 3:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodayAdd(1);
                break;
            // 当日已解决
            case 4:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodaySolve(1);
                break;
            // 挂起数
            case 5:
                faultScreenModule.setHangUp(1);
                break;
            // 解决数
            case 6:
                faultScreenModule.setSolve(1);
                break;
            default:
        }
        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationMapper.getLargeFaultDatails(faultScreenModule);
        largeFaultInfo.stream().forEach(l -> {
            // 字典翻译
            /*String statusName = sysBaseApi.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream().filter(item -> item.getValue().equals(String.valueOf(l.getStatus()))).map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);*/
            //0710状态值映射
            if (l.getStatus().equals(FaultStatusEnum.Close.getStatus())) {
                l.setStatusName("已完成");
            } else if (l.getStatus().equals(FaultStatusEnum.HANGUP.getStatus())) {
                l.setStatusName("已挂起");
            }else {
                l.setStatusName("维修中");
            }

            // 字典翻译
            if(StrUtil.isNotBlank(l.getFaultPhenomenon())){
                LambdaQueryWrapper<FaultKnowledgeBaseType> faultKnowledgeBaseTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                faultKnowledgeBaseTypeLambdaQueryWrapper.eq(FaultKnowledgeBaseType::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(FaultKnowledgeBaseType::getCode,l.getFaultPhenomenon());
                FaultKnowledgeBaseType faultKnowledgeBaseType = faultKnowledgeBaseTypeMapper.selectOne(faultKnowledgeBaseTypeLambdaQueryWrapper);
                if(ObjectUtil.isNotEmpty(faultKnowledgeBaseType)){
                    l.setFaultPhenomenonName(faultKnowledgeBaseType.getName());
                }
            }
        });
        return largeFaultInfo;
    }

    /**
     * 综合大屏-故障信息统计列表
     *
     * @param lineCode
     * @return
     */
    public List<FaultLargeInfoDTO> getLargeFaultInfo(String lineCode,String startDate,String endDate) {
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationMapper.getLargeFaultInfo(startTime, endTime, lineCode, majors);
        largeFaultInfo.stream().forEach(l -> {
            // 字典翻译
            /*String statusName = sysBaseApi.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream().filter(item -> item.getValue().equals(String.valueOf(l.getStatus()))).map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);*/
            //0710状态值映射
            if (l.getStatus().equals(FaultStatusEnum.Close.getStatus())) {
                l.setStatusName("已完成");
            } else if (l.getStatus().equals(FaultStatusEnum.HANGUP.getStatus())) {
                l.setStatusName("已挂起");
            }else {
                l.setStatusName("维修中");
            }

        });
        return largeFaultInfo;
    }


    /**
     * 线路故障统计
     *
     * @return
     */
    public List<FaultLargeLineInfoDTO> getLargeLineFaultInfo(String lineCode,String startDate,String endDate) {
        List<FaultLargeLineInfoDTO> largeLineInfoDtos = new ArrayList<>();
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        List<CsLine> allLine = sysBaseApi.getAllLine();
        List<CsLine> list;
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.OLD_LINE);
        boolean flag = "1".equals(paramModel.getValue());
        SysParamModel paramModel2 = sysParamApi.selectByCode(SysParamCodeConstant.OLD_LINECODE);
        String value = paramModel2.getValue();

        if (StrUtil.isNotEmpty(lineCode)) {
            if (flag) {
                list = allLine.stream().filter(a -> lineCode.equals(a.getLineCode()))
                        .filter(a -> !value.contains(a.getLineCode()))
                        .collect(Collectors.toList());
            } else {
                list = allLine.stream().filter(a -> lineCode.equals(a.getLineCode())).collect(Collectors.toList());
            }

        } else {
            if (flag) {
                list = allLine.stream().filter(a -> !value.contains(a.getLineCode()))
                        .collect(Collectors.toList());
            }else {
                list = new ArrayList<>(allLine);
            }
        }
        List<Fault> largeLineFaultInfo = faultInformationMapper.getLargeLineFaultInfo(startTime, endTime, majors,lineCode);
        //根据line_code分组，查询同一条线路下的所有故障
        Map<String, List<Fault>> collect = new HashMap<>();
        if (flag) {
            collect = largeLineFaultInfo.stream()
                    .filter(a -> !value.contains(a.getLineCode()))
                    .collect(Collectors.groupingBy(Fault::getLineCode));
        }else {
            collect = largeLineFaultInfo.stream().collect(Collectors.groupingBy(Fault::getLineCode));
        }


        Map<String, List<CsLine>> listMap = list.stream().collect(Collectors.groupingBy(CsLine::getLineCode));

        Set<String> lines = listMap.keySet();
        Iterator<String> lineList = lines.iterator();
        while (lineList.hasNext()) {
            String key = lineList.next();
            List<Fault> faults = collect.get(key);
            if (CollUtil.isEmpty(faults)) {
                collect.put(key, new ArrayList<Fault>());
            }
        }

        Set<String> keys = collect.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            FaultLargeLineInfoDTO faultLargeLineInfoDTO = new FaultLargeLineInfoDTO();
            faultLargeLineInfoDTO.setLineCode(key);
            Integer solveCount = 0;
            Integer hangCount = 0;
            faultLargeLineInfoDTO.setSolve(solveCount);
            faultLargeLineInfoDTO.setHang(hangCount);
            List<CsLine> csLines = listMap.get(key);
            faultLargeLineInfoDTO.setLineName(CollUtil.isNotEmpty(csLines) ? csLines.get(0).getLineName() : "");

            List<Fault> faults = collect.get(key);
            //故障总数
            faultLargeLineInfoDTO.setSum(CollUtil.isNotEmpty(faults) ? faults.size() : 0L);
            for (Fault fault : faults) {
                if (FaultStatusEnum.Close.getStatus().equals(fault.getStatus())) {
                    solveCount++;
                }
                if (FaultStatusEnum.HANGUP.getStatus().equals(fault.getStatus())) {
                    hangCount++;
                }
                //已解决数
                faultLargeLineInfoDTO.setSolve(solveCount);
                //挂起数
                faultLargeLineInfoDTO.setHang(hangCount);
            }
            // 已解决率
            if (faultLargeLineInfoDTO.getSum() <= 0 ) {
                faultLargeLineInfoDTO.setSolveRate("100");
            }else if(faultLargeLineInfoDTO.getSum() > 0 && faultLargeLineInfoDTO.getSolve() <= 0){
                faultLargeLineInfoDTO.setSolveRate("0");
            }
            else {
                int d = new BigDecimal((Integer) faultLargeLineInfoDTO.getSolve() * 100 / faultLargeLineInfoDTO.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
                faultLargeLineInfoDTO.setSolveRate(Integer.toString(d));
            }
            largeLineInfoDtos.add(faultLargeLineInfoDTO);
        }
        if (CollUtil.isNotEmpty(largeLineInfoDtos)) {
            List<FaultLargeLineInfoDTO> collect1 = largeLineInfoDtos.stream().sorted(Comparator.comparing(FaultLargeLineInfoDTO::getLineName)).collect(Collectors.toList());
            return collect1;
        }
        return largeLineInfoDtos;
    }


    /**
     * 故障时长趋势图接口
     *
     * @param lineCode
     * @return
     */
    public List<FaultMonthTimeDTO> getLargeFaultTime(String lineCode) {
        List<FaultMonthTimeDTO> monthList = new ArrayList<>();
        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();
        int x = 5;
        for (int i = 0; i <= x; i++) {
            double sum = 0;
            double monthTime = 0;
            //创建一个新的系统故障单集合
            List<FaultSystemTimeDTO> systemlist = new ArrayList<>();
            //月份故障单
            FaultMonthTimeDTO faultMonthTimeDTO = new FaultMonthTimeDTO();
            //获取最近半年月份，上一个月往前推半年
            String month = FaultLargeDateUtil.getLast12Months(i);
            String substring = month.substring(5, 7);
            String changmonth = substring + "月";
            faultMonthTimeDTO.setMonth(changmonth);
            //查询按系统分类好的并计算了故障消耗总时长的记录
            List<FaultSystemTimeDTO> largeFaultTime = faultInformationMapper.getLargeFaultTime(month, lineCode, majors);
            for (FaultSystemTimeDTO faultSystemTimeDTO : largeFaultTime) {
                Double d = null;
                if (!"0".equals(faultSystemTimeDTO.getRepairTime()) && faultSystemTimeDTO.getRepairTime() != null) {
                    d = new BigDecimal(Double.valueOf(faultSystemTimeDTO.getRepairTime()) / 60).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    sum += d;
                    monthTime = new BigDecimal(sum).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                //将故障处理时间为null的改为0
                if (faultSystemTimeDTO.getRepairTime() == null) {
                    faultSystemTimeDTO.setRepairTime("0");
                }
                //将故障处理时间+H
                if (d == null) {
                    d = 0.0;
                }
                String h = d + "H";
                faultSystemTimeDTO.setRepairTime(h);
                //将名字改成系统+小时数
                if (ObjectUtil.isNotEmpty(faultSystemTimeDTO.getSystemName())) {
                    String strm = faultSystemTimeDTO.getSystemName().substring(0, faultSystemTimeDTO.getSystemName().length() - 2);
                    String name = strm + " " + faultSystemTimeDTO.getRepairTime();
                    faultSystemTimeDTO.setSystemName(name);
                }
                //将月份内的所有故障处理时间求和
                faultMonthTimeDTO.setMonthTime(String.valueOf(monthTime));
                systemlist.add(faultSystemTimeDTO);
            }
            faultMonthTimeDTO.setSysTimeList(systemlist);
            monthList.add(faultMonthTimeDTO);
        }

        return monthList;
    }

    /**
     * 故障次数趋势图接口
     * @param lineCode
     * @return
     */
    public List<FaultMonthCountDTO> getLargeFaultMonthCount(String lineCode) {
        List<FaultMonthCountDTO> faultMonthCountDTOList = new ArrayList<>();
        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();
        int x = 5;
        // 当前时间
        Date now = new Date();
        // 存放子系统code和名称
        //HashMap<String, String> systemMap = new HashMap<>(16);
        for (int i = 0; i <= x; i++) {
            double duration = 0D;
            //创建一个新的系统故障单集合
            //List<FaultSystemMonthCountDTO> faultSystemMonthCountDTOList = new ArrayList<>();
            //月份故障单
            FaultMonthCountDTO faultMonthCountDTO = new FaultMonthCountDTO();
            //获取最近半年月份，上一个月往前推半年
            String month = FaultLargeDateUtil.getLast12Months(i);
            String substring = month.substring(5, 7);
            String changmonth = substring + "月";
            faultMonthCountDTO.setMonth(changmonth);
            //查询当月的故障
            //0912需求变更查询排除非信号故障，信号专用
            SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.IS_DISTINGUISH_SIGNAL_FAULT);
            boolean isSignalFault = "1".equals(paramModel.getValue());
            List<FaultSystemDTO> largeFaultMonthCount = faultInformationMapper.getLargeFaultMonthCount(month, lineCode, majors,isSignalFault);
            // 保存每个子系统下的故障数
            Map<String, Long> collect = null;
            if (CollUtil.isNotEmpty(largeFaultMonthCount)) {
                // 计算故障的duration
                largeFaultMonthCount.forEach(f -> {
                    //systemMap.putIfAbsent(f.getSubSystemCode(), f.getSystemName());
                    if (ObjectUtil.isEmpty(f.getDuration())) {
                        f.setDuration(DateUtil.between(f.getHappenTime(), now, DateUnit.SECOND));
                    }
                });
                // 计算总duration
                long sum = largeFaultMonthCount.stream().mapToLong(FaultSystemDTO::getDuration).sum();
                // 转换为分钟
                duration = BigDecimal.valueOf(sum / 60).setScale(1, RoundingMode.HALF_UP).doubleValue();
                // todo 需要显示子系统的数据再打开以下相关注释
                /*// 分组统计故障数
                collect = largeFaultMonthCount.stream().collect(Collectors.groupingBy(FaultSystemDTO::getSubSystemCode, Collectors.counting()));
                collect.entrySet().parallelStream().forEach(e -> {
                    FaultSystemMonthCountDTO faultSystemCountDTO = new FaultSystemMonthCountDTO();
                    faultSystemCountDTO.setSubSystemCode(e.getKey());
                    //将名字改成系统+次数
                    String systemName = systemMap.get(e.getKey());
                    if (ObjectUtil.isNotEmpty(systemName)) {
                        String name = systemMap.get(e.getKey()) + " " + e.getValue();
                        faultSystemCountDTO.setSystemName(name);
                    }
                    faultSystemCountDTO.setFrequency(e.getValue());
                    faultSystemMonthCountDTOList.add(faultSystemCountDTO);
                });*/
            }
            //将月份内的所有故障次数求和
            faultMonthCountDTO.setSum(largeFaultMonthCount.size());
            faultMonthCountDTO.setDuration(String.valueOf(duration));
            //faultMonthCountDTO.setFaultSystemMonthCountDTOList(faultSystemMonthCountDTOList);
            faultMonthCountDTOList.add(faultMonthCountDTO);
        }
        return faultMonthCountDTOList;
    }

    public List<FaultDataStatisticsDTO> getYearFault(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        //先获取用户管理的专业，根据专业筛选
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = sysBaseApi.getMajorByUserId(sysUser.getId());
        List<String> majorCodes = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        faultDataStatisticsDTO.setMajorCodes(majorCodes);
        int month = 12;
        List<FaultDataStatisticsDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < month; i++) {
            FaultDataStatisticsDTO dto = new FaultDataStatisticsDTO();
            Map<String, String> map = FaultLargeDateUtil.getMonthFirstAndLast(i);
            String firstDay = map.get("firstDay");
            String lastDay = map.get("lastDay");
            faultDataStatisticsDTO.setStartDate(firstDay);
            faultDataStatisticsDTO.setEndDate(lastDay);
            Integer yearFault = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            dto.setId(String.valueOf(i));
            dto.setMonth(String.valueOf(i + 1));
            dto.setFaultSum(yearFault);

            faultDataStatisticsDTO.setFaultModeCode(FaultConstant.FAULT_MODE_CODE_0);
            Integer selfCheckFaults = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            dto.setSelfCheckFaults(new BigDecimal(selfCheckFaults));
            //还原报修方式为空,以备下次循环
            faultDataStatisticsDTO.setFaultModeCode(null);

            dto.setRepairFaults(new BigDecimal(yearFault - selfCheckFaults));
            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<FaultDataStatisticsDTO> getSystemYearFault(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        //先获取用户管理的专业，根据专业筛选
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = sysBaseApi.getMajorByUserId(sysUser.getId());
        List<String> majorCodes = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());

        List<FaultDataStatisticsDTO> dtoList = new ArrayList<>();
        String firstDay = null;
        String lastDay = null;
        if (StrUtil.isNotBlank(faultDataStatisticsDTO.getMonth())) {
            String month = faultDataStatisticsDTO.getMonth();
            Integer i = Convert.toInt(month);
            Map<String, String> map = FaultLargeDateUtil.getMonthFirstAndLast(i - 1);
            firstDay = map.get("firstDay");
            lastDay = map.get("lastDay");
            faultDataStatisticsDTO.setStartDate(firstDay);
            faultDataStatisticsDTO.setEndDate(lastDay);
        }

        List<FaultDataStatisticsDTO> allSystemCode = faultInformationMapper.getAllSystemCode(majorCodes);
        for (int i = 0; i < allSystemCode.size(); i++) {
            faultDataStatisticsDTO.setSubSystemCode(allSystemCode.get(i).getSubSystemCode());
            Integer yearFault = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            FaultDataStatisticsDTO dto = new FaultDataStatisticsDTO();
            dto.setId(String.valueOf(i));
            dto.setSubSystemCode(allSystemCode.get(i).getSubSystemCode());
            dto.setSubSystemName(allSystemCode.get(i).getSubSystemName());
            dto.setShortenedForm(allSystemCode.get(i).getShortenedForm());
            dto.setFaultSum(yearFault);

            faultDataStatisticsDTO.setFaultModeCode(FaultConstant.FAULT_MODE_CODE_0);
            Integer selfCheckFaults = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            dto.setSelfCheckFaults(new BigDecimal(selfCheckFaults));
            //还原报修方式为空,以备下次循环
            faultDataStatisticsDTO.setFaultModeCode(null);

            dto.setRepairFaults(new BigDecimal(yearFault - selfCheckFaults));

            dtoList.add(dto);
        }
        return dtoList;
    }

    public FaultDataStatisticsDTO getFaultAnalysis(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        //先获取用户管理的专业，根据专业筛选
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = sysBaseApi.getMajorByUserId(sysUser.getId());
        List<String> majorCodes = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        faultDataStatisticsDTO.setMajorCodes(majorCodes);

        //总数
        Integer yearFault = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
        if (yearFault != 0) {
            BigDecimal total = new BigDecimal(yearFault);
            //自检数量
            faultDataStatisticsDTO.setFaultModeCode(FaultConstant.FAULT_MODE_CODE_0);
            BigDecimal selfCheckFault = new BigDecimal(faultInformationMapper.getYearFault(faultDataStatisticsDTO));
            BigDecimal selfCheckFaultNum = selfCheckFault.divide(total, 3, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setSelfCheckFaultNum(selfCheckFaultNum.multiply(new BigDecimal(100)));
            faultDataStatisticsDTO.setSelfCheckFaults(selfCheckFault);
            //报修数量
            BigDecimal repairFault = total.subtract(selfCheckFault);
            BigDecimal repairFaultNum = repairFault.divide(total, 3, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setRepairFaultNum(repairFaultNum.multiply(new BigDecimal(100)));
            faultDataStatisticsDTO.setRepairFaults(repairFault);
            //已完成数量
            faultDataStatisticsDTO.setFaultModeCode(null);
            faultDataStatisticsDTO.setStatus(FaultStatusEnum.Close.getStatus());
            BigDecimal completedFault = new BigDecimal(faultInformationMapper.getYearFault(faultDataStatisticsDTO));
            BigDecimal completedFaultNum = completedFault.divide(total, 3, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setCompletedFaultNum(completedFaultNum.multiply(new BigDecimal(100)));
            faultDataStatisticsDTO.setCompletedFaults(completedFault);
            //未完成数量
            BigDecimal undoneFault = total.subtract(completedFault);
            BigDecimal undoneFaultNum = undoneFault.divide(total, 3, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setUndoneFaultNum(undoneFaultNum.multiply(new BigDecimal(100)));
            faultDataStatisticsDTO.setUndoneFaults(undoneFault);
        } else {
            faultDataStatisticsDTO.setSelfCheckFaultNum(new BigDecimal(0));
            faultDataStatisticsDTO.setRepairFaultNum(new BigDecimal(0));
            faultDataStatisticsDTO.setCompletedFaultNum(new BigDecimal(0));
            faultDataStatisticsDTO.setUndoneFaultNum(new BigDecimal(0));
            faultDataStatisticsDTO.setSelfCheckFaults(new BigDecimal(0));
            faultDataStatisticsDTO.setRepairFaults(new BigDecimal(0));
            faultDataStatisticsDTO.setCompletedFaults(new BigDecimal(0));
            faultDataStatisticsDTO.setUndoneFaults(new BigDecimal(0));
        }

        return faultDataStatisticsDTO;
    }


    /**
     * 故障数据统计
     *
     * @param lineCode
     * @return
     */
    public FaultDataAnalysisCountDTO queryLargeFaultDataCount(String startDate,String endDate, String lineCode) {
        FaultDataAnalysisCountDTO result = new FaultDataAnalysisCountDTO();

        //获取本周或本月时间
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        int count = 0;
        FaultDataAnalysisCountDTO countDTO = faultInformationMapper.countFaultDataInformation(startTime,endTime,lineCode, majors);
        //FaultDataAnalysisCountDTO countDTO1 = faultInformationMapper.countFaultDataInformation(null,null,lineCode, majors);
        //总故障数
        result.setSum(countDTO.getSum());

        //未修复数
        result.setUnSolve(countDTO.getUnSolve());

        //挂起数
        result.setHangUpNum(countDTO.getHangUpNum());

        //本周已解决
        List<Fault> faultDataInformationweekSolve = faultInformationMapper.queryFaultDataInformationWeekSolve(startTime, endTime, lineCode, majors);
        if (CollUtil.isNotEmpty(faultDataInformationweekSolve)) {
            result.setWeekSolve(faultDataInformationweekSolve.size());
        } else {
            result.setWeekSolve(0);
        }
        //本周新增
        List<Fault> faultDataInformationweekAdd = faultInformationMapper.queryFaultDataInformationWeekAdd(startTime, endTime, lineCode, majors);
        if (CollUtil.isNotEmpty(faultDataInformationweekAdd)) {
            result.setWeekAdd(faultDataInformationweekAdd.size());
        } else {
            result.setWeekAdd(0);
        }
        //当天开始结束时间
        Date todayStartDate = DateUtil.beginOfDay(new Date());
        Date todayEndDate = DateUtil.endOfDay(new Date());
        //当天已解决数
        List<Fault> faultInformationTodaySolve = faultInformationMapper.queryLargeFaultInformationTodaySolve(todayStartDate, todayEndDate, lineCode, majors);
        if (CollUtil.isNotEmpty(faultInformationTodaySolve)) {
            result.setTodaySolve(faultInformationTodaySolve.size());
        } else {
            result.setTodaySolve(0);
        }
        //当天新增
        List<Fault> faults = faultInformationMapper.queryLargeFaultInformationTodayAdd(todayStartDate, todayEndDate, lineCode, majors);
        if (CollUtil.isNotEmpty(faults)) {
            result.setTodayAdd(faults.size());
        } else {
            result.setTodayAdd(0);
        }

        return result;
    }

    /**
     * 大屏-故障数据分析-故障数据统计详情
     *
     * @param largeFaultDataDatailDTO
     * @return
     */
    public IPage<FaultLargeInfoDTO> getLargeFaultDataDatails(LargeFaultDataDatailDTO largeFaultDataDatailDTO) {
        Integer faultModule = largeFaultDataDatailDTO.getFaultModule();

        Integer pageNo = largeFaultDataDatailDTO.getPageNo();
        Integer pageSize = largeFaultDataDatailDTO.getPageSize();

        String lineCode = largeFaultDataDatailDTO.getLineCode();


        FaultScreenModule faultScreenModule = new FaultScreenModule();
        Date startTime = DateUtil.parse(largeFaultDataDatailDTO.getStartDate());
        Date endTime = DateUtil.parse(largeFaultDataDatailDTO.getEndDate());

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        if (ObjectUtil.isEmpty(faultModule)) {
            faultModule = 1;
        }
        faultScreenModule.setStartDate(startTime);
        faultScreenModule.setEndDate(endTime);
        faultScreenModule.setLineCode(lineCode);
        faultScreenModule.setMajors(majors);
        switch (faultModule) {
            // 故障总数
            case 1:
                faultScreenModule.setStartDate(getTime(0));
                faultScreenModule.setEndDate(getTime(1));
                faultScreenModule.setMoonSolve(1);
                break;
            // 未解决故障
            case 2:
                faultScreenModule.setUnSo(1);
                break;
            // 本周或本月新增
            case 3:
                faultScreenModule.setWeekAdd(1);
                break;
            // 本周或本月修复
            case 4:
                faultScreenModule.setWeekSolve(1);
                break;
            // 当日新增
            case 5:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodayAdd(1);
                break;
            // 当日已解决
            case 6:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodaySolve(1);
                break;
            // 挂起数
            case 7:
                faultScreenModule.setHangUp(1);
                break;
            default:
        }
        // 分页
        Page<FaultLargeInfoDTO> pageList = new Page<>(pageNo, pageSize);
        List<FaultLargeInfoDTO> largeFaultDataInfo = faultInformationMapper.getLargeFaultDataDatails(pageList, faultScreenModule);
        largeFaultDataInfo.stream().forEach(l -> {
            // 字典翻译faultScreenModule = {FaultScreenModule@25749} "FaultScreenModule(startDate=2023-07-24 00:00:00, endDate=2023-07-30 00:00:00, status=null, moonSolve=null, unSo=1, solve=null, hangUp=null, todaySolve=null, todayAdd=null, weekSolve=null, weekAdd=null, todayStartDate=null, todayEndDate=null, lineCode=null, majors=[1001, XH001])"
            /*String statusName = sysBaseApi.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream().filter(item -> item.getValue().equals(String.valueOf(l.getStatus()))).map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);*/

            //0710状态值映射
            if (l.getStatus().equals(FaultStatusEnum.Close.getStatus())) {
                l.setStatusName("已完成");
            } else if (l.getStatus().equals(FaultStatusEnum.HANGUP.getStatus())) {
                l.setStatusName("已挂起");
            }else {
                l.setStatusName("维修中");
            }

            String faultModeName = sysBaseApi.getDictItems(FaultDictCodeConstant.FAULT_MODE_CODE).stream().filter(item -> item.getValue().equals(String.valueOf(l.getFaultModeCode()))).map(DictModel::getText).collect(Collectors.joining());
            l.setFaultModeName(faultModeName);

            String faultPhenomenonName = sysBaseApi.translateDictFromTable("fault_knowledge_base_type", "name", "code", l.getFaultPhenomenon());
            l.setFaultPhenomenonName(faultPhenomenonName);
        });
        return pageList.setRecords(largeFaultDataInfo);
    }


    /**
     * 大屏分析-故障数据统计列表
     *
     * @param lineCode
     * @return
     */
    public List<FaultDataAnalysisInfoDTO> getLargeFaultDataInfo(String startDate,String endDate, String lineCode) {
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        List<FaultDataAnalysisInfoDTO> largeFaultDataInfo = faultInformationMapper.getLargeFaultDataInfo(startTime, endTime, lineCode, majors);
        largeFaultDataInfo.stream().forEach(l -> {
            // 字典翻译
            /*String statusName = sysBaseApi.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream().filter(item -> item.getValue().equals(String.valueOf(l.getStatus()))).map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);*/
            //0710状态值映射
            if (l.getStatus().equals(FaultStatusEnum.Close.getStatus())) {
                l.setStatusName("已完成");
            } else if (l.getStatus().equals(FaultStatusEnum.HANGUP.getStatus())) {
                l.setStatusName("已挂起");
            }else {
                l.setStatusName("维修中");
            }

            String faultModeName = sysBaseApi.getDictItems(FaultDictCodeConstant.FAULT_MODE_CODE).stream().filter(item -> item.getValue().equals(String.valueOf(l.getFaultModeCode()))).map(DictModel::getText).collect(Collectors.joining());
            l.setFaultModeName(faultModeName);

            String faultPhenomenonName = sysBaseApi.translateDictFromTable("fault_knowledge_base_type", "name", "code", l.getFaultPhenomenon());
            l.setFaultPhenomenonName(faultPhenomenonName);
        });
        return largeFaultDataInfo;
    }


    /**
     * 故障超时等级详情
     *
     * @param lineCode
     * @return
     */
    public List<FaultLevelDTO> getFaultLevelInfo(String startDate,String endDate, String lineCode) {
        List<FaultLevelDTO> faultLevelList = new ArrayList<>();
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //登录人专业
        List<String> majors = getCurrentLoginUserMajors();

        Integer level = null;
        int y = 3;
        for (int i = 1; i <= y; i++) {
            level = i;
            //创建一个新的超时故障单集合
            List<FaultTimeoutLevelDTO> faultTimeOutList = new ArrayList<>();
            //故障等级实体
            FaultLevelDTO faultLevelDTO = new FaultLevelDTO();
            if (level == 1) {
                faultLevelDTO.setLevel("一级");
            } else if (level == 2) {
                faultLevelDTO.setLevel("二级");
            } else if (level == 3) {
                faultLevelDTO.setLevel("三级");
            }
            List<FaultTimeoutLevelDTO> faultData = faultInformationMapper.getFaultData(level, startTime, endTime, lineCode, majors, lv1Hours, lv2Hours, lv3Hours);
            //计算i级故障数量
            faultLevelDTO.setFaultNumber(faultData.size());

            if (CollUtil.isNotEmpty(faultData)) {
                for (FaultTimeoutLevelDTO faultDatum : faultData) {
                    //查找设备编码
                    List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultDatum.getCode());
                    if (CollUtil.isNotEmpty(faultDeviceList)) {
                        for (FaultDevice faultDevice : faultDeviceList) {
                            faultDatum.setDeviceCode(faultDevice.getDeviceCode());
                            faultDatum.setDeviceName(faultDevice.getDeviceName());
                        }
                    }
                    //计算超时时长
                    long hour = DateUtil.between(faultDatum.getHappenTime(), new Date(), DateUnit.HOUR);
                    String time = hour + "小时";
                    faultDatum.setTimeoutDuration(time);

                    JSONObject csStationByCode = sysBaseApi.getCsStationByCode(faultDatum.getStationCode());
                    faultDatum.setStationName(csStationByCode.getString("stationName"));
                    faultTimeOutList.add(faultDatum);
                }
                faultLevelDTO.setFaultLevelList(faultTimeOutList);

                faultLevelList.add(faultLevelDTO);
            }

        }

        return faultLevelList;
    }

    /**
     * 子系统可靠度
     *
     * @return
     */
    public List<FaultSystemReliabilityDTO> getSystemReliability(String lineCode,String startDate,String endDate) {
        List<FaultSystemReliabilityDTO> reliabilityList = new ArrayList<>();
        Date startTime = DateUtil.parse(startDate);
        Date endTime = DateUtil.parse(endDate);

        //获取登录人专业
        List<String> majors = getCurrentLoginUserMajors();

        List<String> currentLoginUserSubsystems = getCurrentLoginUserSubsystems();
        //计划时长
        Double planTime = null;
        //实际时长
        Double actualTime = null;


        //查询按系统分类好的并计算了故障消耗总时长的记录
        List<FaultSystemTimesDTO> systemFaultSum = faultInformationMapper.getSystemFaultSum(startTime, endTime, majors,lineCode);
        //查询子系统设备数

        List<FaultSystemDeviceSumDTO> systemDeviceSum = faultInformationMapper.getLineSystem(lineCode,currentLoginUserSubsystems);
        if (ObjectUtil.isNotEmpty(systemDeviceSum)) {
            //遍历所有设备
            for (FaultSystemDeviceSumDTO faultSystemDeviceSumDTO : systemDeviceSum) {
                FaultSystemReliabilityDTO faultSystemReliabilityDTO = new FaultSystemReliabilityDTO();
                faultSystemReliabilityDTO.setSystemName(faultSystemDeviceSumDTO.getSystemName());
                faultSystemReliabilityDTO.setSubSystemCode(faultSystemDeviceSumDTO.getSystemCode());
                faultSystemReliabilityDTO.setShortenedForm(faultSystemDeviceSumDTO.getShortenedForm());
                //计划时长
                if (StrUtil.isNotBlank(faultSystemDeviceSumDTO.getShouldWorkTime())){
                    planTime = Double.valueOf(faultSystemDeviceSumDTO.getShouldWorkTime());
                }
                if(StrUtil.isBlank(lineCode) && StrUtil.isNotBlank(faultSystemDeviceSumDTO.getSystemCode())){
                    String sumWorkTime = faultInformationMapper.getSumWorkTime(faultSystemDeviceSumDTO.getSystemCode());
                    if(StrUtil.isNotBlank(sumWorkTime)){
                        planTime = Double.valueOf(sumWorkTime);
                    }
                }
                actualTime = planTime;
                if (actualTime != null) {
                    if (ObjectUtil.isNotEmpty(systemFaultSum)) {
                        //遍历故障时间
                        for (FaultSystemTimesDTO faultSystemTimeDTO : systemFaultSum) {
                            if (ObjectUtil.isNotEmpty(faultSystemTimeDTO) && ObjectUtil.isNotEmpty(faultSystemTimeDTO.getSubSystemCode())) {
                                //实际时长
                                if (faultSystemTimeDTO.getSubSystemCode().equals(faultSystemDeviceSumDTO.getSystemCode())) {
                                    if (ObjectUtil.isNotEmpty(faultSystemTimeDTO.getRepairTime())) {
                                        Double repairTime = faultSystemTimeDTO.getRepairTime();
                                        actualTime = actualTime - repairTime;
                                        Double d = new BigDecimal(actualTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        faultSystemReliabilityDTO.setActualRuntime(d);
                                    } else {
                                        Double d = new BigDecimal(actualTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        faultSystemReliabilityDTO.setActualRuntime(d);
                                    }
                                } else {
                                    Double d = new BigDecimal(actualTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    faultSystemReliabilityDTO.setActualRuntime(d);
                                }
                            }

                        }
                    } else {
                        Double d = new BigDecimal(actualTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        faultSystemReliabilityDTO.setActualRuntime(d);
                    }
//                    planTime = planTime / 60;
                    Double plan = null;
                    plan = new BigDecimal(planTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    faultSystemReliabilityDTO.setScheduledRuntime(plan);
                    if (planTime <= 0 || actualTime <= 0) {
                        faultSystemReliabilityDTO.setReliability("0");
                    } else {
                        Double d = new BigDecimal(faultSystemReliabilityDTO.getActualRuntime() * 100 / plan).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
                        faultSystemReliabilityDTO.setReliability(d + "%");
                    }
                }
                reliabilityList.add(faultSystemReliabilityDTO);
            }
        }
        return reliabilityList;
    }

    /**
     * 获取当前登录用户的专业编号
     *
     * @return
     */
    public List<String> getCurrentLoginUserMajors() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录系统，请登录后操作！");
        }
        List<CsUserMajorModel> majorList = sysBaseApi.getMajorByUserId(loginUser.getId());
        List<String> majors = majorList.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        return majors;
    }

    /**
     * 获取当前登录用户的子系统编码
     *
     * @return
     */
    public List<String> getCurrentLoginUserSubsystems(){
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录系统，请登录后操作！");
        }
        List<CsUserSubsystemModel> subsystemByUserId = sysBaseApi.getSubsystemByUserId(loginUser.getId());
        return subsystemByUserId.stream().map(CsUserSubsystemModel::getSystemCode).collect(Collectors.toList());
    }

    public void insertSystemReliability(ReliabilityWorkTime workTime) {
        faultInformationMapper.insertSystemReliability(workTime);
    }

    /**
     * 根据站点code，获取未完成故障（除了待审核、作废、已完成的故障外的所有故障）的故障现象、故障发生时间、故障code
     * @param stationCodeList 要查询哪个站点的故障
     * @param startDate 查询故障发生时间大于哪个时间点
     * @param endDate 查询故障发生时间小于哪个时间点
     * @return
     */
    public List<FaultUnfinishedDTO> getUnfinishedFault(List<String> stationCodeList, Date startDate, Date endDate) {
        return faultInformationMapper.getUnfinishedFault(stationCodeList, startDate, endDate);
    }
}
