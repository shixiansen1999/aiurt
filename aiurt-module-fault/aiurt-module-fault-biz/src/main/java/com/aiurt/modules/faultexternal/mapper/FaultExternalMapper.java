package com.aiurt.modules.faultexternal.mapper;

import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @Description: 调度系统故障
 * @Author: aiurt
 * @Date:   2023-02-16
 * @Version: V1.0
 */
public interface FaultExternalMapper extends BaseMapper<FaultExternal> {

    List<FaultExternal> selectFaultExternalPage(Page<FaultExternal> page, FaultExternal faultExternal);
}
