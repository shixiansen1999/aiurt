package com.aiurt.modules.index.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelReq;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.index.mapper.FaultCountMapper;
import com.aiurt.modules.index.service.IFaultCountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 首页故障统计
 *
 * @author: qkx
 * @date: 2022年09月05日 15:54
 */
@Service
public class FaultCountServiceImpl implements IFaultCountService {

   @Autowired
   private FaultCountMapper faultCountMapper;

   @Autowired
   private IFaultDeviceService faultDeviceService;

    /**
     * 首页统计故障概况
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public FaultIndexDTO queryFaultCount(Date startDate, Date endDate) {
        FaultIndexDTO faultIndexDTO = new FaultIndexDTO();
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return faultIndexDTO;
        }

        //将符合条件的故障数据查出
        LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Fault::getApprovalPassTime, DateUtil.beginOfDay(startDate));
        queryWrapper.le(Fault::getApprovalPassTime, DateUtil.beginOfDay(endDate));
        List<Fault> faultList = faultCountMapper.queryFaultCount(startDate,endDate);

        //故障总数
        faultIndexDTO.setSum(CollUtil.isNotEmpty(faultList)?faultList.size():0L);
        //已解决数
        faultIndexDTO.setSolve(CollUtil.isNotEmpty(faultList)?faultList.stream().filter(re -> FaultStatusEnum.Close.getStatus().equals(re.getStatus())).count() : 0L);
        //未解决数
        faultIndexDTO.setUnSolve(CollUtil.isNotEmpty(faultList)?faultList.stream().filter(re -> !FaultStatusEnum.Close.getStatus().equals(re.getStatus())).count() : 0L);
        //挂起数
        faultIndexDTO.setHang(CollUtil.isNotEmpty(faultList)?faultList.stream().filter(re -> FaultStatusEnum.HANGUP.getStatus().equals(re.getStatus())).count() : 0L);

        // 已解决率
        if (faultIndexDTO.getSum() <= 0 || faultIndexDTO.getSolve() <= 0) {
            faultIndexDTO.setSolveRate("0");
        } else {
            double d = new BigDecimal((double) faultIndexDTO.getSolve() * 100 / faultIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            faultIndexDTO.setSolveRate(d + "%");
        }


        //故障等级数量统计
        if(faultList!=null &&! faultList.isEmpty()){
            int number1 = 0;
            int number2 = 0;
            int number3 = 0;
            for (Fault fault : faultList) {
                //计算故障发生时间到当前时间时间差
                long result=DateUtil.between(fault.getHappenTime(),new Date(), DateUnit.HOUR);

                //三级故障超时(12-24小时)
                if(result>=12 && result<=24 & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number1++;
                    faultIndexDTO.setLevelThreeNumber(number1);
                }
                //二级故障超时(24-48小时)
                else if(result>=24 && result<=48 & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number2++;
                    faultIndexDTO.setLevelTwoNumber(number2);
                }
                //一级故障超时(大于48小时)
                else if(result>=48 && !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number3++;
                    faultIndexDTO.setLevelOneNumber(number3);
                }

            }

        }
        return faultIndexDTO;
    }

    /**
     *分页查询故障超时等级
     * @param faultTimeoutLevelReq 查询条件
     * @return
     */
    public IPage<FaultTimeoutLevelDTO> getFaultLevelInfo(FaultTimeoutLevelReq faultTimeoutLevelReq) {
        IPage<FaultTimeoutLevelDTO> result = new Page<>();
        if (ObjectUtil.isEmpty(faultTimeoutLevelReq.getLevel())
                || ObjectUtil.isEmpty(faultTimeoutLevelReq)
                || ObjectUtil.isEmpty(faultTimeoutLevelReq.getStartTime())
                || ObjectUtil.isEmpty(faultTimeoutLevelReq.getEndTime())) {
            return result;
        }
        // 分页数据
        Page<FaultTimeoutLevelDTO> page = new Page<>(faultTimeoutLevelReq.getPageNo(), faultTimeoutLevelReq.getPageSize());
        List<FaultTimeoutLevelDTO> faultData = faultCountMapper.getFaultData(faultTimeoutLevelReq.getLevel(), page, faultTimeoutLevelReq);
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
                long min=DateUtil.between(faultDatum.getHappenTime(),new Date(), DateUnit.MINUTE);
                int m = ((new Double(min % 60))).intValue();
                String time = hour + "h" + m + "min";

                if (faultTimeoutLevelReq.getLevel() == 1) {
                    faultDatum.setTimeoutDuration(time);
                    if (hour >= 48 && !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("一级超时");
                    }
                } else if (faultTimeoutLevelReq.getLevel() == 2) {
                    faultDatum.setTimeoutDuration(time);
                    if (hour >= 24 && hour <= 48 & !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("二级超时");
                    }
                } else if (faultTimeoutLevelReq.getLevel() == 3) {
                    faultDatum.setTimeoutDuration(time);
                    if (hour >= 12 && hour <= 24 & !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("三级超时");
                    }
                }

            }
        }
        page.setRecords(faultData);
        return page;
    }



    /**
     *分页查询待办事项故障情况
     * @param page
     * @param startDate
     * @return
     */
    public IPage<FaultTimeoutLevelDTO> getMainFaultCondition(Page<FaultTimeoutLevelDTO> page, Date startDate){
        List<FaultTimeoutLevelDTO> mainFaultCondition = faultCountMapper.getMainFaultCondition(page, startDate);
        return page.setRecords(mainFaultCondition);
    }

}
