package com.aiurt.modules.faultknowledgebase.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.faultanalysisreport.constants.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultknowledgebase.dto.DeviceAssemblyDTO;
import com.aiurt.modules.faultknowledgebase.dto.FaultKnowledgeBaseModel;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Service
public class FaultKnowledgeBaseServiceImpl extends ServiceImpl<FaultKnowledgeBaseMapper, FaultKnowledgeBase> implements IFaultKnowledgeBaseService, IFlowableBaseUpdateStatusService {

    @Autowired
    private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
    @Autowired
    private FaultAnalysisReportMapper faultAnalysisReportMapper;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Autowired
    private FaultMapper faultMapper;

    @Autowired
    private FlowBaseApi flowBaseApi;
    @Override
    public IPage<FaultKnowledgeBase> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        LambdaQueryWrapper<FaultKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();
        List<FaultKnowledgeBase> bases = faultKnowledgeBaseMapper.selectList(queryWrapper.eq(FaultKnowledgeBase::getDelFlag, "0"));
        List<String> ids = bases.stream().map(FaultKnowledgeBase::getId).distinct().collect(Collectors.toList());
        List<String> rolesByUsername = sysBaseApi.getRolesByUsername(sysUser.getUsername());
//        //根据用户角色是否显示未通过的知识库
//        if (!rolesByUsername.contains(RoleConstant.ADMIN)&&!rolesByUsername.contains(RoleConstant.FOREMAN)&&!rolesByUsername.contains(RoleConstant.MAJOR_PEOPLE)) {
//            faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
//        }
//        //工班长只能看到审核通过的和自己创建的未审核通过的
//        if (rolesByUsername.size()==1 && rolesByUsername.contains(RoleConstant.FOREMAN)) {
//            faultKnowledgeBase.setCreateBy(sysUser.getUsername());
//        }
        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        String id = faultKnowledgeBase.getId();
        //根据id条件查询时，jeecg前端会传一个id结尾带逗号的id，所以先去掉结尾id
        if (StringUtils.isNotBlank(id)) {
            String substring = id.substring(0, id.length() - 1);
            faultKnowledgeBase.setId(substring);
        }

        List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.readAll2(page, faultKnowledgeBase,ids,sysUser.getUsername());
        //解决不是审核人去除审核按钮
        if(CollUtil.isNotEmpty(faultKnowledgeBases)){
            for (FaultKnowledgeBase knowledgeBase : faultKnowledgeBases) {
                TaskInfoDTO taskInfoDTO = flowBaseApi.viewRuntimeTaskInfo(knowledgeBase.getProcessInstanceId(), knowledgeBase.getTaskId());
                List<ActOperationEntity> operationList = taskInfoDTO.getOperationList();
                //operationList为空，没有审核按钮
                if(CollUtil.isNotEmpty(operationList)){
                    knowledgeBase.setHaveButton(true);
                }else{
                    knowledgeBase.setHaveButton(false);
                }
            }
        }

        GlobalThreadLocal.setDataFilter(b);
        faultKnowledgeBases.forEach(f->{
            String faultCodes = f.getFaultCodes();
            if (StrUtil.isNotBlank(faultCodes)) {
                String[] split = faultCodes.split(",");
                List<String> list = Arrays.asList(split);
                f.setFaultCodeList(list);
            }
        });
        //正序
        String asc = "asc";
        if (asc.equals(faultKnowledgeBase.getOrder())) {
            List<FaultKnowledgeBase> reportList = faultKnowledgeBases.stream().sorted(Comparator.comparing(FaultKnowledgeBase::getCreateTime)).collect(Collectors.toList());
            return page.setRecords(reportList);
        }

