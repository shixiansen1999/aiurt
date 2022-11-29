package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalYearMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalYearService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyRehearsalYearServiceImpl extends ServiceImpl<EmergencyRehearsalYearMapper, EmergencyRehearsalYear> implements IEmergencyRehearsalYearService {

    @Override
    public IPage<EmergencyRehearsalYear> queryPageList(Page<EmergencyRehearsalYear> page, EmergencyRehearsalYearDTO emergencyRehearsalYearDTO) {
        QueryWrapper<EmergencyRehearsalYear> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(emergencyRehearsalYearDTO)) {
            if (StrUtil.isNotEmpty(emergencyRehearsalYearDTO.getCode())) {
                wrapper.lambda().eq(EmergencyRehearsalYear::getCode, emergencyRehearsalYearDTO.getCode());
            }
            if (StrUtil.isNotEmpty(emergencyRehearsalYearDTO.getName())) {
                wrapper.lambda().like(EmergencyRehearsalYear::getName, emergencyRehearsalYearDTO.getName());
            }
            if (ObjectUtil.isNotEmpty(emergencyRehearsalYearDTO.getStatus())) {
                wrapper.lambda().eq(EmergencyRehearsalYear::getStatus, emergencyRehearsalYearDTO.getStatus());
            }
        }
        Page<EmergencyRehearsalYear> pageList = this.page(page, wrapper);
        return pageList;
    }
}
