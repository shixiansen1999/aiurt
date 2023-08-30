package com.aiurt.modules.faultexternal.mapper;

import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 调度系统故障
 * @Author: aiurt
 * @Date:   2023-02-16
 * @Version: V1.0
 */
public interface FaultExternalMapper extends BaseMapper<FaultExternal> {

    List<FaultExternal> selectFaultExternalPage(@Param("page") Page<FaultExternal> page, @Param("faultExternal")FaultExternal faultExternal);

    Integer getLineCode(Integer iline);
}
