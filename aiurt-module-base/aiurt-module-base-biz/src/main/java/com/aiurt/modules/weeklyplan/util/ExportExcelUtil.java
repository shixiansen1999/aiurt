package com.aiurt.modules.weeklyplan.util;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.weeklyplan.dto.BdOperatePlanDeclarationFormReturnTypeDTO;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * @author Lai W.
 *
 * @version 1.0
 */
public class ExportExcelUtil {

    private static HSSFWorkbook workBook = null;
    private static final String[] SPARE_REPORT_OPERATE_PLAN = new String[]{"id","nature","type","departmentName","taskTime","taskRange",
            "powerSupplyRequirement","taskContent","protectiveMeasure","chargeStaffName","coordinationDepartmentId","firstStationName",
            "secondStationName","assistStationName","taskStaffNum","largeAppliances"};
    private static final String[] SPARE_REPORT_OPERATE_PLAN2 = new String[]{"id","nature","type","departmentName","taskTime","taskRange",
            "powerSupplyRequirement","taskContent","protectiveMeasure","chargeStaffName","coordinationDepartmentId","firstStationName",
            "secondStationName","assistStationName","taskStaffNum","largeAppliances"};
    private static final Hashtable<String, String> WEEk_DAY_HASH = initWeekHash();

    private static Hashtable<String, String> initWeekHash() {
        Hashtable<String, String> result = new Hashtable<>();
        result.put("1", "一");
        result.put("2", "二");
        result.put("3", "三");
        result.put("4", "四");
        result.put("5", "五");
        result.put("6", "六");
        result.put("0", "日");
        return result;
    }


