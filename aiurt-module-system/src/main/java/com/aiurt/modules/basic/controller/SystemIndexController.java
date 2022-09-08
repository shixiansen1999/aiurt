package com.aiurt.modules.basic.controller;

import com.aiurt.modules.basic.dto.WeatherDetailDTO;
import com.aiurt.modules.basic.service.ISystemIndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author fgw
 * @date 2022-09-07
 */
@Api(tags="天气预告")
@RestController
@RequestMapping("/system/index")
public class SystemIndexController {

    @Autowired
    private ISystemIndexService systemIndexService;

    @ApiOperation("获取天气信息")
    @GetMapping("/getWeatherInfo")
    public Result<WeatherDetailDTO> getWeatherInfo() {
        WeatherDetailDTO weatherDetailDTO = systemIndexService.getWeatherInfo();
        return Result.OK(weatherDetailDTO);
    }

}