        return page.setRecords(faultKnowledgeBases);
    }

    @Override
    public IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO) {
        List<FaultDTO> faults = faultMapper.getFault(page, faultDTO,null);
        if (CollUtil.isNotEmpty(faults)) {
            for (FaultDTO fault : faults) {
                LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(FaultKnowledgeBaseType::getCode, fault.getFaultPhenomenon());
                FaultKnowledgeBaseType faultKnowledgeBaseType = faultKnowledgeBaseTypeMapper.selectOne(queryWrapper);
                fault.setFaultPhenomenon(faultKnowledgeBaseType != null ? faultKnowledgeBaseType.getName() : null);
            }
        }
        return page.setRecords(faults);
    }

    @Override
    public Result<String> approval(String approvedRemark, Integer approvedResult, String id) {
        if ( getRole()) {return Result.error("没有权限");}
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultKnowledgeBase faultKnowledgeBase = new FaultKnowledgeBase();
        faultKnowledgeBase.setId(id);
        faultKnowledgeBase.setApprovedRemark(approvedRemark);
        faultKnowledgeBase.setApprovedResult(approvedResult);
        faultKnowledgeBase.setApprovedTime(new Date());
        faultKnowledgeBase.setApprovedUserName(sysUser.getUsername());
        if (approvedResult.equals(FaultConstant.NO_PASS)) {
            faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
        } else {
            faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
        }
        this.updateById(faultKnowledgeBase);
        return Result.OK("审批成功!");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(String id) {
        FaultKnowledgeBase byId = this.getById(id);
        if (ObjectUtil.isEmpty(byId)) {
            return Result.error("没找到对应实体");
        }
        //获取知识库被使用的次数
        int num = faultKnowledgeBaseMapper.getNum(id);
        if (num > 0) {
            return Result.error("该知识库已经被使用，不能删除");
        } else {
            byId.setDelFlag(1);
            this.updateById(byId);
        }
        return Result.OK("删除成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteBatch(List<String> ids) {
        for (String id : ids) {
            FaultKnowledgeBase byId = this.getById(id);
            if (ObjectUtil.isEmpty(byId)) {
                return Result.error("没找到对应实体");
            }
            //获取知识库被使用的次数
            int num = faultKnowledgeBaseMapper.getNum(id);
            if (num > 0) {
                return Result.error("所选知识库中有已经被使用的知识库，不能删除");
            } else {
                byId.setDelFlag(1);
                this.updateById(byId);
            }
        }
        return  Result.OK("批量删除成功!");
    }

    @Override
    public void exportTemplateXls(HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/knowledgeBase.xlsx");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp= new File("/templates/knowledgeBase.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
        Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

        CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);
        //知识库类别下拉框
        List<DictModel> dictModels = bean.queryTableDictItemsByCode("fault_knowledge_base_type", "name", "code");
        Map<String, DictModel> collect = dictModels.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect1 = collect.values().stream().collect(Collectors.toList());
        selectList(workbook, "知识库类别", 0, 0, collect1);

        //设备类型下拉框
        List<DictModel> dictModels1 = bean.queryTableDictItemsByCode("device_Type", "name", "code");
        Map<String, DictModel> collect2 = dictModels1.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect3 = collect2.values().stream().collect(Collectors.toList());
        selectList(workbook, "设备类型", 1, 1, collect3);

        //设备组件下拉框
        List<DictModel> dictModels2 = bean.queryTableDictItemsByCode("device_assembly", "material_name", "material_code");
        Map<String, DictModel> collect4 = dictModels2.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect5 = collect4.values().stream().collect(Collectors.toList());
        selectList(workbook, "设备组件", 2, 2, collect5);

        String fileName = "故障知识库导入模板.xlsx";

        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename="+"故障知识库导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
                tipMessage = "导入失败，文件类型错误！";
                return imporReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {

                List<FaultKnowledgeBase> faultKnowledgeBaseList = new ArrayList<>();

                List<FaultKnowledgeBaseModel> list = ExcelImportUtil.importExcel(file.getInputStream(), FaultKnowledgeBaseModel.class, params);
                Iterator<FaultKnowledgeBaseModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    FaultKnowledgeBaseModel model = iterator.next();
                    boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
                    if (b) {
                        iterator.remove();
                    }
                }
                if (CollectionUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
               //数据校验
                for (FaultKnowledgeBaseModel model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        FaultKnowledgeBase em = new FaultKnowledgeBase();
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        examine(model, em, stringBuilder, list);
                        if (stringBuilder.length() > 0) {
                            // 截取字符
                            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                            model.setDeviceMistake(stringBuilder.toString());
                            errorLines++;
                        }else{
                            faultKnowledgeBaseList.add(em);
                        }
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                } else {
                    successLines = list.size();
                    for (FaultKnowledgeBase material : faultKnowledgeBaseList) {
                        material.setDelFlag(0);
                        material.setApprovedResult(0);
                        material.setStatus(0);
                        faultKnowledgeBaseMapper.insert(material);
                    }
                    return imporReturnRes(errorLines, successLines, tipMessage, true, null);
                }

            }catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if (msg != null && msg.contains("Duplicate entry")) {
                    return Result.error("文件导入失败:有重复数据！");
                } else {
                    return Result.error("文件导入失败:" + e.getMessage());
                }
            }finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return imporReturnRes(errorLines, successLines, tipMessage, true, null);
    }


    private void examine(FaultKnowledgeBaseModel faultKnowledgeBaseModel, FaultKnowledgeBase faultKnowledgeBase, StringBuilder stringBuilder, List<FaultKnowledgeBaseModel> list) {
        BeanUtils.copyProperties(faultKnowledgeBaseModel, faultKnowledgeBase);

        if (StrUtil.isBlank(faultKnowledgeBaseModel.getKnowledgeBaseTypeName())) {
            stringBuilder.append("知识库类别必填，");
        }else {
            LambdaQueryWrapper<FaultKnowledgeBaseType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FaultKnowledgeBaseType::getName,faultKnowledgeBaseModel.getKnowledgeBaseTypeName())
                              .eq(FaultKnowledgeBaseType::getDelFlag,0);
            List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes = faultKnowledgeBaseTypeMapper.selectList(lambdaQueryWrapper);
            if (CollUtil.isNotEmpty(faultKnowledgeBaseTypes)){
                List<String> collect = faultKnowledgeBaseTypes.stream().map(FaultKnowledgeBaseType::getCode).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)){
                    String s = collect.get(0);
                    faultKnowledgeBase.setKnowledgeBaseTypeCode(s);
                }
            }else {
                stringBuilder.append("系统中不存在该知识库类别，");
            }
        }

        if (StrUtil.isBlank(faultKnowledgeBaseModel.getDeviceTypeName())){
            stringBuilder.append("设备类型名称必填，");
        }else {
            String deviceTypeName = faultKnowledgeBaseModel.getDeviceTypeName();
            List<DeviceType> deviceCodeByName = faultKnowledgeBaseMapper.getDeviceCodeByName(deviceTypeName);
            if(CollUtil.isNotEmpty(deviceCodeByName)){
                List<String> collect = deviceCodeByName.stream().map(DeviceType::getCode).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(collect)){
                    String deviceTypeCode = collect.get(0);
                    faultKnowledgeBase.setDeviceTypeCode(deviceTypeCode);
                }
            }else{
                stringBuilder.append("系统中不存在该设备类型，");
            }
        }

        if(StrUtil.isNotBlank(faultKnowledgeBaseModel.getMaterialName())){
            String materialName = faultKnowledgeBaseModel.getMaterialName();
            List<DeviceAssemblyDTO> deviceAssemblyCode = faultKnowledgeBaseMapper.getDeviceAssemblyCode(materialName);
            if(CollUtil.isNotEmpty(deviceAssemblyCode)){
                List<String> collect = deviceAssemblyCode.stream().map(DeviceAssemblyDTO::getMaterialCode).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(collect)){
                    String deviceAssCode = collect.get(0);
                    faultKnowledgeBase.setMaterialCode(deviceAssCode);
                }
            }else{
                stringBuilder.append("系统中不存在该设备组件，");
            }
        }

        if(StrUtil.isBlank(faultKnowledgeBaseModel.getFaultPhenomenon())){
            stringBuilder.append("故障现象必填，");
        }else{
            faultKnowledgeBase.setFaultPhenomenon(faultKnowledgeBaseModel.getFaultPhenomenon());
        }

        if(StrUtil.isBlank(faultKnowledgeBaseModel.getSolution())){
            stringBuilder.append("解决方案必填，");
        }else{
            faultKnowledgeBase.setSolution(faultKnowledgeBaseModel.getSolution());
        }
        faultKnowledgeBase.setFaultReason(faultKnowledgeBaseModel.getFaultReason());
        faultKnowledgeBase.setMethod(faultKnowledgeBaseModel.getMethod());
        faultKnowledgeBase.setTools(faultKnowledgeBaseModel.getTools());

        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            faultKnowledgeBaseModel.setDeviceMistake(stringBuilder.toString());
        }

    }

    private Result<?> getErrorExcel(int errorLines, List<FaultKnowledgeBaseModel> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/knowledgeBaseError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/knowledgeBaseError.xlsx");
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
            FaultKnowledgeBaseModel faultKnowledgeBaseModel = list.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("knowledgeBaseTypeName", faultKnowledgeBaseModel.getKnowledgeBaseTypeName());
            lm.put("deviceTypeName", faultKnowledgeBaseModel.getDeviceTypeName());
            lm.put("materialName", faultKnowledgeBaseModel.getMaterialName());
            lm.put("faultPhenomenon", faultKnowledgeBaseModel.getFaultPhenomenon());
            lm.put("faultReason", faultKnowledgeBaseModel.getFaultReason());
            lm.put("solution", faultKnowledgeBaseModel.getSolution());
            lm.put("method", faultKnowledgeBaseModel.getMethod());
            lm.put("tools", faultKnowledgeBaseModel.getTools());
            lm.put("deviceMistake", faultKnowledgeBaseModel.getDeviceMistake());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "故障知识库导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = File.separator+"errorExcelFiles"+ File.separator+fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imporReturnRes(errorLines, successLines, null, true, url);
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

    //下拉框
    private void selectList(Workbook workbook,String name,int firstCol, int lastCol,List<DictModel> modelList){
        Sheet sheet = workbook.getSheetAt(0);
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

    public boolean getRole() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> rolesByUsername = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        if (!rolesByUsername.contains(RoleConstant.ADMIN)&&!rolesByUsername.contains(RoleConstant.FOREMAN)&&!rolesByUsername.contains(RoleConstant.MAJOR_PEOPLE)) {
            return true;
        }
        return false;
    }

    public String startProcess(FaultKnowledgeBase faultKnowledgeBase){
        String id = faultKnowledgeBase.getId();
        if (StrUtil.isEmpty(id)) {
            //list转string
            getFaultCodeList(faultKnowledgeBase);
            faultKnowledgeBase.setStatus(FaultConstant.PENDING);
            faultKnowledgeBase.setDelFlag(0);
            if (org.apache.commons.lang3.StringUtils.isEmpty(faultKnowledgeBase.getDeviceTypeCode())|| org.apache.commons.lang3.StringUtils.isEmpty(faultKnowledgeBase.getMaterialCode())) {
                Result<String> result = new Result<>();
                result.error500("设备或组件不能为空");
            }
            faultKnowledgeBaseMapper.insert(faultKnowledgeBase);
            String newId = faultKnowledgeBase.getId();
            return newId;
        }else{
            getFaultCodeList(faultKnowledgeBase);
            faultKnowledgeBaseMapper.updateById(faultKnowledgeBase);
            return id;
        }

    }

    /**list转string*/
    private void getFaultCodeList(FaultKnowledgeBase faultKnowledgeBase) {
        List<String> faultCodeList = faultKnowledgeBase.getFaultCodeList();
        if (CollectionUtils.isNotEmpty(faultCodeList)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String faultCode : faultCodeList) {
                stringBuilder.append(faultCode);
                stringBuilder.append(",");
            }
            // 判断字符串长度是否有效
            if (stringBuilder.length() > 0)
            {
                // 截取字符
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            faultKnowledgeBase.setFaultCodes(stringBuilder.toString());
        }
        faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        FaultKnowledgeBase faultKnowledgeBase = this.getById(businessKey);
        if (ObjectUtil.isEmpty(faultKnowledgeBase)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        int states = updateStateEntity.getStates();
        switch (states) {
            case 0:
                // 技术员或者专业技术负责人审核
                faultKnowledgeBase.setStatus(FaultConstant.PENDING);
                break;
            case 2:
                // 技术员驳回，更新状态为已驳回状态
                faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
                break;
            case 4:
                //专业技术负责人驳回
                faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
                break;
            case 5:
                //已审批
                faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
                break;
            default:
                break;
        }
        this.updateById(faultKnowledgeBase);
    }
}
