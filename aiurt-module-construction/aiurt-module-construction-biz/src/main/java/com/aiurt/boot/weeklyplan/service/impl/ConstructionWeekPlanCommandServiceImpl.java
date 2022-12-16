package com.aiurt.boot.weeklyplan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.ConstructionConstant;
import com.aiurt.boot.constant.ConstructionDictConstant;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandExcelDTO;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanExportDTO;
import com.aiurt.boot.weeklyplan.entity.BdTemplate;
import com.aiurt.boot.weeklyplan.entity.ConstructionCommandAssist;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.mapper.BdTemplateMapper;
import com.aiurt.boot.weeklyplan.mapper.ConstructionWeekPlanCommandMapper;
import com.aiurt.boot.weeklyplan.service.IConstructionCommandAssistService;
import com.aiurt.boot.weeklyplan.service.IConstructionWeekPlanCommandService;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.position.entity.CsStation;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Slf4j
@Service
public class ConstructionWeekPlanCommandServiceImpl extends ServiceImpl<ConstructionWeekPlanCommandMapper, ConstructionWeekPlanCommand> implements IConstructionWeekPlanCommandService, IFlowableBaseUpdateStatusService {
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private BdTemplateMapper bdTemplateMapper;
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
        ConstructionWeekPlanCommand planCommand = null;
        QueryWrapper<ConstructionWeekPlanCommand> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(constructionWeekPlanCommand) && ObjectUtil.isNotEmpty(constructionWeekPlanCommand.getId())) {
            wrapper.lambda().eq(ConstructionWeekPlanCommand::getId, constructionWeekPlanCommand.getId());
            planCommand = this.getOne(wrapper);
        }

        if (ObjectUtil.isEmpty(planCommand)) {
            // 生成计划令编码
            StringBuilder code = new StringBuilder();
            List<DictModel> types = iSysBaseApi.getDictItems(ConstructionDictConstant.CATEGORY);
            String typeName = types.stream().filter(l -> l.getValue().equals(String.valueOf(constructionWeekPlanCommand.getType())))
                    .map(DictModel::getText).collect(Collectors.joining());

            // 临时修补计划和日计划
            if (ConstructionConstant.PLAN_TYPE_2.equals(constructionWeekPlanCommand.getPlanChange())
                    || ConstructionConstant.PLAN_TYPE_3.equals(constructionWeekPlanCommand.getPlanChange())) {
                code = new StringBuilder("L-");
            }

            // 获取施工的日期对应的日
            String day = DateUtil.format(constructionWeekPlanCommand.getTaskDate(), "dd");

            // 构建计划令编号
            // XXX fixme 此处得保证线路编号是数值，比如1号线对应01、2号线对应02这种，否则以线路编号拼接
            String lineCode = constructionWeekPlanCommand.getLineCode();
            try {
                lineCode = String.valueOf(Integer.valueOf(lineCode));
            } catch (Exception e) {
                log.info("获取线路编号生成计划令编码异常：", e.getMessage());
                e.printStackTrace();
            }

            String separator = "-";
            code.append(lineCode).append(typeName).append(separator).append(day).append(separator);

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
                constructionAssist.forEach(l -> l.setPlanId(constructionWeekPlanCommand.getId()));
                constructionCommandAssistService.saveBatch(constructionWeekPlanCommand.getConstructionAssist());
            }

        } else {
            // 驳回后再次提审时，更新为待提审状态
            constructionWeekPlanCommand.setApplyId(loginUser.getId());
            constructionWeekPlanCommand.setFormStatus(ConstructionConstant.FORM_STATUS_0);
            this.updateById(constructionWeekPlanCommand);

            List<ConstructionCommandAssist> constructionAssist = constructionWeekPlanCommand.getConstructionAssist();
            QueryWrapper<ConstructionCommandAssist> assistWrapper = new QueryWrapper<>();
            assistWrapper.lambda().eq(ConstructionCommandAssist::getPlanId, constructionWeekPlanCommand.getId());
            constructionCommandAssistService.remove(assistWrapper);
            if (CollectionUtil.isNotEmpty(constructionAssist)) {
                constructionAssist.forEach(l -> l.setPlanId(constructionWeekPlanCommand.getId()));
                constructionCommandAssistService.saveBatch(constructionWeekPlanCommand.getConstructionAssist());
            }
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
        int states = updateStateEntity.getStates();
        if (1 == states) {
            // 线路负责人审批
            command.setLineStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setLineOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (3 == states) {
            command.setLineUserId(loginUser.getId());
            // 生产调度审批
            command.setDispatchStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setDispatchOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (5 == states) {
            if (ConstructionConstant.PLAN_TYPE_1.equals(command.getPlanChange())) {
                command.setDispatchId(loginUser.getId());
            } else if (ConstructionConstant.PLAN_TYPE_2.equals(command.getPlanChange())
                    || ConstructionConstant.PLAN_TYPE_3.equals(command.getPlanChange())) {
                command.setManagerId(loginUser.getId());
            }
            // 已通过
            command.setFormStatus(ConstructionConstant.FORM_STATUS_5);
        } else if (6 == states) {
            if (ConstructionConstant.PLAN_TYPE_2.equals(command.getPlanChange())
                    || ConstructionConstant.PLAN_TYPE_3.equals(command.getPlanChange())) {
                command.setDispatchId(loginUser.getId());
            }
            // 分部主任审批
            command.setDirectorStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setDirectorOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (8 == states) {
            command.setDirectorId(loginUser.getId());
            // 中心经理审批
            command.setManagerStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setManagerOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (2 == states || 4 == states || 7 == states || 9 == states) {
            // 驳回
            command.setRejectId(loginUser.getId());
            command.setRejectReason(updateStateEntity.getReason());
            command.setFormStatus(ConstructionConstant.FORM_STATUS_3);
            // 清空之前的审批人信息
            command.setLineUserId(null);
            command.setDispatchId(null);
            command.setDirectorId(null);
            command.setManagerId(null);
        } else {
            throw new AiurtBootException("你没有权限审批或你不是节点的审批人！");
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        ConstructionWeekPlanCommand command = this.getById(id);
        Assert.notNull(command, "未找到对应数据！");
        if (!ConstructionConstant.FORM_STATUS_0.equals(command.getFormStatus())) {
            throw new AiurtBootException("计划已经提审或者已经完成审批，不允许删除！");
        }
        this.removeById(command);
        // 同时删除对应的辅站信息
        List<ConstructionCommandAssist> commandAssists = constructionCommandAssistService.lambdaQuery()
                .eq(ConstructionCommandAssist::getPlanId, id).list();
        if (CollectionUtil.isNotEmpty(commandAssists)) {
            constructionCommandAssistService.removeBatchByIds(commandAssists);
        }
    }

    @Override
    public Result importExcelMaterial(MultipartFile file, ImportParams params) throws Exception {
        List<ConstructionWeekPlanCommandExcelDTO> listMaterial = ExcelImportUtil.importExcel(file.getInputStream(), ConstructionWeekPlanCommandExcelDTO.class, params);
        List<String> errorStrs = new ArrayList<>();
        // 去掉 sql 中的重复数据
        Integer errorLines = 0;
        Integer successLines = 0;

        List<ConstructionWeekPlanCommandExcelDTO> list = new ArrayList<>();
        for (int i = 0; i < listMaterial.size(); i++) {
            try {
                ConstructionWeekPlanCommand weekPlanCommand = new ConstructionWeekPlanCommand();
                ConstructionWeekPlanCommandExcelDTO planCommand = listMaterial.get(i);
                String finalstr = "";
                //作业性质
                if (StrUtil.isEmpty(planCommand.getNatureName())) {
                    errorStrs.add("第 " + i + " 行：作业性质未输入，忽略导入。");
                    planCommand.setText("作业性质未输入，忽略导入");
                    list.add(planCommand);
                    continue;
                } else {
                    List<DictModel> dictItems = iSysBaseApi.getDictItems(ConstructionDictConstant.NATURE);
                    dictItems.forEach(s -> {
                        if (s.getText().equals(planCommand.getNatureName())) {
                            planCommand.setNature(Integer.valueOf(s.getValue()));
                        }
                    });
                    if (ObjectUtil.isEmpty(planCommand.getNature())) {
                        errorStrs.add("第 " + i + " 行：作业性质未找到,请核对后输入，忽略导入。");
                        planCommand.setText("作业性质未找到,请核对后输入，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                //作业类别
                if (StrUtil.isEmpty(planCommand.getTypeName())) {
                    errorStrs.add("第 " + i + " 行：作业类别未输入，忽略导入。");
                    planCommand.setText("作业类别未输入，忽略导入");
                    list.add(planCommand);
                    continue;
                } else {
                    List<DictModel> dictItems = iSysBaseApi.getDictItems(ConstructionDictConstant.CATEGORY);
                    dictItems.forEach(s -> {
                        if (s.getText().equals(planCommand.getTypeName())) {
                            planCommand.setType(Integer.valueOf(s.getValue()));
                        }
                    });
                    if (ObjectUtil.isEmpty(planCommand.getType())) {
                        errorStrs.add("第 " + i + " 行：作业类别未找到,请核对后输入，忽略导入。");
                        planCommand.setText("作业类别未找到,请核对后输入，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                //作业单位
                if (StrUtil.isEmpty(planCommand.getOrgName())) {
                    errorStrs.add("第 " + i + " 行：作业单位未输入，忽略导入。");
                    planCommand.setText("作业单位未输入，忽略导入");
                    list.add(planCommand);
                    continue;
                } else {
                    JSONObject sysDepartModel = iSysBaseApi.getDepartByName(planCommand.getOrgName());
                    if (ObjectUtil.isNotNull(sysDepartModel)) {
                        planCommand.setOrgCode(sysDepartModel.getString("orgCode"));
                    } else {
                        errorStrs.add("第 " + i + " 行：作业单位未找到,请核对后输入，忽略导入。");
                        planCommand.setText("作业单位未找到,请核对后输入，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                if (StrUtil.isEmpty(planCommand.getDate())) {
                    errorStrs.add("第 " + i + " 行：作业日期未输入,忽略导入。");
                    planCommand.setText("作业日期未输入,忽略导入");
                    list.add(planCommand);
                    continue;
                } else {
                    try {
                        planCommand.setTaskDate(DateUtil.parse(planCommand.getDate(), "yyyy-MM-dd"));
                    } catch (Exception e) {
                        errorStrs.add("第 " + i + " 行：作业日期格式不对请按照模板上说明输入，忽略导入。");
                        planCommand.setText("作业日期格式不对请按照模板上说明输入，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                if (StrUtil.isNotEmpty(planCommand.getEndAndTime())) {
                    List<String> strings = Arrays.asList(planCommand.getEndAndTime().split("-"));
                    DateFormat fmt = new SimpleDateFormat("HH:mm");
                    planCommand.setTaskStartTime(fmt.parse(strings.get(0)));
                    planCommand.setTaskEndTime(fmt.parse(strings.get(1)));
                } else {
                    errorStrs.add("第 " + i + " 行：作业时间未输入,忽略导入。");
                    planCommand.setText("作业时间未输入,忽略导入");
                    list.add(planCommand);
                    continue;
                }
                if (StrUtil.isEmpty(planCommand.getTaskRange())) {
                    errorStrs.add("第 " + i + " 行：作业范围未输入,忽略导入。");
                    planCommand.setText("作业范围未输入,忽略导入");
                    list.add(planCommand);
                    continue;
                }

                if (StrUtil.isEmpty(planCommand.getTaskContent())) {
                    errorStrs.add("第 " + i + " 行：作业内容未输入,忽略导入。");
                    planCommand.setText("作业内容未输入,忽略导入");
                    list.add(planCommand);
                    continue;
                }
                if (StrUtil.isEmpty(planCommand.getProtectiveMeasure())) {
                    errorStrs.add("第 " + i + " 行：防护措施未输入,忽略导入。");
                    planCommand.setText("防护措施未输入,忽略导入");
                    list.add(planCommand);
                    continue;
                }
                if (StrUtil.isEmpty(planCommand.getLineName())) {
                    errorStrs.add("第 " + i + " 行：作业线路未输入,忽略导入。");
                    planCommand.setText("作业线路未输入,忽略导入");
                    list.add(planCommand);
                    continue;
                } else {
                    JSONObject csLine = iSysBaseApi.getLineByName(planCommand.getLineName());
                    if (ObjectUtil.isNotNull(csLine)) {
                        planCommand.setLineCode(csLine.getString("lineCode"));
                    } else {
                        errorStrs.add("第 " + i + " 行：无法跟据输入的作业线路找到资源,忽略导入。");
                        planCommand.setText("无法跟据输入的作业线路找到资源,忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                if (StrUtil.isNotEmpty(planCommand.getChargeStaffName())) {
                    List<String> users = Arrays.asList(planCommand.getChargeStaffName().split("-"));
                    if (users.size() == 2) {
                        String userId = baseMapper.selectUserId(users.get(0), users.get(1));
                        if (StrUtil.isNotEmpty(userId)) {
                            planCommand.setChargeStaffId(userId);
                        } else {
                            errorStrs.add("第 " + i + " 行：查不到此负责人,核对后输入,忽略导入。");
                            planCommand.setText("查不到此负责人,核对后输入,忽略导入");
                            list.add(planCommand);
                            continue;
                        }
                    } else {
                        errorStrs.add("第 " + i + " 行：请按模板格式输入施工负责人,忽略导入。");
                        planCommand.setText("请按模板格式输入施工负责人,忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                if (StrUtil.isNotEmpty(planCommand.getPowerSupplyRequirementContent())) {
                    BdTemplate bdTemplates = bdTemplateMapper.selectOne(new LambdaQueryWrapper<BdTemplate>().eq(BdTemplate::getUserId, user.getId())
                            .eq(BdTemplate::getContent, planCommand.getPowerSupplyRequirementContent()));
                    if (bdTemplates != null) {
                        planCommand.setPowerSupplyRequirementId(bdTemplates.getId());
                    } else {
                        errorStrs.add("第 " + i + " 行：输入的供电要求未找到模板,忽略导入。");
                        planCommand.setText("输入的供电要求未找到模板,忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                //配合部门
                if (StrUtil.isNotEmpty(planCommand.getCoordinationDepartmentName())) {
                    JSONObject sysDepartModel = iSysBaseApi.getDepartByName(planCommand.getCoordinationDepartmentName());
                    if (ObjectUtil.isNotNull(sysDepartModel)) {
                        planCommand.setOrgCode(sysDepartModel.getString("orgCode"));
                    } else {
                        errorStrs.add("第 " + i + " 行：配合部门未找到,请核对后输入，忽略导入。");
                        planCommand.setText("配合部门未找到,请核对后输入，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }
                if (StrUtil.isNotEmpty(planCommand.getFirstStationName())) {
                    JSONObject stationByName = iSysBaseApi.getStationByName(planCommand.getFirstStationName());
                    if (ObjectUtil.isNotNull(stationByName)) {
                        if (stationByName.getString("lineCode").equals(planCommand.getLineCode())) {
                            planCommand.setFirstStationCode(stationByName.getString("stationCode"));
                        } else {
                            errorStrs.add("第 " + i + " 行：该请点车站未存在" + planCommand.getLineName() + "，忽略导入。");
                            planCommand.setText("该请点车站未存在" + planCommand.getLineName() + "，忽略导入");
                            list.add(planCommand);
                            continue;
                        }
                    } else {
                        errorStrs.add("第 " + i + " 行：该请点车站未找到，忽略导入。");
                        planCommand.setText("该请点车站未找到，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                } else {
                    errorStrs.add("第 " + i + " 行：请点车站未输入，忽略导入。");
                    planCommand.setText("请点车站未输入，忽略导入");
                    list.add(planCommand);
                    continue;
                }
                if (StrUtil.isNotEmpty(planCommand.getSecondStationName())) {
                    JSONObject stationByName = iSysBaseApi.getStationByName(planCommand.getSecondStationName());
                    if (ObjectUtil.isNotNull(stationByName)) {
                        if (stationByName.getString("lineCode").equals(planCommand.getLineCode())) {
                            planCommand.setFirstStationCode(stationByName.getString("stationCode"));
                        } else {
                            errorStrs.add("第 " + i + " 行：该销点车站未存在" + planCommand.getLineName() + "，忽略导入。");
                            planCommand.setText("该销点车站未存在" + planCommand.getLineName() + "，忽略导入");
                            list.add(planCommand);
                            continue;
                        }
                    } else {
                        errorStrs.add("第 " + i + " 行：该销点车站未找到，忽略导入。");
                        planCommand.setText("该销点车站未找到，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                } else {
                    errorStrs.add("第 " + i + " 行：销点车站未输入，忽略导入。");
                    planCommand.setText("销点车站未输入，忽略导入");
                    list.add(planCommand);
                    continue;
                }
                if (StrUtil.isNotEmpty(planCommand.getConstruction())) {
                    List<String> asList = Arrays.asList(planCommand.getConstruction().split(","));
                    List<ConstructionCommandAssist> commandAssistList = new ArrayList<>();
                    int finalI = i;
                    for (String l : asList) {
                        ConstructionCommandAssist commandAssist = new ConstructionCommandAssist();
                        List<String> list1 = Arrays.asList(l.split(":"));
                        if (list1.size() == 2) {
                            JSONObject stationByName = iSysBaseApi.getStationByName(list1.get(0));
                            if (ObjectUtil.isNotNull(stationByName)) {
                                commandAssist.setStationCode(stationByName.getString("stationCode"));
                            } else {
                                errorStrs.add("第 " + finalI + " 行：辅站未找到，忽略导入。");
                                planCommand.setText("辅站未找到，忽略导入");
                                list.add(planCommand);
                                continue;
                            }
                            List<String> list2 = Arrays.asList(list1.get(1).split("-"));
                            if (list2.size() == 2) {
                                String userId = baseMapper.selectUserIdByPermitCode(list2.get(0), list2.get(1));
                                if (StrUtil.isEmpty(userId)) {
                                    errorStrs.add("第 " + finalI + " 行：辅站的负责人未找到，忽略导入。");
                                    planCommand.setText("辅站的负责人未找到，忽略导入");
                                    list.add(planCommand);
                                    continue;
                                } else {
                                    commandAssist.setUserId(userId);
                                }
                            }
                            commandAssistList.add(commandAssist);
                        } else {
                            JSONObject stationByName = iSysBaseApi.getStationByName(l);
                            if (ObjectUtil.isNotNull(stationByName)) {
                                commandAssist.setStationCode(stationByName.getString("stationCode"));
                                commandAssistList.add(commandAssist);
                            } else {
                                errorStrs.add("第 " + finalI + " 行：辅站未找到，忽略导入。");
                                planCommand.setText("辅站未找到，忽略导入");
                                list.add(planCommand);
                                continue;
                            }
                        }
                    }
                    planCommand.setConstructionAssist(commandAssistList);
                }
                if (ObjectUtil.isEmpty(planCommand.getNum())) {
                    errorStrs.add("第 " + i + " 行：作业人数未输入，忽略导入。");
                    planCommand.setText("作业人数未输入，忽略导入");
                    list.add(planCommand);
                    continue;
                } else {
                    try {
                        planCommand.setTaskStaffNum(Integer.valueOf(planCommand.getNum()));
                    } catch (Exception e) {
                        errorStrs.add("第 " + i + " 行：作业人数格式不对请用数字类型，忽略导入。");
                        planCommand.setText("作业人数格式不对请用数字类型，忽略导入");
                        list.add(planCommand);
                        continue;
                    }
                }

                BeanUtils.copyProperties(weekPlanCommand, planCommand);
                int save = baseMapper.insert(weekPlanCommand);
                if (CollectionUtil.isNotEmpty(planCommand.getConstructionAssist())) {
                    planCommand.getConstructionAssist().forEach(s -> {
                        s.setPlanId(weekPlanCommand.getId());
                    });
                    constructionCommandAssistService.saveBatch(planCommand.getConstructionAssist());
                }
                if (save <= 0) {
                    throw new Exception(CommonConstant.SQL_INDEX_UNIQ_MATERIAL_BASE_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (list.size() > 0) {
            //创建导入失败错误报告,进行模板导出
            Resource resource = new ClassPathResource("templates\\constructionWeekPlanCommandError.xlsx");
            InputStream resourceAsStream = resource.getInputStream();
            //2.获取临时文件
            File fileTemp = new File("templates\\constructionWeekPlanCommandError.xlsx");
            try {
                //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);
            List<Map<String, Object>> mapList = new ArrayList<>();
            list.forEach(l -> {
                Map<String, Object> lm = new HashMap<String, Object>();
                lm.put("natureName", l.getNatureName());
                lm.put("type", l.getTypeName());
                lm.put("text", l.getText());
                lm.put("orgName", l.getOrgName());
                lm.put("date", l.getDate());
                lm.put("endAndTime", l.getEndAndTime());
                lm.put("coordinationDepartmentName", l.getCoordinationDepartmentName());
                lm.put("chargeStaffName", l.getChargeStaffName());
                lm.put("taskRange", l.getTaskRange());
                lm.put("taskContent", l.getTaskContent());
                lm.put("protectiveMeasure", l.getProtectiveMeasure());
                lm.put("powerSupplyRequirementContent", l.getPowerSupplyRequirementContent());
                lm.put("construction", l.getConstruction());
                lm.put("firstStationName", l.getFirstStationName());
                lm.put("secondStationName", l.getSecondStationName());
                lm.put("num", l.getNum());
                lm.put("largeAppliances", l.getLargeAppliances());
                mapList.add(lm);
            });
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("maplist", mapList);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
            String fileName = "施工周计划错误模板" + "_" + System.currentTimeMillis() + ".xls";
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            String url = fileName;
            workbook.write(out);
            errorLines += errorStrs.size();
            successLines += (listMaterial.size() - errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs, url);
        }
        errorLines += errorStrs.size();
        successLines += (listMaterial.size() - errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs, null);
    }


    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, String lineCode, Date startDate, Date endDate) {
        List<ConstructionWeekPlanExportDTO> dataList = constructionWeekPlanCommandMapper.getExportData(lineCode, startDate, endDate);
        List<CsStation> stations = iSysBaseApi.queryAllStation();
        Map<String, String> stationMap = stations.stream().collect(Collectors.toMap(k -> k.getStationCode(), v -> v.getStationName(), (a, b) -> a));
        dataList.stream().forEach(l -> {
            String stationCode = l.getAssistStationCode();
            if (StrUtil.isNotEmpty(stationCode)) {
                List<String> stationCodes = StrUtil.split(stationCode, ',');
                List<String> stationNames = new ArrayList<>();
                stationCodes.forEach(sc -> Optional.ofNullable(stationMap.get(sc)).ifPresent(stationName -> stationNames.add(stationName)));
                l.setAssistStationName(stationNames.stream().collect(Collectors.joining("、")));
            }
        });
        String title = "运营施工及行车计划申报表";
        if (StrUtil.isNotEmpty(lineCode)) {
            Map<String, String> lineNameMap = iSysBaseApi.getLineNameByCode(Arrays.asList(lineCode));
            String lineName = lineNameMap.get(lineCode);
            if (StrUtil.isNotEmpty(lineName)) {
                title = lineName + title;
            }
        }
        Map<Date, List<ConstructionWeekPlanExportDTO>> listMap = dataList.stream()
                .sorted((a, b) -> DateUtil.compare(a.getTaskDate(), b.getTaskDate()))
                .collect(Collectors.groupingBy(ConstructionWeekPlanExportDTO::getTaskDate));
        Map<String, String> weekMap = iSysBaseApi.getDictItems(ConstructionDictConstant.WEEK).stream()
                .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        ExportParams params = new ExportParams(title, null, "运营施工及行车计划申报表");
        // 设置自定义样式
        params.setStyle(CustomExcelExportStylerImpl.class);
        Workbook workbook = ExcelExportUtil.exportExcel(params, ConstructionWeekPlanExportDTO.class, new ArrayList<>());
        try {
            int flag = 0;
            for (Date date : listMap.keySet()) {
                String secondTitle = DateUtil.format(date, "yyyy年MM月dd日");
                int week = DateUtil.dayOfWeek(date) == 1 ? 7 : DateUtil.dayOfWeek(date) - 1;
                secondTitle += "(" + weekMap.get(String.valueOf(week)) + ")";
                String sheetName = "运营施工及行车计划申报表";
                ExportParams exportParams = new ExportParams(title, secondTitle, sheetName);
                // 设置自定义样式
                exportParams.setStyle(CustomExcelExportStylerImpl.class);
                Workbook sheets = ExcelExportUtil.exportExcel(exportParams, ConstructionWeekPlanExportDTO.class, listMap.get(date));
                CustomExcelExportStylerImpl excelExportStyler = new CustomExcelExportStylerImpl(workbook);
                if (flag == 0) {
                    workbook = sheets;
                    flag++;
                    // 调整格式
                    for (Sheet sheet : workbook) {
                        int lastRowNum = sheet.getLastRowNum();
                        for (int i = 1; i < lastRowNum; i++) {
                            Row row = sheet.getRow(i);
                            for (Cell cell : row) {
                                if (i == 1 || i == 2) {
                                    cell.getCellStyle().cloneStyleFrom(excelExportStyler.getTitleStyle((short) 40));
                                } else {
                                    cell.getCellStyle().cloneStyleFrom(excelExportStyler.stringNoneStyle(workbook, true));
                                }
                            }
                            row.setHeightInPoints(25);
                        }
                        sheet.createFreezePane(sheet.getRow(0).getLastCellNum(), 1);
                    }
                    continue;
                }
                // 实际就一个sheet
                for (Sheet sheet : workbook) {
                    int rowNum = sheet.getLastRowNum() + 1;
                    Sheet afterSheet = sheets.getSheet(sheetName);
                    for (int i = 1; i <= afterSheet.getLastRowNum(); i++) {
                        Row workbookRow = sheet.createRow((short) rowNum);
                        Row afterSheetRow = afterSheet.getRow(i);
                        short lastCellNum = afterSheetRow.getLastCellNum();
                        for (int j = 0; j < lastCellNum; j++) {
                            Cell cell = afterSheetRow.getCell(j);
                            Cell workRowCell = workbookRow.createCell(j);
                            workRowCell.setCellValue(cell.getStringCellValue());
                            if (i == 1 || i == 2) {
                                Workbook sheetWorkbook = sheet.getWorkbook();
                                CellStyle cellStyle = new CustomExcelExportStylerImpl(sheetWorkbook).getTitleStyle((short) 40);
                                workRowCell.setCellStyle(cellStyle);
                            } else {
                                workRowCell.getCellStyle().cloneStyleFrom(cell.getCellStyle());
                            }
                        }
                        if (i == 1) {
                            CellRangeAddress rangeAddress = new CellRangeAddress(rowNum, rowNum, 0, lastCellNum - 1);
                            sheet.addMergedRegion(rangeAddress);
                        }
                        workbookRow.setHeightInPoints(25);
                        rowNum++;
                    }
                }
            }
            String filename = title + ".xls";
            response.setHeader("content-disposition", "attachment;filename=" + new String(filename.getBytes("UTF-8"), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("周施工计划导出失败！", e.getMessage());
            e.printStackTrace();
        }
    }
}
