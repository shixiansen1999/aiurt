package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.*;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.C;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class FaultServiceImpl extends ServiceImpl<FaultMapper, Fault> implements IFaultService {


    @Autowired
    private IFaultDeviceService faultDeviceService;

    @Autowired
    private IOperationProcessService operationProcessService;

    @Autowired
    private IFaultRepairRecordService repairRecordService;

    @Autowired
    private IFaultRepairParticipantsService repairParticipantsService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private IDeviceChangeSparePartService sparePartService;

    /**
     * 故障上报
     *
     * @param fault 故障对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(Fault fault) {

        LoginUser user = checkLogin();

        // 故障编号处理
        String majorCode = fault.getMajorCode();
        StringBuilder builder = new StringBuilder("WX");
        builder.append(majorCode).append(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        fault.setCode(builder.toString());

        // 接报人
        fault.setReceiveTime(new Date());
        fault.setReceiveUserName(user.getUsername());

        String faultModeCode = fault.getFaultModeCode();

        if (StrUtil.equalsIgnoreCase(faultModeCode, "0")) {
            fault.setAppointUserName(user.getUsername());
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
            // 创建维修记录
            FaultRepairRecord record = FaultRepairRecord.builder()
                    // 做类型
                    .faultCode(fault.getCode())
                    // 故障想象
                    .faultPhenomenon(fault.getFaultPhenomenon())
                    .startTime(new Date())
                    // 负责人
                    .appointUserName(user.getUsername())
                    .build();

            repairRecordService.save(record);

            //todo 需要给班组长发送消息
        }else {
            fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());
        }

        // 保存故障
        save(fault);

        // 设置故障编码
        List<FaultDevice> faultDeviceList = fault.getFaultDeviceList();
        faultDeviceList.stream().forEach(faultDevice -> {
            faultDevice.setDelFlag(0);
            faultDevice.setFaultCode(fault.getCode());
        });

        // 保存故障设备
        if (CollectionUtil.isNotEmpty(faultDeviceList)) {
            faultDeviceService.saveBatch(faultDeviceList);
        }

        // 记录日志
        saveLog(user, "故障上报", fault.getCode(), 1, null);

        // todo 消息通知

        return builder.toString();
    }

    /**
     * 故障审批
     *
     * @param approvalDTO 审批对象
     */
    @Override
    public void approval(ApprovalDTO approvalDTO) {

        LoginUser user = checkLogin();

        Fault fault = isExist(approvalDTO.getFaultCode());

        // 通过的状态 = 1
        Integer status = 1;
        Integer approvalStatus = approvalDTO.getApprovalStatus();
        OperationProcess operationProcess = OperationProcess.builder()
                .processTime(new Date())
                .faultCode(fault.getCode())
                .processPerson(user.getUsername())
                .build();
        if (Objects.isNull(approvalStatus) || status.equals(approvalStatus)) {
            // 审批通过
            fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
            operationProcess.setProcessLink(FaultStatusEnum.APPROVAL_PASS.getMessage())
                    .setProcessCode(FaultStatusEnum.APPROVAL_PASS.getStatus()).setRemark(approvalDTO.getApprovalRejection());
        } else {
            // 驳回
            fault.setStatus(FaultStatusEnum.APPROVAL_REJECT.getStatus());
            fault.setApprovalRejection(approvalDTO.getApprovalRejection());
            operationProcess.setProcessLink(FaultStatusEnum.APPROVAL_REJECT.getMessage())
                    .setProcessCode(FaultStatusEnum.APPROVAL_REJECT.getStatus()).setRemark(approvalDTO.getApprovalRejection());
        }

        updateById(fault);

        operationProcessService.save(operationProcess);

        //todo 消息发送
    }

    /**
     * 编辑
     *
     * @param fault 故障对象 @see com.aiurt.modules.fault.entity.Fault
     */
    @Override
    public void edit(Fault fault) {

        LoginUser loginUser = checkLogin();

        //todo 设备处理
        List<FaultDevice> faultDeviceList = fault.getFaultDeviceList();
        if (CollectionUtil.isNotEmpty(faultDeviceList)) {
            // 删除旧设备
            LambdaQueryWrapper<FaultDevice>  wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FaultDevice::getFaultCode, fault.getCode());
            faultDeviceService.remove(wrapper);
        }

        // update status
        fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());

        updateById(fault);

        // 记录日志
        saveLog(loginUser, "修改故障工单", fault.getCode(), FaultStatusEnum.NEW_FAULT.getStatus(), null);
    }

    /**
     * 作废
     *
     * @param cancelDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(CancelDTO cancelDTO) {
        LoginUser user = checkLogin();

        // 故障单
        Fault fault = isExist(cancelDTO.getFaultCode());

        // 作废
        fault.setStatus(FaultStatusEnum.CANCEL.getStatus());

        //更新状态
        fault.setCancelTime(new Date());
        fault.setCancelUserName(user.getUsername());
        updateById(fault);

        // 记录日志
        saveLog(user, FaultStatusEnum.CANCEL.getMessage(), fault.getCode(), FaultStatusEnum.CANCEL.getStatus(),cancelDTO.getCancelRemark());

        // todo 发送消息提醒
    }

    /**
     * 根据编码查询详情
     *
     * @param code
     * @return
     */
    @Override
    public Fault queryByCode(String code) {

        Fault fault = isExist(code);

        // 设备
        List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(code);
        fault.setFaultDeviceList(faultDeviceList);

        // 按钮权限
        return fault;
    }

    /**
     * 指派
     *
     * @param assignDTO
     */
    @Override
    public void assign(AssignDTO assignDTO) {

        LoginUser user = checkLogin();

        Fault fault = isExist(assignDTO.getFaultCode());

        // 删除其他记录
        LambdaUpdateWrapper<FaultRepairRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_1).eq(FaultRepairRecord::getFaultCode, fault.getCode());
        repairRecordService.update(updateWrapper);

        // 更新状态
        fault.setStatus(FaultStatusEnum.ASSIGN.getStatus());
        fault.setAssignTime(new Date());
        fault.setAssignUserName(user.getUsername());
        fault.setAppointUserName(assignDTO.getOperatorUserName());

        // 维修记录
        FaultRepairRecord record = FaultRepairRecord.builder()
                // 做类型
                .workType(assignDTO.getCaWorkCode())
                .planOrderCode(assignDTO.getPlanCode())
                .faultCode(fault.getCode())
                .planOrderImg(assignDTO.getFilepath())
                .assignTime(new Date())
                // 故障想象
                .faultPhenomenon(fault.getFaultPhenomenon())
                // 负责人
                .appointUserName(assignDTO.getOperatorUserName())
                .build();

        // 修改状态
        updateById(fault);

        // 保存维修记录
        repairRecordService.save(record);

        // todo 发送消息
        // 日志记录
        saveLog(user, FaultStatusEnum.ASSIGN.getMessage(), assignDTO.getFaultCode(), FaultStatusEnum.ASSIGN.getStatus(), null);
    }




    /**
     * 领取工单
     *
     * @param assignDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receive(AssignDTO assignDTO) {
        LoginUser user = checkLogin();

        Fault fault = isExist(assignDTO.getFaultCode());

        LambdaUpdateWrapper<FaultRepairRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_1).eq(FaultRepairRecord::getFaultCode, fault.getCode());
        repairRecordService.update(updateWrapper);

        // 更新状态
        fault.setStatus(FaultStatusEnum.RECEIVE.getStatus());
        fault.setAppointUserName(user.getUsername());

        // 维修记录
        FaultRepairRecord record = FaultRepairRecord.builder()
                // 做类型
                .workType(assignDTO.getCaWorkCode())
                .planOrderCode(assignDTO.getPlanCode())
                .faultCode(fault.getCode())
                .planOrderImg(assignDTO.getFilepath())
                // 负责人
                .appointUserName(assignDTO.getOperatorUserName())
                // 故障想象
                .faultPhenomenon(fault.getFaultPhenomenon())
                // 领取时间
                .receviceTime(new Date())
                .build();

        updateById(fault);

        repairRecordService.save(record);

        // 日志记录
        saveLog(user, FaultStatusEnum.RECEIVE.getMessage(), assignDTO.getFaultCode(), FaultStatusEnum.RECEIVE.getStatus(), null);

        // todo 发送消息
    }

    /**
     * 接收指派
     *
     * @param code
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveAssignment(String code) {
        LoginUser loginUser = checkLogin();

        Fault fault = isExist(code);

        // 状态-已接收
        fault.setStatus(FaultStatusEnum.RECEIVE_ASSIGN.getStatus());

        // 修改维修记录的领取时间
        LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRepairRecord::getFaultCode, code).eq(FaultRepairRecord::getAppointUserName,loginUser.getUsername())
                .eq(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
            .orderByDesc(FaultRepairRecord::getCreateTime).last("limit 1");
        FaultRepairRecord repairRecord = repairRecordService.getBaseMapper().selectOne(wrapper);

        if (Objects.isNull(repairRecord)) {
            throw new AiurtBootException("系统错误，维修记录不存在。");
        }

        repairRecord.setReceviceTime(new Date());

        updateById(fault);

        repairRecordService.updateById(repairRecord);

        saveLog(loginUser, "接收指派", code, FaultStatusEnum.RECEIVE_ASSIGN.getStatus(), null);
        //todo 创建维修记录
    }


    /**
     * 拒绝接收指派
     * @param refuseAssignmentDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuseAssignment(RefuseAssignmentDTO refuseAssignmentDTO) {
        LoginUser loginUser = checkLogin();

        Fault fault = isExist(refuseAssignmentDTO.getFaultCode());

        // 状态-已审批待指派
        fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());

        updateById(fault);

        // 设置状态
        saveLog(loginUser, "拒绝接收指派", refuseAssignmentDTO.getFaultCode(), FaultStatusEnum.APPROVAL_PASS.getStatus(), refuseAssignmentDTO.getRefuseRemark());

        //todo 日志
    }

    /**
     * 开始维修
     * @param code
     */
    @Override
    public void startRepair(String code) {
        LoginUser user = checkLogin();

        Fault fault = isExist(code);
        // 开始维修时间
        fault.setStatus(FaultStatusEnum.REPAIR.getStatus());

        LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRepairRecord::getFaultCode, code).eq(FaultRepairRecord::getAppointUserName, user.getUsername())
                .eq(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .orderByDesc(FaultRepairRecord::getCreateTime).last("limit 1");
        FaultRepairRecord repairRecord = repairRecordService.getBaseMapper().selectOne(wrapper);

        // 维修记录
        repairRecord.setStartTime(new Date());

        // 故障单状态
        updateById(fault);

        // 更新时间
        repairRecordService.updateById(repairRecord);

        // 记录日志
        saveLog(user, "开始维修", code, FaultStatusEnum.REPAIR.getStatus(), null);

    }

    /**
     * 挂起申请
     * @param hangUpDTO
     */
    @Override
    public void hangUp(HangUpDTO hangUpDTO) {

        LoginUser user = checkLogin();

        Fault fault = isExist(hangUpDTO.getFaultCode());

        fault.setStatus(FaultStatusEnum.HANGUP_REQUEST.getStatus());
        // 挂起原因
        fault.setHangUpReason(hangUpDTO.getHangUpReason());

        updateById(fault);

        saveLog(user, "申请挂起", hangUpDTO.getFaultCode(), FaultStatusEnum.HANGUP_REQUEST.getStatus(), hangUpDTO.getHangUpReason());

        // todo 发送消息提醒, 维修记录

    }

    /**
     * 审批挂起
     * @param approvalHangUpDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalHangUp(ApprovalHangUpDTO approvalHangUpDTO) {
        LoginUser user = checkLogin();

        Fault fault = isExist(approvalHangUpDTO.getFaultCode());

        // 通过的状态 = 1
        Integer status = 1;
        Integer approvalStatus = approvalHangUpDTO.getApprovalStatus();
        if (Objects.isNull(approvalStatus) || status.equals(approvalStatus)) {
            // 审批通过-挂起
            fault.setStatus(FaultStatusEnum.HANGUP.getStatus());
            saveLog(user, "挂起审批通过", approvalHangUpDTO.getFaultCode(), FaultStatusEnum.HANGUP.getStatus(), null);
        } else {
            // 驳回-维修中
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
            //todo
            fault.setApprovalRejection(approvalHangUpDTO.getApprovalRejection());
            saveLog(user, "挂起审批驳回", approvalHangUpDTO.getFaultCode(), FaultStatusEnum.REPAIR.getStatus(),approvalHangUpDTO.getApprovalRejection());
        }

        updateById(fault);
        // todo 发送消息, 维修记录


    }

    /**
     * 取消挂起
     * @param code 故障编码
     */
    @Override
    public void cancelHangup(String code) {

        LoginUser loginUser = checkLogin();

        Fault fault = isExist(code);

        // 更新状态-维修中
        fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
        // 挂起时间

        updateById(fault);

        saveLog(loginUser, "取消挂起", code, FaultStatusEnum.REPAIR.getStatus(), null);

        // todo 发送消息
    }

    /**
     * 查询故障记录详情
     * @param faultCode 故障编码
     * @return
     */
    @Override
    public RepairRecordDTO queryRepairRecord(String faultCode) {
        LoginUser loginUser = checkLogin();

        LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRepairRecord::getFaultCode, faultCode) //.eq(FaultRepairRecord::getAppointUserName, loginUser.getUsername())
                .eq(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .orderByDesc(FaultRepairRecord::getCreateTime).last("limit 1");
        FaultRepairRecord repairRecord = repairRecordService.getBaseMapper().selectOne(wrapper);

        if (Objects.isNull(repairRecord)) {
            throw  new AiurtBootException(20001, "没有该维修记录");
        }

        RepairRecordDTO repairRecordDTO = new RepairRecordDTO();
        BeanUtils.copyProperties(repairRecord, repairRecordDTO);

        // 查询参与人
        List<FaultRepairParticipants> participantsList = repairParticipantsService.queryParticipantsByRecordId(repairRecord.getId());
        repairRecordDTO.setParticipantsList(participantsList);
        List<String> list = participantsList.stream().map(FaultRepairParticipants::getUserId).collect(Collectors.toList());
        List<String> userNameList = participantsList.stream().map(FaultRepairParticipants::getRealName).collect(Collectors.toList());
        repairRecordDTO.setUserIds(StrUtil.join(",", list));
        repairRecordDTO.setUserNames(StrUtil.join(",", userNameList));

        List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.queryDeviceChangeByFaultCode(faultCode, repairRecord.getId());
        // 易耗品 1是易耗
        List<DeviceChangeDTO> consumableList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("1", sparepart.getConsumables()))
                .map(sparepart -> {
                    DeviceChangeDTO build = DeviceChangeDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .build();
                    return build;
                }).collect(Collectors.toList());

        repairRecordDTO.setConsumableList(consumableList);

        List<DeviceChangeDTO> deviceChangeList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("0", sparepart.getConsumables()))
                .map(sparepart -> {
                    DeviceChangeDTO build = DeviceChangeDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .oldSparePartCode(sparepart.getOldSparePartCode())
                            .oldSparePartName(sparepart.getOldSparePartName())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .build();
                    return build;
                }).collect(Collectors.toList());

        repairRecordDTO.setDeviceChangeList(deviceChangeList);
        // 维修设备
        List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultCode);
        repairRecordDTO.setDeviceList(faultDeviceList);

        // 指派时间
        if (Objects.isNull(repairRecordDTO.getAssignTime())) {
            repairRecordDTO.setAssignTime(repairRecord.getReceviceTime());
        }

        return repairRecordDTO;
    }

    /**
     * 填写维修记录
     * @param repairRecordDTO 填写维修记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fillRepairRecord(RepairRecordDTO repairRecordDTO) {

        LoginUser loginUser = checkLogin();

        String faultCode = repairRecordDTO.getFaultCode();

        Fault fault = isExist(faultCode);

        FaultRepairRecord one = repairRecordService.getById(repairRecordDTO.getId());

        String userIds = repairRecordDTO.getUserIds();

        // todo 删除本次的备件更换信息


        if (StrUtil.isNotBlank(userIds)) {
            List<LoginUser> userList = sysBaseAPI.queryAllUserByIds(StrUtil.split(userIds, ","));
            List<FaultRepairParticipants> participantsList = userList.stream().map(user -> {
                FaultRepairParticipants participants = new FaultRepairParticipants();
                participants.setFaultRepairRecordId(one.getId());
                participants.setUserName(user.getUsername());
                participants.setFaultCode(faultCode);
                return participants;
            }).collect(Collectors.toList());
            repairParticipantsService.saveBatch(participantsList);
        }

        List<DeviceChangeDTO> consumableList = repairRecordDTO.getConsumableList();
        if (CollectionUtil.isNotEmpty(consumableList)) {
            List<DeviceChangeSparePart> sparePartList = consumableList.stream().map(deviceChangeDTO -> {
                DeviceChangeSparePart build = DeviceChangeSparePart.builder()
                        .code(faultCode)
                        .consumables("1")
                        .deviceCode(deviceChangeDTO.getDeviceCode())
                        .newSparePartCode(deviceChangeDTO.getNewSparePartCode())
                        .newSparePartNum(deviceChangeDTO.getNewSparePartNum())
                        .repairRecordId(deviceChangeDTO.getId())
                        .id(deviceChangeDTO.getId())
                        .delFlag(CommonConstant.DEL_FLAG_0)
                        .build();
                return build;
            }).collect(Collectors.toList());
            sparePartService.saveOrUpdateBatch(sparePartList);
        }


        List<DeviceChangeDTO> deviceChangeList = repairRecordDTO.getDeviceChangeList();

        if (CollectionUtil.isNotEmpty(deviceChangeList)) {
            List<DeviceChangeSparePart> sparePartList = deviceChangeList.stream().map(deviceChangeDTO -> {
                DeviceChangeSparePart build = DeviceChangeSparePart.builder()
                        .code(faultCode)
                        .consumables("0")
                        .deviceCode(deviceChangeDTO.getDeviceCode())
                        .newSparePartCode(deviceChangeDTO.getNewSparePartCode())
                        .newSparePartNum(deviceChangeDTO.getNewSparePartNum())
                        .repairRecordId(deviceChangeDTO.getId())
                        .id(deviceChangeDTO.getId())
                        .oldSparePartNum(deviceChangeDTO.getOldSparePartNum())
                        .oldSparePartCode(deviceChangeDTO.getOldSparePartCode())
                        .delFlag(CommonConstant.DEL_FLAG_0)
                        .build();
                return build;
            }).collect(Collectors.toList());

            sparePartService.saveOrUpdateBatch(sparePartList);
        }

        one.setArriveTime(repairRecordDTO.getArriveTime());
        one.setWorkTicketCode(repairRecordDTO.getWorkTickCode());
        // 工作票图片
        one.setSolveStatus(repairRecordDTO.getSolveStatus());
        one.setUnSloveRemark(repairRecordDTO.getUnSloveRemark());
        one.setFilePath(repairRecordDTO.getFilePath());




        // 如果是提交未解决, 0
        Integer assignFlag = repairRecordDTO.getAssignFlag();
        // 解决状态，1已解决， 0为解决
        Integer solveStatus = repairRecordDTO.getSolveStatus();
        Integer flag = 1;
        // 未解决，需要重新指派
        if (!flag.equals(solveStatus) && flag.equals(assignFlag)) {
            // 重新指派
            fault.setStatus(FaultStatusEnum.ASSIGN.getStatus());
            one.setEndTime(new Date());
        }
        // 已解决
        if (flag.equals(solveStatus)) {
            fault.setStatus(FaultStatusEnum.RESULT_CONFIRM.getStatus());
            fault.setEndTime(new Date());
            one.setEndTime(new Date());
        }


        repairRecordService.updateById(one);

        updateById(fault);

        saveLog(loginUser, faultCode, "填写维修记录", FaultStatusEnum.REPAIR.getStatus(), null);

        // todo 发送消息


    }

    /**
     * 审核结果
     * @param resultDTO 审核结果对象
     */
    @Override
    public void approvalResult(ApprovalResultDTO resultDTO) {

        LoginUser loginUser = checkLogin();

        String faultCode = resultDTO.getFaultCode();

        Fault fault = isExist(faultCode);

        Integer approvalStatus = resultDTO.getApprovalStatus();

        Integer flag = 1;

        if (flag.equals(approvalStatus)) {
            fault.setStatus(FaultStatusEnum.Close.getStatus());
            saveLog(loginUser,"维修结果审核通过", faultCode, FaultStatusEnum.Close.getStatus(), null);
        }else {
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
            saveLog(loginUser,"维修结果驳回", faultCode, FaultStatusEnum.REPAIR.getStatus(), resultDTO.getApprovalRejection());
        }

        updateById(fault);

        //todo 发送
    }

    /**
     * 查询工作类型
     * @param faultCode
     * @return
     */
    @Override
    public List<CsWork> queryCsWork(String faultCode) {
        Fault fault = isExist(faultCode);
        String majorCode = fault.getMajorCode();
        List<CsWork> csWorkLsit = baseMapper.queryCsWorkByMajorCode(majorCode);
        if (CollectionUtil.isEmpty(csWorkLsit)) {
            return Collections.emptyList();
        }
        return csWorkLsit;
    }

    /**
     * 查询人员
     * @param faultCode
     * @return
     */
    @Override
    public List<LoginUser> queryUser(String faultCode) {
        LoginUser loginUser = checkLogin();

        String orgCode = loginUser.getOrgCode();

        if (StrUtil.isBlank(orgCode)) {
           return Collections.emptyList();
        }

        List<LoginUser> loginUserList = sysBaseAPI.getUserByDepIds(StrUtil.split(orgCode, ','));
        return loginUserList;
    }

    /**
     * 获取当前登录用户
     */
    private LoginUser checkLogin() {

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(user)) {
            throw new AiurtBootException("请重新登录");
        }

        return user;
    }

    /**
     * 根据编码判断故障单是否存在
     *
     * @param code 故障编码
     * @return
     */
    private Fault isExist(String code) {

        Fault fault = baseMapper.selectByCode(code);

        if (Objects.isNull(fault)) {
            throw new AiurtBootException("故障工单不存在");
        }

        return fault;
    }

    /**
     * 保存日志
     * @param user 用户
     * @param context 日志内容
     * @param faultCode 故障编码
     * @param status 状态
     */
    private void saveLog(LoginUser user, String context, String faultCode, int status, String remark) {
        OperationProcess operationProcess = OperationProcess.builder()
                .processLink(context)
                .processTime(new Date())
                .faultCode(faultCode)
                .processPerson(user.getUsername())
                .processCode(status)
                .remark(remark)
                .build();
        operationProcessService.save(operationProcess);
    }
}
