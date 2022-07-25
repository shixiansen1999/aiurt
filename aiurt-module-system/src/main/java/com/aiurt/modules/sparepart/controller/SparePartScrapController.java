package com.aiurt.modules.sparepart.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartScrapVO;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

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
