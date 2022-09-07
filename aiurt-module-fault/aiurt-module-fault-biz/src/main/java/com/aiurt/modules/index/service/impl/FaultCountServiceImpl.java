package com.aiurt.modules.index.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.index.mapper.FaultCountMapper;
import com.aiurt.modules.index.service.IFaultCountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022年09月05日 15:54
 */
@Service
public class FaultCountServiceImpl implements IFaultCountService {

   @Autowired
   private FaultCountMapper faultCountMapper;

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
                //计算故障发生时间是否大于12,24,48小时
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
                Date date = new Date();
                String format = sdf.format(date);
                String format1 = sdf.format(fault.getHappenTime());
                Date start = null;
                Date end = null;
                try {
                    start = sdf.parse(format1);
                    end = sdf.parse(format);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                //当前时间减去故障发生时间
                long cha = end.getTime() - start.getTime();
                double result = cha * 1.0 / (1000 * 60 * 60);
                //三级故障超时
                if(result>=12 && result<=24 & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number1++;
                    faultIndexDTO.setLevelThreeNumber(number1);
                }
                //二级故障超时
                else if(result>=24 && result<=48 & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number2++;
                    faultIndexDTO.setLevelTwoNumber(number2);
                }
                //一级故障超时
                else if(result>=48 && !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number3++;
                    faultIndexDTO.setLevelOneNumber(number3);
                }

            }

        }
        return faultIndexDTO;
    }
}
