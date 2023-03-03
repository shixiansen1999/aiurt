package com.aiurt.modules.faultproducereport.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.faultproducereport.dto.FaultProduceReportDTO;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.mapper.FaultProduceReportMapper;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportExportService;
import com.aiurt.modules.faultproducereportline.entity.FaultProduceReportLine;
import com.aiurt.modules.faultproducereportline.mapper.FaultProduceReportLineMapper;
import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import com.aiurt.modules.faultproducereportlinedetail.mapper.FaultProduceReportLineDetailMapper;
import com.aiurt.modules.position.entity.CsLine;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date: 2023-02-23
 * @Version: V1.0
 */
@Service
@Slf4j
public class FaultProduceReportExportServiceImpl  implements IFaultProduceReportExportService {
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private FaultProduceReportMapper produceReportMapper;
    @Autowired
    private FaultProduceReportLineMapper produceReportLineMapper;
    @Autowired
    private FaultProduceReportLineDetailMapper produceReportLineDetailMapper;
    @Override
    public void getFaultProduceReportExportExcel(FaultProduceReportDTO faultProduceReportDTO) throws IOException {
        XSSFWorkbook wb = getFaultProduceReportExportTemplate();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos);
        byte[] brray = bos.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(brray);
        //拿到临时文件
        File fileTemp = new File("/templates/inspectionRecordExportExcel.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(inputStream, fileTemp);
        } catch (Exception e) {

        }
        String path = fileTemp.getAbsolutePath();
        TemplateExportParams params = new TemplateExportParams(path);
        Map<String, Object> map = new HashMap<String, Object>();
        FaultProduceReport faultProduceReport = produceReportMapper.selectById(faultProduceReportDTO.getSelections().get(0));
        JSONObject csMajorByCode = iSysBaseAPI.getCsMajorByCode(faultProduceReport.getMajorCode());
        map.put("major", csMajorByCode.getString("majorName"));
        map.put("date", faultProduceReport.getStatisticsDate());

        //1.线路名
        //固定一次
        List<CsLine> lineList = iSysBaseAPI.getAllLine();
        List<String> lineCodes = lineList.stream().map(CsLine::getLineCode).collect(Collectors.toList());
        List<Map<String, String>> lineMap = new ArrayList<Map<String, String>>();
        Map<String, String> lm = new HashMap<String, String>();
        for (CsLine csLine : lineList) {
            lm.put(csLine.getLineCode(), csLine.getLineName());
        }
        lineMap.add(lm);
        map.put("lineList", lineMap);

        //固定一次
        //2.故障数
        List<Map<String, String>> totalMap = new ArrayList<Map<String, String>>();
        List<FaultProduceReportLine> faultProduceReportLines =produceReportLineMapper.selectList(new LambdaQueryWrapper<FaultProduceReportLine>
                ().eq(FaultProduceReportLine::getFaultProduceReportId,faultProduceReportDTO.getSelections().get(0)));

