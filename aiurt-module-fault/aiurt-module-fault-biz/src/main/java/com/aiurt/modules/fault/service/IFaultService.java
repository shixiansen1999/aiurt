package com.aiurt.modules.fault.service;


import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IFaultService extends IService<Fault> {

    /**
     * 故障上报
     * @param fault 故障对象
     * @return 故障编码
     */
    String add(Fault fault);

    /**
     * 故障审批
     * @param approvalDTO 审批对象
     */
    void approval(ApprovalDTO approvalDTO);

    /**
     * 编辑
     * @param fault 故障对象 @see com.aiurt.modules.fault.entity.Fault
     */
    void edit(Fault fault);

    /**
     * 作废
     * @param cancelDTO
     */
    void cancel(CancelDTO cancelDTO);

    /**
     * 查看详情
     * @param code
     * @return
     */
    Fault queryByCode(String code);

    /**
     * 指派
     * @param assignDTO
     */
    void assign(AssignDTO assignDTO);

    /**
     * 故障工单领取
     * @param assignDTO
     */
    void receive(AssignDTO assignDTO);

    /**
     * 领取指派
     * @param code
     */
    void receiveAssignment(String code);

    /**
     * 拒绝指派
     * @param refuseAssignmentDTO
     */
    void refuseAssignment(RefuseAssignmentDTO refuseAssignmentDTO);

    /**
     * 开始维修
     * @param code
     */
    void startRepair(String code);

    /**
     * 发起挂起
     * @param hangUpDTO
     */
    void hangUp(HangUpDTO hangUpDTO);

    /**
     * 审批挂起
     * @param approvalHangUpDTO
     */
    void approvalHangUp(ApprovalHangUpDTO approvalHangUpDTO);

    /**
     * 取消挂起
     * @param code
     */
    void cancelHangup(String code);

    /**
     * 查询故障维修记录详情
     * @param faultCode
     * @return
     */
    RepairRecordDTO queryRepairRecord(String faultCode);

    /**
     * 填写维修信息
     * @param repairRecordDTO
     */
    void fillRepairRecord(RepairRecordDTO repairRecordDTO);



    /**
     * 审核结果
     * @param resultDTO 审核结果对象
     */
    void approvalResult(ApprovalResultDTO resultDTO);

    /**
     * 查询工作类型
     * @param faultCode
     * @return
     */
    List<CsWork> queryCsWork(String faultCode);

    /**
     * 查询指派的人员
     * @param faultCode
     * @return
     */
    List<LoginUser> queryUser(String faultCode);

    /**
     * 查询故障解决方案
     * @param faultKnowledgeBase
     * @return
     */
    KnowledgeDTO queryKnowledge(FaultKnowledgeBase faultKnowledgeBase);

    /**
     * 分页查询
     * @param page
     * @return
     */
    IPage<FaultKnowledgeBase> pageList(Page<FaultKnowledgeBase> page, FaultKnowledgeBase knowledgeBase);

    /**
     * 修改设备
     * @param confirmDeviceDTO
     */
    void confirmDevice(ConfirmDeviceDTO confirmDeviceDTO);

    /***
     * 使用知识库
     * @param faultCode
     * @param knowledgeId
     */
    void useKnowledgeBase(String faultCode, String knowledgeId);

    /**
     * 审核已驳回-再次提交审核
     * @param faultCode
     */
    void submitResult(String faultCode);

    /**
     * 已驳回-提交审核
     * @param fault
     */
    void saveResult(Fault fault);
}
