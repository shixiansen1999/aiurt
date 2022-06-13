package com.aiurt.boot.modules.fault.mapper;

import com.aiurt.common.result.OperationProcessResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.fault.entity.OperationProcess;

import java.util.List;

/**
 * @Description: 运转流程
 * @Author: swsc
 * @Date:   2021-09-27
 * @Version: V1.0
 */
public interface OperationProcessMapper extends BaseMapper<OperationProcess> {

    /**
     * 根据code查询运转记录
     * @param code
     * @return
     */
    List<OperationProcessResult> selectByCode(String code);

}
