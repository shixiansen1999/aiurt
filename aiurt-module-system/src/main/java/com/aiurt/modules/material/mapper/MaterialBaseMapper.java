package com.aiurt.modules.material.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.material.entity.MaterialBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@EnableDataPerm
public interface MaterialBaseMapper extends BaseMapper<MaterialBase> {

}
