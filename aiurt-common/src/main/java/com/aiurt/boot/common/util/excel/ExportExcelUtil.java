package com.aiurt.boot.common.util.excel;

import com.aiurt.boot.common.util.DateUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.Region;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * @author renxl
 * @version 导出excel工具类
 */
public class ExportExcelUtil {

    // 一个sheet有多少行
    private static final int SHEET_ROW_COUNT = 65536;

    private static final int SHEET_TITLE_ROWS = 2;
    // 文件对象
    private HSSFWorkbook workBook = null;
    // sheet对象
    private HSSFSheet sheet = null;
    private HSSFSheet sheet1 = null;
    // 头行
    private HSSFRow headRow = null;
    private HSSFRow headRow1 = null;
    private HSSFRow twoRow = null;
    private HSSFRow threeRow = null;

    private HSSFRow row = null;
    private HSSFRow row1 = null;
    // cell对象
    private HSSFCell cell = null;
    private HSSFCell cellTwo = null;
    private HSSFCell cellThree = null;
    private HSSFCell cell1 = null;

    // 样式
    private HSSFCellStyle cellStyle = null;
    private HSSFCellStyle cellStyleTwo = null;
    private HSSFCellStyle cellStyleThree = null;
    private HSSFCellStyle cellStyle1 = null;
    private HSSFCellStyle cellStyleDate = null;
    private HSSFCellStyle cellStyleString = null;
    private HSSFCellStyle cellStyleDate1 = null;
    private HSSFCellStyle cellStyleString1 = null;

    // 字体
    private HSSFFont font = null;
    private HSSFFont fontTwo = null;
    private HSSFFont fontThree = null;
    private HSSFFont font1 = null;

    /**
     * 导出到excel文件
     *
     * @Description:
     * @Author: jiaoguojin
     * @Company: 宜信-变现通项目组
     * @Version: V1.0
     * @Create Date: 2014年12月30日
     */
    public void exportExcel(OutputStream os, String title,
                            List<Object> fieldName, List<List<Object>> fieldData)
            throws Exception {
        this.workBook = createWorkbook(title, fieldName, fieldData);
        this.workBook.write(os);
        os.close();
    }

