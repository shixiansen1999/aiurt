package com.aiurt.modules.forecast.controller;

import com.aiurt.modules.flow.dto.HighLightedNodeDTO;
import com.aiurt.modules.forecast.service.impl.FlowForecastServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fgw
 */
@Api(tags="流程预测")
@RestController
@RequestMapping("/forecast")
public class FlowForecastController {

    @Autowired
    private FlowForecastServiceImpl flowForecastService;

    /**
     *
     * @param processInstanceId
     * @return
     */
    @GetMapping("/flowChart")
    @ApiOperation(value="流程预测", notes="流程预测")
    @ApiImplicitParams({
            @ApiImplicitParam(dataTypeClass = String.class, name = "processInstanceId", value = "流程实例id", required = true, paramType = "query")
    })
    public Result<HighLightedNodeDTO> flowChart(@RequestParam(value = "processInstanceId") String processInstanceId) {
        HighLightedNodeDTO highLightedNodeDTO =  flowForecastService.flowChart(processInstanceId);
        return Result.OK(highLightedNodeDTO);
    }
}
