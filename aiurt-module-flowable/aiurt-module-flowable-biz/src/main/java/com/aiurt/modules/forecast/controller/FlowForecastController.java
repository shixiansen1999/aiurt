package com.aiurt.modules.forecast.controller;

import com.aiurt.modules.forecast.service.FlowForecastServiceImpl;
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
    public void test() {
        flowForecastService.test("7cc8fe915d0911eebebd0242ac110005");
    }
}
