package com.aiurt.modules.material.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.material.entity.MaterialBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@EnableDataPerm
public interface MaterialBaseMapper extends BaseMapper<MaterialBase> {

    /**
     * 根据code获取物资基础数据，包括已删除的
     * @param code
     * @return
     */
    MaterialBase selectByCode(@Param("code") String code);
}
