package com.aiurt.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.system.entity.CsUserSubsystem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.common.system.vo.CsUserSubsystemModel;

/**
 * @Description: 用户子系统表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface CsUserSubsystemMapper extends BaseMapper<CsUserSubsystem> {
    List<String> getSubsystemIds(@Param("userId") String userId);
    /**
     * 根据用户id获取站点
     * @param id
     * @return
     */
    List<CsUserSubsystemModel> getSubsystemByUserId(@Param("id") String id);
}
