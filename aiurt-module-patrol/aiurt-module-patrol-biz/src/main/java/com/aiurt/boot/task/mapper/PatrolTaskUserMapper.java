package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_task_user
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskUserMapper extends BaseMapper<PatrolTaskUser> {

    String getUsername(@Param("userId") String userId);

    /**
     * 根据用户ID列表获取部门名称
     * @param list
     * @return
     */
    List<String> getDeptName(@Param("list")List<String> list);
}
