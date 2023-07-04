package com.aiurt.modules.system.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.TimeUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.system.dto.SysHolidaysImportDTO;
import com.aiurt.modules.system.entity.SysHolidays;
import com.aiurt.modules.system.mapper.SysHolidaysMapper;
import com.aiurt.modules.system.service.ISysHolidaysService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
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

    @Autowired
    private ISysBaseAPI iSysBaseApi;

    @Override
    public void add(SysHolidays sysHolidays) {
        checkDate(sysHolidays, false);
        this.save(sysHolidays);
    }

    @Override
    public void edit(SysHolidays sysHolidays) {
        checkDate(sysHolidays, true);
        this.updateById(sysHolidays);
    }

    @Override
    public void checkDate(SysHolidays sysHolidays, Boolean isEdit) {
        // 获取所有节假日
        List<Date> allHolidays = iSysBaseApi.getAllHolidaysByType(null);
        HashSet<Date> dates = new HashSet<>();
        if (CollUtil.isNotEmpty(allHolidays)) {
            dates.addAll(allHolidays);
        }
        // 移除当前的日期
        if (isEdit) {
            SysHolidays byId = this.getById(sysHolidays.getId());
            List<Date> collect = DateUtil.rangeToList(byId.getStartDate(), byId.getEndDate(), DateField.DAY_OF_YEAR).stream().map(DateTime::toJdkDate).collect(Collectors.toList());
            dates.removeAll(collect);
        }
        if (sysHolidays.getStartDate().after(sysHolidays.getEndDate())) {
            throw new AiurtBootException("结束日期不能小于开始日期");
        } else {
            // 获取开始日期到结束日期之间的所有日期
            List<Date> dateList = DateUtil.rangeToList(sysHolidays.getStartDate(), sysHolidays.getEndDate(), DateField.DAY_OF_YEAR).stream().map(DateTime::toJdkDate).collect(Collectors.toList());
            int i = dates.size() + dateList.size();
            dates.addAll(dateList);
            // 判断是否已存在节假日
            if (dates.size() < i) {
                throw new AiurtBootException("该日期范围内系统中已存在节假日");
            }
        }
    }

    @Override
    public IPage<SysHolidays> queryPage(Page<SysHolidays> page, SysHolidays sysHolidays) {
        LambdaQueryWrapper<SysHolidays> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ObjectUtil.isNotEmpty(sysHolidays.getStartDate()), SysHolidays::getStartDate, sysHolidays.getStartDate())
                .le(ObjectUtil.isNotEmpty(sysHolidays.getEndDate()), SysHolidays::getEndDate, sysHolidays.getEndDate())
                .like(StrUtil.isNotEmpty(sysHolidays.getName()), SysHolidays::getName, sysHolidays.getName());
        wrapper.orderByDesc(SysHolidays::getStartDate);
        Page<SysHolidays> pageList = this.page(page, wrapper);
        if (CollUtil.isNotEmpty(pageList.getRecords())) {
            String format = "yyyy-MM-dd";
            pageList.getRecords().forEach(h -> h.setDateRange(DateUtil.format(h.getStartDate(), format) + "~" + DateUtil.format(h.getEndDate(), format)));
        }
        return pageList;
    }

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
                List<SysHolidaysImportDTO> list = ExcelImportUtil.importExcel(file.getInputStream(), SysHolidaysImportDTO.class, params);

                errorLines = check(list, errorLines);

                if (errorLines > 0) {
                    //存在错误，导出错误清单
                    return getErrorExcel(errorLines, errorMessage, list, successLines, null, type);
                }
                ArrayList<SysHolidays> targetList = new ArrayList<>();
                list.forEach(e -> {
                    SysHolidays sysHolidays = new SysHolidays();
                    BeanUtil.copyProperties(e, sysHolidays);
                    targetList.add(sysHolidays);
                });
                this.saveBatch(targetList);
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

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<SysHolidaysImportDTO> list, int successLines, String url, String type) {

        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/holidaysError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>(16);

            List<Map<String, String>> listMap = new ArrayList<>();
            for (SysHolidaysImportDTO sysHolidays : list) {
                Map<String, String> lm = new HashMap<>(5);
                lm.put("startDateStr", sysHolidays.getStartDateStr());
                lm.put("endDateStr", sysHolidays.getEndDateStr());
                lm.put("name", sysHolidays.getName());
                lm.put("typeName", sysHolidays.getTypeName());
                lm.put("mistake", sysHolidays.getMistake());
                listMap.add(lm);
            }
            errorMap.put("maplist", listMap);
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(1);
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

    private int check(List<SysHolidaysImportDTO> list, int  errorLines) {
        // 获取字典信息
        List<DictModel> holidaysType = iSysBaseApi.queryDictItemsByCode("holidays_type");
        Map<String, String> typeMap = CollUtil.isNotEmpty(holidaysType) ? holidaysType.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue)) : new HashMap<String, String>(2);
        Set<String> typeNames = typeMap.keySet();
        // 获取所有节假日
        List<Date> allHolidays = iSysBaseApi.getAllHolidaysByType(null);
        HashSet<DateTime> dateTimes = new HashSet<>();
        String format = "yyyy-MM-dd";
        for (SysHolidaysImportDTO sysHoliday : list) {
            StringBuilder error = new StringBuilder();
            String str1 = sysHoliday.getStartDateStr();
            String str2 = sysHoliday.getEndDateStr();
            if (ObjectUtil.isEmpty(str1)) {
                error.append("开始日期不能为空;");
            } else {
                boolean legalDate = TimeUtil.isLegalDate(str1.length(), str1, format);
                if (!legalDate) {
                    error.append("开始日期格式填写不对;");
                } else {
                    sysHoliday.setStartDate(DateUtil.parse(str1, format));
                }
            }
            if (ObjectUtil.isEmpty(str2)) {
                error.append("结束日期不能为空;");
            } else {
                boolean legalDate = TimeUtil.isLegalDate(str2.length(), str2, format);
                if (!legalDate) {
                    error.append("结束日期格式填写不对;");
                } else {
                    sysHoliday.setEndDate(DateUtil.parse(str2, format));
                }
            }
            boolean b = ObjectUtil.isNotEmpty(sysHoliday.getStartDate()) && ObjectUtil.isNotEmpty(sysHoliday.getEndDate());
            if (b && sysHoliday.getStartDate().after(sysHoliday.getEndDate())) {
                error.append("结束日期不能小于开始日期;");
            } else {
                // 获取开始日期到结束日期之间的所有日期
                List<DateTime> dateList = DateUtil.rangeToList(sysHoliday.getStartDate(), sysHoliday.getEndDate(), DateField.DAY_OF_YEAR);
                int size = dateTimes.size() + dateList.size();
                dateTimes.addAll(dateList);
                // 判断填写的日期是否有重复或已包含的日期
                if (dateTimes.size() < size) {
                    error.append("该日期范围内已填写有节假日，请检查冲突;");
                }
                if (CollUtil.isNotEmpty(allHolidays)) {
                    // 判断系统中是否已存在节假日
                    dateList.retainAll(allHolidays);
                    if (CollUtil.isNotEmpty(dateList)) {
                        error.append("该日期范围内系统中已存在节假日;");
                    }
                }
            }
            if (StrUtil.isBlank(sysHoliday.getName())) {
                error.append("节假日名称不能为空;");
            }
            if (StrUtil.isBlank(sysHoliday.getTypeName())) {
                error.append("类型不能为空;");
            } else {
                boolean b1 = CollUtil.isEmpty(typeNames) || (CollUtil.isNotEmpty(typeNames) && !typeNames.contains(sysHoliday.getTypeName()));
                if (b1) {
                    error.append("系统中不存在该类型;");
                } else {
                    sysHoliday.setType(Integer.valueOf(typeMap.get(sysHoliday.getTypeName())));
                }
            }
            if (ObjectUtil.isNotEmpty(error)) {
                errorLines++;
                sysHoliday.setMistake(error.toString());
            }
        }
        return errorLines;
    }
}
