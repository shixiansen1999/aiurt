package com.aiurt.modules.recall.controller;

import com.aiurt.modules.recall.dto.RecallReqDTO;
import com.aiurt.modules.recall.service.IFlowRecallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
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
