package com.aiurt.boot.standard.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.standard.service.IInspectionCodeService;
import com.aiurt.boot.standard.vo.InspectionCodeVo;
import com.aiurt.boot.strategy.entity.InspectionStrDeviceRel;
import com.aiurt.boot.strategy.entity.InspectionStrRel;
import com.aiurt.boot.strategy.mapper.InspectionStrDeviceRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrategyMapper;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class InspectionCodeServiceImpl extends ServiceImpl<InspectionCodeMapper, InspectionCode> implements IInspectionCodeService {
    @Resource
    private InspectionStrDeviceRelMapper inspectionStrDeviceRelMapper;
    @Resource
    private InspectionStrRelMapper inspectionStrRelMapper;
    @Resource
    private InspectionCodeMapper inspectionCodeMapper;
    @Resource
    private InspectionStrategyMapper inspectionStrategyMapper;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private CommonAPI commonApi;
    @Override
    public IPage<InspectionCodeDTO> pageList(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO) {
        List<InspectionCodeDTO> inspectionCodeDTOS = baseMapper.pageList(page,inspectionCodeDTO);
        GlobalThreadLocal.setDataFilter(false);
        inspectionCodeDTOS.forEach(i->{
            i.setNumber(baseMapper.number(i.getCode()));
        });
        if (ObjectUtils.isNotEmpty(inspectionCodeDTO.getInspectionStrCode())) {
            for (InspectionCodeDTO il : inspectionCodeDTOS) {
                InspectionStrRel inspectionStrRel = inspectionStrRelMapper.selectOne(new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStaCode, il.getCode())
                        .eq(InspectionStrRel::getInspectionStrCode,inspectionCodeDTO.getInspectionStrCode()));
                // 判断是否指定了设备
                List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                        new LambdaQueryWrapper<InspectionStrDeviceRel>()
                                .eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRel.getId()));
                il.setSpecifyDevice(CollUtil.isNotEmpty(inspectionStrDeviceRels) ? "是" : "否");
            }
        }
        return page.setRecords(inspectionCodeDTOS);
    }

    @Override
    public void updateDelFlag(String id) {
       InspectionCode inspectionCode =baseMapper.selectById(id);
       inspectionCode.setDelFlag(1);
       baseMapper.updateById(inspectionCode);
    }

    @Override
    public IPage<InspectionCodeDTO> pageLists(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO) {
        List<InspectionCodeDTO> inspectionCodeDTOS = baseMapper.pageLists(page,inspectionCodeDTO);
        GlobalThreadLocal.setDataFilter(false);
        if (ObjectUtils.isNotEmpty(inspectionCodeDTO.getInspectionStrCode())) {
            for (InspectionCodeDTO il : inspectionCodeDTOS) {
                InspectionStrRel inspectionStrRel = inspectionStrRelMapper.selectOne(new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStaCode, il.getCode())
                        .eq(InspectionStrRel::getInspectionStrCode,inspectionCodeDTO.getInspectionStrCode()));
                // 判断是否指定了设备
                List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                        new LambdaQueryWrapper<InspectionStrDeviceRel>()
                                .eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRel.getId()));
                il.setSpecifyDevice(CollUtil.isNotEmpty(inspectionStrDeviceRels) ? "是" : "否");
            }
        }
        return page.setRecords(inspectionCodeDTOS);
    }

    @Override
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response, InspectionCode inspectionCode) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<InspectionCode> inspectionCodeList = inspectionCodeMapper.getList(inspectionCode);
        for (InspectionCode dto : inspectionCodeList) {
            //检修周期类型
            List<DictModel> inspectionType = sysBaseApi.getDictItems("inspection_cycle_type");
            inspectionType= inspectionType.stream().filter(f -> (String.valueOf(dto.getType())).equals(f.getValue())).collect(Collectors.toList());
            String typeName = inspectionType.stream().map(DictModel::getText).collect(Collectors.joining());
            dto.setCycleType(typeName);
            //与设备类型相关
            List<DictModel> appointDevice = sysBaseApi.getDictItems("is_appoint_device");
                appointDevice= appointDevice.stream().filter(f -> (String.valueOf(dto.getIsAppointDevice())).equals(f.getValue())).collect(Collectors.toList());
              String  relatedDevice = appointDevice.stream().map(DictModel::getText).collect(Collectors.joining());
              dto.setIsRelatedDevice(relatedDevice);
              //生效状态
            List<DictModel> takeEffect = sysBaseApi.getDictItems("is_take_effect");
                takeEffect = takeEffect.stream().filter(f -> (String.valueOf(dto.getStatus())).equals(f.getValue())).collect(Collectors.toList());
                String effectStatus = takeEffect.stream().map(DictModel::getText).collect(Collectors.joining());
                dto.setEffectStatus(effectStatus);
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "检修标准导出");
        //excel注解对象Class
        mv.addObject(NormalExcelConstants.CLASS, InspectionCode.class);
        //自定义表格参数
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("检修标准导出", "检修标准导出"));
        //导出数据列表
        mv.addObject(NormalExcelConstants.DATA_LIST, inspectionCodeList);
        return mv;
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        String url = null;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, successLines, errorMessage,false,url);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<InspectionCodeVo> csList = ExcelImportUtil.importExcel(file.getInputStream(), InspectionCodeVo.class, params);
                List<InspectionCodeVo> inspectionCodeVoList = csList.parallelStream()
                        .filter(c->c.getTitle()!=null||c.getCode()!=null||c.getCycleType()!=null||c.getMajorCode()!=null||c.getSubsystemCode() !=null||c.getIsRelatedDevice() !=null||c.getEffectStatus()!=null)
                        .collect(Collectors.toList());

                List<InspectionCode> list = new ArrayList<>();
                for (int i = 0; i < inspectionCodeVoList.size(); i++) {
                    InspectionCodeVo inspectionCodeVo = inspectionCodeVoList.get(i);
                    boolean error = true;
                    StringBuffer sb = new StringBuffer();
                    if (ObjectUtil.isNull(inspectionCodeVo.getTitle())) {
                        errorMessage.add("检修标准表名称为必填项，忽略导入");
                        sb.append("检修标准表名称为必填项;");
                        errorLines++;
                        error = false;
                    }else {
                        InspectionCode inspectionCode = inspectionCodeMapper.selectOne(new QueryWrapper<InspectionCode>().lambda().eq(InspectionCode::getTitle, inspectionCodeVo.getTitle()).eq(InspectionCode::getDelFlag, 0));
                        if (inspectionCode != null) {
                            errorMessage.add(inspectionCodeVo.getTitle() + "检修标准表名称已经存在，忽略导入");
                            sb.append("检修标准表名称已经存在;");
                            if (error) {
                                errorLines++;
                                error =false;
                            }
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeVo.getCycleType())) {
                        errorMessage.add("检修周期类型为必填项，忽略导入");
                        sb.append("检修周期类型为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }else{
                        List<DictModel> inspectionType = sysBaseApi.getDictItems("inspection_cycle_type");
                        List<String> collect = inspectionType.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(!collect.contains(inspectionCodeVo.getCycleType())){
                            errorMessage.add("检修周期类型不是下拉框内的内容，忽略导入");
                            sb.append("格式错误，检修周期类型输入了额外的码值或其他的字符;");
                            if(error){
                                errorLines++;
                                error = false;
                            }
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeVo.getMajorCode())) {
                        errorMessage.add("适用专业为必填项，忽略导入");
                        sb.append("适用专业为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeVo.getSubsystemCode())) {
                        errorMessage.add("适用子系统为必填项，忽略导入");
                        sb.append("适用子系统为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeVo.getIsRelatedDevice())) {
                        errorMessage.add("与设备类型相关为必填项，忽略导入");
                        sb.append("与设备类型相关为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }else{
                        List<DictModel> appointDevice = sysBaseApi.getDictItems("is_appoint_device");
                        List<String> collect = appointDevice.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(!collect.contains(inspectionCodeVo.getIsRelatedDevice())){
                            errorMessage.add("与设备类型相关不是下拉框内的内容，忽略导入");
                            sb.append("格式错误，与设备类型相关输入了额外的码值或其他的字符;");
                            if(error){
                                errorLines++;
                                error = false;
                            }
                        }
                    }

                    if (ObjectUtil.isNull(inspectionCodeVo.getEffectStatus())) {
                        errorMessage.add("生效状态为必填项，忽略导入");
                        sb.append("生效状态为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }else{
                        List<DictModel> takeEffect = sysBaseApi.getDictItems("is_take_effect");
                        List<String> collect = takeEffect.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(!collect.contains(inspectionCodeVo.getEffectStatus())){
                            errorMessage.add("生效状态不是下拉框内的内容，忽略导入");
                            sb.append("格式错误，生效状态输入了额外的码值或其他的字符;");
                            if(error){
                                errorLines++;
                                error = false;
                            }
                        }
                    }

                    InspectionCode inspectionCode = new InspectionCode();
                    BeanUtils.copyProperties(inspectionCodeVo, inspectionCode);
                    //判断是否与下拉框值一致，一致则添加进数据库
                    if(StrUtil.isNotEmpty(inspectionCodeVo.getIsRelatedDevice())){
                        List<DictModel> inspectionType = sysBaseApi.getDictItems("inspection_cycle_type");
                        List<String> collect = inspectionType.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(collect.contains(inspectionCodeVo.getCycleType())){
                            inspectionCode.setIsAppointDevice(Integer.valueOf(inspectionCodeVo.getIsRelatedDevice()));
                        }
                    }
                    if(StrUtil.isNotEmpty(inspectionCodeVo.getCycleType())){
                        List<DictModel> appointDevice = sysBaseApi.getDictItems("is_appoint_device");
                        List<String> collect = appointDevice.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(collect.contains(inspectionCodeVo.getIsRelatedDevice())){
                            inspectionCode.setType(Integer.valueOf(inspectionCodeVo.getCycleType()));
                        }
                    }
                    if(StrUtil.isNotEmpty(inspectionCodeVo.getEffectStatus())){
                        List<DictModel> takeEffect = sysBaseApi.getDictItems("is_take_effect");
                        List<String> collect = takeEffect.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(collect.contains(inspectionCodeVo.getEffectStatus())){
                            inspectionCode.setStatus(Integer.valueOf(inspectionCodeVo.getEffectStatus()));
                        }
                    }
                    list.add(inspectionCode);
                    //判断填写的数据中是否有重复数据
                    if(list.size()>1){
                        if(ObjectUtil.isNotNull(inspectionCode.getTitle())){
                            List<InspectionCode> nameList = list.stream().filter(f -> f.getTitle() != null).collect(Collectors.toList());
                            Map<Object, Long> mapGroup2 = nameList.stream().collect(Collectors.groupingBy(InspectionCode::getTitle, Collectors.counting()));
                            List<Object> collect = mapGroup2.keySet().stream().filter(key -> mapGroup2.get(key) > 1).collect(Collectors.toList());
                            if(collect.contains(inspectionCode.getTitle())){
                                errorMessage.add("检修标准表名称重复，忽略导入");
                                sb.append("检修标准表名称重复；");
                                if(error){
                                    errorLines++;
                                    error = false;
                                }
                            }
                        }
                    }
                    inspectionCodeVo.setErrorCause(String.valueOf(sb));
                    successLines++;
                }
                if(errorLines==0) {
                    for (InspectionCode inspectionCode : list) {
                        String code="BZ"+System.currentTimeMillis();
                        inspectionCode.setCode(code);
                        inspectionCodeMapper.insert(inspectionCode);
                    }
                } else {
                    successLines =0;
                    //1.获取文件流
                    org.springframework.core.io.Resource resource = new ClassPathResource("/templates/inspectionCode.xlsx");
                    InputStream resourceAsStream = resource.getInputStream();

                    //2.获取临时文件
                    File fileTemp= new File("/templates/inspectionCode.xlsx");
                    try {
                        //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                        FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    String path = fileTemp.getAbsolutePath();
                    TemplateExportParams exportParams = new TemplateExportParams(path);
                    Map<String, Object> errorMap = new HashMap<String, Object>(32);
                    errorMap.put("title", "检修标准导入错误清单");
                    List<Map<String, Object>> listMap = new ArrayList<>();
                    for (InspectionCodeVo dto : inspectionCodeVoList) {
                        //获取一条排班记录
                        Map<String, Object> lm = new HashMap<String, Object>(32);
                        //检修周期类型字典值翻译
                        String insType = null;
                        if(ObjectUtil.isNotNull(dto.getCycleType())){
                            List<DictModel> inspectionType = sysBaseApi.getDictItems("inspection_cycle_type");
                            List<String> collect = inspectionType.stream().map(DictModel::getValue).collect(Collectors.toList());
                            if(collect.contains(dto.getCycleType())){
                                inspectionType= inspectionType.stream().filter(f -> (String.valueOf(dto.getCycleType())).equals(f.getValue())).collect(Collectors.toList());
                                insType = inspectionType.stream().map(DictModel::getText).collect(Collectors.joining());
                            }else{
                                insType = String.valueOf(dto.getCycleType());
                            }
                        }else{
                            insType = null;
                        }
                        //与设备类型相关字典值翻译
                        String relatedDevice = null;
                        if(ObjectUtil.isNotNull(dto.getIsRelatedDevice())){
                            List<DictModel> appointDevice = sysBaseApi.getDictItems("is_appoint_device");
                            List<String> collect = appointDevice.stream().map(DictModel::getValue).collect(Collectors.toList());
                            if(collect.contains(dto.getIsRelatedDevice())){
                                appointDevice= appointDevice.stream().filter(f -> (String.valueOf(dto.getIsRelatedDevice())).equals(f.getValue())).collect(Collectors.toList());
                                relatedDevice = appointDevice.stream().map(DictModel::getText).collect(Collectors.joining());
                            }else{
                                relatedDevice = dto.getIsRelatedDevice();
                            }
                        }else{
                            relatedDevice = null;
                        }
                        //生效状态字典值翻译
                        String effectStatus = null;
                        if(ObjectUtil.isNotNull(dto.getEffectStatus())){
                            List<DictModel> takeEffect = sysBaseApi.getDictItems("is_take_effect");
                            List<String> collect = takeEffect.stream().map(DictModel::getValue).collect(Collectors.toList());
                            if(collect.contains(dto.getEffectStatus())){
                                takeEffect= takeEffect.stream().filter(f -> (String.valueOf(dto.getEffectStatus())).equals(f.getValue())).collect(Collectors.toList());
                                effectStatus = takeEffect.stream().map(DictModel::getText).collect(Collectors.joining());
                            }else{
                                effectStatus = dto.getEffectStatus();
                            }
                        }else{
                            effectStatus = null;
                        }
                        //适用子系统字典值翻译
                        String systemName = null;
                        if(ObjectUtil.isNotNull(dto.getMajorCode())){
//                            List<SubsystemDTO> subsystemCode = inspectionCodeMapper.getSubsystemCode();
//                            List<String> collect = subsystemCode.stream().map(SubsystemDTO::getSystemCode).distinct().collect(Collectors.toList());
//                                    if(collect.contains(dto.getSubsystemCode())){
                                        String subsystemName = inspectionStrategyMapper.systemCodeName(dto.getSubsystemCode());
                                        systemName= subsystemName;
//                                    }else{
//                                        systemName=dto.getSubsystemCode();
//                                    }
                        }else{
                            systemName = dto.getSubsystemCode();
                        }
                        //适用专业字典值翻译
                        String majorName = null;
                        if(ObjectUtil.isNotNull(dto.getMajorCode())){
//                            List<MajorDTO> majorCodes = inspectionCodeMapper.getMajorCode();
//                            List<String> collect = majorCodes.stream().map(MajorDTO::getMajorCode).collect(Collectors.toList());
//                            if(collect.contains(dto.getMajorCode())){
                                String mName = inspectionCodeMapper.getMajorName(dto.getMajorCode());
                                majorName= mName;
//                            }else{
//                                majorName=dto.getSubsystemCode();
//                            }
                        }else{
                            majorName = dto.getSubsystemCode();
                        }
                        //设备类型字典值翻译
                        String deviceName = null;
                        if(ObjectUtil.isNotNull(dto.getDeviceTypeCode())){
//                            List<MajorDTO> majorCodes = inspectionCodeMapper.getMajorCode();
//                            List<String> collect = majorCodes.stream().map(MajorDTO::getMajorCode).collect(Collectors.toList());
//                            if(collect.contains(dto.getMajorCode())){
                            String dName = inspectionCodeMapper.deviceTypeCodeName(dto.getDeviceTypeCode());
                            deviceName= dName;
//                            }else{
//                                majorName=dto.getSubsystemCode();
//                            }
                        }else{
                            deviceName = dto.getDeviceTypeCode();
                        }

                        //错误报告获取信息
                        lm.put("title", dto.getTitle());
                        lm.put("code", dto.getCode());
                        lm.put("type", insType);
                        lm.put("majorCode", majorName);
                        lm.put("subsystemCode", systemName);
                        lm.put("isAppointDevice", relatedDevice);
                        lm.put("deviceTypeCode",deviceName);
                        lm.put("status", effectStatus);
                        lm.put("mistake", dto.getErrorCause());
                        listMap.add(lm);
                    }
                    errorMap.put("maplist", listMap);
                    Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
                    String filename = "检修标准导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
                    FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+filename);
                    url =filename;
                    workbook.write(out);
                }
            } catch (Exception e) {
                errorMessage.add("发生异常：" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
        return imporReturnRes(errorLines, successLines, errorMessage,true,url);
    }


    public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage,boolean isType,String failReportUrl) throws IOException {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl",failReportUrl);
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
            res.setMessage("导入失败，文件类型不对。");
            res.setCode(200);
            return res;
        }
    }
}
