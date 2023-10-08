package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.CodeGenerateUtils;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionListReqDTO;
import com.aiurt.modules.stock.dto.resp.StockLevel2RequisitionListRespDTO;
import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.aiurt.modules.stock.service.IStockLevel2CheckService;
import com.aiurt.modules.stock.service.StockLevel2RequisitionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Autowired
    private IStockLevel2CheckService iStockLevel2CheckService;
    /**
     * 二级库管理-分页列表查询
     *
     * @param stockLevel2RequisitionListReqDTO 二级库申领分页列表查询的请求DTO
     * @return Result<IPage<StockLevel2RequisitionListRespDTO>> 返回分页列表查询结果
     */
    @AutoLog(value = "二级库管理-二级库申领-分页列表查询")
    @ApiOperation(value="二级库管理-二级库申领-分页列表查询", notes="二级库管理-二级库申领-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockLevel2RequisitionListRespDTO>> pageList(StockLevel2RequisitionListReqDTO stockLevel2RequisitionListReqDTO){
        Page<StockLevel2RequisitionListRespDTO> pageList = stockLevel2RequisitionService.pageList(stockLevel2RequisitionListReqDTO);
        return Result.ok(pageList);
    }

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

    /**
     * 二级库管理-编辑
     *
     * @param stockLevel2RequisitionAddReqDTO 二级库申领的添加、编辑等请求DTO
     * @return Result<String> 返回编辑成功提示
     */
    @AutoLog(value = "二级库管理-二级库申领-编辑")
    @ApiOperation(value="二级库管理-二级库申领-编辑", notes="二级库管理-二级库申领-编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO){
        stockLevel2RequisitionService.edit(stockLevel2RequisitionAddReqDTO);
        return Result.ok("编辑成功！");
    }

    /**
     * 二级库管理-二级库申领-提交  保存/编辑后提交并发起流程
     * @param stockLevel2RequisitionAddReqDTO 二级库申领的添加、编辑等请求DTO
     * @return Result<String> 返回提交成功的提示
     */
    @AutoLog(value = "二级库管理-二级库申领-提交")
    @ApiOperation(value = "二级库管理-二级库申领-提交", notes = "二级库管理-二级库申领-提交")
    @PostMapping(value = "/submit")
    public Result<String> submit(@RequestBody StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO){
        stockLevel2RequisitionService.submit(stockLevel2RequisitionAddReqDTO);
        return Result.ok("提交成功");
    }

    /**
     * 生成一个code
     * @param codePrefix 编码前缀
     * @param snSize 编码顺序号数量，即生成多少位数的顺序号，不能小于1，当已经是该位数的最大值时，只能返回9999这种类似的
     * @return String 返回一个编码
     */
    @AutoLog(value = "二级库管理-生成一个code")
    @ApiOperation(value="二级库管理-二生成一个code", notes="二级库管理-生成一个code")
    @GetMapping("/getCode")
    public Result<String> getCode(String codePrefix, Integer snSize){
        String code = CodeGenerateUtils.generateSingleCode(codePrefix, snSize);
        return Result.ok(code);
    }

    /**
     * 查看该二级库是否正在进行盘点
     * @param warehouseCode 仓库编码
     */
    @AutoLog(value = "二级库管理-查看该二级库是否正在进行盘点")
    @ApiOperation(value="二级库管理-查看该二级库是否正在进行盘点", notes="二级库管理-查看该二级库是否正在进行盘点")
    @GetMapping("/getStockLevel2Check")
    public Result<String> getStockLevel2Check(String warehouseCode){
        List<StockLevel2Check> stockLevel2CheckList = iStockLevel2CheckService.list(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0)
                .eq("warehouse_code",warehouseCode).eq("status",CommonConstant.STOCK_LEVEL2_CHECK_STATUS_4));
        if(stockLevel2CheckList != null && stockLevel2CheckList.size()>0){
            return Result.error("false");
        }
        return Result.ok("true");
    }
}
