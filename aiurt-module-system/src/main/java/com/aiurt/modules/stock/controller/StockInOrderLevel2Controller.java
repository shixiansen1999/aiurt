package com.aiurt.modules.stock.controller;

import com.aiurt.boot.standard.dto.InspectionCodeExcelDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.dto.StockInOrderLevel2ExportDTO;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
import com.aiurt.modules.stock.service.IStockInOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockLevel2CheckService;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 二级库入库管理
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-二级库入库管理")
@RestController
@RequestMapping("/stock/stockInOrderLevel2")
public class StockInOrderLevel2Controller {

    @Autowired
    private IStockInOrderLevel2Service iStockInOrderLevel2Service;
    @Autowired
    private IStockLevel2CheckService iStockLevel2CheckService;

    /**
     * 分页列表查询
     *
     * @param stockInOrderLevel2
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-二级库入库管理-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "二级库管理-二级库入库管理-分页列表查询", notes = "二级库管理-二级库入库管理-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "secondLevelWarehouse/StockInOrderLevel2List")
    public Result<IPage<StockInOrderLevel2>> queryPageList(StockInOrderLevel2 stockInOrderLevel2,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockInOrderLevel2>> result = new Result<IPage<StockInOrderLevel2>>();
        Page<StockInOrderLevel2> page = new Page<StockInOrderLevel2>(pageNo, pageSize);
        IPage<StockInOrderLevel2> pageList = iStockInOrderLevel2Service.pageList(page, stockInOrderLevel2);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "二级库管理-二级库入库管理-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "二级库管理-二级库入库管理-添加", notes = "二级库管理-二级库入库管理-添加")
    @PostMapping(value = "/add")
    public Result<StockInOrderLevel2> add(@RequestBody StockInOrderLevel2 stockInOrderLevel2) {
        Result<StockInOrderLevel2> result = new Result<StockInOrderLevel2>();
        try {
            iStockInOrderLevel2Service.add(stockInOrderLevel2);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 提交
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-二级库入库管理-提交", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "提交", notes = "提交")
    @GetMapping(value = "/submitInOrderStatus")
    public Result<String> submitInOrderStatus(@RequestParam(name = "status", required = true) String status,
                                     @RequestParam(name = "code", required = true) String code) throws ParseException {
        StockInOrderLevel2 stockInOrderLevel2 = iStockInOrderLevel2Service.getOne(new QueryWrapper<StockInOrderLevel2>().eq("order_code",code));
        String warehouseCode = stockInOrderLevel2.getWarehouseCode();
        List<StockLevel2Check> stockLevel2CheckList = iStockLevel2CheckService.list(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0)
                .eq("warehouse_code",warehouseCode).eq("status",CommonConstant.STOCK_LEVEL2_CHECK_STATUS_4));
        if(stockLevel2CheckList != null && stockLevel2CheckList.size()>0){
            return Result.error("盘点任务执行期间，物资暂时无法进行出入库操作");
        }
        boolean ok = iStockInOrderLevel2Service.submitInOrderStatus(status, code, stockInOrderLevel2);
        if (ok) {
            return Result.ok("操作成功！");
        }else{
            return Result.ok("操作失败！");
        }
    }

    /**
     * 新增获取入库编号
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-二级库入库管理-获取入库单号", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "新增获取入库编号", notes = "新增获取入库编号")
    @GetMapping(value = "/getInOrderCode")
    public Result<StockInOrderLevel2> getInOrderCode() throws ParseException {
        return Result.ok(iStockInOrderLevel2Service.getInOrderCode());
    }

    /**
     * 二级库入库管理详情查询
     * @param id
     * @return
     */
    @AutoLog(value = "二级库管理-二级库入库管理-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "二级库入库管理详情查询", notes = "二级库入库管理详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockInOrderLevel2> queryById(@RequestParam(name = "id", required = true) String id) {
        StockInOrderLevel2 stockInOrderLevel2 = iStockInOrderLevel2Service.getById(id);
        return Result.ok(stockInOrderLevel2);
    }

    @AutoLog(value = "二级库管理-二级库入库管理-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "二级库管理-二级库入库管理-编辑", notes = "二级库管理-二级库入库管理-编辑")
    @PostMapping(value = "/edit")
    public Result<StockInOrderLevel2> edit(@RequestBody StockInOrderLevel2 stockInOrderLevel2) {
        Result<StockInOrderLevel2> result = new Result<StockInOrderLevel2>();
        StockInOrderLevel2 stockInOrderLevel2Entity = iStockInOrderLevel2Service.getById(stockInOrderLevel2.getId());
        if (stockInOrderLevel2Entity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = iStockInOrderLevel2Service.edit(stockInOrderLevel2);
            try{
            }catch (Exception e){
                throw new AiurtBootException(e.getMessage());
            }
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    @AutoLog(value = "二级库管理-二级库入库管理-通过id删除", operateType = 3, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "二级库管理-二级库入库管理-通过id删除", notes = "二级库管理-二级库入库管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            StockInOrderLevel2 stockInOrderLevel2 = iStockInOrderLevel2Service.getById(id);
            iStockInOrderLevel2Service.removeById(stockInOrderLevel2);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "二级库入库管理分类-批量删除", operateType = 3, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "二级库入库管理分类-批量删除", notes = "二级库入库管理分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            iStockInOrderLevel2Service.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }


    @AutoLog(value = "二级库管理-二级库入库管理-导出excel",  operateType =  4, operateTypeAlias = "导出excel",  permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value="二级库管理-二级库入库管理-导出excel", notes="二级库管理-二级库入库管理-导出excel")
    @RequestMapping(value = "/export",method = RequestMethod.GET)
    public void exportXls(HttpServletRequest request, HttpServletResponse response, StockInOrderLevel2ExportDTO stockInOrderLevel2ExportDTO) {
        iStockInOrderLevel2Service.exportXls(request,response,stockInOrderLevel2ExportDTO);
    }

    /**
     * 下载导入模板
     *
     */
    @AutoLog(value = "二级库管理-二级库入库管理-导入模板下载", operateType =  6, operateTypeAlias = "二级库管理-二级库入库管理-导入模板下载", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value="二级库管理-二级库入库管理-导入模板下载", notes="二级库管理-二级库入库管理-导入模板下载")
    @RequestMapping(value = "/exportTemplateXls")
    public void  exportTemplateXl(HttpServletResponse response, HttpServletRequest request) throws IOException {
        iStockInOrderLevel2Service.exportTemplateXls(response);
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "二级库管理-二级库入库管理-通过excel导入数据", operateType =  5, operateTypeAlias = "二级库管理-二级库入库管理-通过excel导入数据", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value="二级库管理-二级库入库管理-通过excel导入数据", notes="二级库管理-二级库入库管理-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return iStockInOrderLevel2Service.importExcel(request,response);
    }
}
