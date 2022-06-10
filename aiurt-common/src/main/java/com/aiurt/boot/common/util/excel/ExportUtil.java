package com.aiurt.boot.common.util.excel;

import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.common.util.excel.entity.ExcelHeaderEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

public class ExportUtil {
    public static void export(Class clz, List list, HttpServletRequest request, HttpServletResponse response, String path, String fileName) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String userAgent = request.getHeader("USER-AGENT");
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            createWorkBookXlsx(path, ExcelHeaderUtil.getHeadres(clz), list).write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String finalFileName;
            if (StringUtils.contains(userAgent, "Firefox")) {
                finalFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } else {
                finalFileName = URLEncoder.encode(fileName, "UTF8");
            }

            response.setHeader("Content-Disposition", "attachment; filename=" + finalFileName);
            ServletOutputStream out = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];

            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (IOException var23) {
            var23.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }

                if (bos != null) {
                    bos.close();
                }
            } catch (IOException var22) {
            }

        }

    }

    public static SXSSFWorkbook createWorkBookXlsx(String xlsxPath, List<ExcelHeaderEntity> listMap, List<?> list) {
        File file = new File(xlsxPath);
        XSSFWorkbook xssfWb = new XSSFWorkbook();
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            xssfWb = new XSSFWorkbook(in);
        } catch (Exception var20) {
        }
        SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWb);
        SXSSFSheet sheet = (SXSSFSheet) workbook.getSheetAt(0);
        CellStyle cs = workbook.createCellStyle();
        CellStyle cs2 = workbook.createCellStyle();
        Font f = workbook.createFont();
        Font f2 = workbook.createFont();
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBoldweight((short) 700);
        f2.setFontHeightInPoints((short) 10);
        f2.setColor(IndexedColors.BLACK.getIndex());
        cs.setFont(f);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);
        cs.setBorderBottom((short) 1);
        cs.setAlignment((short) 2);
        cs2.setFont(f2);
        cs2.setBorderLeft((short) 1);
        cs2.setBorderRight((short) 1);
        cs2.setBorderTop((short) 1);
        cs2.setBorderBottom((short) 1);
        cs2.setAlignment((short) 2);
        for (int i = 0; i < list.size(); ++i) {
            Row row1 = sheet.createRow(i + 2);
            for (int j = 0; j < listMap.size(); ++j) {
                Cell cell = row1.createCell(j);
                Map<String, Object> map = beanToMap(list.get(i));
                if ("Date".equals(listMap.get(j).getAClass().getSimpleName())) {
                    if (map.get(listMap.get(j).getField()) == null) {
                        cell.setCellValue("");
                    } else {
                        String date = DateUtils.format((Date) map.get(listMap.get(j).getField()), listMap.get(j).getFormat());
                        cell.setCellValue(date);
                    }
                } else {
                    if (map.get(listMap.get(j).getField()) == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(map.get(listMap.get(j).getField()).toString());
                    }
                }
                cell.setCellStyle(cs2);
            }
        }
        return workbook;
    }

    private static Map<String, Object> beanToMap(Object object) {
        Map<String, Object> map = new HashMap();
        Class cls = object.getClass();
        Field[] fields = cls.getDeclaredFields();
        Field[] var4 = fields;
        int var5 = fields.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            field.setAccessible(true);

            try {
                map.put(field.getName(), field.get(object));
            } catch (Exception var9) {
            }
        }

        return map;
    }

    public static void export2(List<Date> listMap, List<Map> list, HttpServletRequest request, HttpServletResponse response, String fileName) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String userAgent = request.getHeader("USER-AGENT");
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            createWorkBookXlsx2(listMap, list).write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String finalFileName;
            if (StringUtils.contains(userAgent, "Firefox")) {
                finalFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } else {
                finalFileName = URLEncoder.encode(fileName, "UTF8");
            }

            response.setHeader("Content-Disposition", "attachment; filename=" + finalFileName);
            ServletOutputStream out = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];

            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (IOException var23) {
            var23.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }

                if (bos != null) {
                    bos.close();
                }
            } catch (IOException var22) {
            }

        }

    }


    public static SXSSFWorkbook createWorkBookXlsx2(List<Date> listMap, List<Map> list) {
        XSSFWorkbook xssfWb = new XSSFWorkbook();
        SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWb);
        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet("排班表");
        CellStyle cs = workbook.createCellStyle();
        CellStyle cs2 = workbook.createCellStyle();
        Font f = workbook.createFont();
        Font f2 = workbook.createFont();
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBoldweight((short) 700);
        f2.setFontHeightInPoints((short) 10);
        f2.setColor(IndexedColors.BLACK.getIndex());
        cs.setFont(f);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);
        cs.setBorderBottom((short) 1);
        cs.setAlignment((short) 2);
        cs2.setFont(f2);
        cs2.setBorderLeft((short) 1);
        cs2.setBorderRight((short) 1);
        cs2.setBorderTop((short) 1);
        cs2.setBorderBottom((short) 1);
        cs2.setAlignment((short) 2);

        Row hearRow = sheet.createRow(0);
        Cell c0 = hearRow.createCell(0);
        c0.setCellValue("姓名");
        c0.setCellStyle(cs);
        int index = 1;
        for (Date date : listMap) {
            Cell c1 = hearRow.createCell(index);
            c1.setCellValue(DateUtils.format(date, "dd"));
            c1.setCellStyle(cs);
            index++;
        }
        for (int i = 0; i < list.size(); i++) {
            Row r = sheet.createRow(i + 1);
            Map<Integer, String> dataMap = list.get(i);
            Set<Integer> integers = dataMap.keySet();
            for (Integer j : integers) {
                Cell cell = r.createCell(j);
                cell.setCellValue(dataMap.get(j));
                cell.setCellStyle(cs2);
            }
        }
        return workbook;
    }

}
