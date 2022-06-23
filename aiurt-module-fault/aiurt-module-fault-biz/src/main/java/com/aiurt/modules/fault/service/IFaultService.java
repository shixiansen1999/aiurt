package com.aiurt.modules.fault.service;


import com.aiurt.modules.fault.dto.ApprovalDTO;
import com.aiurt.modules.fault.dto.CancelDTO;
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
}
