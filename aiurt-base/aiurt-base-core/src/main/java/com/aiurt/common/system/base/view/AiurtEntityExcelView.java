package com.aiurt.common.system.base.view;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.system.base.entity.ExcelTemplateExportEntity;
import com.aiurt.common.util.oConvertUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
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
        String codedFileName = "临时模板";
        Workbook workbook = null;

        // 需要导出的字段
        String[] exportFields = null;
        Object exportFieldStr = model.get("exportFields");
        if (exportFieldStr != null && exportFieldStr != "") {
            exportFields = exportFieldStr.toString().split(",");
        }
        ExportParams entity = (ExportParams) model.get("params");
        // 实体
        Class pojoClass = (Class) model.get(NormalExcelConstants.CLASS);

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
        //
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
                list.add(build);
            }
        }

        // 创建 row
        //

        Row row = sheet.createRow(0);
        for (ExcelTemplateExportEntity exportEntity : list) {
            Cell cell = row.createCell(exportEntity.getIndex());
            RichTextString Rtext;

                Rtext = new XSSFRichTextString(exportEntity.getName());

            cell.setCellValue(Rtext);
            cell.setCellType(CellType.STRING);
            List<DictModel> modelList = exportEntity.getDictModelList();
            if (CollectionUtil.isNotEmpty(modelList)) {
                CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 65535, exportEntity.getIndex(), exportEntity.getIndex());
                //  生成下拉框内容
                //格式是HSSF
                //DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(strings);
                //HSSFDataValidation validation = new HSSFDataValidation(cellRangeAddressList, dvConstraint);
                //格式是XSSF
                List<String> collect = modelList.stream().map(DictModel::getText).collect(Collectors.toList());
                String[] nums = collect.toArray(new String[collect.size()]);

                XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(nums);
                XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
                DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
                //  对sheet页生效
                sheet.addValidationData(validation);
            }
        }
       // row.setHeight(10);

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
