package com.aiurt.modules.faultalarm.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.faultalarm.entity.AlmRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 告警记录mapper
 * @Author: aiurt
 * @Date: 2023-06-05
 * @Version: V1.0
 */
@EnableDataPerm
public interface AlmRecordMapper extends BaseMapper<AlmRecord> {

}
