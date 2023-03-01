package com.aiurt.boot.service;

import com.aiurt.boot.dto.ExcelExportDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExportExcelService {
    /**
     * 导出Excel
     *
     * @param request
     * @param response
     * @param excelExportDTO
     * @return
     */
    void exportExcel(HttpServletRequest request, HttpServletResponse response, ExcelExportDTO excelExportDTO);
}
