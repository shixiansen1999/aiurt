package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartScrapService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 备件报损
 * @Author: qian
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "备件报损")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartScrap")
public class SparePartScrapController {
    @Autowired
    private ISparePartScrapService sparePartScrapService;

    /**
     * 分页列表查询
     *
     * @param sparePartScrapQuery
     * @param req
     * @return
     */
    @AutoLog(value = "备件报损-分页列表查询")
    @ApiOperation(value = "备件报损-分页列表查询", notes = "备件报损-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SparePartScrapVO>> queryPageList(
            SparePartScrapQuery sparePartScrapQuery,
            HttpServletRequest req) {
        Result<IPage<SparePartScrapVO>> result = new Result<IPage<SparePartScrapVO>>();
        Page<SparePartScrapVO> page = new Page<SparePartScrapVO>(sparePartScrapQuery.getPageNo(), sparePartScrapQuery.getPageSize());
        IPage<SparePartScrapVO> pageList = sparePartScrapService.queryPageList(page, sparePartScrapQuery);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param sparePartScrap
     * @return
     */
    @AutoLog(value = "备件报损-添加")
    @ApiOperation(value = "备件报损-添加", notes = "备件报损-添加")
    @PostMapping(value = "/add")
    public Result<SparePartScrap> add(@RequestBody SparePartScrap sparePartScrap) {
        Result<SparePartScrap> result = new Result<SparePartScrap>();
        try {
            sparePartScrapService.save(sparePartScrap);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param sparePartScrap
     * @return
     */
    @AutoLog(value = "备件报损-编辑")
    @ApiOperation(value = "备件报损-编辑", notes = "备件报损-编辑")
    @PutMapping(value = "/edit")
    public Result<SparePartScrap> edit(@RequestBody SparePartScrap sparePartScrap) {
        Result<SparePartScrap> result = new Result<SparePartScrap>();
        SparePartScrap sparePartScrapEntity = sparePartScrapService.getById(sparePartScrap.getId());
        if (sparePartScrapEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sparePartScrapService.updateById(sparePartScrap);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @AutoLog("备件报损信息-导出")
    @ApiOperation("备件报损信息导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(
            SparePartScrapQuery sparePartScrapQuery,
            HttpServletRequest request, HttpServletResponse response) {
        // 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SparePartScrapExcel> list = sparePartScrapService.exportXls(sparePartScrapQuery);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件报损信息列表");
        mv.addObject(NormalExcelConstants.CLASS, SparePartScrapExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件报损信息列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }


}
