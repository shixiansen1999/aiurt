package com.aiurt.modules.faultexternallinestarel.mapper;

import com.aiurt.modules.faultexternallinestarel.entity.FaultExternalLineStaRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: fault_external_line_sta_rel
 * @Author: aiurt
 * @Date:   2023-06-13
 * @Version: V1.0
 */
public interface FaultExternalLineStaRelMapper extends BaseMapper<FaultExternalLineStaRel> {

    /**
     * 调度子系统位置-分页列表查询
     * @param page 分页参数
     * @param faultExternalLineStaRel 分页查询
     * @return List<FaultExternalLineStaRel> pageList
     */
    List<FaultExternalLineStaRel> pageList(@Param("pageList") Page<FaultExternalLineStaRel> page,@Param("condition") FaultExternalLineStaRel faultExternalLineStaRel);
}
