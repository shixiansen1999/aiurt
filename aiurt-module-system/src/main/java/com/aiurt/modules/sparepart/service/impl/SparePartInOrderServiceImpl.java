package com.aiurt.modules.sparepart.service.impl;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.entity.SparePartStockNum;
import com.aiurt.modules.sparepart.entity.dto.SparePartInOrderImportExcelDTO;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMaterialMapper;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockNumMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.stock.entity.MaterialStockOutInRecord;
import com.aiurt.modules.stock.service.impl.MaterialStockOutInRecordServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.*;
import org.jeecg.common.util.SpringContextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private ISysParamAPI sysParamApi;
    @Autowired
    private IMaterialBaseService materialBaseService;
    @Autowired
    private ISparePartStockInfoService sparePartStockInfoService;
    @Autowired
    private SparePartStockNumMapper sparePartStockNumMapper;
    @Autowired
    private IMaterialRequisitionService materialRequisitionService;
    @Autowired
    private IMaterialRequisitionDetailService materialRequisitionDetailService;
    @Autowired
    private MaterialStockOutInRecordServiceImpl materialStockOutInRecordService;
    /**
     * 查询列表
     * @param page
     * @param sparePartInOrder
     * @return
     */
    @Override
    public List<SparePartInOrder> selectList(Page page, SparePartInOrder sparePartInOrder){
        //权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserDepartModel> departModels = sysBaseApi.getDepartByUserId(user.getId());
        if(!user.getRoleCodes().contains("admin")&&departModels.size()==0){
            return CollUtil.newArrayList();
        }
        if(!user.getRoleCodes().contains("admin")&&departModels.size()!=0){
            List<String> orgCodes = departModels.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
            sparePartInOrder.setOrgCodes(orgCodes);
        }
        if (ObjectUtil.isNotNull(sparePartInOrder.getEndTime())) {
            sparePartInOrder.setEndTime(DateUtil.endOfDay(sparePartInOrder.getEndTime()));
        }
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
        //同步入库记录到出入库记录表
        MaterialRequisition requisition = materialRequisitionService.getOne(new LambdaQueryWrapper<MaterialRequisition>()
                .eq(MaterialRequisition::getId, sparePartInOrder.getMaterialRequisitionId())
                .eq(MaterialRequisition::getDelFlag, CommonConstant.DEL_FLAG_0));
        MaterialStockOutInRecord record = new MaterialStockOutInRecord();
        BeanUtils.copyProperties(sparePartInOrder, record);
        if (ObjectUtil.isNotNull(requisition)) {
            record.setMaterialRequisitionType(requisition.getMaterialRequisitionType());
        }
        record.setIsOutIn(1);
        record.setOutInType(sparePartInOrder.getInType());
        materialStockOutInRecordService.save(record);
        // 2.回填申领单
        MaterialRequisitionDetail detail = materialRequisitionDetailService.getOne(new LambdaQueryWrapper<MaterialRequisitionDetail>()
                .eq(MaterialRequisitionDetail::getMaterialsCode, sparePartInOrder.getMaterialCode())
                .eq(MaterialRequisitionDetail::getMaterialRequisitionId, sparePartInOrder.getMaterialRequisitionId()));
        if(null!=detail){
            detail.setActualNum(sparePartInOrder.getNum());
            materialRequisitionDetailService.updateById(detail);
        }
        // 3.更新备件库存数据（原库存数+入库的数量）
        //查询要入库的物资，备件库存中是否存在
        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partInOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partInOrder.getWarehouseCode()));
        if(null!=sparePartStock){
            sparePartStock.setNum(sparePartStock.getNum()+partInOrder.getNum());
            sparePartStock.setAvailableNum(sparePartStock.getAvailableNum()+partInOrder.getNum());
            sparePartStockMapper.updateById(sparePartStock);
        }else{
            SparePartStock stock = new SparePartStock();
            stock.setMaterialCode(partInOrder.getMaterialCode());
            stock.setNum(partInOrder.getNum());
            stock.setWarehouseCode(partInOrder.getWarehouseCode());
            //存仓库组织机构的关联班组
            String orgCode = sysBaseApi.getDepartByWarehouseCode(partInOrder.getWarehouseCode());
            SysDepartModel departByOrgCode = sysBaseApi.getDepartByOrgCode(orgCode);
            stock.setOrgId(departByOrgCode.getId());
            stock.setSysOrgCode(departByOrgCode.getOrgCode());
            sparePartStockMapper.insert(stock);
        }

        LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper = new LambdaQueryWrapper<>();
        numLambdaQueryWrapper.eq(SparePartStockNum::getMaterialCode, partInOrder.getMaterialCode())
                .eq(SparePartStockNum::getWarehouseCode, partInOrder.getWarehouseCode())
                .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockNum stockNum = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper);
        if (ObjectUtil.isNull(stockNum)) {
            SparePartStockNum partStockNum = new SparePartStockNum();
            partStockNum.setMaterialCode(partInOrder.getMaterialCode());
            partStockNum.setWarehouseCode(partInOrder.getWarehouseCode());
            // 新增全新数量
            partStockNum.setNewNum(partInOrder.getNewNum());
            // 新增已使用数量
            partStockNum.setUsedNum(partInOrder.getUsedNum());
            // 新增待报损数量
            partStockNum.setScrapNum(partInOrder.getScrapNum());
            // 新增委外送修数量
            Integer reoutsourceRepairNum =  partInOrder.getReoutsourceRepairNum() != null ? partInOrder.getReoutsourceRepairNum() : 0;
            partStockNum.setOutsourceRepairNum(partInOrder.getOutsourceRepairNum() - reoutsourceRepairNum);
            sparePartStockNumMapper.insert(partStockNum);
        }else{
            // 更新全新数量
            stockNum.setNewNum(stockNum.getNewNum() + partInOrder.getNewNum());
            // 更新已使用数量
            stockNum.setUsedNum(stockNum.getUsedNum() + partInOrder.getUsedNum());
            // 更新待报损数量
            stockNum.setScrapNum(stockNum.getScrapNum() + partInOrder.getScrapNum());
            // 更新委外送修数量
            stockNum.setOutsourceRepairNum(stockNum.getOutsourceRepairNum() + partInOrder.getOutsourceRepairNum() - partInOrder.getReoutsourceRepairNum());
            sparePartStockNumMapper.updateById(stockNum);
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
        // 根据配置来获取模板文件的地址
        String filePath;
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.SPARE_PART_EXTRA_NUM);
        if ("1".equals(sysParamModel.getValue())){
            filePath = "/templates/sparePartInOrderTemplateSignal.xlsx";
        }else{
            filePath = "/templates/sparePartInOrderTemplate.xlsx";
        }

        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource(filePath);
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File(filePath);
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

                list = ExcelImportUtil.importExcel(file.getInputStream(), SparePartInOrderImportExcelDTO.class, params);
                // 空表格直接返回
                if (CollUtil.isEmpty(list)) {
                    return imporReturnRes(errorLines,successLines, false, failReportUrl, "暂无导入数据");
                }
                // 校验数据
                for (SparePartInOrderImportExcelDTO sparePartInOrderImportExcelDTO : list) {
                    // 记录校验得到的错误信息
                    StringBuilder errorMessage = new StringBuilder();

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
        String newNum = sparePartInOrderImportExcelDTO.getNewNum();
        String usedNum = sparePartInOrderImportExcelDTO.getUsedNum();
        String scrapNum = sparePartInOrderImportExcelDTO.getScrapNum();
        String outsourceRepairNum = sparePartInOrderImportExcelDTO.getOutsourceRepairNum();

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
        if (ObjectUtil.isEmpty(wareHouseName)) {
            errorMessage.append("保管仓库必须填写，");
        }else{
            List<SparePartStockInfo> stockInfoList = sparePartStockInfoService.lambdaQuery()
                    .eq(SparePartStockInfo::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                    .eq(SparePartStockInfo::getWarehouseName, wareHouseName).list();
//            String wareHouseCode = sparePartInOrderMapper.selectWareHouseCode(wareHouseName);
            if (CollUtil.isNotEmpty(stockInfoList)) {
                for (SparePartStockInfo sparePartStockInfo : stockInfoList) {
                    String warehouseCode = sparePartStockInfo.getWarehouseCode();
                    sparePartInOrder.setWarehouseCode(warehouseCode);
                }
            } else {
                errorMessage.append("保管仓库不存在，");
            }
        }
        //物资编码是否存在
        if (ObjectUtil.isEmpty(materialCode)) {
            errorMessage.append("物资编码必须填写，");
        }else{
            List<MaterialBase> materialBaseList = materialBaseService.lambdaQuery()
                    .eq(MaterialBase::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                    .eq(MaterialBase::getCode, materialCode).list();
            if (CollUtil.isNotEmpty(materialBaseList)) {
                for (MaterialBase materialBase : materialBaseList) {
                    String code = materialBase.getCode();
                    sparePartInOrder.setMaterialCode(code);

                    //获取与物资关联的系统
                    String systemCode = materialBase.getSystemCode();
                    //获取与物资关联的专业
                    String majorCode = materialBase.getMajorCode();
                    //导入填写的专业code
                    JSONObject major = sysBaseApi.getCsMajorByName(majorName);
                    String importMajorCode = major.getString("majorCode");
                    //导入填写的系统
                    JSONObject sName = sysBaseApi.getSystemName(major.getString("majorCode"), systemName);
                    String importSystemCode = sName.getString("systemCode");
                    //判断填写的专业和子系统是否和物资code绑定的专业和子系统一致
                    if(systemCode.equals(importSystemCode)){
                        sparePartInOrder.setSystemName(importSystemCode);
                    }else{
                        errorMessage.append("该子系统和物资不匹配，");
                    }
                    if(majorCode.equals(importMajorCode)){
                        sparePartInOrder.setMajorName(importMajorCode);
                    }else{
                        errorMessage.append("该专业和物资不匹配，");
                    }
                }
            } else {
                errorMessage.append("物资编码不存在，");
            }
        }
        //物资名称是否存在
        if (ObjectUtil.isEmpty(name)) {
            errorMessage.append("物资名称必须填写，");
        }else{
            List<MaterialBase> materialBases = materialBaseService.lambdaQuery()
                    .eq(MaterialBase::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                    .eq(MaterialBase::getName, name).list();
            if (CollUtil.isNotEmpty(materialBases)) {
                for (MaterialBase materialBase : materialBases) {
                    String mName = materialBase.getName();
                    sparePartInOrder.setName(mName);
                }
            } else {
                errorMessage.append("物资名称不存在，");
            }
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

        // 从实施配置中看是否需要验证全新数量、已使用数量、待报废数量、委外送修数量
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.SPARE_PART_EXTRA_NUM);
        if ("1".equals(sysParamModel.getValue())){
            // 入库数量，用来判断是否全新数量+已使用数量=入库数量
            boolean numIsNum = StrUtil.isNotEmpty(num) && NumberUtil.isInteger(num);
            // 全新数量
            boolean newNumIsNum = StrUtil.isNotEmpty(newNum) && NumberUtil.isInteger(newNum);
            // 已使用数量
            boolean usedNumIsNum = StrUtil.isNotEmpty(usedNum) && NumberUtil.isInteger(usedNum);
            // 待报废数量
            boolean scrapNumIsNum = StrUtil.isNotEmpty(scrapNum) && NumberUtil.isInteger(scrapNum);
            // 委外送修数量
            boolean outsourceRepairNumIsNum = StrUtil.isNotEmpty(outsourceRepairNum) && NumberUtil.isInteger(outsourceRepairNum);

            if (!newNumIsNum){
                errorMessage.append("全新数量要必填且为数字，");
            }else {
                sparePartInOrder.setNewNum(Integer.valueOf(newNum));
            }

            if (!usedNumIsNum){
                errorMessage.append("已使用数量要必填且为数字，");
            }else{
                sparePartInOrder.setUsedNum(Integer.valueOf(usedNum));
            }

            if (!scrapNumIsNum){
                errorMessage.append("待报废数量要必填且为数字，");
            }else{
                sparePartInOrder.setScrapNum(Integer.valueOf(scrapNum));
            }

            if (!outsourceRepairNumIsNum){
                errorMessage.append("委外送修数量要必填且为数字，");
            }else {
                sparePartInOrder.setOutsourceRepairNum(Integer.valueOf(outsourceRepairNum));
            }

            // 不加全新数量+已使用数量应当等于入库数量的限制
            // if (numIsNum && newNumIsNum && usedNumIsNum && (Integer.parseInt(newNum) + Integer.parseInt(usedNum) != Integer.parseInt(num))){
            //     errorMessage.append("全新数量+已使用数量应当等于入库数量，");
            // }
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
        result.put("successCount", successLines);
        result.put("failReportUrl", failReportUrl);
        int totalCount = successLines + errorLines;
        result.put("totalCount", totalCount);
        Result res = Result.ok(result);
        res.setMessage(message);
        res.setCode(200);
        return res;
    }

    private Result<?> getErrorExcel(int errorLines,int successLines, List<SparePartInOrderImportExcelDTO> list, String url, String type) throws IOException {
        // 根据配置来获取错误模板文件的地址
        String errorFilePath;
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.SPARE_PART_EXTRA_NUM);
        if ("1".equals(sysParamModel.getValue())){
            errorFilePath = "/templates/sparePartInOrderErrorSignal.xlsx";
        }else{
            errorFilePath = "/templates/sparePartInOrderError.xlsx";
        }

        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource(errorFilePath);
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp = new File(errorFilePath);
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
            lm.put("newNum", sparePartInOrderImportExcelDTO.getNewNum());
            lm.put("usedNum", sparePartInOrderImportExcelDTO.getUsedNum());
            lm.put("scrapNum", sparePartInOrderImportExcelDTO.getScrapNum());
            lm.put("outsourceRepairNum", sparePartInOrderImportExcelDTO.getOutsourceRepairNum());
            lm.put("errorReason", sparePartInOrderImportExcelDTO.getErrorReason());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        return errorMap;
    }

    @Override
    public SparePartInOrder queryByOrderCode(String orderCode) {
        SparePartInOrder inOrder = new SparePartInOrder();
        inOrder.setQueryOrderCode(orderCode);
        List<SparePartInOrder> list = sparePartInOrderMapper.readAll(inOrder);
        if (CollUtil.isEmpty(list)) {
            throw new AiurtBootException("未找到对应数据");
        }
        return list.get(0);
    }
}
