package com.aiurt.modules.largescream.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.modules.fault.constants.FaultDictCodeConstant;
import com.aiurt.modules.fault.dto.FaultLargeCountDTO;
import com.aiurt.modules.fault.dto.FaultLargeInfoDTO;
import com.aiurt.modules.fault.dto.FaultLargeLineInfoDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.largescream.mapper.FaultInformationMapper;
import com.aiurt.modules.largescream.util.DateTimeutil;
import com.aiurt.modules.largescream.util.FaultLargeDateUtil;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

}
