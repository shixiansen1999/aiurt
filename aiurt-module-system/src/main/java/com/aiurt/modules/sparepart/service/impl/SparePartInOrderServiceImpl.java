package com.aiurt.modules.sparepart.service.impl;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.service.impl.InspectionCodeServiceImpl;
import com.aiurt.boot.strategy.dto.*;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartInOrderImportExcelDTO;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMaterialMapper;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Service
public class SparePartInOrderServiceImpl extends ServiceImpl<SparePartInOrderMapper, SparePartInOrder> implements ISparePartInOrderService {
    @Autowired
    private SparePartInOrderMapper sparePartInOrderMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartApplyMaterialMapper sparePartApplyMaterialMapper;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    /**
     * 查询列表
     * @param page
     * @param sparePartInOrder
     * @return
     */
    @Override
    public List<SparePartInOrder> selectList(Page page, SparePartInOrder sparePartInOrder){
         return sparePartInOrderMapper.readAll(page,sparePartInOrder);
    }

    /**
     * 确认
     * @param sparePartInOrder
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirm(SparePartInOrder sparePartInOrder){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartInOrder partInOrder = getById(sparePartInOrder.getId());
        // 1.更新当前表状态为已确认
        partInOrder.setConfirmId(user.getUsername());
        partInOrder.setConfirmTime(new Date());
        partInOrder.setConfirmStatus(sparePartInOrder.getConfirmStatus());
        sparePartInOrderMapper.updateById(partInOrder);
        // 2.回填申领单
        SparePartApplyMaterial material = sparePartApplyMaterialMapper.selectOne(new LambdaQueryWrapper<SparePartApplyMaterial>().eq(SparePartApplyMaterial::getMaterialCode,sparePartInOrder.getMaterialCode()).eq(SparePartApplyMaterial::getApplyCode,sparePartInOrder.getApplyCode()));
        if(null!=material){
            material.setActualNum(sparePartInOrder.getNum());
            sparePartApplyMaterialMapper.updateById(material);
        }
        // 3.更新备件库存数据（原库存数+入库的数量）
        //查询要入库的物资，备件库存中是否存在
        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partInOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partInOrder.getWarehouseCode()));
        if(null!=sparePartStock){
            sparePartStock.setNum(sparePartStock.getNum()+partInOrder.getNum());
            sparePartStockMapper.updateById(sparePartStock);
        }else{
            SparePartStock stock = new SparePartStock();
            stock.setMaterialCode(partInOrder.getMaterialCode());
            stock.setNum(partInOrder.getNum());
            stock.setWarehouseCode(partInOrder.getWarehouseCode());
            stock.setOrgId(user.getOrgId());
            stock.setSysOrgCode(user.getOrgCode());
            sparePartStockMapper.insert(stock);
        }
    }
    /**
     * 修改
     *
     * @param sparePartInOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartInOrder sparePartInOrder) {
        confirm(sparePartInOrder);
        return Result.OK("操作成功！");
    }
    /**
     * 批量入库
     *
     * @param sparePartInOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchStorage(List<SparePartInOrder>  sparePartInOrder) {
        //查询状态为“已确认”的数据数量
        Long confirmedNum = sparePartInOrder.stream().filter(order -> ("1").equals(order.getStatus())).count();
        if(sparePartInOrder.size() == confirmedNum){
            return Result.error("勾选备件已入库，不用重复操作！");
        }
        //查询状态为“待确认”的数据
        sparePartInOrder.stream().filter(order -> ("0").equals(order.getStatus())).forEach(order -> {
            confirm(order);
        });
        return Result.OK("操作成功！");
    }


    @Override
    public void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/sparePartInOrderTemplate.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/sparePartInOrderTemplate.xlsx");
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
        List<DictModel> majorModels = bean.queryTableDictItemsByCode("cs_major", "major_name", "major_code");
        SparePartInOrderServiceImpl.ExcelSelectListUtil.selectList(workbook, "专业", 0, 0, majorModels);
        List<DictModel> subsystemModels = bean.queryTableDictItemsByCode("cs_subsystem", "system_name", "system_code");
        SparePartInOrderServiceImpl.ExcelSelectListUtil.selectList(workbook, "子系统", 1, 1, subsystemModels);
        List<DictModel> wareHouseModels = bean.queryTableDictItemsByCode("spare_part_stock_info", "warehouse_name", "warehouse_code");
        SparePartInOrderServiceImpl.ExcelSelectListUtil.selectList(workbook, "保管仓库", 2, 2, wareHouseModels);

        String fileName = "备件入库导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "备件入库导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * 备件入库导入数据
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException{
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        // 失败条数
        Integer errorLines = 0;
        //成功条数
        Integer successLines = 0;
        // 失败导出的excel下载地址
        String failReportUrl = "";

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();

            // 判断是否xls、xlsx两种类型的文件，不是则直接返回
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines,successLines, false, failReportUrl, "文件导入失败，文件类型不对");
            }

            // 设置excel参数
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);

            // 需要保存的数据
            List<SparePartInOrder> saveData = CollUtil.newArrayList();
            // excel表格数据
            List<SparePartInOrderImportExcelDTO> list = null;
            try {
                // 记录校验得到的错误信息
                StringBuilder errorMessage = new StringBuilder();

                list = ExcelImportUtil.importExcel(file.getInputStream(), SparePartInOrderImportExcelDTO.class, params);
                // 空表格直接返回
                if (CollUtil.isEmpty(list)) {
                    return imporReturnRes(errorLines,successLines, false, failReportUrl, "暂无导入数据");
                }
                // 校验数据
                for (SparePartInOrderImportExcelDTO sparePartInOrderImportExcelDTO : list) {
                    SparePartInOrder sparePartInOrder = new SparePartInOrder();
                    // 校验备件入库
                    this.checkData(errorMessage, sparePartInOrderImportExcelDTO, sparePartInOrder);
                    //如果填写有错误
                    if (errorMessage.length() > 0) {
                        if (errorMessage.length() > 0) {
                            errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                            sparePartInOrderImportExcelDTO.setErrorReason(errorMessage.toString());
                        }
                        errorLines++;
                    } else {
                        saveData.add(sparePartInOrder);
                        successLines++;
                    }
                }

                // 存在错误，错误报告下载
                if (errorLines > 0) {
                    return getErrorExcel(errorLines,successLines, list, failReportUrl, type);
                }
                // 保存到系统
                if (CollUtil.isNotEmpty(saveData)) {
                    for (SparePartInOrder saveDatum : saveData) {
                        saveDatum.setConfirmStatus("0");
                        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                        String orgId = loginUser.getOrgId();
                        saveDatum.setOrgId(orgId);
                        sparePartInOrderMapper.insert(saveDatum);
                    }
                    return imporReturnRes(errorLines,successLines, true, failReportUrl, "文件导入成功");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imporReturnRes(errorLines,successLines, false, failReportUrl, "暂无导入数据");
    }

    /**
     * 校验excel数据
     *
     * @param errorMessage                错误信息
     * @param sparePartInOrderImportExcelDTO excel数据
     * @param sparePartInOrder       转换成要保存的实体数据
     */
    private void checkData(StringBuilder errorMessage, SparePartInOrderImportExcelDTO sparePartInOrderImportExcelDTO, SparePartInOrder sparePartInOrder) {
        // 空数据不处理
        if (ObjectUtil.isEmpty(sparePartInOrderImportExcelDTO)) {
            return;
        }
        // 备件入库必填校验及数据校验
        checkValidity(errorMessage, sparePartInOrderImportExcelDTO, sparePartInOrder);
    }

