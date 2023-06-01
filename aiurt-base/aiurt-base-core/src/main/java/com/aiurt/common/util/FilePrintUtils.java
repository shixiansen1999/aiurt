package com.aiurt.common.util;

import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @className: PrintPatrolUtils
 * @author: hqy
 * @date: 2023/6/1 15:59
 * @version: 1.0
 */

public class FilePrintUtils {


    /**
     * 设置图片属性
     * @param fileByte
     * @return
     * @throws IOException
     */
    public static WriteCellData<Void> writeCellImageData(byte[] fileByte)  {
        if (fileByte == null) {
            return null;
        }

        WriteCellData<Void> writeCellData = new WriteCellData<>();
        // 这里可以设置为 EMPTY 则代表不需要其他数据了
        //writeCellData.setType(CellDataTypeEnum.EMPTY);

        List<ImageData> imageDataList = new ArrayList<>();
        writeCellData.setImageDataList(imageDataList);

        ImageData imageData = new ImageData();
        imageDataList.add(imageData);
        // 设置图片
        imageData.setImage(fileByte);
        // 图片类型
        //imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
        // 上 右 下 左 需要留空设置，类似于 css 的 margin
        imageData.setTop(5);
//        imageData.setRight(1);
        imageData.setBottom(5);
        imageData.setLeft(100);

        // 设置图片的位置：Relative表示相对于当前的单元格index，first是左上点，last是对角线的右下点，这样确定一个图片的位置和大小。
//      imageData.setRelativeFirstRowIndex(0);
        imageData.setRelativeFirstColumnIndex(0);
//      imageData.setRelativeLastRowIndex(0);
        imageData.setRelativeLastColumnIndex(1);

        return writeCellData;
    }
    public static void addReturn(Workbook workbook, int startRow, int endRow, int startColumn, int endColumn){
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setWrapText(true);

        Font font = workbook.createFont();
        // 设置字体为宋体
        font.setFontName("宋体");
        // 设置字体大小为9号
        font.setFontHeightInPoints((short) 9);
        cellStyle.setFont(font);
        sheet.autoSizeColumn(0);
        for (int row = startRow; row <= endRow; row++) {
            Row currentRow = sheet.getRow(row);
            for (int col = startColumn; col <= endColumn; col++) {
                Cell cell = currentRow.getCell(col);
                String cellValue = cell.getStringCellValue();
                cell.setCellStyle(cellStyle);
                String v = addReturnAfterEachCharacter(cellValue);
                cell.setCellValue(v);

            }
        }

    }
    private static String addReturnAfterEachCharacter(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            stringBuilder.append(text.charAt(i));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    public static void setWrapText(Workbook workbook, int returnRowMaxLength, int startRow, int endRow, int startColumn, int endColumn, boolean isBoldFont){
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //设置自动换行
        cellStyle.setWrapText(true);
        Font font = workbook.createFont();
        // 设置字体为宋体
        font.setFontName("宋体");
        font.setBold(isBoldFont);
        // 设置字体大小为9号
        font.setFontHeightInPoints((short) 9);
        cellStyle.setFont(font);
        sheet.autoSizeColumn(0);
        for (int row = startRow; row <= endRow; row++) {
            Row currentRow = sheet.getRow(row);
            for (int col = startColumn; col <= endColumn; col++) {
                Cell cell = currentRow.getCell(col);
                if (cell == null) {
                    cell = currentRow.createCell(col);
                }
                cell.setCellStyle(cellStyle);
                String cellValue = cell.getStringCellValue();
                returnRowMaxLength = getReturnRowMaxLengthForColumn(sheet, startColumn);
                if (Objects.nonNull(cellValue) && cellValue.length() > returnRowMaxLength) {
                    //当字符数大于RowMaxLength的时候，长度除以RowMaxLength+1 就是倍数，默认高度乘倍数即可计算出高度
                    int foldRowNum = getReturnRowNum(cellValue, returnRowMaxLength);
                    currentRow.setHeightInPoints((short) (15 * foldRowNum));
                }

            }
        }

    }
    /**
     * 合并多行范围的单元格
     * @param returnRowMaxLength
     * @param startRow
     * @param endRow
     * @param startColumn
     * @param endColumn
     */
    public static void mergeCellsInColumnRange(Workbook workbook, int returnRowMaxLength, int startRow, int endRow, int startColumn, int endColumn) {
        // 获取要操作的Sheet对象
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        // 设置字体为宋体
        font.setFontName("宋体");
        // 设置字体大小为9号
        font.setFontHeightInPoints((short) 9);
        cellStyle.setFont(font);
        sheet.autoSizeColumn(0);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        //设置自动换行
        cellStyle.setWrapText(true);

        for (int row = startRow; row <= endRow; row++) {
            CellRangeAddress cellRangeAddress = new CellRangeAddress(row, row, startColumn, endColumn);
            sheet.addMergedRegion(cellRangeAddress);
            RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
            Row currentRow = sheet.getRow(row);
            // 给合并的区域设置框线
            for (int col = startColumn; col <= endColumn; col++) {

                Cell cell = currentRow.getCell(col);
                if (cell == null) {
                    cell = currentRow.createCell(col);
                }
                cell.setCellStyle(cellStyle);
                String cellValue = cell.getStringCellValue();
                returnRowMaxLength = getReturnRowMaxLengthForRegion(sheet, cellRangeAddress);
                if (Objects.nonNull(cellValue)&&cellValue.length() > returnRowMaxLength ){
                    //当字符数大于RowMaxLength的时候，长度除以RowMaxLength+1 就是倍数，默认高度乘倍数即可计算出高度
//                    int foldRowNum = (cellValue.length() / returnRowMaxLength) + 1;
                    int foldRowNum = getReturnRowNum(cellValue, returnRowMaxLength);
                    currentRow.setHeightInPoints((short) (15 * foldRowNum));
                }

            }

        }

    }

    private static int getReturnRowNum(String text,int maxReturnLength) {
        // 按照换行符分割字符串，得到字符串数组
        String[] lines = text.split("\n");
        //换行数
        int returnLen = lines.length;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length()>maxReturnLength){
                returnLen += line.length()/maxReturnLength;
            }
        }
        return returnLen+1;
    }

    /**
     * 设置列宽
     * @param sheet
     * @param columnIndex
     * @param columnWidth
     */
    public static void setColumnWidth(Sheet sheet, int columnIndex, int columnWidth){
        sheet.setColumnWidth(columnIndex, columnWidth * 256);
    }

    /**
     * 获取某合并区域最大可容纳字符数
     * @param sheet
     * @param cellRangeAddress
     * @return
     */
    private static int getReturnRowMaxLengthForRegion(Sheet sheet, CellRangeAddress cellRangeAddress) {
        int returnRowMaxLength;
        int firstColumnIndex = cellRangeAddress.getFirstColumn();
        int lastColumnIndex = cellRangeAddress.getLastColumn();
        int columnWidth = sheet.getColumnWidth(firstColumnIndex);
        // 如果合并区域跨多列，则获取第一个单元格的宽度，并累加后续列的宽度
        for (int i = firstColumnIndex + 1; i <= lastColumnIndex; i++) {
            columnWidth += sheet.getColumnWidth(i);
        }

        returnRowMaxLength = (int)(columnWidth*0.6)/256;
        return returnRowMaxLength;
    }

    private static int getReturnRowMaxLengthForColumn(Sheet sheet, int columnIndex) {
        int returnRowMaxLength;
        int columnWidth = sheet.getColumnWidth(columnIndex);
        // 如果合并区域跨多列，则获取第一个单元格的宽度，并累加后续列的宽度
        returnRowMaxLength = (int)(columnWidth*0.6)/256;
        return returnRowMaxLength;
    }

    public static byte[] convert(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteOutput.write(buffer, 0, bytesRead);
        }

        byteOutput.close();
        return byteOutput.toByteArray();
    }

    public static CellRangeAddress findMergeRegions(Sheet sheet, String searchValue){
        int startRow = 1;
        int endRow = 3;

        CellReference cellRef = searchCellWithMergedRegion(sheet, searchValue, startRow, endRow);
        CellRangeAddress mergedRegion = null;
        if (cellRef != null) {
            int rowIndex = cellRef.getRow();
            int columnIndex = cellRef.getCol();
            System.out.println("找到匹配的值 \"" + searchValue + "\"，位于行 " + (rowIndex + 1) + "，列 " + (columnIndex + 1));

            mergedRegion = getMergedRegion(sheet, rowIndex, columnIndex);
            if (mergedRegion != null) {
                int firstRow = mergedRegion.getFirstRow();
                int lastRow = mergedRegion.getLastRow();
                int firstColumn = mergedRegion.getFirstColumn();
                int lastColumn = mergedRegion.getLastColumn();
                System.out.println("合并区域范围：行 " + (firstRow + 1) + " 到 " + (lastRow + 1) + "，列 " + (firstColumn + 1) + " 到 " + (lastColumn + 1));
            } else {
                System.out.println("该单元格未合并");
            }

            return  mergedRegion;
        } else {
            System.out.println("未找到匹配的值 \"" + searchValue + "\"");
        }

        return mergedRegion;
    }


    //删除合并区域
    private List<CellRangeAddress> removeMergedRegions(Sheet sheet){
        List<CellRangeAddress> mergeRegions = sheet.getMergedRegions();
        for (int i = 0; i < mergeRegions.size(); i++) {
            sheet.removeMergedRegion(i);
        }
        return mergeRegions;
    }


    private static CellReference searchCellWithMergedRegion(Sheet sheet, String searchValue, int startRow, int endRow) {
        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    String cellValue = cell.getStringCellValue();
                    if (searchValue.equals(cellValue)) {
                        return new CellReference(rowIndex, cell.getColumnIndex());
                    }
                }
            }
        }
        return null;
    }

    private static CellRangeAddress getMergedRegion(Sheet sheet, int rowIndex, int columnIndex) {
        for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            if (mergedRegion.isInRange(rowIndex, columnIndex)) {
                return mergedRegion;
            }
        }
        return null;
    }
}
