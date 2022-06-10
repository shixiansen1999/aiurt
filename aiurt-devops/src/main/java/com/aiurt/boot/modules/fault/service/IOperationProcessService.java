package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.result.OperationProcessResult;
import com.aiurt.boot.modules.fault.entity.OperationProcess;

import java.util.List;

/**
 * @Description: 运转流程
 * @Author: swsc
 * @Date:   2021-09-27
 * @Version: V1.0
 */
public interface IOperationProcessService extends IService<OperationProcess> {

    /**
     * 根据code查询故障运转记录
     * @param code
     * @return
     */
    List<OperationProcessResult> getOperationProcess(String code);

}
