package com.aiurt.modules.system.mapper;

import com.aiurt.modules.system.entity.CsUserDepart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 用户部门权限表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface CsUserDepartMapper extends BaseMapper<CsUserDepart> {

    List<String> getDepartIds(@Param("userId") String userId);
}
