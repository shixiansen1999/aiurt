package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolCheckResultDTO;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_check_result
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolCheckResultMapper extends BaseMapper<PatrolCheckResult> {

    List<PatrolCheckResultDTO> getListByTaskDeviceId(@Param("taskDeviceId") String taskDeviceId);

    int addResultList(@Param("list") List<PatrolCheckResult> resultList);

    /**
     *app-获取巡检设备检查结果
     * @param taskDeviceId
     * @param taskDeviceId
     * @return
     */
    List<PatrolCheckResultDTO> getCheckResult(String taskDeviceId);
}
