package com.aiurt.common.system.base.view;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.aiurt.common.system.base.entity.ExcelTemplateExportEntity;
import com.aiurt.common.util.oConvertUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.MiniAbstractExcelView;
import org.jeecgframework.poi.util.PoiPublicUtil;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Controller("aiurtEntityExcelView")
public class AiurtEntityExcelView extends MiniAbstractExcelView {


    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Workbook workbook = null;

        // 需要导出的字段
        String[] exportFields = null;
        Object exportFieldStr = model.get(NormalExcelConstants.EXPORT_FIELDS);
        if (exportFieldStr != null && exportFieldStr != "") {
            exportFields = exportFieldStr.toString().split(",");
        }
        ExportParams entity = (ExportParams) model.get(NormalExcelConstants.PARAMS);
        // 实体
        Class pojoClass = (Class) model.get(NormalExcelConstants.CLASS);

        String remark = null;
        Object o = model.get("remark");
        if (Objects.nonNull(o)) {
            remark = (String) o;
        }

        String codedFileName = entity.getTitle();
        // 创建workbook

        workbook = new XSSFWorkbook();

        Sheet sheet = null;
        try {
            sheet  = workbook.createSheet(entity.getSheetName());
        } catch (Exception e) {
            sheet = workbook.createSheet();
        }

        // 获取所有的字段
        Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
        if (exportFields != null) {
            List<Field> list = new ArrayList(Arrays.asList(fileds));
            for(int i = 0; i < list.size(); ++i) {
                if (!Arrays.asList(exportFields).contains(((Field)list.get(i)).getName())) {
                    list.remove(i);
                    --i;
                }
            }
            if (list != null && list.size() > 0) {
                fileds = (Field[])list.toArray(new Field[0]);
            } else {
                fileds = null;
            }
        }
        // todo
        List<ExcelTemplateExportEntity> list = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < fileds.length; i++) {
            Field field = fileds[i];
            if (Objects.nonNull(field.getAnnotation(Excel.class))) {
                ExcelTemplateExportEntity build = ExcelTemplateExportEntity.builder().build();
                Excel excel = field.getAnnotation(Excel.class);
                build.setIndex(j);
                j = j+1;
                build.setName(excel.name());
               // 字典值，下拉处理
                Dict dict = field.getAnnotation(Dict.class);
                if (Objects.nonNull(dict)) {
                    String dicText = dict.dicText();
                    String dictTable = dict.dictTable();
                    String dicCode = dict.dicCode();
                    CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);
                    if (StrUtil.isBlank(dictTable)) {
                        List<DictModel> models = bean.queryDictItemsByCode(dict.dicCode());
                        build.setDictModelList(models);
                    }else {
                        dicText = oConvertUtils.getString(dicText, dicCode);
                        List<DictModel> dictModels = bean.queryTableDictItemsByCode(dictTable, dicText, dicCode);
                        build.setDictModelList(dictModels);
                    }
                }
                // 批注内容， 是否必填
                ExcelExtend excelExtend = field.getAnnotation(ExcelExtend.class);
                build.setIsRequired(false);
                if (Objects.nonNull(excelExtend)) {
                    build.setRemark(excelExtend.remark());
                    build.setIsRequired(excelExtend.isRequired());
                }
                list.add(build);
            }
        }

        // 样式
        CellStyle style = workbook.createCellStyle();
        // 左右居中
        style.setAlignment(HorizontalAlignment.CENTER);

        // 标题
        Row titleRow = sheet.createRow(0);
        Cell titleRowCell = titleRow.createCell(0);
        titleRowCell.setCellValue(codedFileName);
        titleRowCell.setCellStyle(style);
        // 合并单元格,合并后的内容取决于合并区域的左上角单元格的值
        CellRangeAddress region = new CellRangeAddress(0,0,0,list.size()-1);
        sheet.addMergedRegion(region);

        CellStyle wrapStyle = workbook.createCellStyle();
        // 左右居中
        wrapStyle.setWrapText(true);
        int rowListIndex = 1;
        if (StrUtil.isNotBlank(remark)) {
            // 说明
            Row remarkRow = sheet.createRow(1);
            Cell remarkRowCell = remarkRow.createCell(0);
            // 自动换行
            remarkRowCell.setCellValue(remark);
            remarkRow.setHeight((short) 1500);
            remarkRowCell.setCellStyle(wrapStyle);
            CellRangeAddress cellAddresses = new CellRangeAddress(1, 1, 0, list.size() - 1);
            sheet.addMergedRegion(cellAddresses);
            rowListIndex = 2;
        }

        Row row = sheet.createRow(rowListIndex);
        for (ExcelTemplateExportEntity exportEntity : list) {
            Cell cell = row.createCell(exportEntity.getIndex());
            String name = exportEntity.getName();
            int length = name.getBytes().length;
            RichTextString Rtext = new XSSFRichTextString(name);

            sheet.setColumnWidth(exportEntity.getIndex(), length*256);

            cell.setCellValue(Rtext);
           // cell.getCellStyle.set(CellType.STRING);
            List<DictModel> modelList = exportEntity.getDictModelList();
            if (CollectionUtil.isNotEmpty(modelList)) {
                //将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
                int sheetTotal = workbook.getNumberOfSheets();
                String hiddenSheetName = name + "_hiddenSheet";
                List<String> collect = modelList.stream().map(DictModel::getText).collect(Collectors.toList());
                Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
                if (hiddenSheet == null) {
                    hiddenSheet = workbook.createSheet(hiddenSheetName);
                    //写入下拉数据到新的sheet页中
                    for (int i = 0; i < collect.size(); i++) {
                        Row hiddenRow = hiddenSheet.createRow(i);
                        Cell  hiddenCell = hiddenRow.createCell(0);
                        hiddenCell.setCellValue(collect.get(i));
                    }
                    workbook.setSheetHidden(sheetTotal, true);
                }

                // 下拉数据
                CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(rowListIndex+1, 65535, exportEntity.getIndex(), exportEntity.getIndex());
                //  生成下拉框内容名称
                String strFormula = hiddenSheetName + "!$A$1:$A$65535";
                // 根据隐藏页面创建下拉列表
                XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
                XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
                DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
                //  对sheet页生效
                sheet.addValidationData(validation);
            }

            String rk = exportEntity.getRemark();
            if (StrUtil.isNotBlank(rk)) {
                Drawing draw = sheet.createDrawingPatriarch();
                Comment comment = draw.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, rowListIndex, exportEntity.getIndex(), 9, 7));
                comment.setString(new XSSFRichTextString(rk));//设置批注内容
                cell.setCellComment(comment);
            }
            // 批注， 必填处理
            if (exportEntity.getIsRequired()) {
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex()); // 背景色
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(cellStyle);
            }
        }

        if (model.containsKey("fileName")) {
            codedFileName = (String)model.get("fileName");
        }

        if (workbook instanceof HSSFWorkbook) {
            codedFileName = codedFileName + ".xls";
        } else {
            codedFileName = codedFileName + ".xlsx";
        }

        if (this.isIE(request)) {
            codedFileName = URLEncoder.encode(codedFileName, "UTF8");
        } else {
            codedFileName = new String(codedFileName.getBytes("UTF-8"), "ISO-8859-1");
        }

        response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }
}
