package com.aiurt.modules.fault.mapper;

import java.util.List;

import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 维修记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
public interface FaultRepairRecordMapper extends BaseMapper<FaultRepairRecord> {

    List<RepairRecordDetailDTO> queryRecordByFaultCode(@Param("faultCode") String faultCode);

    @Select("select `name` from device_type where (`code` = #{deviceTypeCode} or id = #{deviceTypeCode} ) limit 1")
    String queryDeviceTypeName(@Param("deviceTypeCode") String deviceTypeCode);
}
