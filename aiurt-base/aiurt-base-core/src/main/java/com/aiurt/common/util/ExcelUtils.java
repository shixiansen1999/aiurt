package com.aiurt.common.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * 处理excel的工具类
 * @author 华宜威
 * @date 2023-08-22 14:49:49
 */
public class ExcelUtils {

    /**
     * 给excel单元格填充下拉列表
     * @param workbook excel对应的 workbook
     * @param sheetIndex 开始列
     * @param firstRow 填充下拉列表开始的行索引（默认填充到最后一行）
     * @param firstCol 填充下拉列表开始的列索引
     * @param lastCol 填充下拉列表结束的列索引
     * @param dataList 下拉列表数据
     */
    public static void selectList(Workbook workbook, Integer sheetIndex, int firstRow,  int firstCol, int lastCol, List<String> dataList) {
        // 没有下拉列表数据的话，直接返回
        if (CollUtil.isEmpty(dataList)){
            return;
        }
        // 下拉列表数据要填充哪个sheet
        Sheet sheet = workbook.getSheetAt(sheetIndex);

        // 下拉列表数据填充的范围
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(firstRow, 65535, firstCol, lastCol);

        // 填充下拉列表
        // 从sheet中创建数据验证助手
        XSSFDataValidationHelper dataValidationHelper = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
        // 创建一个显式列表的数据验证约束
        DataValidationConstraint listConstraint = dataValidationHelper.createExplicitListConstraint(dataList.toArray(new String[dataList.size()]));
        // 将填充返回和列表的数据验证约束关联起来
        DataValidation validation = dataValidationHelper.createValidation(listConstraint, cellRangeAddressList);
        // 对sheet页生效
        sheet.addValidationData(validation);
    }

    /**
     * excel导入数据时的返回格式
     *
     * @param errorLines    错误条数
     * @param successLines  成功(无错误)条数
     * @param isSucceed     是否成功
     * @param failReportUrl 错误报告下载地址
     * @param message       提示信息
     * @return Result<JSONObject>对象
     */
    public static Result<JSONObject> importReturnRes(int errorLines, int successLines, boolean isSucceed, String failReportUrl, String message) {
        JSONObject result = new JSONObject(5);
        result.put("isSucceed", isSucceed);
        result.put("errorCount", errorLines);
        result.put("successCount", successLines);
        result.put("failReportUrl", failReportUrl);
        result.put("totalCount", successLines + errorLines);
        Result<JSONObject> res = Result.ok(result);
        res.setMessage(message);
        res.setCode(200);
        return res;
    }
}
