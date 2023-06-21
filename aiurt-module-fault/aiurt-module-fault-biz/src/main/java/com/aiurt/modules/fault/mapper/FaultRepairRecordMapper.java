package com.aiurt.modules.fault.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 维修记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
public interface FaultRepairRecordMapper extends BaseMapper<FaultRepairRecord> {
    /**
     * 根据维修编号查询故障维修记录
     * @param faultCode
     * @return
     */
    List<RepairRecordDetailDTO> queryRecordByFaultCode(@Param("faultCode") String faultCode);

    /**
     * 查询故障分类名称
     * @param deviceTypeCode
     * @return
     */
    @Select("select `name` from device_type where (`code` = #{deviceTypeCode} or id = #{deviceTypeCode} ) limit 1")
    String queryDeviceTypeName(@Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 根据故障编号获取维修记录中的处理方案
     * @param faultCode
     * @return
     */
    RepairRecordDetailDTO getRecordByFaultCode(String faultCode);

    /**
     * 获取开始维修时间在指定时间范围内的维修单
     * @param startTime  开始时间
     * @param endTime 结束时间
     * @param userNames 用户集合
     * @return
     */
    List<FaultRepairRecord> getTodayRecord(@Param("startTime")DateTime startTime, @Param("endTime")DateTime endTime, @Param("userNames")List<String> userNames);
    /**
     *获取所有未完成的故障信息和当日已完成的故障信息
     * @param startTime  开始时间
     * @param endTime 结束时间
     * @param userNames 用户集合
     * @return
     */
    List<Fault> getTodayFault(@Param("startTime")DateTime startTime, @Param("endTime")DateTime endTime, @Param("userNames")List<String> userNames);
}