    /**
     * @Description: 导出到excel文件
     * @Author: jiaoguojin
     * @Company: 宜信-变现通项目组
     * @Version: V1.0
     * @Create Date: 2014年12月30日
     */
    @SuppressWarnings("deprecation")
    public HSSFWorkbook createWorkbook(String title, List<Object> fieldName,
                                       List<List<Object>> fieldData) {
        List<Object> rowList = null;
        this.workBook = new HSSFWorkbook();
        int rows = fieldData.size();
        int sheetNum = 0;

        // excle默认一个sheet页可以保存65536条数据，但是由于有两行标题，所以此处设置为65534
        if (rows % (SHEET_ROW_COUNT - SHEET_TITLE_ROWS) == 0)
            sheetNum = rows / (SHEET_ROW_COUNT - SHEET_TITLE_ROWS) == 0 ? 1 : rows / (SHEET_ROW_COUNT - SHEET_TITLE_ROWS);
        else {
            sheetNum = rows / (SHEET_ROW_COUNT - SHEET_TITLE_ROWS) + 1;
        }
        int cellnum = fieldName.size() - 1;
        for (int i = 1; i <= sheetNum; i++) {
            this.sheet = this.workBook.createSheet("Page " + i);
            HSSFRow titleRow = this.sheet.createRow(0);
            // 合并第一行，标题
            this.sheet.addMergedRegion(new Region(0, (short) 0, 0,
                    (short) cellnum));

            this.headRow = this.sheet.createRow(1);
            for (int j = 0; j < fieldName.size(); j++) {
                titleRow.createCell((short) j);
                this.cell = this.headRow.createCell((short) j);

                this.sheet.setColumnWidth((short) j, 6000);
                this.cellStyle = this.workBook.createCellStyle();
                this.cellStyle.setAlignment((short) 2);
                this.font = this.workBook.createFont();

                this.font.setBoldweight((short) 700);

                this.font.setColor((short) 8);

                this.cellStyle.setFont(this.font);
                this.cellStyle.setBorderBottom((short) 1);
                this.cellStyle.setBorderTop((short) 1);
                this.cellStyle.setBorderLeft((short) 1);
                this.cellStyle.setBorderRight((short) 1);

                this.cell.setCellType(1);
                if (fieldName.get(j) != null) {
                    this.cell.setCellStyle(this.cellStyle);
                    this.cell.setCellValue((String) fieldName.get(j));
                } else {
                    this.cell.setCellValue("");
                    this.cell.setCellStyle(this.cellStyle);
                }
            }
            if (fieldData != null && fieldData.size() > 0) {
                // 文本格式（不要放到数据循环体内，超过4000条报错）
                HSSFDataFormat format = workBook.createDataFormat();
                this.cellStyle = this.workBook.createCellStyle();

                for (int k = 0; k < (rows < 65534 ? rows : 65534); k++) {

                    if ((i - 1) * 65534 + k >= rows)
                        break;
                    this.row = this.sheet.createRow((k + 2));
                    // 设置样式
                    // this.cellStyle = this.workBook.createCellStyle();
                    this.cellStyle.setDataFormat(format.getFormat("@"));

                    this.cellStyle.setAlignment((short) 2);
                    this.cellStyle.setBorderBottom((short) 1);
                    this.cellStyle.setBorderTop((short) 1);
                    this.cellStyle.setBorderLeft((short) 1);
                    this.cellStyle.setBorderRight((short) 1);

                    rowList = (List<Object>) fieldData.get((i - 1) * 65534 + k);
                    for (int n = 0; n < rowList.size(); n++) {
                        this.cell = this.row.createCell(n);
                        if (rowList.get(n) != null) {
                            this.cell.setCellStyle(this.cellStyle);
                            this.cell.setCellValue(rowList.get(n).toString());
                        } else {
                            this.cell.setCellValue("");
                            this.cell.setCellStyle(this.cellStyle);
                        }
                    }
                }
            }

            HSSFCellStyle setBorder = this.workBook.createCellStyle();
            HSSFFont font = this.workBook.createFont();
            font.setBoldweight((short) 700);
            font.setFontHeightInPoints((short) 14);
            setBorder.setFont(font);
            setBorder.setAlignment((short) 2);
            titleRow.getCell(0).setCellValue(title);
            titleRow.getCell(0).setCellStyle(setBorder);
        }
        return this.workBook;
    }

