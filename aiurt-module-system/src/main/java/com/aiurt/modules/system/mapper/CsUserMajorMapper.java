package com.aiurt.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.system.entity.CsUserMajor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户专业表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface CsUserMajorMapper extends BaseMapper<CsUserMajor> {
    List<String> getMajorIds(@Param("userId") String userId);
}
