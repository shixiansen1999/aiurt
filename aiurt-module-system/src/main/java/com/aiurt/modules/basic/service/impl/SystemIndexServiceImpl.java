package com.aiurt.modules.basic.service.impl;

import com.aiurt.modules.basic.dto.WeatherDetailDTO;
import com.aiurt.modules.basic.service.ISystemIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

/**
 * @author fgw
 */
@Slf4j
@Service
public class SystemIndexServiceImpl implements ISystemIndexService, Serializable {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${aiurt.weather.url}")
    private String url;

    @Value("${aiurt.weather.key}")
    private String key;

    @Value("${aiurt.weather.city}")
    private String city;

    @Override
    public WeatherDetailDTO getWeatherInfo() {


        return new WeatherDetailDTO();
    }
}