    /**
     * 检验检修策略正确性
     *
     * @param errorMessage
     * @param sparePartInOrderImportExcelDTO
     */
    private void checkValidity(StringBuilder errorMessage, SparePartInOrderImportExcelDTO sparePartInOrderImportExcelDTO, SparePartInOrder sparePartInOrder) {
        String majorName = sparePartInOrderImportExcelDTO.getMajorName();
        String systemName = sparePartInOrderImportExcelDTO.getSystemName();
        String wareHouseName = sparePartInOrderImportExcelDTO.getWarehouseName();
        String materialCode = sparePartInOrderImportExcelDTO.getMaterialCode();
        String name = sparePartInOrderImportExcelDTO.getName();
        String num = sparePartInOrderImportExcelDTO.getNum();

        // 专业和子系统是否在系统中存在
        if (ObjectUtil.isNotEmpty(majorName)) {
            JSONObject major = sysBaseApi.getCsMajorByName(majorName);
            if (ObjectUtil.isNotEmpty(major)) {
                sparePartInOrder.setMajorName(major.getString("majorCode"));
                if (ObjectUtil.isNotEmpty(systemName)) {
                    JSONObject sName = sysBaseApi.getSystemName(major.getString("majorCode"), systemName);
                    if (ObjectUtil.isNotEmpty(sName)) {
                        sparePartInOrder.setSystemName(sName.getString("systemCode"));
                    } else {
                        errorMessage.append("系统不存在该专业下的子系统，");
                    }
                }
            }
            else{
                errorMessage.append("系统不存在该专业，");
            }
        }else{
            errorMessage.append("专业必须填写，");
        }

        //保管仓库是否存在
        if (ObjectUtil.isNotEmpty(wareHouseName)) {
            String wareHouseCode = sparePartInOrderMapper.selectWareHouseCode(wareHouseName);
            if (ObjectUtil.isNotEmpty(wareHouseCode)) {
                sparePartInOrder.setWarehouseCode(wareHouseCode);
            } else {
                errorMessage.append("保管仓库不存在，");
            }
        }else{
            errorMessage.append("保管仓库必须填写，");
        }
        //物资编码是否存在
        if (ObjectUtil.isNotEmpty(materialCode)) {
            String materialName =sparePartInOrderMapper.selectMaterialName(materialCode);
            if (ObjectUtil.isNotEmpty(materialName)) {
                sparePartInOrder.setMaterialCode(materialCode);
            } else {
                errorMessage.append("物资编码不存在，");
            }
        }else{
            errorMessage.append("物资编码必须填写，");
        }
        //物资名称是否存在
        if (ObjectUtil.isNotEmpty(name)) {
            String materialName = null;
            if(ObjectUtil.isNotEmpty(materialCode)){
                materialName=sparePartInOrderMapper.selectMaterialName(materialCode);
            }
            if (ObjectUtil.isNotEmpty(materialName)) {
                sparePartInOrder.setName(materialName);
            } else {
                errorMessage.append("物资名称不存在，");
            }
        }else{
            errorMessage.append("物资名称必须填写，");
        }
        //入库数量是否为数字
        if (ObjectUtil.isNotEmpty(num)) {
            String regular = "^[0-9]*$";
            Pattern pattern = Pattern.compile(regular);
            Matcher matcher = pattern.matcher(num);
            if (matcher.find()) {
                sparePartInOrder.setNum(Integer.valueOf(num));
            } else {
                errorMessage.append("入库数量(填写必须是数字)，");
            }
        }else{
            errorMessage.append("入库数量必须填写，");
        }

    }


