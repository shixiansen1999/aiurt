package com.aiurt.boot.task.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.task.entity.RepairTaskDeviceRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: repair_task_device_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface RepairTaskDeviceRelMapper extends BaseMapper<RepairTaskDeviceRel> {

    /**
     * 根据编码查询检修单id
     * @param code
     * @return
     */
    String getId (String code);

    /**
     * 根据当前用户id,当前时间，查询用户提交的工单
     * @param userId
     * @param date
     * @return
     */
    List<RepairTaskDeviceRel> getUser(@Param("userId") String userId,@Param("date") Date date);

    /**
     * 根据当前用户id,获取用户姓名
     * @param staffId
     * @return
     */
    String getSubmitName(String staffId);

    /**
     * 根据设备code，获取设备站点code
     * @param deviceCode
     * @return
     */
    String getStationCode(String deviceCode);

    /**
     * 根据站点code,查询站点名称
     * @param stationCode
     * @return
     */
    String getStationName(String stationCode);

    /**
     * 获取当天提交的工单
     * @param startTime
     * @param endTime
     * @param taskId
     * @param taskDeviceCode
     * @return
     */
    List<RepairTaskDeviceRel> getTodaySubmit(@Param("startTime")DateTime startTime,@Param("endTime") DateTime endTime, @Param("taskId")String taskId, @Param("taskDeviceCode")String taskDeviceCode);
}
