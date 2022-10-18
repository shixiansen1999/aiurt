package com.aiurt.modules.robot.mapper;

import com.aiurt.modules.robot.entity.IpMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: ip_mapping
 * @Author: jeecg-boot
 * @Date:   2022-10-10
 * @Version: V1.0
 */
public interface IpMappingMapper extends BaseMapper<IpMapping> {
    /**
     * 编辑页面重复性校验
     *
     * @param condition
     * @return
     */
    Long duplicateCheckCount(@Param("condition") IpMapping condition);
    /**
     * 添加页面校验
     *
     * @param condition
     * @return
     */
    Long duplicateCheckCountNoId(@Param("condition") IpMapping condition);
}
