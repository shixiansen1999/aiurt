package com.aiurt.modules.fault.mapper;

import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.aiurt.modules.fault.dto.RadarNumberModelDTO;

import java.util.List;

/**
 * @Description: 维修记录
 * @Author: aiurt
 * @Date: 2022-06-28
 * @Version: V1.0
 */
public interface FaultRepairRecordMapper extends BaseMapper<FaultRepairRecord> {
    /**
     * 根据维修编号查询故障维修记录
     *
     * @param faultCode
     * @return
     */
    List<RepairRecordDetailDTO> queryRecordByFaultCode(@Param("faultCode") String faultCode);

    /**
     * 查询故障分类名称
     *
     * @param deviceTypeCode
     * @return
     */
    @Select("select `name` from device_type where (`code` = #{deviceTypeCode} or id = #{deviceTypeCode} ) limit 1")
    String queryDeviceTypeName(@Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 根据故障编号获取维修记录中的处理方案
     *
     * @param faultCode
     * @return
     */
    RepairRecordDetailDTO getRecordByFaultCode(String faultCode);

    /**
     * 人员画像擅长维修统计
     *
     * @param usernames
     * @return
     */
    List<FaultMaintenanceDTO> personnelPortraitStatic(@Param("usernames") List<String> usernames);

    /**
     * 处理的设备TOP5
     *
     * @param username
     * @return
     */
    List<FaultHistoryDTO> repairDeviceTopFive(@Param("username") String username);

    /**
     * 历史维修记录-设备故障信息列表
     *
     * @param username
     * @return
     */
    List<FaultDeviceDTO> deviceInfo(@Param("username") String username);

    /**
     * 获取故障处理总次数
     *
     * @return
     */
    List<RadarNumberModelDTO> getHandleNumber();

    /**
     * 获取用户故障处理的平均响应时间和平均解决时间
     *
     * @return
     */
    List<EfficiencyDTO> getEfficiency();
}
