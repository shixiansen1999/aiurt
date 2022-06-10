package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStockVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 备件仓库信息
 * @Author: qian
 * @Date: 2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "备件仓库信息")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartStockInfo")
public class SparePartStockInfoController {
    @Resource
    private ISparePartStockInfoService iSparePartStockInfoService;

    @AutoLog(value = "备件仓库信息-列表查询")
    @ApiOperation(value = "备件仓库信息-列表查询", notes = "备件仓库信息-列表查询")
    @GetMapping(value = "/list")
    public Result<List<SparePartStockVO>> queryList() {
        Result<List<SparePartStockVO>> result = new Result<List<SparePartStockVO>>();
        List<SparePartStockVO> sparePartStockInfos = iSparePartStockInfoService.queryList();
        result.setSuccess(true);
        result.setResult(sparePartStockInfos);
        return result;
    }


}
