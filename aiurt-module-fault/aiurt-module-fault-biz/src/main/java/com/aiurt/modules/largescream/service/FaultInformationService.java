package com.aiurt.modules.largescream.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
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
        int count =0;
                    List<Fault> faultList = faultInformationMapper.queryLargeFaultInformation(startDate, endDate, lineCode);
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
                    List<Fault> faultInformationTodaySolve = faultInformationMapper.queryLargeFaultInformationTodaySolve(todayStartDate,todayEndDate, lineCode);
                   if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
                          result.setSolve(faultInformationTodaySolve.size());
                    }else{
                       result.setSolve(0);
                   }
                    //当天新增
                    List<Fault> faults = faultInformationMapper.queryLargeFaultInformationTodayAdd(todayStartDate,todayEndDate, lineCode);
                   if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
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
        switch (faultModule) {
            // 总故障数详情
            case 1:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setLineCode(lineCode);
                break;
            // 未解决故障
            case 2:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setUnSo(1);
                faultScreenModule.setLineCode(lineCode);
                break;
            // 当日新增
            case 3:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodayAdd(1);
                faultScreenModule.setLineCode(lineCode);
                break;
            // 当日已解决
            case 4:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayStartDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodaySolve(1);
                faultScreenModule.setLineCode(lineCode);
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
        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationMapper.getLargeFaultInfo(startDate, endDate, lineCode);
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

        List<Fault> largeLineFaultInfo = faultInformationMapper.getLargeLineFaultInfo(startDate, endDate);
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
        for (int i = 1; i<=6; i++) {
            int sum = 0;
            //创建一个新的系统故障单集合
            List<FaultSystemTimeDTO> systemlist = new ArrayList<>();
            //月份故障单
            FaultMonthTimeDTO faultMonthTimeDTO = new FaultMonthTimeDTO();
            //获取最近半年月份，上一个开始
            String month = FaultLargeDateUtil.getLast12Months(i);
            String substring = month.substring(5,7);
            String changmonth = substring+"月";
            faultMonthTimeDTO.setMonth(changmonth);
            //查询按系统分类好的并计算了故障消耗总时长的记录
            List<FaultSystemTimeDTO> largeFaultTime = faultInformationMapper.getLargeFaultTime(month, lineCode);
                for (FaultSystemTimeDTO faultSystemTimeDTO : largeFaultTime) {
                    if (!"0".equals(faultSystemTimeDTO.getRepairTime()) && faultSystemTimeDTO.getRepairTime()!=null) {
                        sum += Integer.parseInt(faultSystemTimeDTO.getRepairTime());
                    }
                    //将故障处理时间为null的改为0
                    if(faultSystemTimeDTO.getRepairTime()==null){
                        faultSystemTimeDTO.setRepairTime("0");
                    }
                       //将故障处理时间+H
                        String h = faultSystemTimeDTO.getRepairTime()+"H";
                        faultSystemTimeDTO.setRepairTime(h);
                        //将名字改成系统+小时数
                      String strm = faultSystemTimeDTO.getSystemName().substring(0,faultSystemTimeDTO.getSystemName().length()-2);   //截掉
                      String name = strm+" "+faultSystemTimeDTO.getRepairTime();
                      faultSystemTimeDTO.setSystemName(name);
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
        List<FaultDataStatisticsDTO> dtoList = new ArrayList<>();
        String firstDay = null;
        String lastDay = null;
        if (StrUtil.isNotBlank(faultDataStatisticsDTO.getMonth())) {
            String month = faultDataStatisticsDTO.getMonth();
            Integer i = Convert.toInt(month);
            Map<String, String> map = FaultLargeDateUtil.getMonthFirstAndLast(i+1);
             firstDay = map.get("firstDay");
             lastDay = map.get("lastDay");
            faultDataStatisticsDTO.setFirstDay(firstDay);
            faultDataStatisticsDTO.setLastDay(lastDay);
        }

        List<String> allSystemCode = faultInformationMapper.getAllSystemCode();
        for (int i = 0; i < allSystemCode.size(); i++) {
            faultDataStatisticsDTO.setSubSystemCode(allSystemCode.get(i));
            Integer yearFault = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            FaultDataStatisticsDTO dto = new FaultDataStatisticsDTO();
            dto.setId(String.valueOf(i));
            dto.setSubSystemCode(allSystemCode.get(i));
            dto.setFaultSum(yearFault);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public FaultDataStatisticsDTO getFaultAnalysis(FaultDataStatisticsDTO faultDataStatisticsDTO) {
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
            //自检数量
            faultDataStatisticsDTO.setFaultModeCode(FaultConstant.FAULT_MODE_CODE_0);
            Integer selfCheckFaultNum = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            BigDecimal decimal1 = new BigDecimal((selfCheckFaultNum / yearFault) * 100).setScale(1, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setSelfCheckFaultNum(decimal1);
            //报修数量
            Integer repairFaultNum = yearFault - selfCheckFaultNum;
            BigDecimal decimal2 = new BigDecimal((repairFaultNum / yearFault) * 100).setScale(1, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setRepairFaultNum(decimal2);
            //已完成数量
            faultDataStatisticsDTO.setFaultModeCode(null);
            faultDataStatisticsDTO.setStatus(FaultStatusEnum.Close.getStatus());
            Integer completedFaultNum = faultInformationMapper.getYearFault(faultDataStatisticsDTO);
            BigDecimal decimal3 = new BigDecimal((completedFaultNum / yearFault) * 100).setScale(1, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setCompletedFaultNum(decimal3);
            //未完成数量
            Integer undoneFaultNum = yearFault - completedFaultNum;
            BigDecimal decimal4 = new BigDecimal((undoneFaultNum / yearFault) * 100).setScale(1, BigDecimal.ROUND_HALF_UP);
            faultDataStatisticsDTO.setUndoneFaultNum(decimal4);
        } else {
            faultDataStatisticsDTO.setSelfCheckFaultNum(new BigDecimal(0));
            faultDataStatisticsDTO.setRepairFaultNum(new BigDecimal(0));
            faultDataStatisticsDTO.setCompletedFaultNum(new BigDecimal(0));
            faultDataStatisticsDTO.setUndoneFaultNum(new BigDecimal(0));
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
//        String dateTime1 = FaultLargeDateUtil.getDateTime(boardTimeType);
//        String[] split1 = dateTime1.split("~");
//        Date startDate = DateUtil.parse(split1[0]);
//        Date endDate = DateUtil.parse(split1[1]);
        //获取本周或本月时间
        String dateTime = FaultLargeDateUtil.getDateTime(boardTimeType);
        String[] split = dateTime.split("~");
        Date weekStartDate = DateUtil.parse(split[0]);
        Date weekEndDate = DateUtil.parse(split[1]);
        int count =0;
        List<Fault> faultList = faultInformationMapper.queryFaultDataInformation(lineCode);
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
        List<Fault> faultDataInformationweekSolve = faultInformationMapper.queryFaultDataInformationWeekSolve(weekStartDate, weekEndDate, lineCode);
        if(CollUtil.isNotEmpty(faultDataInformationweekSolve)){
            result.setWeekSolve(faultDataInformationweekSolve.size());
        }else{
            result.setWeekSolve(0);
        }
        //本周新增
        List<Fault> faultDataInformationweekAdd = faultInformationMapper.queryFaultDataInformationWeekAdd(weekStartDate, weekEndDate, lineCode);
        if(CollUtil.isNotEmpty(faultDataInformationweekAdd)){
            result.setWeekAdd(faultDataInformationweekAdd.size());
        }else{
            result.setWeekAdd(0);
        }
        //当天开始结束时间
        Date todayStartDate = DateUtil.beginOfDay(new Date());
        Date todayEndDate = DateUtil.endOfDay(new Date());
        //当天已解决数
        List<Fault> faultInformationTodaySolve = faultInformationMapper.queryLargeFaultInformationTodaySolve(todayStartDate,todayEndDate, lineCode);
        if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
            result.setTodaySolve(faultInformationTodaySolve.size());
        }else{
            result.setTodaySolve(0);
        }
        //当天新增
        List<Fault> faults = faultInformationMapper.queryLargeFaultInformationTodayAdd(todayStartDate,todayEndDate, lineCode);
        if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
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
        switch (faultModule) {
            // 故障总数
            case 1:
                faultScreenModule.setLineCode(lineCode);
                break;
            // 未解决故障
            case 2:
                faultScreenModule.setUnSo(1);
                faultScreenModule.setLineCode(lineCode);
                break;
            // 本周或本月新增
            case 3:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setWeekAdd(1);
                faultScreenModule.setLineCode(lineCode);
                break;
            // 本周或本月修复
            case 4:
                faultScreenModule.setStartDate(startDate);
                faultScreenModule.setEndDate(endDate);
                faultScreenModule.setWeekSolve(1);
                faultScreenModule.setLineCode(lineCode);
                break;
            // 当日新增
            case 5:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayEndDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodayAdd(1);
                faultScreenModule.setLineCode(lineCode);
                break;
            // 当日已解决
            case 6:
                faultScreenModule.setStartDate(null);
                faultScreenModule.setEndDate(null);
                faultScreenModule.setTodayStartDate(DateUtil.beginOfDay(new Date()));
                faultScreenModule.setTodayStartDate(DateUtil.endOfDay(new Date()));
                faultScreenModule.setTodaySolve(1);
                faultScreenModule.setLineCode(lineCode);
                break;
        }
        List<FaultLargeInfoDTO> largeFaultDataInfo = faultInformationMapper.getLargeFaultDataDatails(faultScreenModule);
        largeFaultDataInfo.stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseAPI.getDictItems(FaultDictCodeConstant.FAULT_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);
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
        List<FaultDataAnalysisInfoDTO> largeFaultDataInfo = faultInformationMapper.getLargeFaultDataInfo(startDate,endDate,lineCode);
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
            List<FaultTimeoutLevelDTO> faultData = faultInformationMapper.getFaultData(level,startDate, endDate,lineCode);
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


}
