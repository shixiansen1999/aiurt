package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearAddDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalYearMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalYearService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyRehearsalYearServiceImpl extends ServiceImpl<EmergencyRehearsalYearMapper, EmergencyRehearsalYear> implements IEmergencyRehearsalYearService {

    @Autowired
    private IEmergencyRehearsalMonthService emergencyRehearsalMonthService;

    @Override
    public IPage<EmergencyRehearsalYear> queryPageList(Page<EmergencyRehearsalYear> page, EmergencyRehearsalYearDTO emergencyRehearsalYearDTO) {
        QueryWrapper<EmergencyRehearsalYear> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(emergencyRehearsalYearDTO)) {
            Optional.ofNullable(emergencyRehearsalYearDTO.getCode()).ifPresent(code -> {
                wrapper.lambda().eq(EmergencyRehearsalYear::getCode, code);
            });
            Optional.ofNullable(emergencyRehearsalYearDTO.getName()).ifPresent(name -> {
                wrapper.lambda().like(EmergencyRehearsalYear::getName, name);
            });
            Optional.ofNullable(emergencyRehearsalYearDTO.getStatus()).ifPresent(status -> {
                wrapper.lambda().eq(EmergencyRehearsalYear::getStatus, status);
            });
            Optional.ofNullable(emergencyRehearsalYearDTO.getOrgCode()).ifPresent(orgCode -> {
                wrapper.lambda().eq(EmergencyRehearsalYear::getOrgCode, orgCode);
            });
        }
        Page<EmergencyRehearsalYear> pageList = this.page(page, wrapper);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        EmergencyRehearsalYear rehearsalYear = new EmergencyRehearsalYear();
        BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, rehearsalYear);
        boolean save = this.save(rehearsalYear);

        List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
        monthList.forEach(l -> {
            l.setPlanId(rehearsalYear.getId());
        });
        boolean saveBatch = emergencyRehearsalMonthService.saveBatch(monthList);
        return save && saveBatch;
    }
}