    /**
     * @throws IOException
     * @Description: 导出模板
     * @Author: jiaoguojin
     * @Company: 宜信-变现通项目组
     * @Version: V1.0
     * @Create Date: 2014年12月29日
     */
    public void createSheet(OutputStream os, String title, String title1,
                            List<Object> fieldName, List<Object> fieldName1,
                            List<Object> twoList, List<Object> threeList,
                            List<List<Object>> fieldData, List<List<Object>> field1Data)
            throws Exception {
        List<Object> rowList = null;
        List<Object> rowList1 = null;
        this.workBook = new HSSFWorkbook();
        int rows = fieldData.size();
        int sheetNum = 0;

        if (rows % 65533 == 0)
            sheetNum = rows / 65533 == 0 ? 1 : rows / 65533;
        else {
            sheetNum = rows / 65533 + 1;
        }
        // 循环计算sheet
        for (int i = 1; i <= sheetNum; i++) {

            this.sheet = this.workBook.createSheet("债权导入" + i);
            this.sheet1 = this.workBook.createSheet("现金流" + i);

            // 第一行
            this.headRow = this.sheet.createRow(0);
            // 第二行
            this.twoRow = this.sheet.createRow(1);
            // 第三行
            this.threeRow = this.sheet.createRow(2);
            // 第二个sheet的第一行
            this.headRow1 = this.sheet1.createRow(0);
            // 第一个sheet页
            for (int j = 0; j < fieldName.size(); j++) {
                this.cell = this.headRow.createCell((short) j);

                this.sheet.setColumnWidth((short) j, 4500);
                this.cellStyle = this.workBook.createCellStyle();
                this.cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                this.font = this.workBook.createFont();

                this.font.setBoldweight((short) 700);

                this.font.setColor((short) 8);

                this.cellStyle.setFont(this.font);
                this.cellStyle.setBorderBottom((short) 1);
                this.cellStyle.setBorderTop((short) 1);
                this.cellStyle.setBorderLeft((short) 1);
                this.cellStyle.setBorderRight((short) 1);

                this.cell.setCellType(1);
                if (fieldName.get(j) != null) {
                    this.cell.setCellStyle(this.cellStyle);
                    this.cell.setCellValue((String) fieldName.get(j));
                } else {
                    this.cell.setCellValue("");
                    this.cell.setCellStyle(this.cellStyle);
                }
            }
            for (int j = 0; j < twoList.size(); j++) {
                this.cellTwo = this.twoRow.createCell((short) j);

                this.sheet.setColumnWidth((short) j, 4500);
                this.cellStyleTwo = this.workBook.createCellStyle();
                this.cellStyleTwo.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置居中
                this.cellStyleTwo.setWrapText(true);// 设置自动换行
                this.fontTwo = this.workBook.createFont();

                this.fontTwo.setBoldweight((short) 700);

                this.fontTwo.setColor((short) 8);

                this.cellStyleTwo.setFont(this.font);
                this.cellStyleTwo.setBorderBottom((short) 1);
                this.cellStyleTwo.setBorderTop((short) 1);
                this.cellStyleTwo.setBorderLeft((short) 1);
                this.cellStyleTwo.setBorderRight((short) 1);

                this.cellTwo.setCellType(1);
                if (twoList.get(j) != null) {
                    this.cellTwo.setCellStyle(this.cellStyleTwo);
                    this.cellTwo.setCellValue((String) twoList.get(j));
                } else {
                    this.cellTwo.setCellValue("");
                    this.cellTwo.setCellStyle(this.cellStyleTwo);
                }
            }
            for (int j = 0; j < threeList.size(); j++) {
                this.cellThree = this.threeRow.createCell((short) j);

                this.sheet.setColumnWidth((short) j, 4500);
                this.cellStyleThree = this.workBook.createCellStyle();
                this.cellStyleThree.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                this.fontThree = this.workBook.createFont();

                this.fontThree.setBoldweight((short) 700);

                this.fontThree.setColor((short) 8);

                this.cellStyleThree.setFont(this.font);
                this.cellStyleThree.setBorderBottom((short) 1);
                this.cellStyleThree.setBorderTop((short) 1);
                this.cellStyleThree.setBorderLeft((short) 1);
                this.cellStyleThree.setBorderRight((short) 1);

                this.cellThree.setCellType(1);
                if (threeList.get(j) != null) {
                    this.cellThree.setCellStyle(this.cellStyleThree);
                    this.cellThree.setCellValue((String) threeList.get(j));
                } else {
                    this.cellThree.setCellValue("");
                    this.cellThree.setCellStyle(this.cellStyleThree);
                }
            }
            // 第二个sheet页
            for (int j = 0; j < fieldName1.size(); j++) {
                this.cell1 = this.headRow1.createCell((short) j);

                this.sheet1.setColumnWidth((short) j, 4500);
                this.cellStyle1 = this.workBook.createCellStyle();
                this.cellStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置居中
                this.font1 = this.workBook.createFont();

                this.font1.setBoldweight((short) 700);

                this.font1.setColor((short) 8);

                this.cellStyle1.setFont(this.font);
                this.cellStyle1.setBorderBottom((short) 1);
                this.cellStyle1.setBorderTop((short) 1);
                this.cellStyle1.setBorderLeft((short) 1);
                this.cellStyle1.setBorderRight((short) 1);

                this.cell1.setCellType(1);
                if (fieldName1.get(j) != null) {
                    this.cell1.setCellStyle(this.cellStyle1);
                    this.cell1.setCellValue((String) fieldName1.get(j));
                } else {
                    this.cell1.setCellValue("");
                    this.cell1.setCellStyle(this.cellStyle1);
                }
            }
            // 第一个sheet页数据
            if (fieldData != null && fieldData.size() > 0) {
                // 设置常规样式
                this.cellStyle = this.workBook.createCellStyle();
                // 日期格式
                HSSFDataFormat format = workBook.createDataFormat();
                this.cellStyleDate = this.workBook.createCellStyle();
                // 文本格式
                this.cellStyleString = this.workBook.createCellStyle();
                for (int k = 0; k < (rows < 65533 ? rows : 65533); k++) {

                    if ((i - 1) * 65533 + k >= rows)
                        break;
                    this.row = this.sheet.createRow((k + 3));

                    this.cellStyleDate.setDataFormat(format
                            .getFormat("yyyy/mm/dd"));
                    this.cellStyleString.setDataFormat(format.getFormat("@"));

                    this.cellStyle.setAlignment((short) 2);
                    this.cellStyle.setBorderBottom((short) 1);
                    this.cellStyle.setBorderTop((short) 1);
                    this.cellStyle.setBorderLeft((short) 1);
                    this.cellStyle.setBorderRight((short) 1);

                    this.cellStyleString.setAlignment((short) 2);
                    this.cellStyleString.setBorderBottom((short) 1);
                    this.cellStyleString.setBorderTop((short) 1);
                    this.cellStyleString.setBorderLeft((short) 1);
                    this.cellStyleString.setBorderRight((short) 1);

                    this.cellStyleDate.setAlignment((short) 2);
                    this.cellStyleDate.setBorderBottom((short) 1);
                    this.cellStyleDate.setBorderTop((short) 1);
                    this.cellStyleDate.setBorderLeft((short) 1);
                    this.cellStyleDate.setBorderRight((short) 1);
                    // 先循环遍历有多少行
                    rowList = (List<Object>) fieldData.get((i - 1) * 65533 + k);
                    // 循环遍历具体某一行
                    for (int n = 0; n < rowList.size(); n++) {
                        this.cell = this.row.createCell(n);

                        if (rowList.get(n) != null
                                && (n == 0 || n == 3 || n == 10)) {
                            this.cell.setCellStyle(this.cellStyleString);
                            this.cell.setCellValue(rowList.get(n).toString());
                        }/*
                         * else if(rowList.get(n) != null && (n==5 || n== 8)){
                         * this.cell.setCellStyle(this.cellStyleDate);
                         * this.cell.setCellValue(rowList.get(n).toString()); }
                         */ else if (rowList.get(n) != null) {
                            this.cell.setCellStyle(this.cellStyle);
                            this.cell.setCellValue(rowList.get(n).toString());
                        } else {
                            this.cell.setCellValue("");
                            this.cell.setCellStyle(this.cellStyle);
                        }
                    }
                }
            }
            // 第二个sheet页数据
            if (field1Data != null && field1Data.size() > 0) {
                // 设置样式
                this.cellStyle1 = this.workBook.createCellStyle();
                // 文本格式
                HSSFDataFormat format = workBook.createDataFormat();
                this.cellStyleString1 = this.workBook.createCellStyle();
                // 日期格式
                this.cellStyleDate1 = this.workBook.createCellStyle();
                for (int k = 0; k < (rows < 65533 ? rows : 65533); k++) {
                    if ((i - 1) * 65533 + k >= rows)
                        break;
                    this.row1 = this.sheet1.createRow((k + 1));

                    this.cellStyleString1.setDataFormat(format.getFormat("@"));

                    this.cellStyleDate1.setDataFormat(format
                            .getFormat("yyyy/mm/dd"));

                    this.cellStyle1.setAlignment((short) 2);
                    this.cellStyle1.setBorderBottom((short) 1);
                    this.cellStyle1.setBorderTop((short) 1);
                    this.cellStyle1.setBorderLeft((short) 1);
                    this.cellStyle1.setBorderRight((short) 1);

                    this.cellStyleString1.setAlignment((short) 2);
                    this.cellStyleString1.setBorderBottom((short) 1);
                    this.cellStyleString1.setBorderTop((short) 1);
                    this.cellStyleString1.setBorderLeft((short) 1);
                    this.cellStyleString1.setBorderRight((short) 1);

                    this.cellStyleDate1.setAlignment((short) 2);
                    this.cellStyleDate1.setBorderBottom((short) 1);
                    this.cellStyleDate1.setBorderTop((short) 1);
                    this.cellStyleDate1.setBorderLeft((short) 1);
                    this.cellStyleDate1.setBorderRight((short) 1);

                    rowList1 = (List<Object>) field1Data.get((i - 1) * 65533
                            + k);
                    for (int n = 0; n < rowList1.size(); n++) {
                        this.cell1 = this.row1.createCell(n);
                        if (rowList1.get(n) != null && n == 0) {
                            this.cell1.setCellStyle(this.cellStyleString1);
                            this.cell1.setCellValue(rowList1.get(n).toString());
                        }/*
                         * else if(rowList1.get(n) != null && n==1){
                         * this.cell1.setCellStyle(this.cellStyleDate1);
                         * this.cell1.setCellValue(rowList1.get(n).toString());
                         * }
                         */ else if (rowList1.get(n) != null) {
                            this.cell1.setCellStyle(this.cellStyle1);
                            this.cell1.setCellValue(rowList1.get(n).toString());
                        } else {
                            this.cell1.setCellValue("");
                            this.cell1.setCellStyle(this.cellStyle1);
                        }
                    }
                }
            }

            HSSFCellStyle setBorder = this.workBook.createCellStyle();
            HSSFFont font = this.workBook.createFont();
            font.setBoldweight((short) 700);
            font.setFontHeightInPoints((short) 14);
            setBorder.setFont(font);
            setBorder.setAlignment((short) 2);

            HSSFCellStyle setBorder1 = this.workBook.createCellStyle();
            HSSFFont font1 = this.workBook.createFont();
            font1.setBoldweight((short) 700);
            font1.setFontHeightInPoints((short) 14);
            setBorder1.setFont(font1);
            setBorder1.setAlignment((short) 2);
        }
        this.workBook.write(os);
        os.close();
    }

