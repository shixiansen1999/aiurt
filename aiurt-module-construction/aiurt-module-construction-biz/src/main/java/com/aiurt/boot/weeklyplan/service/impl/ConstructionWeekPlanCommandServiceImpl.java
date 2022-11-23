package com.aiurt.boot.weeklyplan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.ConstructionConstant;
import com.aiurt.boot.constant.ConstructionDictConstant;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionCommandAssist;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.mapper.ConstructionWeekPlanCommandMapper;
import com.aiurt.boot.weeklyplan.service.IConstructionCommandAssistService;
import com.aiurt.boot.weeklyplan.service.IConstructionWeekPlanCommandService;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Service
public class ConstructionWeekPlanCommandServiceImpl extends ServiceImpl<ConstructionWeekPlanCommandMapper, ConstructionWeekPlanCommand> implements IConstructionWeekPlanCommandService {
    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private ConstructionWeekPlanCommandMapper constructionWeekPlanCommandMapper;
    @Autowired
    private IConstructionCommandAssistService constructionCommandAssistService;

    @Override
    public IPage<ConstructionWeekPlanCommandVO> queryPageList(Page<ConstructionWeekPlanCommandVO> page, ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO) {
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandMapper.queryPageList(page, constructionWeekPlanCommandDTO);
        // 字典和站点、线路、人员信息转换
        List<String> dictCodes = Arrays.asList(ConstructionDictConstant.WEEK, ConstructionDictConstant.APPROVE, ConstructionDictConstant.CATEGORY,
                ConstructionDictConstant.PLAN_TYPE, ConstructionDictConstant.NATURE, ConstructionDictConstant.STATUS);
        Map<String, List<DictModel>> dictItems = iSysBaseApi.getManyDictItems(dictCodes);
        pageList.getRecords().forEach(command -> {
            conversion(command, dictItems);
        });
        return pageList;
    }

    /**
     * 人员，站点，字典信息转换
     */
    public void conversion(ConstructionWeekPlanCommandVO command, Map<String, List<DictModel>> dictItems) {
        // 字典
        String typeDictName = dictItems.get(ConstructionDictConstant.CATEGORY).stream().filter(l -> command.getType().equals(l.getValue())).map(DictModel::getText).findFirst().get();
        String planChangeDictName = dictItems.get(ConstructionDictConstant.PLAN_TYPE).stream().filter(l -> command.getPlanChange().equals(l.getValue())).map(DictModel::getText).findFirst().get();
        String weekdayDictName = dictItems.get(ConstructionDictConstant.WEEK).stream().filter(l -> command.getWeekday().equals(l.getValue())).map(DictModel::getText).findFirst().get();
        String formStatus = dictItems.get(ConstructionDictConstant.STATUS).stream().filter(l -> command.getFormStatus().equals(l.getValue())).map(DictModel::getText).findFirst().get();
        List<DictModel> approveList = dictItems.get(ConstructionDictConstant.APPROVE);
        String natureName = dictItems.get(ConstructionDictConstant.NATURE).stream().filter(l -> command.getNature().equals(l.getValue())).map(DictModel::getText).findFirst().get();

        // 字典名称
        command.setTypeDictName(typeDictName);
        command.setPlanChangeDictName(planChangeDictName);
        command.setWeekdayDictName(weekdayDictName);
        command.setFormStatusDictName(formStatus);
        command.setDispatchStatusDictName(approveList.stream().filter(l -> command.getDispatchStatus().equals(l.getValue())).map(DictModel::getText).findFirst().get());
        command.setLineStatusDictName(approveList.stream().filter(l -> command.getLineStatus().equals(l.getValue())).map(DictModel::getText).findFirst().get());
        command.setDirectorStatusDictName(approveList.stream().filter(l -> command.getDirectorStatus().equals(l.getValue())).map(DictModel::getText).findFirst().get());
        command.setManagerStatusDictName(approveList.stream().filter(l -> command.getManagerStatus().equals(l.getValue())).map(DictModel::getText).findFirst().get());
        command.setNatureDictName(natureName);

        // 组织机构名称
        String orgCode = command.getOrgCode();
        String coordinationDepartmentCode = command.getCoordinationDepartmentCode();
        command.setOrgName(ObjectUtil.isEmpty(orgCode) ? null : Optional.ofNullable(iSysBaseApi.getDepartByOrgCode(orgCode))
                .orElseGet(SysDepartModel::new).getDepartName());
        command.setCoordinationDepartmentName(ObjectUtil.isEmpty(coordinationDepartmentCode) ? null : Optional
                .ofNullable(iSysBaseApi.getDepartByOrgCode(coordinationDepartmentCode)).orElseGet(SysDepartModel::new).getDepartName());

        // 站点名称
        String firstStationCode = command.getFirstStationCode();
        String secondStationCode = command.getSecondStationCode();
        command.setFirstStationName(ObjectUtil.isEmpty(firstStationCode) ? null : iSysBaseApi.getStationNameByCode(Arrays.asList(firstStationCode)).get(firstStationCode));
        command.setSecondStationName(ObjectUtil.isEmpty(secondStationCode) ? null : iSysBaseApi.getStationNameByCode(Arrays.asList(secondStationCode)).get(secondStationCode));

        // 线路名称
        command.setLineName(ObjectUtil.isEmpty(command.getLineCode()) ? null : iSysBaseApi.getLineNameByCode(Arrays.asList(command.getLineCode())).get(command.getLineCode()));
        // 工区
//        command.setSiteName(ObjectUtil.isEmpty(command.getChargeStaffId()) ? null :);

        // 人员名称
        command.setChargeStaffName(ObjectUtil.isEmpty(command.getChargeStaffId()) ? null :
                Optional.ofNullable(iSysBaseApi.getUserById(command.getChargeStaffId())).orElseGet(LoginUser::new).getRealname());
        command.setApplyName(ObjectUtil.isNotEmpty(command.getApplyId()) ? null :
                Optional.ofNullable(iSysBaseApi.getUserById(command.getApplyId())).orElseGet(LoginUser::new).getRealname());
        command.setLineUserName(ObjectUtil.isEmpty(command.getLineUserId()) ? null :
                Optional.ofNullable(iSysBaseApi.getUserById(command.getLineUserId())).orElseGet(LoginUser::new).getRealname());
        command.setDispatchName(ObjectUtil.isEmpty(command.getDispatchId()) ? null :
                Optional.ofNullable(iSysBaseApi.getUserById(command.getDispatchId())).orElseGet(LoginUser::new).getRealname());
        command.setDirectorName(ObjectUtil.isEmpty(command.getDirectorId()) ? null :
                Optional.ofNullable(iSysBaseApi.getUserById(command.getDirectorId())).orElseGet(LoginUser::new).getRealname());
        command.setManagerName(ObjectUtil.isEmpty(command.getManagerId()) ? null :
                Optional.ofNullable(iSysBaseApi.getUserById(command.getManagerId())).orElseGet(LoginUser::new).getRealname());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String declaration(ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        this.save(constructionWeekPlanCommand);
        List<ConstructionCommandAssist> constructionAssist = constructionWeekPlanCommand.getConstructionAssist();
        if (CollectionUtil.isNotEmpty(constructionAssist)) {
            constructionCommandAssistService.saveBatch(constructionWeekPlanCommand.getConstructionAssist());
        }
        return constructionWeekPlanCommand.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        Integer formStatus = constructionWeekPlanCommand.getFormStatus();
        if (!ConstructionConstant.FORM_STATUS_0.equals(formStatus) || !ConstructionConstant.FORM_STATUS_3.equals(formStatus)) {
            throw new AiurtBootException("该记录在审核中，不能进行修改！");
        }
        this.updateById(constructionWeekPlanCommand);
        QueryWrapper<ConstructionCommandAssist> assistWrapper = new QueryWrapper<>();
        assistWrapper.lambda().eq(ConstructionCommandAssist::getPlanId, constructionWeekPlanCommand.getId());
        constructionCommandAssistService.remove(assistWrapper);
        List<ConstructionCommandAssist> assists = constructionWeekPlanCommand.getConstructionAssist();
        if (CollectionUtil.isNotEmpty(assists)) {
            constructionCommandAssistService.saveBatch(assists);
        }
    }

    @Override
    public void cancel(String id, String reason) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(id);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        command.setFormStatus(ConstructionConstant.FORM_STATUS_4);
        command.setCancelReason(reason);
        command.setCancelId(loginUser.getId());
        this.updateById(command);
    }

