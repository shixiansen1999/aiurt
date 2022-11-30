package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearAddDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalYearMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalYearService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
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
            Optional.ofNullable(emergencyRehearsalYearDTO.getCode())
                    .ifPresent(code -> wrapper.lambda().eq(EmergencyRehearsalYear::getCode, code));
            Optional.ofNullable(emergencyRehearsalYearDTO.getName())
                    .ifPresent(name -> wrapper.lambda().like(EmergencyRehearsalYear::getName, name));
            Optional.ofNullable(emergencyRehearsalYearDTO.getStatus())
                    .ifPresent(status -> wrapper.lambda().eq(EmergencyRehearsalYear::getStatus, status));
            Optional.ofNullable(emergencyRehearsalYearDTO.getOrgCode())
                    .ifPresent(orgCode -> wrapper.lambda().eq(EmergencyRehearsalYear::getOrgCode, orgCode));
        }
        Page<EmergencyRehearsalYear> pageList = this.page(page, wrapper);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        EmergencyRehearsalYear rehearsalYear = new EmergencyRehearsalYear();
        BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, rehearsalYear);
        // 构造年计划编号
        String code = "NDYJ" + DateUtil.format(new Date(), "yyyyMMdd-");
        EmergencyRehearsalYear emergencyRehearsalYear = this.lambdaQuery().like(EmergencyRehearsalYear::getCode, code)
                .orderByDesc(EmergencyRehearsalYear::getCode)
                .last("limit 1")
                .one();
        if (ObjectUtil.isEmpty(emergencyRehearsalYear)) {
            code += String.format("%02d", 1);
        } else {
            String yearCode = emergencyRehearsalYear.getCode();
            Integer serialNo = Integer.valueOf(yearCode.substring(yearCode.indexOf("-") + 1));
            if (serialNo >= 99) {
                code += (serialNo + 1);
            } else {
                code += String.format("%02d", (serialNo + 1));
            }
        }
        rehearsalYear.setCode(code);
        this.save(rehearsalYear);

        String id = rehearsalYear.getId();
        List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
        if (CollectionUtil.isNotEmpty(monthList)) {
            // 构造月计划编号
            String monthCode = "YYLJH-" + DateUtil.format(new Date(), "yyyyMMdd-");
            EmergencyRehearsalMonth rehearsalMonth = emergencyRehearsalMonthService.lambdaQuery()
                    .like(EmergencyRehearsalMonth::getCode, monthCode)
                    .orderByDesc(EmergencyRehearsalMonth::getCode)
                    .last("limit 1")
                    .one();
            int serialNo = 0;
            if (ObjectUtil.isNotEmpty(rehearsalMonth)) {
                String rehearsalMonthCode = rehearsalMonth.getCode();
                serialNo = Integer.valueOf(rehearsalMonthCode.substring(rehearsalMonthCode.indexOf("-") + 1));
            }
            for (EmergencyRehearsalMonth month : monthList) {
                serialNo++;
                if (999 < serialNo) {
                    monthCode += serialNo;
                } else {
                    monthCode += String.format("%03d", serialNo);
                }
                month.setPlanId(id);
                month.setCode(monthCode);
            }
            emergencyRehearsalMonthService.saveBatch(monthList);
        }
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        EmergencyRehearsalYear rehearsalYear = this.getById(id);
        Assert.notNull(rehearsalYear, "未找到对应数据！");
        // 非待提审状态不允许删除
        if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
            throw new AiurtBootException("已提审的计划不允许删除！");
        }
        this.removeById(id);

        QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
        emergencyRehearsalMonthService.remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String edit(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        String id = emergencyRehearsalYearAddDTO.getId();
        Assert.notNull(id, "记录ID为空！");
        EmergencyRehearsalYear rehearsalYear = this.getById(id);
        Assert.notNull(rehearsalYear, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
            throw new AiurtBootException("已提审的计划不允许编辑！");
        }
        EmergencyRehearsalYear emergencyRehearsalYear = new EmergencyRehearsalYear();
        BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, emergencyRehearsalYear);
        this.updateById(emergencyRehearsalYear);

        QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
        emergencyRehearsalMonthService.remove(wrapper);
        List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
        if (CollectionUtil.isNotEmpty(monthList)) {
            monthList.forEach(l -> {
                l.setPlanId(rehearsalYear.getId());
            });
            emergencyRehearsalMonthService.saveBatch(monthList);
        }
        return id;
    }

    @Override
    public String startProcess(String id) {
        EmergencyRehearsalYear rehearsalYear = this.getById(id);
        Assert.notNull(rehearsalYear, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
            throw new AiurtBootException("该计划已经提审，无需重复提审！");
        }
        rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_2);
        this.updateById(rehearsalYear);
        return id;
    }
}
