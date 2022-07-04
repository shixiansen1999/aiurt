package com.aiurt.modules.fault.mapper;

import java.util.List;

import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.entity.Fault;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface FaultMapper extends BaseMapper<Fault> {

     Fault selectByCode(@Param("code") String code);

     List<CsWork> queryCsWorkByMajorCode(@Param("majorCode") String majorCode);

}
