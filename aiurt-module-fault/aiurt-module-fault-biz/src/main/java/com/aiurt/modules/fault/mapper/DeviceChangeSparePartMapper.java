package com.aiurt.modules.fault.mapper;


import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 备件更换记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
public interface DeviceChangeSparePartMapper extends BaseMapper<DeviceChangeSparePart> {

    /**
     * 根据故障编码以及维修记录id查询换件信息
     * @param faultCode 故障编码
     * @param recordId 维修记录id
     * @return
     */
    List<DeviceChangeSparePart> queryDeviceChangeByFaultCode(@Param("faultCode") String faultCode, @Param("recordId") String recordId);
}
