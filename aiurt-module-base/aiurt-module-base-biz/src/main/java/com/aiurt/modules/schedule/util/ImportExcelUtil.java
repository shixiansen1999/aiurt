package com.aiurt.modules.schedule.util;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ImportExcelUtil {

    /**
     * 读取Excel数据内容
     *
     * @param is
     * @return List<Map < String, String>>  Map的key是列Id(0代表第一列)，值是具体内容
     */
    public static List<Map<Integer, String>> readExcelContentByList(InputStream is, String extName, int sheetIndex, int rowIndex) throws Exception {
        /*Workbook wb=null;
        Sheet sheet=null;
        Row row=null;*/
        List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();

        if ("xlsx".equals(extName)) {
            XSSFWorkbook wb = null;
            XSSFSheet sheet = null;
            XSSFRow row = null;

            try {
                //fs = new POIFSFileSystem(is);
                wb = new XSSFWorkbook(is);
                //wb = new XSSFWorkbook(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sheet = wb.getSheetAt(sheetIndex);

            // 得到总行数
            int rowNum = sheet.getLastRowNum();
            row = sheet.getRow(rowIndex);
            int colNum = row.getPhysicalNumberOfCells();

            // 正文内容应该从第二行开始,第一行为表头的标题
            for (int i = rowIndex; i <= rowNum; i++) {
                row = sheet.getRow(i);
                int j = 0;
                Map<Integer, String> map = new HashMap<Integer, String>();

                while (j < colNum) {
                    // 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
                    // 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
                    // str += getStringCellValue(row.getCell((short) j)).trim() +
                    // "-";
                    map.put(j, replaceBlank(getCellFormatValue(row.getCell( j)).replaceAll("[\\s\\u00A0]+", "").trim().replaceAll("\t", "").replaceAll("\t\r", "").replace(" ", "").replaceAll("\\s+", "").replace("\u00A0", "")).replaceAll("\uFEFF", ""));
                    j++;
                }
                list.add(map);
            }

        } else {

            HSSFWorkbook wb = null;
            HSSFSheet sheet = null;
            HSSFRow row = null;

            try {
                //fs = new POIFSFileSystem(is);
                wb = new HSSFWorkbook(is);
                //wb = new XSSFWorkbook(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sheet = wb.getSheetAt(0);

            // 得到总行数
            int rowNum = sheet.getLastRowNum();
            row = sheet.getRow(rowIndex);
            int colNum = row.getPhysicalNumberOfCells();

            // 正文内容应该从第二行开始,第一行为表头的标题
            for (int i = rowIndex; i <= rowNum; i++) {
                row = sheet.getRow(i);
                int j = 0;
                Map<Integer, String> map = new HashMap<Integer, String>();

                while (j < colNum) {
                    // 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
                    // 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
                    // str += getStringCellValue(row.getCell((short) j)).trim() +
                    // "-";
                    map.put(j, replaceBlank(getCellFormatValue(row.getCell(j)).replaceAll("[\\s\\u00A0]+", "").trim().replaceAll("\t", "").replaceAll("\t\r", "").replace(" ", "").replaceAll("\\s+", "").replace("\u00A0", "")).replaceAll("\uFEFF", ""));
                    j++;
                }
                list.add(map);
            }

        }
        return list;
    }

    /**
     * 根据XSSFCell类型设置数据
     *
     * @param cell
     * @return
     */
    private static String getCellFormatValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            if(cell.getCellType() != Cell.CELL_TYPE_STRING){
                cell.setCellType(Cell.CELL_TYPE_STRING);
            }
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case XSSFCell.CELL_TYPE_NUMERIC:
                case XSSFCell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        cellvalue = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    } else {
                        // 如果是纯数字取得当前Cell的数值
                        cellvalue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                // 如果当前Cell的Type为STRING
                case XSSFCell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                // 默认的Cell值
                default:
                    cellvalue = "";
            }
        }
        return cellvalue;
    }

    /**
     * 统计表头个数
     *
     * @return
     */
    public static int countTitleNum(Map<Integer, String> titleMap) {
        int titleNum = 0;
        for (Map.Entry<Integer, String> entry : titleMap.entrySet()) {
            titleNum++;
        }
        return titleNum;
    }

    public static void importExcel(InputStream inputStream) {

        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        List<List<Object>> read = excelReader.read(2, excelReader.getRowCount());
        for (List<Object> objects : read) {
            System.out.println(objects.get(0).toString());
        }
    }

    private static Pattern NUMBER_PATTERN = Pattern.compile("\\s*|\t|\r|\n");
    /*
     * 去除数据的空格、回车、换行符、制表符
     */
    public static String replaceBlank(String str) {
        String dest = null;
        if (str != null) {
            //空格\t、回车\n、换行符\r、制表符\t
            Pattern p = NUMBER_PATTERN;
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