    /**
     * 检修策略导入统一返回格式
     *
     * @param errorLines    错误条数
     * @param isSucceed     是否成功
     * @param failReportUrl 错误报告下载地址
     * @param message       提示信息
     * @return
     */
    public static Result<?> imporReturnRes(int errorLines,int successLines, boolean isSucceed, String failReportUrl, String message) {
        JSONObject result = new JSONObject(5);
        result.put("isSucceed", isSucceed);
        result.put("errorCount", errorLines);
        result.put("successLines", successLines);
        result.put("failReportUrl", failReportUrl);
        Result res = Result.ok(result);
        res.setMessage(message);
        res.setCode(200);
        return res;
    }

    private Result<?> getErrorExcel(int errorLines,int successLines, List<SparePartInOrderImportExcelDTO> list, String url, String type) throws IOException {
        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/sparePartInOrderError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp = new File("/templates/sparePartInOrderError.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);

            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);

            // 封装数据
            Map<String, Object> errorMap = handleData(list);

            // 将数据填入表格
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);


            String fileName = "备件入库数据导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imporReturnRes(errorLines,successLines, false, url, "文件导入失败，数据有错误");
    }

    @NotNull
    private Map<String, Object> handleData(List<SparePartInOrderImportExcelDTO> list) {
        Map<String, Object> errorMap = CollUtil.newHashMap();
        List<Map<String, Object>> listMap = CollUtil.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> lm = CollUtil.newHashMap();
            SparePartInOrderImportExcelDTO sparePartInOrderImportExcelDTO = list.get(i);
            if (ObjectUtil.isEmpty(sparePartInOrderImportExcelDTO)) {
                continue;
            }
            //错误报告获取信息
            lm.put("majorName", sparePartInOrderImportExcelDTO.getMajorName());
            lm.put("systemName", sparePartInOrderImportExcelDTO.getSystemName());
            lm.put("warehouseName", sparePartInOrderImportExcelDTO.getWarehouseName());
            lm.put("materialCode", sparePartInOrderImportExcelDTO.getMaterialCode());
            lm.put("name", sparePartInOrderImportExcelDTO.getName());
            lm.put("num", sparePartInOrderImportExcelDTO.getNum());
            lm.put("errorReason", sparePartInOrderImportExcelDTO.getErrorReason());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        return errorMap;
    }
}
