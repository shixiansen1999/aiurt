package com.aiurt.modules.forecast.controller;

import com.aiurt.modules.flow.dto.HighLightedNodeDTO;
import com.aiurt.modules.forecast.service.FlowForecastServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fgw
 */
@RestController
@RequestMapping("/forecast")
public class FlowForecastController {

    @Autowired
    private FlowForecastServiceImpl flowForecastService;

    @RequestMapping("/test")
    public Result<HighLightedNodeDTO> test() {
        HighLightedNodeDTO highLightedNodeDTO =  flowForecastService.test("8310561064b811eeb8da0242ac110005");
        return Result.OK(highLightedNodeDTO);
    }
}
