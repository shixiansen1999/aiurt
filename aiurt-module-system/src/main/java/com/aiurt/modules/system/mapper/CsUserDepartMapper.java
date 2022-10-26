package com.aiurt.modules.system.mapper;

import com.aiurt.modules.system.entity.CsUserDepart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.CsUserDepartModel;

import java.util.List;

/**
 * @Description: 用户部门权限表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface CsUserDepartMapper extends BaseMapper<CsUserDepart> {

    /**
     * 获取部门id
     * @param userId
     * @return
     */
    List<String> getDepartIds(@Param("userId") String userId);

    /**
     * 根据用户id获取部门权限
     * @param id
     * @return
     */
    List<CsUserDepartModel> getDepartByUserId(@Param("id") String id);
}
