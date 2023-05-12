package com.aiurt.modules.basic.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.basic.WeatherIconEnum;
import com.aiurt.modules.basic.dto.QWeatherDTO;
import com.aiurt.modules.basic.dto.WeatherDetailDTO;
import com.aiurt.modules.basic.service.ISystemIndexService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

        WeatherDetailDTO detailDTO = null;
        try {
            JSONArray lives = jsonObject.getJSONArray("lives");
            if (Objects.isNull(lives)) {
                return new WeatherDetailDTO();
            }
            detailDTO = lives.getObject(0, WeatherDetailDTO.class);
        } catch (Exception e) {
        }
        return detailDTO;
    }

    /**
     * 获取和风环境的
     *
     * @return
     */
    @Override
    public QWeatherDTO getQWeatherInfo() {
        if (StrUtil.startWithIgnoreCase(url,"https://")) {
            String resultJson = restTemplate.getForObject(String.format("%s?key=%s&city=%s&extensions=base", url, key, city), String.class);

            log.info("请求获取的天气结果:{}", resultJson);

            if (StrUtil.isBlank(resultJson)) {
                return new QWeatherDTO();
            }

            JSONObject jsonObject = JSONObject.parseObject(resultJson);

            String status = jsonObject.getString("status");
            if (StrUtil.equalsIgnoreCase("0", status)) {
                log.info("请求数据失败");
                return new QWeatherDTO();
            }

            JSONArray lives = jsonObject.getJSONArray("lives");
            WeatherDetailDTO detailDTO = lives.getObject(0, WeatherDetailDTO.class);

            QWeatherDTO qWeatherDTO = new QWeatherDTO();
            qWeatherDTO.setIndocno(0L);
            qWeatherDTO.setFxDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
            qWeatherDTO.setFxDateCN(DateUtil.format(new Date(), "yyyy年MM月dd日"));
            DateUtil.dayOfWeekEnum(new Date());
            qWeatherDTO.setFxDateOfWeek(DateUtil.dayOfWeekEnum(new Date()).toChinese());
            qWeatherDTO.setTempMax(detailDTO.getTemperature());
            qWeatherDTO.setTempMin(detailDTO.getTemperature());
            String weather = detailDTO.getWeather();
            qWeatherDTO.setTextDay(weather);
            WeatherIconEnum weatherIconEnum = WeatherIconEnum.getByCode(weather);
            qWeatherDTO.setIconDay(Objects.nonNull(weatherIconEnum)?String.valueOf(weatherIconEnum.getCode()) : String.valueOf( WeatherIconEnum.CODE_999.getCode()));

            return qWeatherDTO;

        }else {
            String resultJson = null;
            try {
                resultJson = restTemplate.getForObject(url, String.class);
            } catch (RestClientException e) {
               log.info(e.getMessage(),e);
            }
            log.info("请求获取的天气结果:{}", resultJson);
            if (StrUtil.isBlank(resultJson)) {
                return new QWeatherDTO();
            }
            JSONObject jsonObject = JSONObject.parseObject(resultJson);
            String code = jsonObject.getString("code");

            if (StrUtil.equalsIgnoreCase("0", code)) {
                return new QWeatherDTO();
            }

            QWeatherDTO data = jsonObject.getObject("data", QWeatherDTO.class);

            return data;
        }
    }
}
