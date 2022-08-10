package com.aiurt.boot.task.mapper;


import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.task.dto.DeviceDTO;
import com.aiurt.boot.task.dto.PatrolBillDTO;
import com.aiurt.boot.task.dto.PatrolTaskDeviceDTO;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskDeviceMapper extends BaseMapper<PatrolTaskDevice> {

    /**
     * PC巡检任务池详情-巡检工单
     *
     * @param patrolTaskDeviceParam
     * @return
     */
    IPage<PatrolTaskDeviceParam> selectBillInfo(@Param("page") Page<PatrolTaskDeviceParam> page, @Param("taskDevice") PatrolTaskDeviceParam patrolTaskDeviceParam);

    /**
     * 设备台账-巡视履历
     *
     * @param page
     * @param patrolTaskDeviceParam
     * @return
     */
    IPage<PatrolTaskDeviceParam> selectBillInfoForDevice(@Param("page") Page<PatrolTaskDeviceParam> page, @Param("taskDevice") PatrolTaskDeviceParam patrolTaskDeviceParam);

    /**
     * PC巡检任务池详情-巡检工单详情
     *
     * @param patrolNumber
     * @return
     */
    PatrolTaskDeviceParam selectBillInfoByNumber(@Param("patrolNumber") String patrolNumber);

    /**
     * app-获取巡检清单列表
     *
     * @param pageList
     * @param id
     * @param search
     * @return
     */
    List<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(@Param("pageList") Page<PatrolTaskDeviceDTO> pageList, @Param("id") String id, @Param("search") String search);

    /**
     * app-获取提交人名称
     *
     * @param userId
     * @return
     */
    String getSubmitName(String userId);

    /**
     * 获取线路
     *
     * @param code
     * @return
     */
    List<String> getPosition(String code);

    /**
     * app-获取巡检表名称
     *
     * @param deviceId
     * @return
     */
    PatrolStandard getStandardName(String deviceId);

    /**
     * pc -获取设备的位置
     *
     * @param code
     * @return
     */
    Device getDevice(String code);

    /**
     * 根据设备编号查询设备信息
     *
     * @param deviceCode
     * @return
     */
    Device getDeviceInfoByCode(String deviceCode);

    /**
     * 根据设备编号获取这个设备的信息
     *
     * @param deviceCode
     * @return
     */
    DeviceDTO getTaskStandardDevice(String deviceCode);

    /**
     * 根据位置code,获取设备位置
     *
     * @param positionCode
     * @return
     */
    String getDevicePosition(String positionCode);

    /**
     * 获取设备的专业
     *
     * @param majorCode
     * @return
     */
    String getMajorName(String majorCode);

    /**
     * 获取设备的子系统
     *
     * @param systemCode
     * @return
     */
    String getSysName(String systemCode);

    /**
     * 根据站点code,获取站点下的全部位置
     *
     * @param stationCode
     * @return
     */
    List<String> getAllPosition(String stationCode);

    /**
     * 根据任务ID获取工单站点和巡检表联动信息
     *
     * @param taskId
     * @return
     */
    List<PatrolBillDTO> getBillGangedInfo(@Param("taskId") String taskId);

    /**
     * 根据站点编号获取站点名称
     * @param stationCode
     * @return
     */
    String getStationName(String stationCode);
}
