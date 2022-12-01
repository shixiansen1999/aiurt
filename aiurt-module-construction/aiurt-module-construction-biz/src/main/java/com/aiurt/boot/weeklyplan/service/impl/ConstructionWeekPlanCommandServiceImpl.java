package com.aiurt.boot.weeklyplan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
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
import com.aiurt.common.util.RedisUtil;
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
import org.jeecg.common.system.vo.DictModel;
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
    public IPage<ConstructionWeekPlanCommandVO> queryPageList(Page<ConstructionWeekPlanCommandVO> page, ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandMapper.queryPageList(page, loginUser.getId(), constructionWeekPlanCommandDTO);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String declaration(ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }

        // 生成计划令编码
        StringBuilder code = new StringBuilder();
        List<DictModel> types = iSysBaseApi.getDictItems(ConstructionDictConstant.CATEGORY);
        String typeName = types.stream().filter(l -> l.getValue().equals(constructionWeekPlanCommand.getType()))
                .map(DictModel::getText).findFirst().get();

        // 临时修补计划和日计划
        if (ConstructionConstant.PLAN_TYPE_2.equals(constructionWeekPlanCommand.getPlanChange())
                || ConstructionConstant.PLAN_TYPE_3.equals(constructionWeekPlanCommand.getPlanChange())) {
            code = new StringBuilder("L-");
        }

        // 获取施工的日期对应的日
        String day = DateUtil.format(constructionWeekPlanCommand.getTaskDate(), "dd");

        // 构建计划令编号
        String separator = "-";
        code.append(constructionWeekPlanCommand.getWorkline()).append(typeName).append(separator).append(day).append(separator);

        // 计划令自增序号，如果是一位或两位数的则保留两位，三位则保留三位，即6->06、66->66,大于99小于1000则保留三位
        List<ConstructionWeekPlanCommand> codeNumbers = this.lambdaQuery().like(ConstructionWeekPlanCommand::getCode, code.toString())
                .orderByDesc(ConstructionWeekPlanCommand::getCode)
                .last("limit 1")
                .list();

        if (CollectionUtil.isNotEmpty(codeNumbers) && ObjectUtil.isNotEmpty(codeNumbers.get(0).getCode())) {
            String planCode = codeNumbers.get(0).getCode();
            Integer serialNumber = Integer.valueOf(planCode.substring(planCode.lastIndexOf(separator) + 1));
            if (100 > serialNumber) {
                code.append(String.format("%02d", serialNumber + 1));
            } else {
                code.append(serialNumber + 1);
            }
        } else {
            code.append(String.format("%02d", 1));
        }

        constructionWeekPlanCommand.setCode(code.toString());
        constructionWeekPlanCommand.setApplyId(loginUser.getId());
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
        if (ConstructionConstant.FORM_STATUS_5.equals(command.getFormStatus())) {
            throw new AiurtBootException("已通过的任务不能取消！");
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
        // 只有为待提审状态或者已驳回状态的计划才可以提审
        if (!ConstructionConstant.FORM_STATUS_0.equals(formStatus)
                && !ConstructionConstant.FORM_STATUS_3.equals(formStatus)) {
            throw new AiurtBootException("该周计划已在审批中或已完成审批！");
        }
        command.setApplyId(loginUser.getId());
        // 修改状态为待审核
        command.setFormStatus(ConstructionConstant.FORM_STATUS_1);
        // 初始化各个角色的审批状态
        command.setLineStatus(ConstructionConstant.APPROVE_STATUS_0);
        command.setDirectorStatus(ConstructionConstant.APPROVE_STATUS_0);
        command.setDispatchStatus(ConstructionConstant.APPROVE_STATUS_0);
        command.setManagerStatus(ConstructionConstant.APPROVE_STATUS_0);
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
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        if (ObjectUtil.isEmpty(loginUser) || ObjectUtil.isEmpty(userId)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(entity.getId());
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        // 更新为已驳回状态，此时可以再次提审
        command.setFormStatus(ConstructionConstant.FORM_STATUS_3);
        command.setRejectId(loginUser.getId());
        command.setRejectReason(entity.getReason());
        this.updateById(command);
        log.info("流程ID为：【{}】的流程驳回成功！", entity.getProcessInstanceId());
    }

    /**
     * 更新状态
     *
     * @param updateStateEntity
     */
    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        log.info("更新状态参数：{}", JSONObject.toJSONString(updateStateEntity));
        String businessKey = updateStateEntity.getBusinessKey();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        if (ObjectUtil.isEmpty(loginUser) || ObjectUtil.isEmpty(userId)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(businessKey);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        if (userId.equals(command.getLineUserId())) {
            // 线路负责人审批
            command.setLineStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setLineOpinion(updateStateEntity.getReason());
        } else if (userId.equals(command.getDispatchId())) {
            // 生产调度审批
            command.setDispatchStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setDispatchOpinion(updateStateEntity.getReason());
        } else if (userId.equals(command.getDirectorId())) {
            // 分部主任审批
            command.setDirectorStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setDirectorOpinion(updateStateEntity.getReason());
        } else if (userId.equals(command.getManagerId())) {
            // 中心经理审批
            command.setManagerStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setManagerOpinion(updateStateEntity.getReason());
        } else {
            throw new AiurtBootException("你没有权限审批或你不是节点的审批人！");
        }
        boolean lineUser = ConstructionConstant.APPROVE_STATUS_1.equals(command.getLineStatus());
        boolean dispatchUser = ConstructionConstant.APPROVE_STATUS_1.equals(command.getDispatchStatus());
        boolean directorUser = ConstructionConstant.APPROVE_STATUS_1.equals(command.getDirectorStatus());
        boolean managerUser = ConstructionConstant.APPROVE_STATUS_1.equals(command.getManagerStatus());

        // 更新计划令审批状态为已通过
        if (lineUser && dispatchUser && directorUser && managerUser) {
            command.setFormStatus(ConstructionConstant.FORM_STATUS_5);
        }
        this.updateById(command);
    }

    /**
     * 查询待办
     *
     * @param page                           分页
     * @param constructionWeekPlanCommandDTO 请求参数
     * @return
     */
    @Override
    public IPage<ConstructionWeekPlanCommandVO> queryWorkToDo(Page<ConstructionWeekPlanCommandVO> page, ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandMapper.queryWorkToDo(page, loginUser.getUsername(), constructionWeekPlanCommandDTO);
        return pageList;
    }
}
