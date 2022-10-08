package com.aiurt.modules.largescream.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.constants.FaultDictCodeConstant;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.largescream.mapper.FaultInformationMapper;
import com.aiurt.modules.largescream.model.FaultScreenModule;
import com.aiurt.modules.largescream.util.FaultLargeDateUtil;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    private ISysBaseAPI sysBaseAPI;

    @Resource
    private IFaultDeviceService faultDeviceService;

    @Autowired
    private ISysBaseAPI sysBaseApi;


    /**
     * 综合大屏-故障信息统计数量
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    public FaultLargeCountDTO queryLargeFaultInformation(Integer boardTimeType,String lineCode){
        FaultLargeCountDTO result = new FaultLargeCountDTO();
        String dateTime = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split = dateTime.split("~");
        Date startDate = DateUtil.parse(split[0]);
        Date endDate = DateUtil.parse(split[1]);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        int count =0;
                    List<Fault> faultList = faultInformationMapper.queryLargeFaultInformation(startDate, endDate, lineCode,majors);
                    //总故障数
                    if(CollUtil.isNotEmpty(faultList)){
                        result.setSum(faultList.size());
                    }else{
                        result.setSum(0);
                    }
                    //未解决数
                    if(CollUtil.isNotEmpty(faultList)){
                        for (Fault fault : faultList) {
                            if(!FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                                count++;
                            }
                            result.setUnSolve(count);
                        }
                    }else{
                        result.setUnSolve(0);
                    }
                    Date todayStartDate = DateUtil.beginOfDay(new Date());
                    Date todayEndDate = DateUtil.endOfDay(new Date());
                    //当天已解决数
                    List<Fault> faultInformationTodaySolve = faultInformationMapper.queryLargeFaultInformationTodaySolve(todayStartDate,todayEndDate, lineCode,majors);
                   if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
                          result.setSolve(faultInformationTodaySolve.size());
                    }else{
                       result.setSolve(0);
                   }
                    //当天新增
                    List<Fault> faults = faultInformationMapper.queryLargeFaultInformationTodayAdd(todayStartDate,todayEndDate, lineCode,majors);
                   if(CollUtil.isNotEmpty(faults)){
                       result.setNewAddNumber(faults.size());
                   }else{
                       result.setNewAddNumber(0);
                   }
      return result;
    }

    /**
     * 综合大屏-故障信息统计详情
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    public List<FaultLargeInfoDTO> getLargeFaultDatails(Integer boardTimeType,Integer faultModule, String lineCode){
        FaultScreenModule faultScreenModule = new FaultScreenModule();
        String dateTime = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split = dateTime.split("~");
        Date startDate = DateUtil.parse(split[0]);
        Date endDate = DateUtil.parse(split[1]);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        switch (faultModule) {
            // 总故障数详情
            case 1:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 未解决故障
            case 2:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setUnSo(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 当日新增
            case 3:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodayAdd(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 当日已解决
            case 4:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodaySolve(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
        }
        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationMapper.getLargeFaultDatails(faultScreenModule);
        largeFaultInfo.stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseAPI.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);
        });
        return  largeFaultInfo;
    }

    /**
     * 综合大屏-故障信息统计列表
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    public List<FaultLargeInfoDTO> getLargeFaultInfo(Integer boardTimeType, String lineCode){
        String dateTime = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split = dateTime.split("~");
        Date startDate = DateUtil.parse(split[0]);
        Date endDate = DateUtil.parse(split[1]);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationMapper.getLargeFaultInfo(startDate, endDate, lineCode,majors);
        largeFaultInfo.stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseAPI.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);
        });
        return largeFaultInfo;
    }


    /**
     *线路故障统计
     * @param boardTimeType
     * @return
     */
    public List<FaultLargeLineInfoDTO> getLargeLineFaultInfo(Integer boardTimeType){
        List<FaultLargeLineInfoDTO> largeLineInfoDTOS = new ArrayList<>();
        String dateTime = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split = dateTime.split("~");
        Date startDate = DateUtil.parse(split[0]);
        Date endDate = DateUtil.parse(split[1]);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        List<Fault> largeLineFaultInfo = faultInformationMapper.getLargeLineFaultInfo(startDate, endDate,majors);
        //根据line_code分组，查询同一条线路下的所有故障
        Map<String, List<Fault>> collect = largeLineFaultInfo.stream().collect(Collectors.groupingBy(Fault::getLineCode));
        Set<String> keys = collect.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
                FaultLargeLineInfoDTO faultLargeLineInfoDTO = new FaultLargeLineInfoDTO();
                faultLargeLineInfoDTO.setLineCode(key);
                Integer solveCount = 0;
                Integer hangCount = 0;
                List<Fault> faults = collect.get(key);
                //故障总数
                faultLargeLineInfoDTO.setSum(CollUtil.isNotEmpty(faults)?faults.size():0L);
                for (Fault fault : faults) {
                    faultLargeLineInfoDTO.setLineName(fault.getLineName());
                      if(FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                          solveCount++;
                      }
                      if( FaultStatusEnum.HANGUP.getStatus().equals(fault.getStatus())){
                          hangCount++;
                      }
                      //已解决数
                    faultLargeLineInfoDTO.setSolve(solveCount);
                    //挂起数
                    faultLargeLineInfoDTO.setHang(hangCount);
                }
                // 已解决率
                if (faultLargeLineInfoDTO.getSum() <= 0 || faultLargeLineInfoDTO.getSolve() <= 0) {
                    faultLargeLineInfoDTO.setSolveRate("0");
                } else {
                    Integer d = new BigDecimal((Integer) faultLargeLineInfoDTO.getSolve() * 100 / faultLargeLineInfoDTO.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
                    faultLargeLineInfoDTO.setSolveRate(d + "%");
                }
                largeLineInfoDTOS.add(faultLargeLineInfoDTO);
            }
       return largeLineInfoDTOS;
    }


    /**
     * 故障时长趋势图接口
     * @param lineCode
     * @return
     */
    public List<FaultMonthTimeDTO> getLargeFaultTime(String lineCode){
        List<FaultMonthTimeDTO> monthList = new ArrayList<>();

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        for (int i = 1; i<=6; i++) {
            int sum = 0;
            //创建一个新的系统故障单集合
            List<FaultSystemTimeDTO> systemlist = new ArrayList<>();
            //月份故障单
            FaultMonthTimeDTO faultMonthTimeDTO = new FaultMonthTimeDTO();
            //获取最近半年月份，上一个月往前推半年
            String month = FaultLargeDateUtil.getLast12Months(i);
            String substring = month.substring(5,7);
            String changmonth = substring+"月";
            faultMonthTimeDTO.setMonth(changmonth);
            //查询按系统分类好的并计算了故障消耗总时长的记录
            List<FaultSystemTimeDTO> largeFaultTime = faultInformationMapper.getLargeFaultTime(month, lineCode,majors);
                for (FaultSystemTimeDTO faultSystemTimeDTO : largeFaultTime) {
                    if (!"0".equals(faultSystemTimeDTO.getRepairTime()) && faultSystemTimeDTO.getRepairTime() != null) {
                        sum += Integer.parseInt(faultSystemTimeDTO.getRepairTime());
                    }
                    //将故障处理时间为null的改为0
                    if (faultSystemTimeDTO.getRepairTime() == null) {
                        faultSystemTimeDTO.setRepairTime("0");
                    }
                    //将故障处理时间+H
                    String h = faultSystemTimeDTO.getRepairTime() + "H";
                    faultSystemTimeDTO.setRepairTime(h);
                    //将名字改成系统+小时数
                    if(ObjectUtil.isNotEmpty(faultSystemTimeDTO.getSystemName())) {
                        String strm = faultSystemTimeDTO.getSystemName().substring(0, faultSystemTimeDTO.getSystemName().length() - 2);
                        String name = strm + " " + faultSystemTimeDTO.getRepairTime();
                        faultSystemTimeDTO.setSystemName(name);
                    }
                    //将月份内的所有故障处理时间求和
                    faultMonthTimeDTO.setMonthTime(String.valueOf(sum));
                    systemlist.add(faultSystemTimeDTO);
                }
                faultMonthTimeDTO.setSysTimeList(systemlist);
                monthList.add(faultMonthTimeDTO);
        }

        return monthList;
    }

    public List<FaultDataStatisticsDTO> getYearFault(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        //先获取用户管理的专业，根据专业筛选
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = sysBaseAPI.getMajorByUserId(sysUser.getId());
        List<String> majorCodes = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        faultDataStatisticsDTO.setMajorCodes(majorCodes);
        int month = 12;
        List<FaultDataStatisticsDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < month ; i++) {
            FaultDataStatisticsDTO dto = new FaultDataStatisticsDTO();
            Map<String, String> map = FaultLargeDateUtil.getMonthFirstAndLast(i);
            String firstDay = map.get("firstDay");
            String lastDay = map.get("lastDay");
            faultDataStatisticsDTO.setFirstDay(firstDay);
            faultDataStatisticsDTO.setLastDay(lastDay);
            Integer yearFault = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            dto.setId(String.valueOf(i));
            dto.setMonth(String.valueOf(i+1));
            dto.setFaultSum(yearFault);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<FaultDataStatisticsDTO> getSystemYearFault(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        //先获取用户管理的专业，根据专业筛选
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = sysBaseAPI.getMajorByUserId(sysUser.getId());
        List<String> majorCodes = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());

        List<FaultDataStatisticsDTO> dtoList = new ArrayList<>();
        String firstDay = null;
        String lastDay = null;
        if (StrUtil.isNotBlank(faultDataStatisticsDTO.getMonth())) {
            String month = faultDataStatisticsDTO.getMonth();
            Integer i = Convert.toInt(month);
            Map<String, String> map = FaultLargeDateUtil.getMonthFirstAndLast(i-1);
             firstDay = map.get("firstDay");
             lastDay = map.get("lastDay");
            faultDataStatisticsDTO.setFirstDay(firstDay);
            faultDataStatisticsDTO.setLastDay(lastDay);
        }

        List<FaultDataStatisticsDTO> allSystemCode = faultInformationMapper.getAllSystemCode(majorCodes);
        for (int i = 0; i < allSystemCode.size(); i++) {
            faultDataStatisticsDTO.setSubSystemCode(allSystemCode.get(i).getSubSystemCode());
            Integer yearFault = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            FaultDataStatisticsDTO dto = new FaultDataStatisticsDTO();
            dto.setId(String.valueOf(i));
            dto.setSubSystemCode(allSystemCode.get(i).getSubSystemCode());
            dto.setSubSystemName(allSystemCode.get(i).getSubSystemName());
            dto.setFaultSum(yearFault);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public FaultDataStatisticsDTO getFaultAnalysis(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        //先获取用户管理的专业，根据专业筛选
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = sysBaseAPI.getMajorByUserId(sysUser.getId());
        List<String> majorCodes = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        faultDataStatisticsDTO.setMajorCodes(majorCodes);

        if (faultDataStatisticsDTO.getBoardTimeType() != null) {
            String dateTime = FaultLargeDateUtil.getDateTime(faultDataStatisticsDTO.getBoardTimeType());
            String[] split = dateTime.split("~");
            Date startDate = DateUtil.parse(split[0]);
            Date endDate = DateUtil.parse(split[1]);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            faultDataStatisticsDTO.setFirstDay(format.format(startDate));
            faultDataStatisticsDTO.setLastDay(format.format(endDate));
        }
        //总数
        Integer yearFault = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
        if (yearFault != 0) {
            BigDecimal total = new BigDecimal(yearFault);
            //自检数量
            faultDataStatisticsDTO.setFaultModeCode(FaultConstant.FAULT_MODE_CODE_0);
            BigDecimal selfCheckFault= new BigDecimal(faultInformationMapper.getYearFault(faultDataStatisticsDTO));
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
            BigDecimal completedFaultNum =  completedFault.divide(total, 3, BigDecimal.ROUND_HALF_UP);
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
     * @param lineCode
     * @return
     */
    public FaultDataAnalysisCountDTO queryLargeFaultDataCount(Integer boardTimeType,String lineCode){
        FaultDataAnalysisCountDTO result = new FaultDataAnalysisCountDTO();

        //获取本周或本月时间
        String dateTime = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split = dateTime.split("~");
        Date weekStartDate = DateUtil.parse(split[0]);
        Date weekEndDate = DateUtil.parse(split[1]);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        int count =0;
        List<Fault> faultList = faultInformationMapper.queryFaultDataInformation(lineCode,majors);
        //总故障数
        if(CollUtil.isNotEmpty(faultList)){
            result.setSum(faultList.size());
        }else{
            result.setSum(0);
        }
        //未解决数
        if(CollUtil.isNotEmpty(faultList)){
            for (Fault fault : faultList) {
                if(!FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    count++;
                }
                result.setUnSolve(count);
            }
        }else{
            result.setUnSolve(0);
        }
        //本周已解决
        List<Fault> faultDataInformationweekSolve = faultInformationMapper.queryFaultDataInformationWeekSolve(weekStartDate, weekEndDate, lineCode,majors);
        if(CollUtil.isNotEmpty(faultDataInformationweekSolve)){
            result.setWeekSolve(faultDataInformationweekSolve.size());
        }else{
            result.setWeekSolve(0);
        }
        //本周新增
        List<Fault> faultDataInformationweekAdd = faultInformationMapper.queryFaultDataInformationWeekAdd(weekStartDate, weekEndDate, lineCode,majors);
        if(CollUtil.isNotEmpty(faultDataInformationweekAdd)){
            result.setWeekAdd(faultDataInformationweekAdd.size());
        }else{
            result.setWeekAdd(0);
        }
        //当天开始结束时间
        Date todayStartDate = DateUtil.beginOfDay(new Date());
        Date todayEndDate = DateUtil.endOfDay(new Date());
        //当天已解决数
        List<Fault> faultInformationTodaySolve = faultInformationMapper.queryLargeFaultInformationTodaySolve(todayStartDate,todayEndDate, lineCode,majors);
        if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
            result.setTodaySolve(faultInformationTodaySolve.size());
        }else{
            result.setTodaySolve(0);
        }
        //当天新增
        List<Fault> faults = faultInformationMapper.queryLargeFaultInformationTodayAdd(todayStartDate,todayEndDate, lineCode,majors);
        if(CollUtil.isNotEmpty(faults)){
            result.setTodayAdd(faults.size());
        }else{
            result.setTodayAdd(0);
        }

        return result;
    }

    /**
     * 大屏-故障数据分析-故障数据统计详情
     * @param lineCode
     * @return
     */
    public List<FaultLargeInfoDTO> getLargeFaultDataDatails(Integer boardTimeType,Integer faultModule, String lineCode){
        FaultScreenModule faultScreenModule = new FaultScreenModule();
        //本周或本月时间
        String dateTime = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split = dateTime.split("~");
        Date startDate = DateUtil.parse(split[0]);
        Date endDate = DateUtil.parse(split[1]);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        if(ObjectUtil.isEmpty(faultModule)){
            faultModule = 1;
        }
        switch (faultModule) {
            // 故障总数
            case 1:
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 未解决故障
            case 2:
                faultScreenModule.setUnSo(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 本周或本月新增
            case 3:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setWeekAdd(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 本周或本月修复
            case 4:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setWeekSolve(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 当日新增
            case 5:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodayAdd(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
            // 当日已解决
            case 6:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodaySolve(1);
                faultScreenModule.setLineCode(lineCode);
                faultScreenModule.setMajors(majors);
                break;
        }
        List<FaultLargeInfoDTO> largeFaultDataInfo = faultInformationMapper.getLargeFaultDataDatails(faultScreenModule);
        largeFaultDataInfo.stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseAPI.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);

            String faultModeName = sysBaseAPI.getDictItems(FaultDictCodeConstant.FAULT_MODE_CODE).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getFaultModeCode())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setFaultModeName(faultModeName);
        });
        return  largeFaultDataInfo;
    }


    /**
     * 大屏分析-故障数据统计列表
     * @param lineCode
     * @return
     */
    public List<FaultDataAnalysisInfoDTO> getLargeFaultDataInfo(Integer boardTimeType,String lineCode){
        String dateTime1 = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split1 = dateTime1.split("~");
        Date startDate = DateUtil.parse(split1[0]);
        Date endDate = DateUtil.parse(split1[1]);

        //获取当前登录人的专业编码
        List<String> majors = getCurrentLoginUserMajors();

        List<FaultDataAnalysisInfoDTO> largeFaultDataInfo = faultInformationMapper.getLargeFaultDataInfo(startDate,endDate,lineCode,majors);
        largeFaultDataInfo.stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseAPI.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);

            String faultModeName = sysBaseAPI.getDictItems(FaultDictCodeConstant.FAULT_MODE_CODE).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getFaultModeCode())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setFaultModeName(faultModeName);
        });
        return largeFaultDataInfo;
    }


    /**
     * 故障超时等级详情
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    public List<FaultLevelDTO> getFaultLevelInfo(Integer boardTimeType,String lineCode) {
        List<FaultLevelDTO> faultLevelList = new ArrayList<>();
        //设置时间查询条件
        String dateTime1 = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split1 = dateTime1.split("~");
        Date startDate = DateUtil.parse(split1[0]);
        Date endDate = DateUtil.parse(split1[1]);

        //登录人专业
        List<String> majors = getCurrentLoginUserMajors();

        Integer level = null;
        for (int i = 1; i <=3 ; i++) {
             level = i;
            //创建一个新的超时故障单集合
            List<FaultTimeoutLevelDTO> faultTimeOutList = new ArrayList<>();
            //故障等级实体
            FaultLevelDTO faultLevelDTO = new FaultLevelDTO();
            if(level==1){
                faultLevelDTO.setLevel("一级");
            }
            else if(level ==2){
                faultLevelDTO.setLevel("二级");
            }
            else if(level ==3){
                faultLevelDTO.setLevel("三级");
            }
            List<FaultTimeoutLevelDTO> faultData = faultInformationMapper.getFaultData(level,startDate, endDate,lineCode,majors);
            //计算i级故障数量
            faultLevelDTO.setFaultNumber(faultData.size());

            if (CollUtil.isNotEmpty(faultData)) {
                for (FaultTimeoutLevelDTO faultDatum : faultData) {
                    //查找设备编码
                    List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultDatum.getCode());
                    if(CollUtil.isNotEmpty(faultDeviceList)){
                        for (FaultDevice faultDevice : faultDeviceList) {
                            faultDatum.setDeviceCode(faultDevice.getDeviceCode());
                            faultDatum.setDeviceName(faultDevice.getDeviceName());
                        }
                    }
                    //计算超时时长
                    long hour=DateUtil.between(faultDatum.getHappenTime(),new Date(), DateUnit.HOUR);
                        String time = hour + "H" ;
                        faultDatum.setTimeoutDuration(time);

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
     * @param boardTimeType
     * @return
     */
    public List<FaultSystemReliabilityDTO> getSystemReliability(Integer boardTimeType){
        List<FaultSystemReliabilityDTO> reliabilityList = new ArrayList<>();
        //设置时间获取本月/周小时数
        String dateTime1 = FaultLargeDateUtil.getDateHours(boardTimeType);
        String[] split1 = dateTime1.split("~");
        Date startDate1 = DateUtil.parse(split1[0]);
        Date endDate1 = DateUtil.parse(split1[1]);

        //获取登录人专业
        List<String> majors = getCurrentLoginUserMajors();

        //本周/本月时长总数
        Integer time = Math.toIntExact(DateUtil.between(startDate1, endDate1, DateUnit.MINUTE));
        //计划时长
        Double planTime =null;
        //实际时长
        Double actualTime = null;
        //设置时间查询条件
        String dateTime = FaultLargeDateUtil.getDateTimes(boardTimeType);
        String[] split = dateTime.split("~");
        Date startDate = DateUtil.parse(split[0]);
        Date endDate = DateUtil.parse(split[1]);

        //查询按系统分类好的并计算了故障消耗总时长的记录
        List<FaultSystemTimesDTO> systemFaultSum = faultInformationMapper.getSystemFaultSum(startDate, endDate,majors);
        //查询子系统设备数
        List<FaultSystemDeviceSumDTO> systemDeviceSum = faultInformationMapper.getSystemDeviceSum(majors);
        if(ObjectUtil.isNotEmpty(systemDeviceSum)){
            //遍历所有设备
            for (FaultSystemDeviceSumDTO faultSystemDeviceSumDTO : systemDeviceSum) {
                FaultSystemReliabilityDTO faultSystemReliabilityDTO = new FaultSystemReliabilityDTO();
                faultSystemReliabilityDTO.setSystemName(faultSystemDeviceSumDTO.getSystemName());
                faultSystemReliabilityDTO.setSubSystemCode(faultSystemDeviceSumDTO.getSystemCode());
                //计划时长
                planTime = Double.valueOf(faultSystemDeviceSumDTO.getDeviceNumber()*time);
                actualTime = planTime;
                if(ObjectUtil.isNotEmpty(systemFaultSum)){
                    //遍历故障时间
                    for (FaultSystemTimesDTO faultSystemTimeDTO : systemFaultSum) {
                        if(ObjectUtil.isNotEmpty(faultSystemTimeDTO.getSubSystemCode())) {
                            //实际时长
                            if (faultSystemTimeDTO.getSubSystemCode().equals(faultSystemDeviceSumDTO.getSystemCode())) {
                                if (ObjectUtil.isNotEmpty(faultSystemTimeDTO.getRepairTime())) {
                                    Double repairTime = faultSystemTimeDTO.getRepairTime();
                                    actualTime = planTime - repairTime;
                                    Double d = new BigDecimal(actualTime /60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    faultSystemReliabilityDTO.setActualRuntime(d);
                                } else {
                                    Double d = new BigDecimal(actualTime /60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    faultSystemReliabilityDTO.setActualRuntime(d);
                                }
                            } else {
                                Double d = new BigDecimal(actualTime /60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                faultSystemReliabilityDTO.setActualRuntime(d);
                            }
                        }

                    }
                }else{
                    Double d = new BigDecimal(actualTime /60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    faultSystemReliabilityDTO.setActualRuntime(d);
                }
                planTime = planTime/60;
                Double plan = new BigDecimal(planTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                faultSystemReliabilityDTO.setScheduledRuntime(plan);
                if (planTime <= 0 || actualTime <= 0) {
                    faultSystemReliabilityDTO.setReliability("0");
                } else {
                    Double d = new BigDecimal(faultSystemReliabilityDTO.getActualRuntime() * 100 /planTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    faultSystemReliabilityDTO.setReliability(d + "%");
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

}
