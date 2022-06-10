package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.Stock2CheckVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2CheckDetailService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2CheckService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 二级库盘点列表
 * @Author: qian
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库盘点列表")
@RestController
@RequestMapping("/secondLevelWarehouse/stockLevel2Check")
public class StockLevel2CheckController {
    @Autowired
    private IStockLevel2CheckService stockLevel2CheckService;
    @Resource
    private IStockLevel2CheckDetailService iStockLevel2CheckDetailService;
    /**
     * 分页列表查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库盘点列表-分页列表查询")
    @ApiOperation(value = "二级库盘点列表-分页列表查询", notes = "二级库盘点列表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Stock2CheckVO>> queryPageList(
            StockLevel2CheckDTO stockLevel2CheckDTO,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        Result<IPage<Stock2CheckVO>> result = new Result<IPage<Stock2CheckVO>>();
        IPage<Stock2CheckVO> page = new Page<>(pageNo, pageSize);
        IPage<Stock2CheckVO> pageList = stockLevel2CheckService.queryPageList(page,stockLevel2CheckDTO);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 二级库盘点列表-添加
     *
     * @return
     */
    @AutoLog(value = "二级库盘点列表-添加")
    @ApiOperation(value = "二级库盘点列表-添加", notes = "二级库盘点列表-添加")
    @PostMapping(value = "/add")
    public Result<StockLevel2Check> add(@RequestBody StockLevel2Check stockLevel2Check,
                                           HttpServletRequest req) {
        Result<StockLevel2Check> result = new Result<>();
        try {
            stockLevel2CheckService.addCheck(stockLevel2Check,req);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "二级库盘点列表-通过id删除")
    @ApiOperation(value = "二级库盘点列表-通过id删除", notes = "二级库盘点列表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            stockLevel2CheckService.removeById(id);
            StockLevel2Check byId = stockLevel2CheckService.getById(id);
            if(ObjectUtil.isNotEmpty(byId)){
                iStockLevel2CheckDetailService.remove(new QueryWrapper<StockLevel2CheckDetail>()
                        .eq("stock_check_code",byId.getStockCheckCode()));
            }
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }


    /**
     * 导出excel
     * @param stockLevel2CheckDTO
     * @return
     */
    @AutoLog("二级库盘点-导出")
    @ApiOperation("二级库盘点导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(StockLevel2CheckDTO stockLevel2CheckDTO) {
        // 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<StockLevel2CheckExcel> list = stockLevel2CheckService.exportXls(stockLevel2CheckDTO);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "二级库盘点列表列表");
        mv.addObject(NormalExcelConstants.CLASS, StockLevel2CheckExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("二级库盘点列表列表数据","导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

}
