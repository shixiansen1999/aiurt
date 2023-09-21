package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.stock.dto.req.MaterialStockOutInRecordReqDTO;
import com.aiurt.modules.stock.dto.resp.MaterialStockOutInRecordRespDTO;
import com.aiurt.modules.stock.service.IMaterialStockOutInRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 出入库记录表的controller
 *
 * @author 华宜威
 * @date 2023-09-20 11:43:00
 */

@Slf4j
@Api(tags = "出入库记录")
@RestController
@RequestMapping("/materialStockOutInRecord")
public class MaterialStockOutInRecordController {

    @Autowired
    private IMaterialStockOutInRecordService materialStockOutInRecordService;

    /**
     * 出入库记录分页列表查询
     *
     * @param materialStockOutInRecordReqDTO 出入库记录查询的请求DTO
     * @return Result<IPage<MaterialStockOutInRecordRespDTO>> 返回出入库记录查询的响应DTO的Page对象
     */
    @AutoLog(value = "出入库记录-分页列表查询", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "出入库记录-分页列表查询", notes = "出入库记录-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<MaterialStockOutInRecordRespDTO>> pageList(MaterialStockOutInRecordReqDTO materialStockOutInRecordReqDTO) {
        IPage<MaterialStockOutInRecordRespDTO> pageList = materialStockOutInRecordService.pageList(materialStockOutInRecordReqDTO);
        return Result.ok(pageList);
    }
}
