package com.aiurt.modules.multideal.controller;

import com.aiurt.modules.flow.dto.ProcessParticipantsInfoDTO;
import com.aiurt.modules.multideal.dto.AddReduceMultiInstanceDTO;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author fgw
 */
@RestController
@RequestMapping("/multi")
public class MultiTaskController {

    @Autowired
    private IMultiInTaskService multiInTaskService;

    /**
     * 加签
     * @return
     */
    @ApiOperation(value = "加签", notes = "加签接口")
    @PostMapping("/addMultiInstance")
    public Result<?> addMultiInstance(@RequestBody AddReduceMultiInstanceDTO addReduceMultiInstanceDTO) {
        multiInTaskService.addMultiInstance(addReduceMultiInstanceDTO);
        return Result.OK();
    }



    /**
     * 减签
     * @return
     */
    @ApiOperation(value = "减签", notes = "减签接口")
    @PostMapping("/reduceMultiInstance")
    public Result<?> reduceMultiInstance(@RequestBody AddReduceMultiInstanceDTO addReduceMultiInstanceDTO) {
        multiInTaskService.reduceMultiInstance(addReduceMultiInstanceDTO);
        return Result.OK();
    }



    /**
     * 减签用户查询
     * @return
     */
    @ApiOperation(value = "减签用户查询", notes = "减签用户查询")
    @GetMapping("/getReduceMultiUser")
    public Result<List<ProcessParticipantsInfoDTO>> getReduceMultiUser(@RequestParam(value = "taskId") String taskId) {
        List<ProcessParticipantsInfoDTO> result = multiInTaskService.getReduceMultiUser(taskId);
        return Result.OK(result);
    }
}