    /**
     * 由Excel流的Sheet导出至List
     *
     * @param is
     * @param extensionName
     * @param sheetNum
     * @return
     * @throws IOException
     */
    public List<List<String>> importListFromExcel(InputStream is, String extensionName, int sheetNum) throws IOException {
        Workbook workbook = null;
        if (extensionName.toLowerCase().indexOf("xlsx") >= 0) {
            workbook = new XSSFWorkbook(is);
        } else {
            workbook = new HSSFWorkbook(is);
        }

        return importListFromExcel(workbook, sheetNum);
    }

    /**
     * 由指定的Sheet导出至List
     *
     * @param workbook
     * @param sheetNum
     * @return
     * @throws IOException
     */
    private List<List<String>> importListFromExcel1(Workbook workbook,
                                                   int sheetNum) {

        Sheet sheet = workbook.getSheetAt(sheetNum);

        // 解析公式结果
        FormulaEvaluator evaluator = workbook.getCreationHelper()
                .createFormulaEvaluator();

        List<List<String>> list = new LinkedList<List<String>>();

        int minRowIx = sheet.getFirstRowNum();
        int maxRowIx = sheet.getLastRowNum();
        for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
            Row row = sheet.getRow(rowIx);
            if (null == row) {
                continue;
            }
            List<String> rowList = new LinkedList<String>();

            short minColIx = row.getFirstCellNum();
            short maxColIx = row.getLastCellNum();
            for (short colIx = minColIx; colIx <= maxColIx; colIx++) {
                String value = ""; // 默认空值, 防止跳过之后, list中顺序错位
                Cell cell = row.getCell(new Integer(colIx));
                CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue == null) {
                    ; // do nothing, take default value
                } else {
                    // 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
                    // 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
                    switch (cellValue.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            value = cellValue.getBooleanValue() ? "true" : "false";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:

                            // 如果是数字类型的话,判断是不是日期类型
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                value = DateUtils.format(cell.getDateCellValue(), DateUtils.PATTERN_YYYY_MM_DD);
                            } else if(cell.getCellStyle().getDataFormat() == 57 || cell.getCellStyle().getDataFormat() == 58 || cell.getCellStyle().getDataFormat() == 31) {
                                value = DateUtils.format(cell.getDateCellValue(), DateUtils.PATTERN_YYYY_MM_DD);
                            } else {
                                value = new BigDecimal(cellValue.getNumberValue())
                                        .toPlainString();
                            }
                            break;
                        case Cell.CELL_TYPE_STRING:
                            value = cellValue.getStringValue();
                            break;
                        case Cell.CELL_TYPE_FORMULA:
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            break;
                        case Cell.CELL_TYPE_ERROR:
                            break;
                        default:
                            break;
                    }
                }
                rowList.add(value);
            }
            list.add(rowList);
        }
        return list;
    }


    private List<List<String>> importListFromExcel(Workbook workbook, int sheetNum) {
        Sheet sheet;

            sheet = workbook.getSheetAt(sheetNum);
        int sheets = workbook.getNumberOfSheets();
        if (sheets != 9 && sheets !=2 && sheets !=1 ) {
            throw new RuntimeException("模板有误！请注意不能增加或减少sheet页！");
        }

        // 解析公式结果
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        List<List<String>> list = new LinkedList<List<String>>();

        Row rowOne = sheet.getRow(0);
        /*if(rowOne == null){
            throw new RuntimeException("模板第"+sheetNum+"张表有误！请注意不能增加sheet模板！");
        }*/

        //获取最大列数
        int maxColIx = rowOne.getPhysicalNumberOfCells();

        int minRowIx = sheet.getFirstRowNum();
        int maxRowIx = sheet.getLastRowNum();
        for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
            Row row = sheet.getRow(rowIx);
            if (null == row) {
                continue;
            }
            List<String> rowList = new LinkedList<String>();

            //short minColIx = row.getFirstCellNum();
            short minColIx =0;
            if(minColIx < 0){
                continue;
            }
            for (short colIx = minColIx; colIx <= maxColIx; colIx++) {
                String value = ""; // 默认空值, 防止跳过之后, list中顺序错位
                Cell cell = row.getCell(new Integer(colIx));
                CellValue cellValue = null;
                try {
                    cellValue = evaluator.evaluate(cell);
                } catch (Exception e) {
                    if (sheetNum == 0) {
                        int rowErro = rowIx + 1;
                        int colErro = colIx + 1;
                        throw new RuntimeException("上报信息汇总表-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                    } else if (sheetNum == 1) {
                        int rowErro = rowIx + 1;
                        int colErro = colIx + 1;
                        throw new RuntimeException("附表1-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                    } else if (sheetNum == 2) {
                        int rowErro = rowIx + 1;
                        int colErro = colIx + 1;
                        throw new RuntimeException("附表2-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                    } else if (sheetNum == 3) {
                        int rowErro = rowIx + 1;
                        int colErro = colIx + 1;
                        throw new RuntimeException("附表3-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                    } else if (sheetNum == 4) {
                        int rowErro = rowIx + 1;
                        int colErro = colIx + 1;
                        throw new RuntimeException("附表4-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                    } else if (sheetNum == 5) {
                        int rowErro = rowIx + 1;
                        int colErro = colIx + 1;
                        throw new RuntimeException("附表5-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                    } else {
                        int rowErro = rowIx + 1;
                        int colErro = colIx + 1;
                        throw new RuntimeException("附表6-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                    }
                    //throw new RuntimeException("引用外部文件!");
                }

                if (cellValue == null) {
                    ; // do nothing, take default value
                } else {
                    // 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
                    // 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
                    switch (cellValue.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            value = cellValue.getBooleanValue() ? "true" : "false";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:

                            // 如果是数字类型的话,判断是不是日期类型
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                value = DateUtils.format(cell.getDateCellValue(), DateUtils.PATTERN_YYYY_MM_DD);
                            } else if(cell.getCellStyle().getDataFormat() == 57 || cell.getCellStyle().getDataFormat() == 58 || cell.getCellStyle().getDataFormat() == 31) {
                                value = DateUtils.format(cell.getDateCellValue(), DateUtils.PATTERN_YYYY_MM_DD);
                            } else {
                                value = new BigDecimal(cellValue.getNumberValue())
                                        .toPlainString();
                            }
                            break;
                        case Cell.CELL_TYPE_STRING:
                            value = cellValue.getStringValue();
                            break;
                        case Cell.CELL_TYPE_FORMULA:
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            break;
                        case Cell.CELL_TYPE_ERROR:
                            break;
                        default:
                            break;
                    }
                }
                rowList.add(value);
            }
            list.add(rowList);
        }
        return list;
    }


    public List<List<String>> listFromExcelAcid(InputStream is,String extensionName) throws IOException {
            Workbook workbook = null;
            if (extensionName.toLowerCase().contains("xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                workbook = new HSSFWorkbook(is);
            }

            Sheet sheet = workbook.getSheetAt(0);
            // 解析公式结果
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<List<String>> list = new LinkedList<List<String>>();
            Row rowOne = sheet.getRow(0);

            //获取最大列数
            int maxColIx = rowOne.getPhysicalNumberOfCells();
            int minRowIx = sheet.getFirstRowNum();
            int maxRowIx = sheet.getLastRowNum();
            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx);
                if (null == row) {
                    continue;
                }
                List<String> rowList = new LinkedList<String>();
                //short minColIx = row.getFirstCellNum();
                short minColIx =0;
                if(minColIx < 0){
                    continue;
                }
                for (short colIx = minColIx; colIx <= maxColIx; colIx++) {
                    String value = ""; // 默认空值, 防止跳过之后, list中顺序错位
                    Cell cell = row.getCell(new Integer(colIx));
                    CellValue cellValue = null;
                    try {
                        cellValue = evaluator.evaluate(cell);
                    } catch (Exception e) {
                            int rowErro = rowIx + 1;
                            int colErro = colIx + 1;
                            throw new RuntimeException("核酸检测信息采集表-第" + rowErro + "行第" + colErro + "列,可能引用外部文件,请修改后重新导入!");
                        //throw new RuntimeException("引用外部文件!");
                    }

                    if (cellValue == null) {
                        ; // do nothing, take default value
                    } else {
                        // 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
                        // 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
                        switch (cellValue.getCellType()) {
                            case Cell.CELL_TYPE_BOOLEAN:
                                value = cellValue.getBooleanValue() ? "true" : "false";
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                // 如果是数字类型的话,判断是不是日期类型
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    value = DateUtils.format(cell.getDateCellValue(), DateUtils.PATTERN_YYYY_MM_DD);
                                } else if(cell.getCellStyle().getDataFormat() == 57 || cell.getCellStyle().getDataFormat() == 58 || cell.getCellStyle().getDataFormat() == 31) {
                                    value = DateUtils.format(cell.getDateCellValue(), DateUtils.PATTERN_YYYY_MM_DD);
                                } else {
                                    value = new BigDecimal(cellValue.getNumberValue())
                                            .toPlainString();
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                value = cellValue.getStringValue();
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                break;
                            case Cell.CELL_TYPE_BLANK:
                                break;
                            case Cell.CELL_TYPE_ERROR:
                                break;
                            default:
                                break;
                        }
                    }
                    rowList.add(value);
                }
                list.add(rowList);
            }
        return list;
    }

}
