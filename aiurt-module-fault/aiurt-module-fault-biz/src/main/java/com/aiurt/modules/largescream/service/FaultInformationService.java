package com.aiurt.modules.largescream.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.dto.FaultLargeCountDTO;
import com.aiurt.modules.fault.dto.FaultLargeInfoDTO;
import com.aiurt.modules.fault.dto.FaultLargeLineInfoDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.largescream.mapper.FaultInformationMapper;
import com.aiurt.modules.largescream.util.DateTimeutil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
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


    /**
     * 综合大屏-故障信息统计数量
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    public FaultLargeCountDTO queryLargeFaultInformation(String boardTimeType,String lineCode){
        String startDate = null;
        String endDate = null;
        FaultLargeCountDTO result = new FaultLargeCountDTO();
        switch (boardTimeType) {
            case "1":
                //本周开始结束时间
                startDate= DateTimeutil.getTimesWeekmorning();
                endDate = DateTimeutil.getTimesWeeknight();
                break;
            case "2":
                //上周开始结束时间
                startDate= DateTimeutil.getBeginDayOfLastWeek();
                endDate = DateTimeutil.getEndDayOfLastWeek();
                break;
            case "3":
                //本月开始结束时间
                startDate= DateTimeutil.getTimesMonthmorning();
                endDate = DateTimeutil.getTimesMonthnight();
                break;
            case "4":
                //上月开始结束时间
                startDate= DateTimeutil.getTimesLastMonthmorning();
                endDate = DateTimeutil.getTimesLastMonthnight();
                break;
            default:
                //本周开始结束时间
                startDate= DateTimeutil.getTimesWeekmorning();
                endDate = DateTimeutil.getTimesWeeknight();
        }
        int count =0;
                    List<Fault> faultList = faultInformationMapper.queryLargeFaultInformation(startDate, endDate, lineCode);
                    //总故障数
                    if(CollUtil.isNotEmpty(faultList)){
                        result.setSum(faultList.size());
                    }
                    //未解决数
                    if(CollUtil.isNotEmpty(faultList)){
                        for (Fault fault : faultList) {
                            if(!FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                                count++;
                            }
                            result.setUnSolve(count);
                        }
                    }
                    String todayStartDate = DateTimeutil.getDayBegin();
                    String todayEndDate = DateTimeutil.getDayEnd();
                    //当天已解决数
                    List<Fault> faultInformationTodaySolve = faultInformationMapper.queryLargeFaultInformationTodaySolve(todayStartDate,todayEndDate, lineCode);
                   if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
                          result.setSolve(faultInformationTodaySolve.size());
                    }
                    //当天新增
                    List<Fault> faults = faultInformationMapper.queryLargeFaultInformationTodayAdd(todayStartDate,todayEndDate, lineCode);
                   if(CollUtil.isNotEmpty(faultInformationTodaySolve)){
                       result.setNewAddNumber(faults.size());
                   }
      return result;
    }


    /**
     * 综合大屏-故障信息统计详情
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    public List<FaultLargeInfoDTO> getLargeFaultInfo(String boardTimeType, String lineCode){
        String startDate = null;
        String endDate = null;
        switch (boardTimeType) {
            case "1":
                //本周开始结束时间
                startDate= DateTimeutil.getTimesWeekmorning();
                endDate = DateTimeutil.getTimesWeeknight();
                break;
            case "2":
                //上周开始结束时间
                startDate= DateTimeutil.getBeginDayOfLastWeek();
                endDate = DateTimeutil.getEndDayOfLastWeek();
                break;
            case "3":
                //本月开始结束时间
                startDate= DateTimeutil.getTimesMonthmorning();
                endDate = DateTimeutil.getTimesMonthnight();
                break;
            case "4":
                //上月开始结束时间
                startDate= DateTimeutil.getTimesLastMonthmorning();
                endDate = DateTimeutil.getTimesLastMonthnight();
                break;
            default:
                //本周开始结束时间
                startDate= DateTimeutil.getTimesWeekmorning();
                endDate = DateTimeutil.getTimesWeeknight();
        }
        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationMapper.getLargeFaultInfo(startDate, endDate, lineCode);

        return largeFaultInfo;
    }


    /**
     *线路故障统计
     * @param boardTimeType
     * @return
     */
    public List<FaultLargeLineInfoDTO> getLargeLineFaultInfo(String boardTimeType){
        List<FaultLargeLineInfoDTO> largeLineInfoDTOS = new ArrayList<>();
        String startDate = null;
        String endDate = null;
        switch (boardTimeType) {
            case "1":
                //本周开始、结束时间
                startDate= DateTimeutil.getTimesWeekmorning();
                endDate = DateTimeutil.getTimesWeeknight();
                break;
            case "2":
                //上周开始结束时间
                startDate= DateTimeutil.getBeginDayOfLastWeek();
                endDate = DateTimeutil.getEndDayOfLastWeek();
                break;
            case "3":
                //本月开始结束时间
                startDate= DateTimeutil.getTimesMonthmorning();
                endDate = DateTimeutil.getTimesMonthnight();
                break;
            case "4":
                //上月开始结束时间
                startDate= DateTimeutil.getTimesLastMonthmorning();
                endDate = DateTimeutil.getTimesLastMonthnight();
                break;
            default:
                //本周开始结束时间
                startDate= DateTimeutil.getTimesWeekmorning();
                endDate = DateTimeutil.getTimesWeeknight();
        }

        List<Fault> largeLineFaultInfo = faultInformationMapper.getLargeLineFaultInfo(startDate, endDate);
        //根据line_code分组，查询同一条线路下的所有故障
        Map<String, List<Fault>> collect = largeLineFaultInfo.stream().collect(Collectors.groupingBy(Fault::getLineCode));
        Set<String> keys = collect.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if("A".equals(key)){
                FaultLargeLineInfoDTO faultLargeLineInfoDTO = new FaultLargeLineInfoDTO();
                faultLargeLineInfoDTO.setLineName("1号线");
                faultLargeLineInfoDTO.setLineCode(key);
                Integer solveCount = 0;
                Integer hangCount = 0;
                List<Fault> faults = collect.get(key);
                //故障总数
                faultLargeLineInfoDTO.setSum(CollUtil.isNotEmpty(faults)?faults.size():0L);
                for (Fault fault : faults) {
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

           else if("2".equals(key)){
                FaultLargeLineInfoDTO faultLargeLineInfoDTO2 = new FaultLargeLineInfoDTO();
                faultLargeLineInfoDTO2.setLineName("2号线");
                faultLargeLineInfoDTO2.setLineCode(key);
                Integer solveCount = 0;
                Integer hangCount = 0;
                List<Fault> faults = collect.get(key);
                //故障总数
                faultLargeLineInfoDTO2.setSum(CollUtil.isNotEmpty(faults)?faults.size():0L);
                for (Fault fault : faults) {
                    if(FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                        solveCount++;
                    }
                    if( FaultStatusEnum.HANGUP.getStatus().equals(fault.getStatus())){
                        hangCount++;
                    }
                    //已解决数
                    faultLargeLineInfoDTO2.setSolve(solveCount);
                    //挂起数
                    faultLargeLineInfoDTO2.setHang(hangCount);
                }
                // 已解决率
                if (faultLargeLineInfoDTO2.getSum() <= 0 || faultLargeLineInfoDTO2.getSolve() <= 0) {
                    faultLargeLineInfoDTO2.setSolveRate("0");
                } else {
                    Integer d = new BigDecimal((Integer) faultLargeLineInfoDTO2.getSolve() * 100 / faultLargeLineInfoDTO2.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
                    faultLargeLineInfoDTO2.setSolveRate(d + "%");
                }
                largeLineInfoDTOS.add(faultLargeLineInfoDTO2);
            }

            else if("334-we123".equals(key)){
                FaultLargeLineInfoDTO faultLargeLineInfoDTO3 = new FaultLargeLineInfoDTO();
                faultLargeLineInfoDTO3.setLineName("3号线");
                faultLargeLineInfoDTO3.setLineCode(key);
                Integer solveCount = 0;
                Integer hangCount = 0;
                List<Fault> faults = collect.get(key);
                //故障总数
                faultLargeLineInfoDTO3.setSum(CollUtil.isNotEmpty(faults)?faults.size():0L);
                for (Fault fault : faults) {
                    if(FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                        solveCount++;
                    }
                    if( FaultStatusEnum.HANGUP.getStatus().equals(fault.getStatus())){
                        hangCount++;
                    }
                    //已解决数
                    faultLargeLineInfoDTO3.setSolve(solveCount);
                    //挂起数
                    faultLargeLineInfoDTO3.setHang(hangCount);
                }
                // 已解决率
                if (faultLargeLineInfoDTO3.getSum() <= 0 || faultLargeLineInfoDTO3.getSolve() <= 0) {
                    faultLargeLineInfoDTO3.setSolveRate("0");
                } else {
                    Integer d = new BigDecimal((Integer) faultLargeLineInfoDTO3.getSolve() * 100 / faultLargeLineInfoDTO3.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
                    faultLargeLineInfoDTO3.setSolveRate(d + "%");
                }
                largeLineInfoDTOS.add(faultLargeLineInfoDTO3);
            }
//            else if("3".equals(key)){
//                FaultLargeLineInfoDTO faultLargeLineInfoDTO4 = new FaultLargeLineInfoDTO();
//                faultLargeLineInfoDTO4.setLineName("3号线");
//                Integer solveCount = 0;
//                Integer hangCount = 0;
//                List<Fault> faults = collect.get(key);
//                //故障总数
//                faultLargeLineInfoDTO4.setSum(CollUtil.isNotEmpty(faults)?faults.size():0L);
//                for (Fault fault : faults) {
//                    if(FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
//                        solveCount++;
//                    }
//                    if( FaultStatusEnum.HANGUP.getStatus().equals(fault.getStatus())){
//                        hangCount++;
//                    }
//                    //已解决数
//                    faultLargeLineInfoDTO4.setSolve(solveCount);
//                    //挂起数
//                    faultLargeLineInfoDTO4.setHang(hangCount);
//                }
//                // 已解决率
//                if (faultLargeLineInfoDTO4.getSum() <= 0 || faultLargeLineInfoDTO4.getSolve() <= 0) {
//                    faultLargeLineInfoDTO4.setSolveRate("0");
//                } else {
//                    double d = new BigDecimal((double) faultLargeLineInfoDTO4.getSolve() * 100 / faultLargeLineInfoDTO4.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//                    faultLargeLineInfoDTO4.setSolveRate(d + "%");
//                }
//                largeLineInfoDTOS.add(faultLargeLineInfoDTO4);
//            }

           else if("line2-3#".equals(key)){
                FaultLargeLineInfoDTO faultLargeLineInfoDTO5 = new FaultLargeLineInfoDTO();
                faultLargeLineInfoDTO5.setLineName("4号线");
                faultLargeLineInfoDTO5.setLineCode(key);
                Integer solveCount = 0;
                Integer hangCount = 0;
                List<Fault> faults = collect.get(key);
                //故障总数
                faultLargeLineInfoDTO5.setSum(CollUtil.isNotEmpty(faults)?faults.size():0L);
                for (Fault fault : faults) {
                    if(FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                        solveCount++;
                    }
                    if( FaultStatusEnum.HANGUP.getStatus().equals(fault.getStatus())){
                        hangCount++;
                    }
                    //已解决数
                    faultLargeLineInfoDTO5.setSolve(solveCount);
                    //挂起数
                    faultLargeLineInfoDTO5.setHang(hangCount);
                }
                // 已解决率
                if (faultLargeLineInfoDTO5.getSum() <= 0 || faultLargeLineInfoDTO5.getSolve() <= 0) {
                    faultLargeLineInfoDTO5.setSolveRate("0");
                } else {
                    Integer d = new BigDecimal((Integer) faultLargeLineInfoDTO5.getSolve() * 100 / faultLargeLineInfoDTO5.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
                    faultLargeLineInfoDTO5.setSolveRate(d + "%");
                }
                largeLineInfoDTOS.add(faultLargeLineInfoDTO5);
            }

           else if("202200705A-1".equals(key)){
                FaultLargeLineInfoDTO faultLargeLineInfoDTO6 = new FaultLargeLineInfoDTO();
                faultLargeLineInfoDTO6.setLineName("8号线");
                faultLargeLineInfoDTO6.setLineCode(key);
                Integer solveCount = 0;
                Integer hangCount = 0;
                List<Fault> faults = collect.get(key);
                //故障总数
                faultLargeLineInfoDTO6.setSum(CollUtil.isNotEmpty(faults)?faults.size():0L);
                for (Fault fault : faults) {
                    if(FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                        solveCount++;
                    }
                    if( FaultStatusEnum.HANGUP.getStatus().equals(fault.getStatus())){
                        hangCount++;
                    }
                    //已解决数
                    faultLargeLineInfoDTO6.setSolve(solveCount);
                    //挂起数
                    faultLargeLineInfoDTO6.setHang(hangCount);
                }
                // 已解决率
                if (faultLargeLineInfoDTO6.getSum() <= 0 || faultLargeLineInfoDTO6.getSolve() <= 0) {
                    faultLargeLineInfoDTO6.setSolveRate("0");
                } else {
                    Integer d = new BigDecimal((Integer) faultLargeLineInfoDTO6.getSolve() * 100 / faultLargeLineInfoDTO6.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
                    faultLargeLineInfoDTO6.setSolveRate(d + "%");
                }
                largeLineInfoDTOS.add(faultLargeLineInfoDTO6);
            }
        }


       return largeLineInfoDTOS;
    }

}
