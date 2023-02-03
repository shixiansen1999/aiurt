package com.aiurt.boot.materials.service.impl;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.EmergencyMaterialsCategoryModel;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsCategoryMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsCategoryServiceImpl extends ServiceImpl<EmergencyMaterialsCategoryMapper, EmergencyMaterialsCategory> implements IEmergencyMaterialsCategoryService {

    @Autowired
    private EmergencyMaterialsCategoryMapper emergencyMaterialsCategoryMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<EmergencyMaterialsCategory> treeFirst(List<EmergencyMaterialsCategory> list) {
        //这里的Menu是我自己的实体类，参数只需要菜单id和父id即可，其他元素可任意增添
        Map<String, EmergencyMaterialsCategory> map = new HashMap<>(50);
        for (EmergencyMaterialsCategory treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }


    /**
     * @param list
     * @param map
     * @return
     */
    private static List<EmergencyMaterialsCategory> addChildren(List<EmergencyMaterialsCategory> list, Map<String, EmergencyMaterialsCategory> map) {
        List<EmergencyMaterialsCategory> rootNodes = new ArrayList<>();
        for (EmergencyMaterialsCategory treeNode : list) {
            EmergencyMaterialsCategory parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                //当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<EmergencyMaterialsCategory>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }

    @Override
    public List<EmergencyMaterialsCategory> selectTreeList() {
        LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyMaterialsCategory::getDelFlag, 0);
        queryWrapper.orderByDesc(EmergencyMaterialsCategory::getSort);
        queryWrapper.orderByDesc(EmergencyMaterialsCategory::getCreateTime);
        List<EmergencyMaterialsCategory> emergencyMaterialsCategories = emergencyMaterialsCategoryMapper.selectList(queryWrapper);
        return treeFirst(emergencyMaterialsCategories);
    }

    @Override
    public void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/emCategory.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/emCategory.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);
        List<DictModel> categoryList = bean.queryTableDictItemsByCode("emergency_materials_category", "category_name", "category_code");
        ExcelSelectListUtil.selectList(workbook, "上级节点", 0, 0, categoryList);
        String fileName = "应急分类导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "应急分类导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        String tipMessage = null;
        String url = null;
        int errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                tipMessage = "导入失败，文件类型不对。";
                return imporReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<EmergencyMaterialsCategory> categoryList = new ArrayList<>();
                List<EmergencyMaterialsCategoryModel> list = ExcelImportUtil.importExcel(file.getInputStream(), EmergencyMaterialsCategoryModel.class, params);
                list = list.stream().filter(l -> l.getFatherName() != null || l.getCategoryCode() != null || l.getCategoryName() != null).collect(Collectors.toList());
                if (CollUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                Integer i = 1;
                for (EmergencyMaterialsCategoryModel model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        EmergencyMaterialsCategory category = new EmergencyMaterialsCategory();
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        examine(model, category, stringBuilder, list);
                        if (stringBuilder.length() > 0) {
                            // 截取字符
                            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                            model.setWrongReason(stringBuilder.toString());
                            errorLines++;
                        }
                        category.setSort(i);
                        i++;
                        categoryList.add(category);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                } else {
                    List<EmergencyMaterialsCategory> collect = categoryList.stream().filter(c -> c.getIsExitParent() == true).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(collect)){
                        this.saveBatch(collect);
                    }
                    List<EmergencyMaterialsCategory> materialsCategoryList = categoryList.stream().filter(c -> c.getIsExitParent() == false).collect(Collectors.toList());
                    List<EmergencyMaterialsCategory> materialsCategoryTree = materialsCategoryTree(materialsCategoryList);
                    saveTree(null,materialsCategoryTree);
                    successLines = list.size();
                    return imporReturnRes(errorLines, successLines, tipMessage, true, null);
                }

            } catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if (msg != null && msg.contains("Duplicate entry")) {
                    return Result.error("文件导入失败:有重复数据！");
                } else {
                    return Result.error("文件导入失败:" + e.getMessage());
                }
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return imporReturnRes(errorLines, successLines, tipMessage, true, null);
    }

    private void saveTree(String id, List<EmergencyMaterialsCategory> materialsCategoryTree) {
        for (EmergencyMaterialsCategory menu : materialsCategoryTree) {
            menu.setPid(id==null?"0":id);
            emergencyMaterialsCategoryMapper.insert(menu);
            if(CollUtil.isNotEmpty(menu.getChildren())){
                saveTree(menu.getId(),menu.getChildren());
            }
        }
    }

    private List<EmergencyMaterialsCategory> materialsCategoryTree(List<EmergencyMaterialsCategory> materialsCategoryList) {

        List<EmergencyMaterialsCategory> firstTree = materialsCategoryList.stream().filter(m -> m.getFatherName()==null).collect(Collectors.toList());
        List<EmergencyMaterialsCategory> menuTree = new ArrayList<>();
        for(EmergencyMaterialsCategory menuSample:firstTree)
        {

            EmergencyMaterialsCategory tree = buildChildTree(materialsCategoryList, menuSample);
            menuTree.add(tree);
        }
        return menuTree;

    }
    private  EmergencyMaterialsCategory buildChildTree(List<EmergencyMaterialsCategory> menuList, EmergencyMaterialsCategory menuSample) {
        List<EmergencyMaterialsCategory> childMenu = new ArrayList<>();
        for (EmergencyMaterialsCategory menu :menuList)
        {
            if(menuSample.getCategoryName().equals(menu.getFatherName()))
            {
                childMenu.add(menu);
                buildChildTree(menuList,menu);
            }
        }
        menuSample.setChildren(childMenu);
        return menuSample;
    }

    private Result<?> getErrorExcel(int errorLines, List<EmergencyMaterialsCategoryModel> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/emCategoryError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/emCategoryError.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<String, Object> errorMap = new HashMap<String, Object>(16);
        List<Map<String, String>> listMap = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            EmergencyMaterialsCategoryModel categoryModel = list.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("fatherName", categoryModel.getFatherName());
            lm.put("categoryCode", categoryModel.getCategoryCode());
            lm.put("categoryName", categoryModel.getCategoryName());
            lm.put("wrongReason", categoryModel.getWrongReason());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "应急分类信息导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imporReturnRes(errorLines, successLines, null, true, url);
    }

    private void examine(EmergencyMaterialsCategoryModel model, EmergencyMaterialsCategory category, StringBuilder stringBuilder, List<EmergencyMaterialsCategoryModel> list) {
        BeanUtils.copyProperties(model, category);
        if (ObjectUtil.isNotEmpty(model.getCategoryCode()) && ObjectUtil.isNotEmpty(model.getCategoryName())) {
            String regular = "^\\w+$";
            Pattern pattern = Pattern.compile(regular);
            Matcher matcher = pattern.matcher(model.getCategoryCode());
            if (!matcher.find()) {
                stringBuilder.append("编码未按要求填写，");
            }
            List<EmergencyMaterialsCategory> categoryCodeList = emergencyMaterialsCategoryMapper.selectList(new LambdaQueryWrapper<EmergencyMaterialsCategory>().eq(EmergencyMaterialsCategory::getCategoryCode, model.getCategoryCode()).eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (categoryCodeList.size() > 0) {
                stringBuilder.append("已添加相同的编码，");
            }
            List<EmergencyMaterialsCategoryModel> modeCodeList = list.stream().filter(l -> l.getCategoryCode() != null && !l.equals(model) && l.getCategoryCode().equals(model.getCategoryCode())).collect(Collectors.toList());
            if (modeCodeList.size() > 0) {
                stringBuilder.append("文件里有相同的编码，");
            }
            List<EmergencyMaterialsCategory> categoryNameList = emergencyMaterialsCategoryMapper.selectList(new LambdaQueryWrapper<EmergencyMaterialsCategory>().eq(EmergencyMaterialsCategory::getCategoryName, model.getCategoryName()).eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (categoryNameList.size() > 0) {
                stringBuilder.append("已添加相同的名称，");
            }
            List<EmergencyMaterialsCategoryModel> modeNameList = list.stream().filter(l -> l.getCategoryName() != null && !l.equals(model) && l.getCategoryName().equals(model.getCategoryName())).collect(Collectors.toList());
            if (modeNameList.size() > 0) {
                stringBuilder.append("文件里有相同的名称，");
            }
            List<EmergencyMaterialsCategoryModel> myselfList = list.stream().filter(l -> l.equals(model)).collect(Collectors.toList());
            if (myselfList.size() != 1) {
                stringBuilder.append("文件里有相同的数据，");
            }
            if (ObjectUtil.isNotEmpty(model.getFatherName())) {
                EmergencyMaterialsCategory categoryFatherName = emergencyMaterialsCategoryMapper.selectOne(new LambdaQueryWrapper<EmergencyMaterialsCategory>().eq(EmergencyMaterialsCategory::getCategoryName, model.getFatherName()).eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1"));
                List<EmergencyMaterialsCategoryModel> collect = list.stream().filter(l -> model.getFatherName().equals(l.getCategoryName())).collect(Collectors.toList());
                if (ObjectUtil.isEmpty(categoryFatherName)) {
                    if (collect.size() == 0) {
                        stringBuilder.append("上级节点不存在，");
                    }else {
                        category.setIsExitParent(false);
                    }
                } else {
                    if (categoryFatherName.getStatus() == 0) {
                        stringBuilder.append("该上级节点已被禁用，");
                    } else {
                        List<EmergencyMaterialsCategory> deptAll = emergencyMaterialsCategoryMapper.selectList(new LambdaQueryWrapper<EmergencyMaterialsCategory>().eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0));
                        Set<EmergencyMaterialsCategory> deptUpList = getDeptUpList(deptAll, category);
                        List<EmergencyMaterialsCategory> disabledList = deptUpList.stream().filter(e -> e.getStatus() == 0).collect(Collectors.toList());
                        if (disabledList.size() > 0) {
                            stringBuilder.append("该上级节点已被禁用，");
                        }

                    }
                    category.setPid(categoryFatherName.getId());
                    category.setIsExitParent(true);
                }
            } else {
                category.setPid("0");
                category.setIsExitParent(false);
            }
            category.setStatus(1);
        } else {
            if (ObjectUtil.isEmpty(model.getCategoryCode())) {
                stringBuilder.append("分类编码必填");
            }
            if (ObjectUtil.isEmpty(model.getCategoryName())) {
                stringBuilder.append("分类名称必填");
            }
        }
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            model.setWrongReason(stringBuilder.toString());
        }
    }

    public static Set<EmergencyMaterialsCategory> getDeptUpList(List<EmergencyMaterialsCategory> deptAll, EmergencyMaterialsCategory categoryFatherName) {
        if (ObjectUtil.isNotEmpty(categoryFatherName)) {
            Set<EmergencyMaterialsCategory> set = new HashSet<>();
            String parentId = categoryFatherName.getPid();
            List<EmergencyMaterialsCategory> parentDepts = deptAll.stream().filter(item -> item.getId().equals(parentId)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(parentDepts)) {
                EmergencyMaterialsCategory parentDept = parentDepts.get(0);
                set.add(parentDept);
                Set<EmergencyMaterialsCategory> deptUpTree = getDeptUpList(deptAll, parentDept);
                if (deptUpTree != null) {
                    set.addAll(deptUpTree);
                }
                return set;
            }
        }
        return null;
    }

    public static Result<?> imporReturnRes(int errorLines, int successLines, String tipMessage, boolean isType, String failReportUrl) throws IOException {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl", failReportUrl);
                Result res = Result.ok(result);
                res.setMessage("文件失败，数据有错误。");
                res.setCode(200);
                return res;
            } else {
                //是否成功
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", true);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                Result res = Result.ok(result);
                res.setMessage("文件导入成功！");
                res.setCode(200);
                return res;
            }
        } else {
            JSONObject result = new JSONObject(5);
            result.put("isSucceed", false);
            result.put("errorCount", errorLines);
            result.put("successCount", successLines);
            int totalCount = successLines + errorLines;
            result.put("totalCount", totalCount);
            Result res = Result.ok(result);
            res.setMessage(tipMessage);
            res.setCode(200);
            return res;
        }

    }

    public static final class ExcelSelectListUtil {
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
}
