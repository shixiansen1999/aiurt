package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.service.StockLevel2RequisitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 二级库申领的controller
 *
 * @author 华宜威
 * @date 2023-09-21 09:54:40
 */
@Slf4j
@Api(tags = "二级库管理-二级库申领")
@RestController
@RequestMapping("/stock/StockLevel2Requisition")
public class StockLevel2RequisitionController {

    @Autowired
    private StockLevel2RequisitionService stockLevel2RequisitionService;

    /**
     * 二级库管理-添加
     *
     * @param stockLevel2RequisitionAddReqDTO 二级库申领的添加、编辑等请求DTO
     * @return Result<String> 返回添加成功提示
     */
    @AutoLog(value = "二级库管理-二级库申领-添加")
    @ApiOperation(value="二级库管理-二级库申领-添加", notes="二级库管理-二级库申领-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO){
        stockLevel2RequisitionService.add(stockLevel2RequisitionAddReqDTO);
        return Result.ok("添加成功！");
    }
}
