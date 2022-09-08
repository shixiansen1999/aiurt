package com.aiurt.modules.basic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.basic.dto.WeatherDetailDTO;
import com.aiurt.modules.basic.service.ISystemIndexService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
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

        String resultJson = restTemplate.getForObject(String.format("%s?key=%s&city=%s&extensions=base", url, key, city), String.class);

        log.info("请求获取的天气结果:{}", resultJson);

        if (StrUtil.isBlank(resultJson)) {
            return new WeatherDetailDTO();
        }

        JSONObject jsonObject = JSONObject.parseObject(resultJson);

        String status = jsonObject.getString("status");
        if (StrUtil.equalsIgnoreCase("0", status)) {
            log.info("请求数据失败");
            return new WeatherDetailDTO();
        }

        JSONArray lives = jsonObject.getJSONArray("lives");
        WeatherDetailDTO detailDTO = lives.getObject(0, WeatherDetailDTO.class);
        return detailDTO;
    }
}
