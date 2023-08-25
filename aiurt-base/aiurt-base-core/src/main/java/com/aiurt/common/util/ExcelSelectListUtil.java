package com.aiurt.common.util;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jeecg.common.system.vo.DictModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : sbx
 * @Classname : ExcelSelectListUtil
 * @Description : TODO
 * @Date : 2023/7/25 13:35
 */
public class ExcelSelectListUtil {
    /**
     * firstRow 開始行號 根据此项目，默认为3(下标0开始)
     * lastRow  根据此项目，默认为最大65535
     * firstCol 区域中第一个单元格的列号 (下标0开始)
     * lastCol 区域中最后一个单元格的列号
     * strings 下拉内容
     */
    public static void selectList(Workbook workbook, String name, int firstCol, int lastCol, List<DictModel> modelList) {
        if (CollectionUtil.isNotEmpty(modelList)) {
            Sheet sheet = workbook.getSheetAt(0);
            //将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
            int sheetTotal = workbook.getNumberOfSheets();
            List<String> collect = modelList.stream().map(DictModel::getText).collect(Collectors.toList());
            String hiddenSheetName = name + "_hiddenSheet";
            Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
            if (hiddenSheet == null) {
                hiddenSheet = workbook.createSheet(hiddenSheetName);
                //写入下拉数据到新的sheet页中
                for (int i = 0; i < collect.size(); i++) {
                    Row hiddenRow = hiddenSheet.createRow(i);
                    Cell hiddenCell = hiddenRow.createCell(0);
                    hiddenCell.setCellValue(collect.get(i));
                }
                workbook.setSheetHidden(sheetTotal, true);
            }

            // 下拉数据
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 65535, firstCol, lastCol);
            //  生成下拉框内容名称
            String strFormula = hiddenSheetName + "!$A$1:$A$65535";
            // 根据隐藏页面创建下拉列表
            XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
            DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
            //  对sheet页生效
            sheet.addValidationData(validation);
        }
    }
}
