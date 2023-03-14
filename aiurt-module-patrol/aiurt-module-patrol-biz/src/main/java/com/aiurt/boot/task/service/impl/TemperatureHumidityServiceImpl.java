package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.boot.task.mapper.TemperatureHumidityMapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


/**
 * @Description: patrol_accessory
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class TemperatureHumidityServiceImpl extends ServiceImpl<TemperatureHumidityMapper,TemperatureHumidity> implements IService<TemperatureHumidity> {

}
