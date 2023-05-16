package com.aiurt.modules.sensorinformation.mapper;

import com.aiurt.modules.sensorinformation.entity.SensorInformation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: sensor_information
 * @Author: aiurt
 * @Date:   2023-05-15
 * @Version: V1.0
 */
public interface SensorInformationMapper extends BaseMapper<SensorInformation> {

    /**
     * 传感器-列表
     * @param page
     * @param sensorInformation
     * @return
     */
    List<SensorInformation> queryPageList(@Param("page") Page<SensorInformation> page, @Param("condition")SensorInformation sensorInformation);

    /**
     * 传感器-列表（不分页）
     * @param sensorInformation
     * @return
     */
    List<SensorInformation> getList(@Param("condition")SensorInformation sensorInformation);
}
