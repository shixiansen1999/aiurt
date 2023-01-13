package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalMonthDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.mapper.EmergencyImplementationRecordMapper;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalMonthMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import com.aiurt.boot.rehearsal.vo.EmergencyRehearsalMonthVO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: emergency_rehearsal_month
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyRehearsalMonthServiceImpl extends ServiceImpl<EmergencyRehearsalMonthMapper, EmergencyRehearsalMonth> implements IEmergencyRehearsalMonthService {
    @Autowired
    private EmergencyRehearsalMonthMapper emergencyRehearsalMonthMapper;
    @Autowired
    private EmergencyImplementationRecordMapper emergencyImplementationRecordMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Override
    public String addMonthPlan(EmergencyRehearsalMonth emergencyRehearsalMonth) {
        Assert.notNull(emergencyRehearsalMonth.getPlanId(), "年计划ID不能为空！");
        // 构造月计划编号
        String monthCode = this.getMonthCode();
        emergencyRehearsalMonth.setCode(monthCode);
        emergencyRehearsalMonth.setYearWithin(EmergencyConstant.WITHIN_0);
        this.save(emergencyRehearsalMonth);
        return emergencyRehearsalMonth.getId();
    }

    /**
     * 生成月计划编号
     *
     * @return
     */
    @Override
    public String getMonthCode() {
        // 构造月计划编号
        String code = "YYLJH-" + DateUtil.format(new Date(), "yyyyMMdd-");
        EmergencyRehearsalMonth rehearsalMonth = this.lambdaQuery()
                .like(EmergencyRehearsalMonth::getCode, code)
                .orderByDesc(EmergencyRehearsalMonth::getCode)
                .last("limit 1")
                .one();
        int serialNo = 0;
        if (ObjectUtil.isNotEmpty(rehearsalMonth)) {
            String rehearsalMonthCode = rehearsalMonth.getCode();
            serialNo = Integer.valueOf(rehearsalMonthCode.substring(rehearsalMonthCode.lastIndexOf("-") + 1));
        }
        serialNo++;
        if (999 >= serialNo) {
            code += String.format("%03d", serialNo);
        } else {
            code += serialNo;
        }
        return code;
    }

    @Override
    public IPage<EmergencyRehearsalMonthVO> queryPageList(Page<EmergencyRehearsalMonthVO> page, EmergencyRehearsalMonthDTO emergencyRehearsalMonthDTO) {
        if (ObjectUtil.isEmpty(emergencyRehearsalMonthDTO) || StrUtil.isEmpty(emergencyRehearsalMonthDTO.getPlanId())) {
            throw new AiurtBootException("年演练计划ID不能为空！");
        }
        // 允许挑选用户组织机构权限下的月计划
        if (ObjectUtil.isNotEmpty(emergencyRehearsalMonthDTO.getRecordInterface()) && emergencyRehearsalMonthDTO.getRecordInterface()) {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
            List<CsUserDepartModel> depts = sysBaseApi.getDepartByUserId(loginUser.getId());
            List<String> orgCodes = depts.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(orgCodes)) {
                return page;
            }
            emergencyRehearsalMonthDTO.setOrgCodes(orgCodes);
        }
        IPage<EmergencyRehearsalMonthVO> pageList = emergencyRehearsalMonthMapper.queryPageList(page, emergencyRehearsalMonthDTO);
        pageList.getRecords().forEach(monthPlan -> {
            boolean exists = emergencyImplementationRecordMapper.exists(new LambdaQueryWrapper<EmergencyImplementationRecord>()
                    .eq(EmergencyImplementationRecord::getPlanId, monthPlan.getId())
                    .eq(EmergencyImplementationRecord::getDelFlag, CommonConstant.DEL_FLAG_0));
            monthPlan.setDelete(!exists);
        });
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        boolean exists = emergencyImplementationRecordMapper.exists(new LambdaQueryWrapper<EmergencyImplementationRecord>()
                .eq(EmergencyImplementationRecord::getPlanId, id));
        if (exists) {
            throw new AiurtBootException("该月计划应急演练记录已在使用，不允许删除！");
        }
        this.removeById(id);
    }
}
