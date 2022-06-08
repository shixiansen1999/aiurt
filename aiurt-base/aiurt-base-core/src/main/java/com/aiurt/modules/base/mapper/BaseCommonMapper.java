package com.aiurt.modules.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.aiurt.common.api.dto.LogDTO;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: BaseCommonMapper
 * @author: jeecg-boot
 */
public interface BaseCommonMapper {

    /**
     * 保存日志
     * @param dto
     */
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto") LogDTO dto);

}
