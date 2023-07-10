package com.aiurt.modules.worklog.controller;

import com.aiurt.common.result.WorkLogResult;
import com.aiurt.modules.worklog.dto.WorkLogBigScreenReqDTO;
import com.aiurt.modules.worklog.dto.WorkLogBigScreenRespDTO;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * 大屏-工作日志相关接口
 * @author 华宜威
 * @date 2023-07-10 12:07:05
 */
@Slf4j
@Api(tags="大屏-工作日志")
@RestController
@RequestMapping("/worklog/workLogBigScreen")
public class WorkLogBigScreenController {
    @Autowired
    private IWorkLogService workLogDepotService;

    /**
     * 大屏-工作日志列表查询
     * @param workLogBigScreenReqDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value="大屏-工作日志列表查询", notes="大屏-工作日志列表查询")
    @GetMapping(value = "/bigScreenPageList")
    public Result<IPage<WorkLogBigScreenRespDTO>> bigScreenPageList(WorkLogBigScreenReqDTO workLogBigScreenReqDTO,
                                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        Page<WorkLogResult> page = new Page<WorkLogResult>(pageNo, pageSize);
        IPage<WorkLogBigScreenRespDTO> bigScreenPageList = workLogDepotService.bigScreenPageList(page, workLogBigScreenReqDTO);
        return Result.ok(bigScreenPageList);
    }
}
