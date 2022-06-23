package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {

    /**
     * app-列表查询
     * @param pageList
     * @param patrolTaskDTO
     * @return
     * author hlq
     */
    List<PatrolTaskDTO>getPatrolTaskList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO")PatrolTaskDTO patrolTaskDTO);

    /**
     * app-获取组织机构名称
     * @param planCode
     * @param organizationId
     * @return
     * author hlq
     */
    List<String> getOrganizationName(@Param("organizationId")String organizationId, @Param("planCode")String planCode);
    /**
     * app-获取站点名称
     * @param code
     * @return
     * author hlq
     */
    List<String> getStationName(String code);
    /**
     * app-获取巡检人名称
     * @param code
     * @return
     * author hlq
     */
    List<String> getPatrolUserName(String code);

    /**
     * 查询巡检任务列表
     *
     * @param page
     * @param patrolTask
     * @return
     */
    IPage<PatrolTask> getTaskList(Page<PatrolTask> page, @Param("patrolTask") PatrolTask patrolTask);

    /**
     * 获取退回人的名称
     * @param patrolReturnUserId
     * @return
     */
    String getUserName(String patrolReturnUserId);
}