    public static void writeToExcel(List<BdOperatePlanDeclarationFormReturnTypeDTO> data, String sheetName,
                                    String[] titleRow, String fileName, HttpServletResponse response) throws Exception {
        //创建Workbook

        workBook = new HSSFWorkbook();
        workBook.createSheet("sheetName");

        HSSFFont fontTitle = workBook.createFont();
        fontTitle.setItalic(false);
        fontTitle.setStrikeout(false);
        fontTitle.setBold(false);
        fontTitle.setFontHeightInPoints((short)24);
        fontTitle.setFontName("黑体");

        HSSFFont fontTime = workBook.createFont();
        fontTime.setItalic(false);
        fontTime.setStrikeout(false);
        fontTime.setBold(false);
        fontTime.setFontHeightInPoints((short)16);
        fontTime.setFontName("黑体");

        HSSFFont fontWeek=workBook.createFont();
        fontWeek.setItalic(false);
        fontWeek.setStrikeout(false);
        fontWeek.setBold(true);
        fontWeek.setFontName("黑体");

        HSSFFont fontData=workBook.createFont();
        fontData.setItalic(false);
        fontData.setStrikeout(false);
        fontData.setBold(false);
        fontData.setFontName("宋体");

        //设定标题，日期，数据的基本格式与样式
        HSSFCellStyle styleTitle = workBook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setBorderBottom(BorderStyle.THIN);
        styleTitle.setBorderLeft(BorderStyle.THIN);
        styleTitle.setBorderRight(BorderStyle.THIN);
        styleTitle.setBorderTop(BorderStyle.THIN);
        styleTitle.setFont(fontTitle);

        HSSFCellStyle styleTime = workBook.createCellStyle();
        styleTime.setAlignment(HorizontalAlignment.CENTER);
        styleTime.setBorderBottom(BorderStyle.THIN);
        styleTime.setBorderLeft(BorderStyle.THIN);
        styleTime.setBorderRight(BorderStyle.THIN);
        styleTime.setBorderTop(BorderStyle.THIN);
        styleTime.setFont(fontTime);

        HSSFCellStyle styleWeek = workBook.createCellStyle();
        styleWeek.setAlignment(HorizontalAlignment.CENTER);
        styleWeek.setVerticalAlignment(VerticalAlignment.CENTER);
        styleWeek.setBorderBottom(BorderStyle.THIN);
        styleWeek.setBorderLeft(BorderStyle.THIN);
        styleWeek.setBorderRight(BorderStyle.THIN);
        styleWeek.setBorderTop(BorderStyle.THIN);
        styleWeek.setWrapText(true);
        styleWeek.setFont(fontWeek);

        HSSFCellStyle styleData = workBook.createCellStyle();
        styleData.setAlignment(HorizontalAlignment.CENTER);
        styleData.setVerticalAlignment(VerticalAlignment.CENTER);
        styleData.setBorderBottom(BorderStyle.THIN);
        styleData.setBorderLeft(BorderStyle.THIN);
        styleData.setBorderRight(BorderStyle.THIN);
        styleData.setBorderTop(BorderStyle.THIN);
        styleData.setWrapText(true);
        styleData.setFont(fontData);

        HSSFSheet sheet = workBook.getSheet("sheetName");

        //获取对应key
        String[] keys = new String[SPARE_REPORT_OPERATE_PLAN.length];
        System.arraycopy(SPARE_REPORT_OPERATE_PLAN, 0, keys, 0, SPARE_REPORT_OPERATE_PLAN.length);

        // 获取表头的列数
        int rowId = 0;
        int dataRow = 0;
        int weekDay = -1;
        int orderNum = 1;

        try {
            //创建第一行
            HSSFRow firstRow = sheet.createRow(0);
            HSSFCell cell = firstRow.createCell(0);
            cell.setCellValue(sheetName);
            cell.setCellStyle(styleTitle);
            //遍历表头
            for (short columnIndex = 1; columnIndex < SPARE_REPORT_OPERATE_PLAN.length; columnIndex++) {
                cell = firstRow.createCell(columnIndex);
                cell.setCellValue("");
                cell.setCellStyle(styleTitle);
            }
            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, SPARE_REPORT_OPERATE_PLAN.length-1);
            sheet.addMergedRegion(cra);
            Set<String> set = new HashSet<>();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            //垂直居中
            for(;dataRow<data.size();rowId++){
                if(!set.contains(data.get(dataRow).getWeekday()+simpleDateFormat.format(data.get(dataRow).getTaskDate()))){
                    set.add(data.get(dataRow).getWeekday()+simpleDateFormat.format(data.get(dataRow).getTaskDate()));
                    String weekValue = "";
                    if (data.get(dataRow).getWeekday() != null) {
                        weekDay = data.get(dataRow).getWeekday();

                        String date = simpleDateFormat.format(data.get(dataRow).getTaskDate());
                        String[] dateList = date.split("-");
                        weekValue = data.get(dataRow).getWeekday().toString();
                        date = dateList[0] + "年" + dateList[1] + "月" + dateList[2] + "日";
                        weekValue = date + " (星期" + WEEk_DAY_HASH.get(weekValue) + ")";
                    }
                    orderNum = 1;
                    HSSFRow newRow=sheet.createRow(rowId + 1);
                    cell = newRow.createCell(0);

                    cell.setCellValue(weekValue);
                    cell.setCellStyle(styleTime);
                    //遍历表头
                    for (short columnIndex = 1; columnIndex < SPARE_REPORT_OPERATE_PLAN.length; columnIndex++) {
                        cell = newRow.createCell(columnIndex);
//                            cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                        cell.setCellValue("");
                        cell.setCellStyle(styleWeek);
                    }
                    CellRangeAddress craRow = new CellRangeAddress(rowId+1, rowId+1, 0, SPARE_REPORT_OPERATE_PLAN.length-1);
                    sheet.addMergedRegion(craRow);
                    rowId++;
                    newRow = sheet.createRow(rowId+1);
                    //遍历表头
                    for (short columnIndex = 1; columnIndex < titleRow.length; columnIndex++) {
                        cell = newRow.createCell(columnIndex);
//                            cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                        cell.setCellValue(titleRow[columnIndex]);
                        cell.setCellStyle(styleWeek);
                    }
                }else {
                    BdOperatePlanDeclarationFormReturnTypeDTO returnTypeDTO = data.get(dataRow);
                    HSSFRow newRow=sheet.createRow(rowId+1);
                    cell = newRow.createCell(0);
                    cell.setCellValue(orderNum);
                    cell.setCellStyle(styleData);
                    //遍历表头
                    for (short columnIndex = 1; columnIndex < SPARE_REPORT_OPERATE_PLAN.length; columnIndex++) {
                        cell = newRow.createCell(columnIndex);
                        //写入值
                        System.out.println("-------------------------------" + getByString(returnTypeDTO, keys[columnIndex]));
//                            cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                        cell.setCellValue(getByString(returnTypeDTO, keys[columnIndex]) == null ? "" : getByString(returnTypeDTO, keys[columnIndex]));
                        cell.setCellStyle(styleData);
                    }
                    orderNum++;
                    dataRow++;
                }
            }
            //设置列宽
            sheet.setColumnWidth(0, 256*6);
            sheet.setColumnWidth(1, 256*12);
            sheet.setColumnWidth(2, 256*12);
            sheet.setColumnWidth(3, 256*20);
            sheet.setColumnWidth(4, 256*20);
            sheet.setColumnWidth(5, 256*60);
            sheet.setColumnWidth(6, 256*20);
            sheet.setColumnWidth(7, 256*20);
            sheet.setColumnWidth(8, 256*20);
            sheet.setColumnWidth(9, 256*20);
            sheet.setColumnWidth(10, 256*16);
            sheet.setColumnWidth(11, 256*16);
            sheet.setColumnWidth(12, 256*16);
            sheet.setColumnWidth(13, 256*10);
            sheet.setColumnWidth(14, 256*16);
        } catch (Exception e) {
            throw e;
        } finally {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();
            workBook.write(response.getOutputStream());
            workBook.close();
        }
    }

    public static void writeToExcelChangeable(List<BdOperatePlanDeclarationFormReturnTypeDTO> data,
                                              String sheetName, String[] titleRow, String fileName,
                                              HttpServletResponse response) throws Exception {

        workBook = new HSSFWorkbook();
        workBook.createSheet(sheetName);

        HSSFFont fontTitle = workBook.createFont();
        fontTitle.setItalic(false);
        fontTitle.setStrikeout(false);
        fontTitle.setBold(false);
        fontTitle.setFontHeightInPoints((short)24);
        fontTitle.setFontName("黑体");

        HSSFFont fontWeek=workBook.createFont();
        fontWeek.setItalic(false);
        fontWeek.setStrikeout(false);
        fontWeek.setBold(true);
        fontWeek.setFontName("黑体");

        HSSFFont fontData=workBook.createFont();
        fontData.setItalic(false);
        fontData.setStrikeout(false);
        fontData.setBold(false);
        fontData.setFontName("宋体");

        //设定标题，日期，数据的基本格式与样式
        HSSFCellStyle styleTitle = workBook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setBorderBottom(BorderStyle.THIN);
        styleTitle.setBorderLeft(BorderStyle.THIN);
        styleTitle.setBorderRight(BorderStyle.THIN);
        styleTitle.setBorderTop(BorderStyle.THIN);
        styleTitle.setFont(fontTitle);

        HSSFCellStyle styleWeek = workBook.createCellStyle();
        styleWeek.setAlignment(HorizontalAlignment.CENTER);
        styleWeek.setBorderBottom(BorderStyle.THIN);
        styleWeek.setBorderLeft(BorderStyle.THIN);
        styleWeek.setBorderRight(BorderStyle.THIN);
        styleWeek.setBorderTop(BorderStyle.THIN);
        styleWeek.setFont(fontWeek);

        HSSFCellStyle styleData = workBook.createCellStyle();
        styleData.setAlignment(HorizontalAlignment.CENTER);
        styleData.setBorderBottom(BorderStyle.THIN);
        styleData.setBorderLeft(BorderStyle.THIN);
        styleData.setBorderRight(BorderStyle.THIN);
        styleData.setBorderTop(BorderStyle.THIN);
        styleData.setFont(fontData);

        HSSFCellStyle styleApproval = workBook.createCellStyle();
        styleApproval.setAlignment(HorizontalAlignment.LEFT);
        styleApproval.setBorderBottom(BorderStyle.THIN);
        styleApproval.setBorderLeft(BorderStyle.THIN);
        styleApproval.setBorderRight(BorderStyle.THIN);
        styleApproval.setBorderTop(BorderStyle.THIN);
        styleApproval.setFont(fontWeek);

        HSSFSheet sheet = workBook.getSheet(sheetName);

        //获取对应key
        String[] keys = new String[SPARE_REPORT_OPERATE_PLAN2.length];
        System.arraycopy(SPARE_REPORT_OPERATE_PLAN2, 0, keys, 0, SPARE_REPORT_OPERATE_PLAN2.length);

        int rowId = 0;
        int dataRow = 0;
        int weekDay = -1;
        int orderNum = 1;

        try {
            //创建第一行
            HSSFRow firstRow = sheet.createRow(0);
            HSSFCell cell = firstRow.createCell(0);
            cell.setCellValue(sheetName);
            cell.setCellStyle(styleTitle);
            //遍历表头
            for (short columnIndex = 1; columnIndex < SPARE_REPORT_OPERATE_PLAN2.length+1; columnIndex++) {
                cell = firstRow.createCell(columnIndex);
                cell.setCellValue("");
                cell.setCellStyle(styleTitle);
            }
            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, SPARE_REPORT_OPERATE_PLAN2.length);
            sheet.addMergedRegion(cra);
            //垂直居中
            for(;dataRow<data.size();rowId++){
                if(weekDay != data.get(dataRow).getWeekday()) {
                    String weekValue = "";
                    if (data.get(dataRow).getWeekday() != null) {
                        weekDay = data.get(dataRow).getWeekday();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        String date = simpleDateFormat.format(data.get(dataRow).getTaskDate());
                        String[] dateList = date.split("-");
                        weekValue = data.get(dataRow).getWeekday().toString();
                        date = dateList[0] + "年" + dateList[1] + "月" + dateList[2] + "日";
                        weekValue = date + " (星期" + WEEk_DAY_HASH.get(weekValue) + ")";
                    }
                    orderNum = 1;
                    HSSFRow newRow=sheet.createRow(rowId+1);
                    cell = newRow.createCell(0);
                    cell.setCellValue(weekValue);
                    cell.setCellStyle(styleWeek);
                    //这个是为了让右边框变黑
                    //遍历表头
                    for (short columnIndex = 1; columnIndex < SPARE_REPORT_OPERATE_PLAN2.length+1; columnIndex++) {
                        cell = newRow.createCell(columnIndex);
                        cell.setCellValue("");
                        cell.setCellStyle(styleWeek);
                    }
                    CellRangeAddress craRow = new CellRangeAddress(rowId+1, rowId+1, 0, SPARE_REPORT_OPERATE_PLAN2.length);
                    sheet.addMergedRegion(craRow);
                    rowId++;
                    newRow = sheet.createRow(rowId+1);
                    //遍历表头
                    for (short columnIndex = 0; columnIndex < titleRow.length; columnIndex++) {
                        if(columnIndex!=0) {
                            cell = newRow.createCell(columnIndex+1);
                            cell.setCellValue(titleRow[columnIndex]);
                            cell.setCellStyle(styleWeek);
                        }else {
                            //添加一个空白格子
                            cell = newRow.createCell(columnIndex);
                            cell.setCellValue(titleRow[columnIndex]);
                            cell.setCellStyle(styleWeek);
                            cell = newRow.createCell(columnIndex+1);
                            cell.setCellValue("变更对比");
                            cell.setCellStyle(styleWeek);
                        }
                    }
                } else {
                    BdOperatePlanDeclarationFormReturnTypeDTO returnTypeDTO = data.get(dataRow);
                    //新增施工计划变更前添加一个空行
                    if(returnTypeDTO.getPlanChange() == 1||returnTypeDTO.getPlanChange() == 2) {
                        HSSFRow newRow=sheet.createRow(rowId+1);
                        cell = newRow.createCell(0);
                        cell.setCellValue(orderNum);
                        cell.setCellStyle(styleData);
                        cell = newRow.createCell(1);
                        cell.setCellValue("变更前");
                        cell.setCellStyle(styleData);
                        //遍历表头
                        for (short columnIndex = 2; columnIndex < SPARE_REPORT_OPERATE_PLAN2.length+1; columnIndex++) {
                            cell = newRow.createCell(columnIndex);
                            cell.setCellValue(" ");
                            cell.setCellStyle(styleData);
                        }
                        rowId++;
                        newRow=sheet.createRow(rowId+1);
                        cell = newRow.createCell(1);
                        cell.setCellValue("变更后");
                        cell.setCellStyle(styleData);
                        orderNum++;
                        CellRangeAddress craRow = new CellRangeAddress(rowId, rowId+1, 0, 0);
                        sheet.addMergedRegion(craRow);
                        //遍历表头
                        for (short columnIndex = 2; columnIndex < SPARE_REPORT_OPERATE_PLAN2.length+1; columnIndex++) {
                            cell = newRow.createCell(columnIndex);
                            cell.setCellValue(getByString(returnTypeDTO, keys[columnIndex-1]) == null ? "" :
                                    getByString(returnTypeDTO, keys[columnIndex-1]));
                            cell.setCellStyle(styleData);
                        }
                        //取消施工计划变更后添加一个空行
                    }else if(returnTypeDTO.getPlanChange() == 4) {
                        HSSFRow newRow=sheet.createRow(rowId+1);
                        cell = newRow.createCell(0);
                        cell.setCellValue(orderNum);
                        cell.setCellStyle(styleData);
                        cell = newRow.createCell(1);
                        cell.setCellValue("变更前");
                        cell.setCellStyle(styleData);
                        //遍历表头
                        for (short columnIndex = 2; columnIndex < SPARE_REPORT_OPERATE_PLAN2.length+1; columnIndex++) {
                            cell = newRow.createCell(columnIndex);
                            cell.setCellValue(getByString(returnTypeDTO, keys[columnIndex-1]) == null ? "" :
                                    getByString(returnTypeDTO, keys[columnIndex-1]));
                            cell.setCellStyle(styleData);
                        }
                        rowId++;
                        newRow=sheet.createRow(rowId+1);
                        cell = newRow.createCell(1);
                        cell.setCellValue("变更后");
                        cell.setCellStyle(styleData);
                        //遍历表头
                        for (short columnIndex = 2; columnIndex < SPARE_REPORT_OPERATE_PLAN2.length+1; columnIndex++) {
                            cell = newRow.createCell(columnIndex);
                            cell.setCellValue(" ");
                            cell.setCellStyle(styleData);
                        }
                        orderNum++;
                        CellRangeAddress craRow = new CellRangeAddress(rowId, rowId+1, 0, 0);
                        sheet.addMergedRegion(craRow);
                    }else {
                        HSSFRow newRow=sheet.createRow(rowId+1);
                        cell = newRow.createCell(0);
                        cell.setCellValue(orderNum);
                        cell.setCellStyle(styleData);
                        if(returnTypeDTO.getChangeCorrelation() == null || returnTypeDTO.getChangeCorrelation() == 0) {
                            cell = newRow.createCell(1);
                            cell.setCellValue("变更前");
                            cell.setCellStyle(styleData);
                        }else {
                            cell = newRow.createCell(1);
                            cell.setCellValue("变更后");
                            cell.setCellStyle(styleData);
                            orderNum++;
                            CellRangeAddress craRow = new CellRangeAddress(rowId, rowId+1, 0, 0);
                            sheet.addMergedRegion(craRow);
                        }
                        //遍历表头
                        for (short columnIndex = 2; columnIndex < SPARE_REPORT_OPERATE_PLAN2.length+1; columnIndex++) {
                            cell = newRow.createCell(columnIndex);
                            cell.setCellValue(getByString(returnTypeDTO, keys[columnIndex-1]) == null ? "" :
                                    getByString(returnTypeDTO, keys[columnIndex-1]));
                            cell.setCellStyle(styleData);
                        }
                    }
                    dataRow++;
                }
            }
            //底边签字项目（倒数第二行）
            // 创建第一行
            HSSFRow lastRow = sheet.createRow(rowId+1);
            cell = lastRow.createCell(0);
            cell.setCellValue("审批流程");
            cell.setCellStyle(styleApproval);
            cell = lastRow.createCell(1);

            cell.setCellValue("（1）申报原因：");
            cell.setCellStyle(styleApproval);
            //遍历表头
            for (short columnIndex = 2; columnIndex < 5; columnIndex++) {
                cell = lastRow.createCell(columnIndex);
//                    cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                cell.setCellValue("");
                cell.setCellStyle(styleApproval);
            }
            CellRangeAddress cralast = new CellRangeAddress(rowId+1, rowId+1, 1, 4);
            sheet.addMergedRegion(cralast);
            //
            cell = lastRow.createCell(5);
            cell.setCellValue("（2）申报确认");
            cell.setCellStyle(styleApproval);
            //遍历表头
            for (short columnIndex = 6; columnIndex < 9; columnIndex++) {
                cell = lastRow.createCell(columnIndex);
//                    cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                cell.setCellValue("");
                cell.setCellStyle(styleApproval);
            }
            cralast = new CellRangeAddress(rowId+1, rowId+1, 5, 8);
            sheet.addMergedRegion(cralast);
            //
            cell = lastRow.createCell(9);
            cell.setCellValue("（3）冲突检测、审批");
            cell.setCellStyle(styleApproval);
            //遍历表头
            for (short columnIndex = 10; columnIndex < 16; columnIndex++) {
                cell = lastRow.createCell(columnIndex);
//                    cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                cell.setCellValue("");
                cell.setCellStyle(styleApproval);
            }
            cralast = new CellRangeAddress(rowId+1, rowId+1, 9, 15);
            sheet.addMergedRegion(cralast);
            //最后一行
            rowId++;
            //创建最后一行
            lastRow = sheet.createRow(rowId+1);
            cell = lastRow.createCell(0);
            cell.setCellValue("");
            cell.setCellStyle(styleTitle);
            cell = lastRow.createCell(1);
            cralast = new CellRangeAddress(rowId, rowId+1, 0, 0);
            sheet.addMergedRegion(cralast);
            //
            cell.setCellValue("申报人签字确认：\n" + "申报部门负责人签字确认：");
            cell.setCellStyle(styleApproval);
            //遍历表头
            for (short columnIndex = 2; columnIndex < 5; columnIndex++) {
                cell = lastRow.createCell(columnIndex);
//                    cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                cell.setCellValue("");
                cell.setCellStyle(styleApproval);
            }
            cralast = new CellRangeAddress(rowId+1, rowId+1, 1, 4);
            sheet.addMergedRegion(cralast);
            //
            cell = lastRow.createCell(5);
            cell.setCellValue("申报公司负责人签字确认：");
            cell.setCellStyle(styleApproval);
            //遍历表头
            for (short columnIndex = 6; columnIndex < 9; columnIndex++) {
                cell = lastRow.createCell(columnIndex);
//                    cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                cell.setCellValue("");
                cell.setCellStyle(styleApproval);
            }
            cralast = new CellRangeAddress(rowId+1, rowId+1, 5, 8);
            sheet.addMergedRegion(cralast);
            //
            cell = lastRow.createCell(9);
            cell.setCellValue("施工管理工程师签字确认：");
            cell.setCellStyle(styleApproval);
            //遍历表头
            for (short columnIndex = 10; columnIndex < 16; columnIndex++) {
                cell = lastRow.createCell(columnIndex);
//                    cell.setCellValue(jsonObject.getString(keys[columnIndex]));
                cell.setCellValue("");
                cell.setCellStyle(styleApproval);
            }
            cralast = new CellRangeAddress(rowId+1, rowId+1, 9, 15);
            sheet.addMergedRegion(cralast);

            //设置列宽
            sheet.setColumnWidth(0, 256*12);
            sheet.setColumnWidth(1, 256*6);
            sheet.setColumnWidth(2, 256*12);
            sheet.setColumnWidth(3, 256*12);
            sheet.setColumnWidth(4, 256*12);
            sheet.setColumnWidth(5, 256*12);
            sheet.setColumnWidth(6, 256*12);
            sheet.setColumnWidth(7, 256*12);
            sheet.setColumnWidth(8, 256*12);
            sheet.setColumnWidth(9, 256*12);
            sheet.setColumnWidth(10, 256*12);
            sheet.setColumnWidth(11, 256*12);
            sheet.setColumnWidth(12, 256*12);
            sheet.setColumnWidth(13, 256*6);
            sheet.setColumnWidth(14, 256*12);
        } catch (Exception e) {
            throw e;
        } finally {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();
            workBook.write(response.getOutputStream());
            workBook.close();
        }
    }

    private static String getByString(BdOperatePlanDeclarationFormReturnTypeDTO returnTypeDTO, String key) {
        switch (key) {
            case "id": return returnTypeDTO.getId().toString();
            case "nature": return returnTypeDTO.getNature();
            case "type": return returnTypeDTO.getType();
            case "departmentName": return returnTypeDTO.getDepartmentName();
            case "taskTime": return returnTypeDTO.getTaskTime();
            case "taskRange": return  returnTypeDTO.getTaskRange();
            case "powerSupplyRequirement": return returnTypeDTO.getPowerSupplyRequirement();
            case "taskContent": return returnTypeDTO.getTaskContent();
            case "protectiveMeasure": return returnTypeDTO.getProtectiveMeasure();
            case "chargeStaffName": return returnTypeDTO.getChargeStaffName();
            case "coordinationDepartmentId": return returnTypeDTO.getCoordinationDepartmentId();
            case "firstStationName": return returnTypeDTO.getFirstStationName();
            case "secondStationName": return returnTypeDTO.getSecondStationName();
            case "assistStationName": return returnTypeDTO.getAssistStationName();
            case "taskStaffNum": {
                if(!ObjectUtil.isEmpty(returnTypeDTO.getTaskStaffNum())){
                    return returnTypeDTO.getTaskStaffNum().toString();
                }else{
                    return "";
                }
            }
            case "largeAppliances": return returnTypeDTO.getLargeAppliances();
            /*case "siteName": return returnTypeDTO.getSiteName();*/
            default:
        }
        return "";
    }
}
