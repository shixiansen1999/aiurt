package com.aiurt.modules.recall.controller;

import com.aiurt.modules.recall.dto.RecallReqDTO;
import com.aiurt.modules.recall.service.IFlowRecallService;
import com.aiurt.modules.remind.service.IFlowRemindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import liquibase.pro.packaged.I;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author fgw
 */
@Api(tags="流程撤回")
@RestController
@RequestMapping("/recall/")
@Slf4j
public class FlowRecallController {
     @Resource
     private IFlowRecallService flowRecallService;

    @PostMapping("/manualRecall")
    @ApiOperation(value = "流程撤回")
    public Result<?> manualRecall(@RequestBody RecallReqDTO recallReqDTO) {
        flowRecallService.recall(recallReqDTO);
        return Result.OK("撤回成功");
    }
}
