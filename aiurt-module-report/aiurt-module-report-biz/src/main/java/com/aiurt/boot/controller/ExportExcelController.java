package com.aiurt.boot.controller;

import com.aiurt.boot.dto.ExcelExportDTO;
import com.aiurt.boot.service.ExportExcelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "通用导出接口")
@RestController
@RequestMapping("/export")
public class ExportExcelController {

    @Autowired
    private ExportExcelService exportExcelService;

    @ApiOperation(value = "excel导出", notes = "excel导出")
    @RequestMapping(value = "/excel", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> exportExcel(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody @Validated ExcelExportDTO excelExportDTO) {
        exportExcelService.exportExcel(request, response, excelExportDTO);
        return Result.OK("导出成功！");
    }

}
