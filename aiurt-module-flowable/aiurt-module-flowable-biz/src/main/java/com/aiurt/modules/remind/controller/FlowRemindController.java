package com.aiurt.modules.remind.controller;

import com.aiurt.modules.remind.service.IFlowRemindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author fgw
 */
@Api(tags="流程催办")
@RestController
@RequestMapping("/remind/")
@Slf4j
public class FlowRemindController {

    @Autowired
    private IFlowRemindService flowRemindService;

    @GetMapping("/manualRemind")
    @ApiOperation(value = "流程催办")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", value = "流程实例id", required = true, paramType = "query")
    })
    public Result<?> manualRemind(@RequestParam(value = "processInstanceId", required = true) String processInstanceId) {
        flowRemindService.manualRemind(processInstanceId);
        return Result.OK("催办成功");
    }
}
