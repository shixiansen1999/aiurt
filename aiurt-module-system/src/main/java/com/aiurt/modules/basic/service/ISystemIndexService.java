package com.aiurt.modules.basic.service;

import com.aiurt.modules.basic.dto.QWeatherDTO;
import com.aiurt.modules.basic.dto.WeatherDetailDTO;

/**
 * @author fgw
 */
public interface ISystemIndexService {

    /**
     * 获取天气信息
     * @return
     */
    public WeatherDetailDTO getWeatherInfo();

    /**
     * 获取和风环境的
     * @return
     */
    QWeatherDTO getQWeatherInfo();
}
