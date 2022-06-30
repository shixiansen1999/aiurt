package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.*;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        builder.append(majorCode).append(DateUtil.format(new Date(), "yyyyMMddHHmm"));
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
        saveLog(user, "故障上报", fault.getCode(), 1);

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
                    .setProcessCode(FaultStatusEnum.APPROVAL_PASS.getStatus());
        } else {
            // 驳回
            fault.setStatus(FaultStatusEnum.APPROVAL_REJECT.getStatus());
            fault.setApprovalRejection(approvalDTO.getApprovalRejection());
            operationProcess.setProcessLink(FaultStatusEnum.APPROVAL_REJECT.getMessage())
                    .setProcessCode(FaultStatusEnum.APPROVAL_REJECT.getStatus());
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
        saveLog(loginUser, "修改故障工单", fault.getCode(), FaultStatusEnum.NEW_FAULT.getStatus());
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
        saveLog(user, FaultStatusEnum.CANCEL.getMessage(), fault.getCode(), FaultStatusEnum.CANCEL.getStatus());

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

        // 字典值转换
        Map<String, List<DictModel>> manyDictItems = sysBaseAPI.getManyDictItems(null);


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
        saveLog(user, FaultStatusEnum.ASSIGN.getMessage(), assignDTO.getFaultCode(), FaultStatusEnum.ASSIGN.getStatus());
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
        saveLog(user, FaultStatusEnum.RECEIVE.getMessage(), assignDTO.getFaultCode(), FaultStatusEnum.RECEIVE.getStatus());

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

        saveLog(loginUser, "接收指派", code, FaultStatusEnum.RECEIVE_ASSIGN.getStatus());
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
        saveLog(loginUser, "拒绝接收指派", refuseAssignmentDTO.getFaultCode(), FaultStatusEnum.APPROVAL_PASS.getStatus());

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
        saveLog(user, "开始维修", code, FaultStatusEnum.REPAIR.getStatus());

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

        saveLog(user, "申请挂起", hangUpDTO.getFaultCode(), FaultStatusEnum.HANGUP_REQUEST.getStatus());

        // todo 发送消息提醒

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
            saveLog(user, "挂起审批通过", approvalHangUpDTO.getFaultCode(), FaultStatusEnum.HANGUP.getStatus());
        } else {
            // 驳回-维修中
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
            //todo
            fault.setApprovalRejection(approvalHangUpDTO.getApprovalRejection());
            saveLog(user, "挂起审批驳回", approvalHangUpDTO.getFaultCode(), FaultStatusEnum.REPAIR.getStatus());
        }

        updateById(fault);
        // todo 发送消息


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

        saveLog(loginUser, "取消挂起", code, FaultStatusEnum.REPAIR.getStatus());

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

       // Fault fault = isExist(faultCode);
        LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRepairRecord::getFaultCode, faultCode).eq(FaultRepairRecord::getAppointUserName, loginUser.getUsername())
                .eq(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .orderByDesc(FaultRepairRecord::getCreateTime).last("limit 1");
        FaultRepairRecord repairRecord = repairRecordService.getBaseMapper().selectOne(wrapper);

        RepairRecordDTO repairRecordDTO = new RepairRecordDTO();
        BeanUtils.copyProperties(repairRecord, repairRecordDTO);

        // 查询参与人
        List<FaultRepairParticipants> participantsList = repairParticipantsService.queryParticipantsByRecordId(repairRecord.getId());
        repairRecordDTO.setParticipantsList(participantsList);

        List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.queryDeviceChangeByFaultCode(faultCode, repairRecord.getId());
        // 易耗品
       // deviceChangeSparePartList.stream().filter()

        // 维修设备
        List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultCode);
        repairRecordDTO.setDeviceList(faultDeviceList);


        return repairRecordDTO;
    }

    /**
     * 填写维修记录
     * @param repairRecordDTO 填写维修记录
     */
    @Override
    public void fillRepairRecord(RepairRecordDTO repairRecordDTO) {

        LoginUser loginUser = checkLogin();

    }

    /**
     * 审核结果
     * @param resultDTO 审核结果对象
     */
    @Override
    public void approvalResult(ApprovalResultDTO resultDTO) {

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
    private void saveLog(LoginUser user, String context, String faultCode, int status) {
        OperationProcess operationProcess = OperationProcess.builder()
                .processLink(context)
                .processTime(new Date())
                .faultCode(faultCode)
                .processPerson(user.getUsername())
                .processCode(status)
                .build();
        operationProcessService.save(operationProcess);
    }
}
