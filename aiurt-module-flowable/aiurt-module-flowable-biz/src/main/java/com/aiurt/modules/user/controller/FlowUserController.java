package com.aiurt.modules.user.controller;

import com.aiurt.modules.user.dto.FlowUserRelationRespDTO;
import com.aiurt.modules.user.service.IFlowUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author fgw
 */
@Api(tags="流程办理人")
@RestController
@RequestMapping("/flow/user")
@Slf4j
public class FlowUserController {

    @Autowired
    private IFlowUserService flowUserService;

    /**
     * 流程选人关系列表
     * @return
     */
    @GetMapping("queryRelationList")
    public Result<List<FlowUserRelationRespDTO>> queryRelationList() {
        List<FlowUserRelationRespDTO> list = flowUserService.queryRelationList();
        return Result.OK(list);
    }
}
