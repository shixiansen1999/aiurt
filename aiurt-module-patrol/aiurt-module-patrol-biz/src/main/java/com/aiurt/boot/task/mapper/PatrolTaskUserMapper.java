package com.aiurt.boot.task.mapper;

import com.aiurt.boot.screen.model.ScreenDurationTask;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
     *
     * @param list
     * @return
     */
    List<String> getDeptName(@Param("list") List<String> list);

    /**
     * 获取当前用户
     *
     * @param id
     * @return
     */
    List<PatrolTask> getUserTask(String id);

    /**
     * 获取人员在指定时间范围内的任务时长(单位秒)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<ScreenDurationTask> getScreenUserDuration(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
