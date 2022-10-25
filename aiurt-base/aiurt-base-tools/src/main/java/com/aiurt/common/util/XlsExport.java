package com.aiurt.common.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jdl
 *
 */
public class XlsExport {
    // 设置cell编码解决中文高位字节截断
    // private static short XLS_ENCODING = HSSFWorkbook.ENCODING_UTF_16;

    private static enum XlsFormatEm {
        DATE("m/d/yy"), NUMBER("0.00"), CURRENCY("#,##0.00"), PERCENT("0.00%");
        private final String pattern;

        XlsFormatEm(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return this.pattern;
        }
    }

    private HSSFWorkbook workbook;

    private HSSFSheet sheet;

    private HSSFRow row;

    private HSSFCellStyle hstyle;
    /**
     * @return the hstyle
     */
    public HSSFCellStyle getHstyle() {
        return hstyle;
    }
    public HSSFWorkbook getWorkbook() {
        return this.workbook;
    }
    public HSSFSheet getSheet() {
        return this.sheet;
    }
    public XlsExport() {
        this.workbook = new HSSFWorkbook();
        this.sheet = workbook.createSheet();
        this.sheet.setDefaultColumnWidth((short) 15);
        this.hstyle = workbook.createCellStyle();
        hstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hstyle.setBorderTop(BorderStyle.THIN);
        hstyle.setBorderBottom(BorderStyle.THIN);
        hstyle.setBorderLeft(BorderStyle.THIN);
        hstyle.setBorderRight(BorderStyle.THIN);
    }

    public XlsExport(String tem) {
        try {
            InputStream input = new FileInputStream(tem);
            POIFSFileSystem fs = new POIFSFileSystem(input);
            this.workbook = new HSSFWorkbook(fs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.sheet = workbook.createSheet();
        this.sheet.setDefaultColumnWidth((short) 15);
    }

    /** */
    /**
     * 导出Excel文件
     *
     * @throws
     */
    public void exportXls(String xlsFileName) throws RuntimeException {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(xlsFileName);
            workbook.write(fOut);
            fOut.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("生成导出Excel文件出错!", e);
        } catch (IOException e) {
            throw new RuntimeException("写入Excel文件出错!", e);
        } finally {
            try {
                if (fOut != null){
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportXls(HttpServletResponse response) throws RuntimeException {
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            workbook.write(os);
            os.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("生成导出Excel文件出错了!", e);
        } catch (IOException e) {
            throw new RuntimeException("写入Excel文件出错了!", e);
        } finally {
            try {
                if (os != null){
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** */
    /**
     * 增加一行
     *
     * @param index
     *            行号
     */
    public void createRow(int index) {
        this.row = this.sheet.createRow(index);
        //添加第一行500
        if(index==0) {
            this.row.setHeight((short) 500);
        }else {
            this.row.setHeight((short) 350);
        }

    }

    /** */
    /**
     * 设置单元格
     *
     * @param index
     *            列号
     * @param value
     *            单元格填充值
     */
    public void setCell(int index, String value) {
        HSSFCell cell = this.row.createCell((short) index);
        cell.setCellType(CellType.STRING);
        //统一格式 代码
        cell.setCellStyle(this.hstyle);
        cell.setCellValue(value);
    }

    public void setCell(int index, String value, HSSFCellStyle cStyle) {
        HSSFCell cell = this.row.createCell((short) index);
        cell.setCellStyle(cStyle);
        cell.setCellValue(value);
    }

    public void setCell(int index, Calendar value) {
        if (value != null) {
            setCell(index, value.getTime());
        }
    }

    public void setCell(int index, BigDecimal value) {
        if (value != null) {
            setCell(index, value.toString());
        }
    }

    public void setCell(int index, Date value, HSSFCellStyle cStyle) {
        if (value != null) {
            HSSFCell cell = this.row.createCell((short) index);
            cell.setCellValue(value);
            cell.setCellStyle(cStyle);
            // 设置cell样式为定制的日期格式
            cStyle.setDataFormat(HSSFDataFormat
                    .getBuiltinFormat(XlsFormatEm.DATE.getPattern()));
            // 设置该cell日期的显示格式
            cell.setCellStyle(cStyle);
        }
    }
    public void setCell(int index, Date value) {
        if (value != null) {
            HSSFCell cell = this.row.createCell((short) index);
            cell.setCellValue(value);
            // 建立新的cell样式
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            // 设置cell样式为定制的日期格式
            cellStyle.setDataFormat(HSSFDataFormat
                    .getBuiltinFormat(XlsFormatEm.DATE.getPattern()));
            // 设置该cell日期的显示格式
            cell.setCellStyle(cellStyle);
        }
    }

    public void setCell(int index, int value) {
        HSSFCell cell = this.row.createCell((short) index);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value);
    }

    private void setCell(int index, double value, XlsFormatEm formatEm) {
        HSSFCell cell = this.row.createCell((short) index);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(value);
        // 建立新的cell样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFDataFormat format = workbook.createDataFormat();
        // 设置cell样式为定制的浮点数格式
        cellStyle.setDataFormat(format.getFormat(formatEm.getPattern()));
        // 设置该cell浮点数的显示格式
        cell.setCellStyle(cellStyle);
    }

    public void setCell(int index, double value) {
        setCell(index, value, XlsFormatEm.NUMBER);
    }

    public void setCurrency(int index, double value) {
        setCell(index, value, XlsFormatEm.CURRENCY);
    }

    public void setPercent(int index, double value) {
        setCell(index, value, XlsFormatEm.PERCENT);
    }
}