    @Override
    public void submit(String id) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(id);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        Integer formStatus = command.getFormStatus();
        // 已取消的计划不给提审
        if (ConstructionConstant.FORM_STATUS_4.equals(formStatus)) {
            throw new AiurtBootException("该周计划已经取消！");
        }
        // 只有为待提审状态的计划才可以提审
        if (!ConstructionConstant.APPROVE_STATUS_0.equals(formStatus)) {
            throw new AiurtBootException("该周计划已在审批中或已完成审批！");
        }
        command.setApplyId(loginUser.getId());
        // 修改状态为待审核
        command.setFormStatus(ConstructionConstant.FORM_STATUS_1);
        this.updateById(command);
    }

    @Override
    public void audit(String id) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(id);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        // todo 审批逻辑
    }

    @Override
    public ConstructionWeekPlanCommandVO queryById(String id) {
        ConstructionWeekPlanCommand constructionWeekPlanCommand = this.getById(id);
        if (constructionWeekPlanCommand == null) {
            throw new AiurtBootException("未找到对应数据");
        }
        ConstructionWeekPlanCommandVO commandVO = new ConstructionWeekPlanCommandVO();
        BeanUtils.copyProperties(constructionWeekPlanCommand, commandVO);

        QueryWrapper<ConstructionCommandAssist> assistWrapper = new QueryWrapper<>();
        assistWrapper.lambda().eq(ConstructionCommandAssist::getPlanId, id);
        List<ConstructionCommandAssist> assists = constructionCommandAssistService.list(assistWrapper);
        if (CollectionUtil.isNotEmpty(assists)) {
            commandVO.setConstructionAssist(assists);
        }
        // 字典和站点、线路、人员信息转换
        List<String> dictCodes = Arrays.asList(ConstructionDictConstant.WEEK, ConstructionDictConstant.APPROVE, ConstructionDictConstant.CATEGORY,
                ConstructionDictConstant.PLAN_TYPE, ConstructionDictConstant.NATURE, ConstructionDictConstant.STATUS);
        Map<String, List<DictModel>> dictItems = iSysBaseApi.getManyDictItems(dictCodes);
        this.conversion(commandVO, dictItems);
        return commandVO;
    }
}
