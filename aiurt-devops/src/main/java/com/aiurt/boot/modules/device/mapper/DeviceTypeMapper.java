package com.aiurt.boot.modules.device.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 设备分类
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface DeviceTypeMapper extends BaseMapper<DeviceType> {

    String getTypeByCode(String code);

    Integer getDeviceType(String code);

    List<DeviceType> getDeviceTypeListBySystemCode(String systemCode);

    @Select("select code from device_type where name = #{typeCode}")
    String getTypeCode(@Param("typeCode")String typeCode);
}
