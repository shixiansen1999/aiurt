package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.stock.dto.resp.StockLevel2RespDTO;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 二级库库存管理
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-二级库库存管理")
@RestController
@RequestMapping("/stock/stockLevel2")
public class StockLevel2Controller {

    @Autowired
    private IStockLevel2Service iStockLevel2Service;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    /**
     * 分页列表查询
     *
     * @param stockLevel2
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-二级库库存管理-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2List")
    @ApiOperation(value = "二级库管理-二级库库存管理-分页列表查询", notes = "二级库管理-二级库库存管理-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "secondLevelWarehouse/StockLevel2List")
    public Result<IPage<StockLevel2RespDTO>> queryPageList(StockLevel2 stockLevel2,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Page<StockLevel2> page = new Page<StockLevel2>(pageNo, pageSize);
        IPage<StockLevel2RespDTO> pageList = iStockLevel2Service.pageList(page,stockLevel2);
        return Result.OK(pageList);
    }



    /**
     *  编辑
     *
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-二级库库存管理-编辑")
    @ApiOperation(value="二级库管理-二级库库存管理-编辑", notes="二级库管理-二级库库存管理-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody StockLevel2 stockLevel2) {
        iStockLevel2Service.updateById(stockLevel2);
        return Result.OK("编辑成功!");
    }
    /**
     * 二级库库存管理详情查询
     * @param id
     * @return
     */
    @AutoLog(value = "二级库管理-二级库库存管理-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2List")
    @ApiOperation(value = "二级库管理-二级库库存管理-详情查询", notes = "二级库管理-二级库库存管理-详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockLevel2RespDTO> queryById(@RequestParam(name = "id", required = true) String id) {
        // StockLevel2 stockLevel2 = iStockLevel2Service.getDetailById(id);
        StockLevel2RespDTO stockLevel2RespDTO = iStockLevel2Service.queryDetailById(id);
        return Result.ok(stockLevel2RespDTO);
    }

    @AutoLog(value = "二级库管理-二级库库存管理-导出", operateType = 6, operateTypeAlias = "导出", permissionUrl = "/secondLevelWarehouse/StockLevel2List")
    @ApiOperation(value = "二级库管理-二级库库存管理-导出", notes = "二级库管理-二级库库存管理-导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(StockLevel2 stockLevel2,
                                  @RequestParam(name = "ids",required =  false) String ids,
                                  HttpServletRequest request) {
        List<StockLevel2> exportList = iStockLevel2Service.exportXls(stockLevel2,ids);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "二级库库存管理");
        mv.addObject(NormalExcelConstants.CLASS, StockLevel2.class);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("二级库库存管理" + "报表", "导出人:" + sysUser.getRealname(), "二级库库存管理");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

}
