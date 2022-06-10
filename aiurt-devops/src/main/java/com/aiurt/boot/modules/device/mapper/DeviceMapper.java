package com.aiurt.boot.modules.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.statistical.vo.DeviceDataVo;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface DeviceMapper extends BaseMapper<Device> {

//    String selectNameByCode(String code);


    Device getById(String id);

    /**
     * 大屏
     */
    Integer getDeviceNum(Map map);
    List<DeviceDataVo> getSystemDeviceData(Map map);
    List<DeviceDataVo> getDeviceNumByStation(Map map);

    List<Device> queryDeviceByStationCodeAndSystemCode(@Param("stationCode") String stationCode , @Param("systemCode") String systemCode);
}