        Map<String, String> tm = new HashMap<String, String>();
        for (int i = 0; i < lineCodes.size(); i++) {
            int finalI = i;
            List<FaultProduceReportLine> collect = faultProduceReportLines.stream().filter(e -> e.getLineCode().equals(lineCodes.get(finalI))).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) {
                String s = collect.get(0).getDelayNum() + "/" + collect.get(0).getTotalNum();
                tm.put(lineCodes.get(i), s);
            } else {
                tm.put(lineCodes.get(i), "0/0");
            }
        }

        //可能有多次
        List<FaultProduceReportLineDetail> faultProduceReportLineDetails = produceReportLineDetailMapper.selectList(
                new LambdaQueryWrapper<FaultProduceReportLineDetail>().eq(FaultProduceReportLineDetail::getFaultProduceReportId,faultProduceReportDTO.getSelections().get(0)));
        //3.故障修复情况
        //取最大的线路故障数
        List<Map<String, String>> situationMap = new ArrayList<Map<String, String>>();
        if(CollUtil.isNotEmpty(faultProduceReportLineDetails))
        {
            int size = 2;
            for (int i = 0; i < size; i++) {
                Map<String, String> sm = new HashMap<String, String>();
                for (int j = 0; j < lineCodes.size(); j++) {
                    int finalI = j;
                    List<FaultProduceReportLineDetail> collect = faultProduceReportLineDetails.stream().filter(e -> e.getLineCode().equals(lineCodes.get(finalI))).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(collect)) {
                        FaultProduceReportLineDetail parent = collect.get(0);
                        sm.put(lineCodes.get(j), collect.get(0).getMaintenanceMeasures());
                        Iterator<FaultProduceReportLineDetail> iterator = faultProduceReportLineDetails.iterator();
                        while (iterator.hasNext()) {
                            FaultProduceReportLineDetail next = iterator.next();
                            if (next.getId().equals(parent.getId())) {
                                iterator.remove();
                            }
                        }
                    }
                }
                situationMap.add(sm);
            }
        }else {
            Map<String, String> sm = new HashMap<String, String>();
            for (int i = 0; i < lineCodes.size(); i++) {
                int finalI = i;
                sm.put(lineCodes.get(i), "");
            }
            situationMap.add(sm);
        }
        map.put("situationList", situationMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        Sheet sheet = workbook.getSheet("生产日报");
        sheet.addMergedRegion(new CellRangeAddress(4, 0, 0, 0));
        totalMap.add(tm);
        map.put("totalList", totalMap);

    }
    public XSSFWorkbook getFaultProduceReportExportTemplate() throws IOException {
        //创建workbook,即创建一个excel
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建excel里对应的sheet
        XSSFSheet sheet = wb.createSheet("生产日报");
        // 行号
        int rowNum = 0;
        // 创建第一页的第一行，索引从0开始
        XSSFRow row0 = sheet.createRow(rowNum++);
        // 设置行高
        row0.setHeight((short) 800);
        String title = "-运营生产日报-";
        //创建表头
        createHeadCell(wb, row0, title, sheet);
        return wb;
    }
    private void createHeadCell(XSSFWorkbook wb, XSSFRow row0, String title, XSSFSheet sheet) {
        XSSFCellStyle titleStyle = createTitleCellStyle(wb);
        XSSFCellStyle firstHeadCellStyle = createFirstHeadCellStyle(wb);

        //第一行
        XSSFCell c00 = row0.createCell(0);
        c00.setCellValue("{{major}}" + title + "{{date}}");


        XSSFRow rowone = sheet.createRow(1);
        XSSFCell c01 = rowone.createCell(0);
        c01.setCellStyle(firstHeadCellStyle);
        c01.setCellValue("行车类设备设施故障情况");
        List<CsLine> lineList = iSysBaseAPI.getAllLine();
        List<String> lineCodes = lineList.stream().map(CsLine::getLineCode).collect(Collectors.toList());
        //String[] lineCodes = {"A", "2", "334-we123", "line2-3#", "202200705A-1"};
        XSSFRow rowZero = sheet.createRow(2);
        XSSFCell zeroCell = rowZero.createCell(0);
        zeroCell.setCellValue("统计名称");
        zeroCell.setCellStyle(firstHeadCellStyle);
        rowZero.setHeight((short) 700);
        //将 rowSeconds 的名称，即一级表头名 ，写入单元格，每个空一格，然后合并
        for (int i = 0; i < lineCodes.size(); i++) {
            int b = i + 1;
            zeroCell = rowZero.createCell(b);
            if (i == 0) {
                zeroCell.setCellValue("{{$fe:lineList t." + lineCodes.get(i));
            } else {
                zeroCell.setCellValue("t." + lineCodes.get(i));
            }
            if (i == (lineCodes.size() - 1)) {
                zeroCell.setCellValue("t." + lineCodes.get(i) + "}}");
            }
            zeroCell.setCellStyle(firstHeadCellStyle);
        }


        XSSFRow rowOne = sheet.createRow(3);
        XSSFCell oneCell = rowOne.createCell(0);
        oneCell.setCellValue("导致延误的故障次数/总故障次数");
        oneCell.setCellStyle(firstHeadCellStyle);
        rowOne.setHeight((short) 700);
        //将 rowSeconds 的名称，即一级表头名 ，写入单元格，每个空一格，然后合并
        for (int i = 0; i < lineCodes.size(); i++) {
            int b = i + 1;
            oneCell = rowOne.createCell(b);
            if (i == 0) {
                oneCell.setCellValue("{{$fe:totalList t." + lineCodes.get(i));
            } else {
                oneCell.setCellValue("t." + lineCodes.get(i));
            }
            if (i == (lineCodes.size() - 1)) {
                oneCell.setCellValue("t." + lineCodes.get(i) + "}}");
            }
            oneCell.setCellStyle(firstHeadCellStyle);
        }


        XSSFRow rowSecond = sheet.createRow(4);
        XSSFCell tempCell = rowSecond.createCell(0);
        tempCell.setCellValue("故障修复情况及管控措施");
        tempCell.setCellStyle(firstHeadCellStyle);
        rowSecond.setHeight((short) 700);
        XSSFRow rowThree = sheet.createRow(5);
        XSSFCell c07 = rowThree.createCell(0);
        c07.setCellValue("备注：行车类设备设施故障情况（以上数据未经安技部分析，对故障情况及原因进行简单解释说明，非本中心原因造成的内容填写无）");

        //将 rowSeconds 的名称，即一级表头名 ，写入单元格，每个空一格，然后合并
        for (int i = 0; i < lineCodes.size(); i++) {
            int b = i + 1;
            tempCell = rowSecond.createCell(b);
            if (i == 0) {

                tempCell.setCellValue("{{$fe:situationList t." + lineCodes.get(i));
            } else {
                tempCell.setCellValue("t." + lineCodes.get(i));
            }
            if (i == (lineCodes.size() - 1)) {
                tempCell.setCellValue("t." + lineCodes.get(i) + "}}");
            }
            tempCell.setCellStyle(firstHeadCellStyle);
        }
        CellRangeAddress cellAddresses = new CellRangeAddress(0, 0, 0, lineCodes.size());
        style(cellAddresses, sheet);
        CellRangeAddress a = new CellRangeAddress(1, 1, 0, lineCodes.size());
        style(a, sheet);
        CellRangeAddress b = new CellRangeAddress(5, 5, 0, lineCodes.size());
        style(b, sheet);
        c00.setCellStyle(titleStyle);
        c07.setCellStyle(firstHeadCellStyle);
    }
    private static XSSFCellStyle createTitleCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //垂直对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 创建字体样式
        XSSFFont headerFont1 = (XSSFFont) wb.createFont();
        //字体加粗
        headerFont1.setBold(true);
        // 设置字体类型
        headerFont1.setFontName("宋体");
        // 设置字体大小
        headerFont1.setFontHeightInPoints((short) 16);
        // 为标题样式设置字体样式
        cellStyle.setFont(headerFont1);
        return cellStyle;
    }
    private void style(CellRangeAddress cellAddresses, Sheet sheet) {
        sheet.addMergedRegion(cellAddresses);
        //合并后设置下边框
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);

    }

    /**
     * 创建一级表头样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle createFirstHeadCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        //左对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置自动换行
        cellStyle.setWrapText(true);
        //下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        //左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        //上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        //背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 创建字体样式
        XSSFFont headerFont1 = (XSSFFont) wb.createFont();
        //字体加粗
        headerFont1.setBold(true);
        // 设置字体类型
        headerFont1.setFontName("宋体");
        // 设置字体大小
        headerFont1.setFontHeightInPoints((short) 11);
        // 为标题样式设置字体样式
        cellStyle.setFont(headerFont1);
        return cellStyle;
    }
}
