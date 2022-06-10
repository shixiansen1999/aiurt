package com.aiurt.boot.common.util.excel;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ExcelUtils {
    /**
     * 解析excel到list
     * @param uploadExcel
     * @return
     * @throws IOException
     */
    public static List<List<String>> listFromExcel(MultipartFile uploadExcel)
            throws IOException {
        String fileName = uploadExcel.getOriginalFilename();
        String extensionName = fileName.substring(fileName.lastIndexOf(".") - 1);
        // 实例化工具类
        ExportExcelUtil excelUtil = new ExportExcelUtil();
        // 解析
        List<List<String>> list = excelUtil.importListFromExcel(uploadExcel.getInputStream(),extensionName, 0);
        return list;
    }
}
