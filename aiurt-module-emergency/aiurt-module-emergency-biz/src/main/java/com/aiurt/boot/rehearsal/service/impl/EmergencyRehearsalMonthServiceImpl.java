package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalMonthMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @Description: emergency_rehearsal_month
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyRehearsalMonthServiceImpl extends ServiceImpl<EmergencyRehearsalMonthMapper, EmergencyRehearsalMonth> implements IEmergencyRehearsalMonthService {

    @Override
    public String addMonthPlan(EmergencyRehearsalMonth emergencyRehearsalMonth) {
        Assert.notNull(emergencyRehearsalMonth.getPlanId(), "年计划ID不能为空！");
        // 构造月计划编号
        String monthCode = "YYLJH-" + DateUtil.format(new Date(), "yyyyMMdd-");
        EmergencyRehearsalMonth rehearsalMonth = this.lambdaQuery()
                .like(EmergencyRehearsalMonth::getCode, monthCode)
                .orderByDesc(EmergencyRehearsalMonth::getCode)
                .last("limit 1")
                .one();
        int serialNo = 0;
        if (ObjectUtil.isNotEmpty(rehearsalMonth)) {
            String rehearsalMonthCode = rehearsalMonth.getCode();
            serialNo = Integer.valueOf(rehearsalMonthCode.substring(rehearsalMonthCode.lastIndexOf("-") + 1));
        }
        serialNo++;
        if (999 < serialNo) {
            monthCode += serialNo;
        } else {
            monthCode += String.format("%03d", serialNo);
        }
        emergencyRehearsalMonth.setCode(monthCode);
        this.save(emergencyRehearsalMonth);
        return emergencyRehearsalMonth.getId();
    }
}
