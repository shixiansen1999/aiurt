package com.aiurt.modules.fault.service;


import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.extension.service.IService;

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


}
