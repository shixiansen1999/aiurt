package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.PatrolTaskDeviceReadRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/11/29
 * @desc
 */
public interface PatrolTaskDeviceReadRecordMapper extends BaseMapper<PatrolTaskDeviceReadRecord> {

    /**
     * 获取用户的阅读记录
     * @param taskDeviceId
     * @param majorCode
     * @param subsystemCode
     * @param taskId
     * @param userId
     * @return
     */
    List<PatrolTaskDeviceReadRecord> getReadList(@Param("taskDeviceId") String taskDeviceId,@Param("majorCode") String majorCode, @Param("subsystemCode")String subsystemCode, @Param("taskId")String taskId,@Param("userId")String userId);
}
