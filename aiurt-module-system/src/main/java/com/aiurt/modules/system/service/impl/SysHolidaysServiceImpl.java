package com.aiurt.modules.system.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.TimeUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.system.entity.SysHolidays;
import com.aiurt.modules.system.mapper.SysHolidaysMapper;
import com.aiurt.modules.system.service.ISysHolidaysService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: sys_holidays
 * @Author: aiurt
 * @Date:   2023-03-16
 * @Version: V1.0
 */
@Service
public class SysHolidaysServiceImpl extends ServiceImpl<SysHolidaysMapper, SysHolidays> implements ISysHolidaysService {
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<SysHolidays> sysHolidaysClass) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        // 错误信息
        int  errorLines = 0;

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysHolidays> list = ExcelImportUtil.importExcel(file.getInputStream(), SysHolidays.class, params);

                errorLines = check(list, errorLines);

                if (errorLines > 0) {
                    //存在错误，导出错误清单
                    return getErrorExcel(errorLines, errorMessage, list, successLines, null, type);
                }
                this.saveBatch(list);
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                String msg = e.getMessage();
                log.error(msg, e);
                if(msg!=null && msg.indexOf("Duplicate entry")>=0){
                    return Result.error("文件导入失败:有重复数据！");
                }else{
                    return Result.error("文件导入失败:" + e.getMessage());
                }
                //update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<SysHolidays> list, int successLines, String url, String type) {

        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/holidaysError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>();

            List<Map<String, String>> listMap = new ArrayList<>();
            for (SysHolidays sysHolidays : list) {
                Map<String, String> lm = new HashMap<>(3);
                lm.put("date", sysHolidays.getDate());
                lm.put("name", sysHolidays.getName());
                lm.put("mistake", sysHolidays.getMistake());
                listMap.add(lm);
            }
            errorMap.put("maplist", listMap);
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

            String fileName = "节假日表导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
            url = File.separator+"errorExcelFiles"+ File.separator+fileName;
            workbook.write(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage,true,url);
    }

    private int check(List<SysHolidays> list, int  errorLines ) {
        LambdaQueryWrapper<SysHolidays> wrapper = new LambdaQueryWrapper<>();
        List<SysHolidays> holidays = this.list(wrapper);
        for (SysHolidays sysHoliday : list) {
            String date = sysHoliday.getDate();
            if (StrUtil.isEmpty(date)) {
                sysHoliday.setMistake("日期不能为空");
                errorLines++;
            }else {
                boolean legalDate = TimeUtil.isLegalDate(date.length(), date, "yyyy-MM-dd");
                if (!legalDate) {
                    sysHoliday.setMistake("日期格式不对");
                    errorLines++;
                } else {
                    List<SysHolidays> collect = holidays.stream().filter(h -> h.getDate().equals(date)).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(collect)) {
                        sysHoliday.setMistake("已存在该日期");
                        errorLines++;
                    }
                }
            }
        }
        return errorLines;
    }
}
