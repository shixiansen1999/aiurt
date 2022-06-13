package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import cn.hutool.core.collection.CollUtil;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2Detail;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2DTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2Excel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInOrderLevel2VO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockInOrderLevel2DetailService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockInOrderLevel2Service;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库入库单信息")
@RestController
@RequestMapping("/secondLevelWarehouse/stockInOrderLevel2")
public class StockInOrderLevel2Controller {
    @Autowired
    private IStockInOrderLevel2Service stockInOrderLevel2Service;

    @Autowired
    private IStockInOrderLevel2DetailService iStockInOrderLevel2DetailService;

    /**
     * 分页列表查询
     *
     * @param stockInOrderLevel2
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级入库单信息-分页列表查询")
    @ApiOperation(value = "二级入库单信息-分页列表查询", notes = "二级入库单信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockInOrderLevel2VO>> queryPageList(StockInOrderLevel2 stockInOrderLevel2,
                                                           @RequestParam(name = "startTime", required = false) String startTime,
                                                           @RequestParam(name = "endTime", required = false) String endTime,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        Result<IPage<StockInOrderLevel2VO>> result = new Result<>();
        Page<StockInOrderLevel2VO> page = new Page<>(pageNo, pageSize);
        IPage<StockInOrderLevel2VO> queryPageList =
                stockInOrderLevel2Service.queryPageList(page, stockInOrderLevel2, startTime, endTime);
        result.setSuccess(true);
        result.setResult(queryPageList);
        return result;
    }
    /**
     * 添加入库单
     *
     * @param stockInOrderLevel2DTO
     * @return
     */
    @AutoLog(value = "添加入库单-添加")
    @ApiOperation(value = "添加入库单-添加", notes = "新增入库单-添加")
    @PostMapping(value = "/add")
    public Result<StockInOrderLevel2> add(
            @Valid @RequestBody StockInOrderLevel2DTO stockInOrderLevel2DTO, HttpServletRequest req) {
        Result<StockInOrderLevel2> result = new Result<StockInOrderLevel2>();
            try {
                String s = stockInOrderLevel2Service.addWarehouseIn(stockInOrderLevel2DTO, req);
                result.success("添加成功！");
                result.setMessage(s);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.error500("操作失败"+e.getMessage());
            }

        return result;
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "二级入库单信息-通过id删除")
    @ApiOperation(value = "二级入库单信息-通过id删除", notes = "二级入库单信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            stockInOrderLevel2Service.removeById(id);
            iStockInOrderLevel2DetailService.remove(
                    new QueryWrapper<StockInOrderLevel2Detail>().eq("order_id", id));
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }


    /**
     * 导出excel
     * @param selections
     * @return
     */
    @ApiOperation("入库列表导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(
            @ApiParam(value = "行数据ids" ,required = true) @RequestParam("selections") List<Integer> selections) {
        if(CollUtil.isEmpty(selections)){
            throw new AiurtBootException("行数据ids不能为空");
        }
        // 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<StockInOrderLevel2Excel> list = stockInOrderLevel2Service.selectExcelData(selections);

        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "二级入库单信息列表");
        mv.addObject(NormalExcelConstants.CLASS, StockInOrderLevel2Excel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("二级入库单信息列表数据","导出信息",ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<StockInOrderLevel2> listStockInOrderLevel2s = ExcelImportUtil.importExcel(file.getInputStream(), StockInOrderLevel2.class, params);
                stockInOrderLevel2Service.saveBatch(listStockInOrderLevel2s);
                return Result.ok("文件导入成功！数据行数:" + listStockInOrderLevel2s.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

}
