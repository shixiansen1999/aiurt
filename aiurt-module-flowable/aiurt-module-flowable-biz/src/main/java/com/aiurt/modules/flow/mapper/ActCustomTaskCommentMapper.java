package com.aiurt.modules.flow.mapper;

import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


/**
 * @Description: act_custom_task_comment
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
public interface ActCustomTaskCommentMapper extends BaseMapper<ActCustomTaskComment> {

    void updateWorkticketState(@Param("id") String id);

    void updateConstructionWeekPlanCommand(@Param("id") String id);
}
