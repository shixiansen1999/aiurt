package com.aiurt.boot.weeklyplan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.ConstructionConstant;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionCommandAssist;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.mapper.ConstructionWeekPlanCommandMapper;
import com.aiurt.boot.weeklyplan.service.IConstructionCommandAssistService;
import com.aiurt.boot.weeklyplan.service.IConstructionWeekPlanCommandService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Slf4j
@Service
public class ConstructionWeekPlanCommandServiceImpl extends ServiceImpl<ConstructionWeekPlanCommandMapper, ConstructionWeekPlanCommand> implements IConstructionWeekPlanCommandService, IFlowableBaseUpdateStatusService {
    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private ConstructionWeekPlanCommandMapper constructionWeekPlanCommandMapper;
    @Autowired
    private IConstructionCommandAssistService constructionCommandAssistService;

    @Override
    public IPage<ConstructionWeekPlanCommand> queryPageList(Page<ConstructionWeekPlanCommand> page, ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        IPage<ConstructionWeekPlanCommand> pageList = constructionWeekPlanCommandMapper.queryPageList(page, loginUser.getId(), constructionWeekPlanCommandDTO);
        return pageList;
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
    public ConstructionWeekPlanCommand queryById(String id) {
        ConstructionWeekPlanCommand constructionWeekPlanCommand = this.getById(id);
        if (constructionWeekPlanCommand == null) {
            throw new AiurtBootException("未找到对应数据");
        }
        // 辅站信息
        List<ConstructionCommandAssist> assists = constructionCommandAssistService.lambdaQuery()
                .eq(ConstructionCommandAssist::getPlanId, id).list();
        if (CollectionUtil.isNotEmpty(assists)) {
            assists.stream().forEach(l -> {
                String userId = l.getUserId();
                String stationCode = l.getStationCode();
                if (StrUtil.isNotEmpty(userId)) {
                    LoginUser loginUser = iSysBaseApi.getUserById(userId);
                    l.setUserName(loginUser.getRealname());
                }
                if (StrUtil.isNotEmpty(stationCode)) {
                    Map<String, String> stationMap = iSysBaseApi.getStationNameByCode(Arrays.asList(stationCode));
                    l.setStationName(stationMap.get(stationCode));
                }
            });
            constructionWeekPlanCommand.setConstructionAssist(assists);
        }
        return constructionWeekPlanCommand;
    }

    /**
     * 驳回第一个节点
     *
     * @param entity
     */
    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    /**
     * 更新状态
     *
     * @param updateStateEntity
     */
    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        log.info("更新状态参数：{}", JSONObject.toJSONString(updateStateEntity));
    }
}
