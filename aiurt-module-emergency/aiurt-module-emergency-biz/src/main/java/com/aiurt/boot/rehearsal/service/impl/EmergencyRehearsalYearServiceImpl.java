package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearAddDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalYearMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalYearService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyRehearsalYearServiceImpl extends ServiceImpl<EmergencyRehearsalYearMapper, EmergencyRehearsalYear> implements IEmergencyRehearsalYearService, IFlowableBaseUpdateStatusService {

    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private IEmergencyRehearsalMonthService emergencyRehearsalMonthService;
    @Autowired
    private EmergencyRehearsalYearMapper emergencyRehearsalYearMapper;

    @Override
    public IPage<EmergencyRehearsalYear> queryPageList(Page<EmergencyRehearsalYear> page, EmergencyRehearsalYearDTO emergencyRehearsalYearDTO) {
//        QueryWrapper<EmergencyRehearsalYear> wrapper = new QueryWrapper<>();
//        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
//        List<CsUserDepartModel> deptModel = iSysBaseApi.getDepartByUserId(loginUser.getId());
//        List<String> orgCodes = deptModel.stream().filter(l -> StrUtil.isNotEmpty(l.getOrgCode()))
//                .map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(orgCodes)) {
//            return page;
//        }
//        wrapper.lambda().in(EmergencyRehearsalYear::getOrgCode, orgCodes);
//
//        if (ObjectUtil.isNotEmpty(emergencyRehearsalYearDTO)) {
//            Optional.ofNullable(emergencyRehearsalYearDTO.getCode())
//                    .ifPresent(code -> wrapper.lambda().eq(EmergencyRehearsalYear::getCode, code));
//            Optional.ofNullable(emergencyRehearsalYearDTO.getName())
//                    .ifPresent(name -> wrapper.lambda().like(EmergencyRehearsalYear::getName, name));
//            Optional.ofNullable(emergencyRehearsalYearDTO.getStatus())
//                    .ifPresent(status -> wrapper.lambda().eq(EmergencyRehearsalYear::getStatus, status));
//            Optional.ofNullable(emergencyRehearsalYearDTO.getOrgCode())
//                    .ifPresent(orgCode -> wrapper.lambda().eq(EmergencyRehearsalYear::getOrgCode, orgCode));
//            Optional.ofNullable(emergencyRehearsalYearDTO.getYear())
//                    .ifPresent(year -> wrapper.lambda().like(EmergencyRehearsalYear::getYear, year));
//        }
//        Page<EmergencyRehearsalYear> pageList = this.page(page, wrapper);
        Page<EmergencyRehearsalYear> pageList = emergencyRehearsalYearMapper.queryPageList(page, emergencyRehearsalYearDTO, EmergencyConstant.YEAR_STATUS_3);
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
            Integer serialNo = Integer.valueOf(yearCode.substring(yearCode.lastIndexOf("-") + 1));
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
            for (EmergencyRehearsalMonth month : monthList) {
                String monthCode = emergencyRehearsalMonthService.getMonthCode();
                month.setPlanId(id);
                month.setCode(monthCode);
                month.setYearWithin(EmergencyConstant.WITHIN_1);
                emergencyRehearsalMonthService.save(month);
            }
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
            for (EmergencyRehearsalMonth month : monthList) {
                String monthCode = emergencyRehearsalMonthService.getMonthCode();
                month.setPlanId(id);
                month.setCode(monthCode);
                month.setYearWithin(EmergencyConstant.WITHIN_1);
                emergencyRehearsalMonthService.save(month);
            }
        }
        return id;
    }

    /**
     * 保存或者编辑年演练计划信息
     *
     * @param emergencyRehearsalYearAddDTO
     * @return
     */
    public String startProcess(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        String id = emergencyRehearsalYearAddDTO.getId();
        if (StrUtil.isEmpty(id)) {
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
                Integer serialNo = Integer.valueOf(yearCode.substring(yearCode.lastIndexOf("-") + 1));
                if (serialNo >= 99) {
                    code += (serialNo + 1);
                } else {
                    code += String.format("%02d", (serialNo + 1));
                }
            }
            rehearsalYear.setCode(code);
            this.save(rehearsalYear);

            String planId = rehearsalYear.getId();
            List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
            if (CollectionUtil.isNotEmpty(monthList)) {
                for (EmergencyRehearsalMonth month : monthList) {
                    String monthCode = emergencyRehearsalMonthService.getMonthCode();
                    month.setPlanId(planId);
                    month.setCode(monthCode);
                    month.setYearWithin(EmergencyConstant.WITHIN_1);
                    emergencyRehearsalMonthService.save(month);
                }
            }
            return planId;
        } else {
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
                for (EmergencyRehearsalMonth month : monthList) {
                    String monthCode = emergencyRehearsalMonthService.getMonthCode();
                    month.setPlanId(id);
                    month.setCode(monthCode);
                    month.setYearWithin(EmergencyConstant.WITHIN_1);
                    emergencyRehearsalMonthService.save(month);
                }
            }
            return id;
        }
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        EmergencyRehearsalYear rehearsalYear = this.getById(businessKey);
        if (ObjectUtil.isEmpty(rehearsalYear)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        int states = updateStateEntity.getStates();
        switch (states) {
            case 2:
                // 演练计划负责人审批
                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_2);
                break;
            case 3:
                // 演练计划负责人驳回，更新状态为待提交状态
                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_1);
                break;
            case 4:
                // 已通过
                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_3);
                break;
        }
        this.updateById(rehearsalYear);
    }

}
