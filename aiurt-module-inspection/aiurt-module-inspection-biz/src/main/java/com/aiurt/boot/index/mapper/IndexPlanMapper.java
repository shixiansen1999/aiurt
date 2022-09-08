package com.aiurt.boot.index.mapper;

import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.index.dto.TaskStateDTO;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: 首页检修模块
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
public interface IndexPlanMapper {

    /**
     * 分页聚合检修数据
     *
     * @param type
     * @param page
     * @param taskDetailsReq
     * @return
     */
    List<TaskDetailsDTO> getGropuByData(@Param("type") Integer type, @Param("page") Page<TaskDetailsDTO> page, @Param("taskDetailsReq") TaskDetailsReq taskDetailsReq,@Param("condition") List<String> condition);

    /**
     * 点击站点获取检修数据
     * @param page
     * @param type
     * @param taskDetailsReq
     * @return
     */
    List<RepairPoolDetailsDTO> getMaintenancDataByStationCode(@Param("page")Page<RepairPoolDetailsDTO> page,@Param("type")Integer type, @Param("taskDetailsReq")TaskDetailsReq taskDetailsReq);

    /**
     * 根据条件查询检修数据（无分页）
     * @param stationCode
     * @param taskDetailsReq
     * @return
     */
    List<RepairPoolDetailsDTO> getMainDataByStationCodeNoPage(@Param("stationCode") String stationCode,@Param("taskDetailsReq") TaskDetailsReq taskDetailsReq);

    /**
     * 查询站点下的检修任务是否已经完成
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskStateDTO> selectStationState(@Param("startTime") Date startTime, @Param("endTime")  Date endTime);
}
