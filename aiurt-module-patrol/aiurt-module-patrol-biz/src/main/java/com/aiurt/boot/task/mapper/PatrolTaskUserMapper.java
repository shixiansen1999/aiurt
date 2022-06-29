package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: patrol_task_user
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskUserMapper extends BaseMapper<PatrolTaskUser> {

    String getUsername(@Param("userId") String userId);
}
