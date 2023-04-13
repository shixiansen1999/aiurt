package com.aiurt.boot.standard.service.impl;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.manager.dto.OrgVO;
import com.aiurt.boot.standard.dto.InspectionCodeContentDTO;
import com.aiurt.boot.standard.dto.InspectionCodeErrorDTO;
import com.aiurt.boot.standard.dto.InspectionCodeExcelDTO;
import com.aiurt.boot.standard.dto.InspectionCodeImportDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.standard.service.IInspectionCodeService;
import com.aiurt.boot.strategy.entity.InspectionCoOrgRel;
import com.aiurt.boot.strategy.entity.InspectionStrDeviceRel;
import com.aiurt.boot.strategy.entity.InspectionStrRel;
import com.aiurt.boot.strategy.mapper.InspectionCoOrgRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrDeviceRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrategyMapper;
import com.aiurt.boot.strategy.service.IInspectionCoOrgRelService;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.device.entity.DeviceType;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
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
import org.jeecg.common.system.vo.SysDepartModel;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @Autowired
    private InspectionCodeContentMapper inspectionCodeContentMapper;
    @Resource
    private InspectionCoOrgRelMapper inspectionCoOrgRelMapper;

    @Autowired
    private IInspectionCoOrgRelService orgRelService;

    @Override
    public IPage<InspectionCodeDTO> pageList(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO) {
        List<InspectionCodeDTO> inspectionCodeDTOS = baseMapper.pageList(page,inspectionCodeDTO);
        GlobalThreadLocal.setDataFilter(false);
        inspectionCodeDTOS.forEach(i->{
            //字典翻译
            List<DictModel> repairTypeValue = sysBaseApi.getDictItems("repair_type");
            repairTypeValue= repairTypeValue.stream().filter(f -> (String.valueOf(i.getRepairType())).equals(f.getValue())).collect(Collectors.toList());
            String value = repairTypeValue.stream().map(DictModel::getText).collect(Collectors.joining());
            i.setRepairTypeValue(value);
            i.setNumber(baseMapper.number(i.getCode()));
            List<InspectionCoOrgRel> orgRelList = inspectionCoOrgRelMapper.selectList(new LambdaQueryWrapper<InspectionCoOrgRel>().eq(InspectionCoOrgRel::getInspectionCoCode, i.getCode()));
            if(CollUtil.isNotEmpty(orgRelList)){
                List<OrgVO> orgCodeList = inspectionCoOrgRelMapper.getOrgList(orgRelList);
                i.setOrgCodeList(orgCodeList);
                String orgNames = orgCodeList.stream().map(OrgVO::getLabel).collect(Collectors.joining(";"));
                i.setOrgName(orgNames);
            }
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
        if(ObjectUtil.isNotEmpty(inspectionCodeDTO.getOrgCodes())){
            List<String> list = StrUtil.splitTrim(inspectionCodeDTO.getOrgCodes(), ",");
            inspectionCodeDTO.setOrgList(list);
        }
        List<InspectionCodeDTO> inspectionCodeDTOS = baseMapper.pageLists(page,inspectionCodeDTO);
        for (InspectionCodeDTO codeDTO : inspectionCodeDTOS) {
            LambdaQueryWrapper<InspectionCoOrgRel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InspectionCoOrgRel::getInspectionCoCode, codeDTO.getCode());
            queryWrapper.eq(InspectionCoOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<InspectionCoOrgRel> inspectionCoOrgRels = inspectionCoOrgRelMapper.selectList(queryWrapper);
            if (ObjectUtil.isNotEmpty(inspectionCoOrgRels)) {
                List<String> list = inspectionCoOrgRels.stream().map(InspectionCoOrgRel::getOrgCode).collect(Collectors.toList());
                codeDTO.setOrgList(list);
            }
        }
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
    public void exportXls(HttpServletRequest request, HttpServletResponse response, InspectionCodeExcelDTO inspectionCodeExcelDto) {
        // 封装数据
        List<InspectionCodeExcelDTO> pageList = this.getinspectionStrategyList(inspectionCodeExcelDto);
        List<InspectionCodeExcelDTO> exportList = null;
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "检修表数据";
        cn.afterturn.easypoi.excel.entity.ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), ExcelType.XSSF);
        //调用ExcelExportUtil.exportExcel方法生成workbook
        Workbook wb = cn.afterturn.easypoi.excel.ExcelExportUtil.exportExcel(exportParams, InspectionCodeExcelDTO.class, exportList);
        String fileName = "检修表数据";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            //xlsx格式设置
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            wb.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取excel表格数据
     *
     * @param inspectionCodeExcelDto
     * @return
     */
    private List<InspectionCodeExcelDTO> getinspectionStrategyList(InspectionCodeExcelDTO inspectionCodeExcelDto) {
        List<InspectionCodeExcelDTO> inspectionCodeList = inspectionCodeMapper.getList(inspectionCodeExcelDto);
        for (InspectionCodeExcelDTO dto : inspectionCodeList) {
            //适用部门
            List<InspectionCoOrgRel> inspectionCoOrgRels = inspectionCoOrgRelMapper.selectList(new LambdaQueryWrapper<InspectionCoOrgRel>().eq(InspectionCoOrgRel::getInspectionCoCode, dto.getCode()).eq(InspectionCoOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
            ArrayList<String> orgNamelist = new ArrayList<>();
            if (CollUtil.isNotEmpty(inspectionCoOrgRels)) {
                inspectionCoOrgRels.forEach(t -> {
                    String departNameByOrgCode = sysBaseApi.getDepartNameByOrgCode(t.getOrgCode());
                    if (StrUtil.isNotEmpty(departNameByOrgCode)) {
                        orgNamelist.add(departNameByOrgCode);
                    }
                });
            }
            if (CollUtil.isNotEmpty(orgNamelist)) {
                String orgName = orgNamelist.stream().collect(Collectors.joining(";"));
                dto.setOrgName(orgName);
            }
            //检修表类型
            List<DictModel> repairType = sysBaseApi.getDictItems("repair_type");
            repairType = repairType.stream().filter(f -> (String.valueOf(dto.getRepairType())).equals(f.getValue())).collect(Collectors.toList());
            String repairValue = repairType.stream().map(DictModel::getText).collect(Collectors.joining());
            dto.setRepairTypeValue(repairValue);
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
            //适用专业翻译
            String mName = inspectionCodeMapper.getMajorName(dto.getMajorCode());
            dto.setMajorName(mName);
            //适用子系统翻译
            String subsystemName = inspectionStrategyMapper.systemCodeName(dto.getSubsystemCode());
            dto.setSubsystemName(subsystemName);
            //设备类型
            String dName = inspectionCodeMapper.deviceTypeCodeName(dto.getDeviceTypeCode());
            dto.setDeviceTypeName(dName);

            if (CollUtil.isEmpty(inspectionCodeList)) {
                return inspectionCodeList;
            }
            // 配置检查项
                if (ObjectUtil.isEmpty(dto)) {
                    continue;
                }
                List<InspectionCodeContentDTO> inspectionCodeContentDTOList = inspectionCodeContentMapper.selectByInspectionId(dto.getId());
                if (CollUtil.isEmpty(inspectionCodeContentDTOList)) {
                    continue;
                }
                // 导出配置项内容
                inspectionCodeContentDTOList.forEach(e -> {
                    String hierarchyType = StrUtil.equals(e.getPid(), "0") ? "0" : "1";
                    String hierarchyTypeName = sysBaseApi.translateDict("inspection_level_type", hierarchyType);
                    e.setHierarchyTypeName(hierarchyTypeName);
                    if (!StrUtil.equals(e.getPid(), "0") || !StrUtil.equals(e.getPid(), StrUtil.NULL)) {
                        InspectionCodeContent parent = inspectionCodeContentMapper.selectOne(new LambdaQueryWrapper<InspectionCodeContent>().eq(InspectionCodeContent::getId, e.getPid()).eq(InspectionCodeContent::getDelFlag, CommonConstant.DEL_FLAG_0));
                        if (ObjectUtil.isNotEmpty(parent)) {
                            e.setParent(parent.getName());
                        }
                    }
                    String isType = sysBaseApi.translateDict("inspection_project", Convert.toStr(e.getType()));
                    e.setIsType(isType);
                    String sStatusItem = sysBaseApi.translateDict("patrol_input_type", e.getStatusItem());
                    e.setSStatusItem(sStatusItem);
                    String isInspectionType = sysBaseApi.translateDict("inspection_value", e.getInspectionType());
                    e.setIsInspectionType(isInspectionType);
                    List<DictModel> dictModelList = inspectionCodeMapper.querySysDict(1);
                    if (CollUtil.isNotEmpty(dictModelList)) {
                        String dictCode = dictModelList.stream().filter(t -> StrUtil.equals(t.getValue(), e.getDictCode())).map(DictModel::getText).limit(1).collect(Collectors.joining());
                        e.setDictCode(dictCode);
                    }
                    String dataCheck = sysBaseApi.translateDict("regex", e.getDataCheck());
                    e.setDataCheck(dataCheck);
                });
                dto.setInspectionCodeContentDTOList(inspectionCodeContentDTOList);

        }
        return inspectionCodeList;
    }

    @Override
    public void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/InspectionCodeTemplate.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/InspectionCodeTemplate.xlsx");
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
        List<DictModel> isTypeModels = bean.queryDictItemsByCode("inspection_cycle_type");
        ExcelSelectListUtil.selectList(workbook, "检修周期类型", 1, 1, isTypeModels);
        List<DictModel> majorModels = bean.queryTableDictItemsByCode("cs_major", "major_name", "major_code");
        ExcelSelectListUtil.selectList(workbook, "适用专业", 2, 2, majorModels);
        List<DictModel> subsystemModels = bean.queryTableDictItemsByCode("cs_subsystem", "system_name", "system_code");
        ExcelSelectListUtil.selectList(workbook, "适用子系统", 3, 3, subsystemModels);
        List<DictModel> isDeviceTypeModels = bean.queryDictItemsByCode("is_appoint_device");
        ExcelSelectListUtil.selectList(workbook, "与设备类型相关", 4, 4, isDeviceTypeModels);
        List<DictModel> repairTypeModels = bean.queryDictItemsByCode("repair_type");
        ExcelSelectListUtil.selectList(workbook, "检修表类型", 6, 6, repairTypeModels);
        List<DictModel> statusModels = bean.queryDictItemsByCode("is_take_effect");
        ExcelSelectListUtil.selectList(workbook, "生效状态", 7, 7, statusModels);
        List<DictModel> deviceTypeModels = bean.queryTableDictItemsByCode("device_type", "name", "code");
        ExcelSelectListUtil.selectList(workbook, "设备类型", 8, 8, deviceTypeModels);
        List<DictModel> hierarchyTypeModels = bean.queryDictItemsByCode("patrol_hierarchy_type");
        ExcelSelectListUtil.selectList(workbook, "层级类型", 9, 9, hierarchyTypeModels);
        List<DictModel> isStandardModels = bean.queryDictItemsByCode("patrol_check");
        ExcelSelectListUtil.selectList(workbook, "是否为检修项目", 14, 14, isStandardModels);
        List<DictModel> requiredDictModels = bean.queryDictItemsByCode("patrol_input_type");
        ExcelSelectListUtil.selectList(workbook, "检查值类型", 16, 16, requiredDictModels);
        List<DictModel> requiredModels = bean.queryDictItemsByCode("patrol_item_required");
        ExcelSelectListUtil.selectList(workbook, "检查值是否必填", 17, 17, requiredModels);
        Integer modules = 1;
        List<DictModel> modelList = inspectionCodeMapper.querySysDict(modules);
        ExcelSelectListUtil.selectList(workbook, "关联数据字典", 18, 18, modelList);
        List<DictModel> regularModels = bean.queryDictItemsByCode("regex");
        ExcelSelectListUtil.selectList(workbook, "数据校验表达式", 19, 19, regularModels);
        String fileName = "检修标准导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "检修标准导入模板.xlsx");
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcels(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        // 标记是否有错误信息
        Boolean errorSign = false;
        String url = null;
        String tipMessage = null;
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
            params.setHeadRows(2);
            List<InspectionCodeErrorDTO> deviceAssemblyErrorModels = new ArrayList<>();
            List<InspectionCode> standardList = new ArrayList<>();
            try {
                List<InspectionCodeImportDTO> list = ExcelImportUtil.importExcel(file.getInputStream(), InspectionCodeImportDTO.class, params);
                if (CollUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                //判断检修标准是否读取空数据
                if(CollUtil.isNotEmpty(list)){
                    Iterator<InspectionCodeImportDTO> iterator = list.iterator();
                    if(CollUtil.isNotEmpty(iterator)){
                        while (iterator.hasNext()) {
                            InspectionCodeImportDTO model = iterator.next();
                            boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                            if (a) {
                                iterator.remove();
                            }
                        }
                    }
                }
                for (InspectionCodeImportDTO inspectionCodeImportDTO : list) {
                    List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeImportDTO.getInspectionCodeContentList();
                    //判断配置项是否读取空数据
                    if(CollUtil.isNotEmpty(inspectionCodeContentList)){
                        Iterator<InspectionCodeContent> iterator = inspectionCodeContentList.iterator();
                        if(CollUtil.isNotEmpty(iterator)){
                            while (iterator.hasNext()) {
                                InspectionCodeContent model = iterator.next();
                                boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                                if (a) {
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
                for (InspectionCodeImportDTO model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        InspectionCode inspectionCode = new InspectionCode();
                        //信息数据校验
                        standard(model, inspectionCode, stringBuilder);
                        //配置项数据校验
                        errorSign = this.itemsModel(inspectionCode, errorLines,errorSign);
                        if (stringBuilder.length() > 0 || errorSign) {
                            // 截取字符
                            if(stringBuilder.length() > 0){
                                stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                model.setInspectionCodeErrorReason(stringBuilder.toString());
                            }
                            errorLines++;
                        }
                        if (errorLines > 0) {
                            for (InspectionCodeContent inspectionCodeContent : inspectionCode.getInspectionCodeContentList()) {
                                if(inspectionCodeContent.getIsNUll()!=true)
                                {
                                    HashMap<String, String> checkMap = CollUtil.newHashMap();
                                    checkMap.put("0", "一级");
                                    checkMap.put("1", "子级");
                                    //层级转换
                                    String hasChild = inspectionCodeContent.getHasChild();
                                    String  hierarchyType= checkMap.get(hasChild);
                                    inspectionCodeContent.setHasChild(hierarchyType);
                                    InspectionCodeErrorDTO errorModel = new InspectionCodeErrorDTO();
                                    BeanUtils.copyProperties(model, errorModel);
                                    BeanUtils.copyProperties(inspectionCodeContent, errorModel);
                                    deviceAssemblyErrorModels.add(errorModel);
                                }
                            }
                        }
                        standardList.add(inspectionCode);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, deviceAssemblyErrorModels, errorMessage, successLines, url, type);
                } else {
                    //没有错误，数据添加进数据库
                    LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    for (InspectionCode inspectionCode : standardList) {
                        String code="BZ"+System.currentTimeMillis();
                        List<OrgVO> orgCodeList = inspectionCode.getOrgCodeList();
                        for (OrgVO s : orgCodeList) {
                            InspectionCoOrgRel inspectionCoOrgRel = new InspectionCoOrgRel();
                            inspectionCoOrgRel.setOrgCode(s.getLabel());
                            inspectionCoOrgRel.setInspectionCoCode(code);
                            orgRelService.save(inspectionCoOrgRel);
                        }
                        inspectionCode.setCode(code);
                        inspectionCode.setCreateBy(user.getUsername());
                        inspectionCodeMapper.insert(inspectionCode);
                        //配置项
                        List<InspectionCodeContent> items = inspectionCode.getInspectionCodeContentList();
                        if (CollUtil.isNotEmpty(items)) {
                            List<InspectionCodeContent> parents = items.stream().filter(e -> e.getHasChild()!=null&&e.getHasChild() == "0").collect(Collectors.toList());
                            List<InspectionCodeContent> sons = items.stream().filter(e -> e.getHasChild()!=null&&e.getHasChild() == "1").collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(parents)){
                                for (InspectionCodeContent item : parents) {
                                    item.setPid("0");
                                    item.setInspectionCodeId(inspectionCode.getId());
                                    inspectionCodeContentMapper.insert(item);
                                    List<InspectionCodeContent> standardItems = sons.stream().filter(e -> e.getPid().equals(item.getName())).collect(Collectors.toList());
                                    if (CollUtil.isNotEmpty(standardItems)) {
                                        for (InspectionCodeContent standardItem : standardItems) {
                                            standardItem.setPid(item.getId());
                                            standardItem.setInspectionCodeId(inspectionCode.getId());
                                            inspectionCodeContentMapper.insert(standardItem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    successLines = standardList.size();
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
        return imporReturnRes(errorLines, successLines, tipMessage, true, url);
    }


    private void standard(InspectionCodeImportDTO model, InspectionCode inspectionCode, StringBuilder stringBuilder) {
        BeanUtils.copyProperties(model, inspectionCode);

        // 转换是否值
        HashMap<String, Integer> checkMap = CollUtil.newHashMap();
        checkMap.put("是", 1);
        checkMap.put("否", 0);

        // 转换生效未生效值
        HashMap<String, Integer> checkMap2 = CollUtil.newHashMap();
        checkMap2.put("生效", 1);
        checkMap2.put("未生效", 0);

        // 生成检修标准编码
        inspectionCode.setCode("BZ" + System.currentTimeMillis());
        if (StrUtil.isEmpty(model.getTitle())) {
            stringBuilder.append("检修标准名称必须填写，");
        } else {
            inspectionCode.setTitle(model.getTitle());
        }

        if (ObjectUtil.isEmpty(model.getCycleType())) {
            stringBuilder.append("检修周期类型必须填写，");
        } else {
            Map<String, String> inspectionCycleTypeMap = Optional.ofNullable(sysBaseApi.getDictItems(DictConstant.INSPECTION_CYCLE_TYPE)).orElse(CollUtil.newArrayList()).stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
            if (StrUtil.isEmpty(inspectionCycleTypeMap.get(model.getCycleType()))) {
                stringBuilder.append("检修周期类型格式错误,");
            } else {
                inspectionCode.setType(Integer.parseInt(inspectionCycleTypeMap.get(model.getCycleType())));
            }

            String name = model.getTitle();
            String majorName = model.getMajorCode();
            String isDeviceType = model.getIsAppointDevice();
            String statusName = model.getStatus();
            String deviceTypeName = model.getDeviceTypeCode();
            String orgName = model.getOrgName();
            String repairTypeName =model.getRepairTypeName();
            Integer statusCode = checkMap2.get(statusName);
            Integer isDeviceCode = checkMap.get(isDeviceType);
            List<String> orgNameList = null;
            if (StrUtil.isEmpty(orgName)) {
                stringBuilder.append("适用部门不能为空，");
            } else {
                orgNameList =  StrUtil.splitTrim(orgName, "，");
                int begSize = orgNameList.size();
                orgNameList =  orgNameList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o))), ArrayList::new));
                int endSize = orgNameList.size();
                if (endSize < begSize) {
                    stringBuilder.append("适用部门填写不规范有重复内容，");
                }
            }
            List<DictModel> repairTypes = sysBaseApi.getDictItems("repair_type");
            String repairTypeNames = null;
            if (CollUtil.isNotEmpty(repairTypes)) {
                repairTypeNames =  repairTypes.stream().map(e -> e.getText()).collect(Collectors.joining());
            }
            if (StrUtil.isNotEmpty(majorName) && StrUtil.isNotEmpty(isDeviceType) && CollUtil.isNotEmpty(orgNameList) && StrUtil.isNotEmpty(repairTypeName)&& StrUtil.isNotEmpty(statusName) && StrUtil.isNotEmpty(name)) {
                JSONObject major = sysBaseApi.getCsMajorByName(majorName);
                ArrayList<OrgVO> orgVOS = new ArrayList<>();
                if (ObjectUtil.isNotEmpty(major)) {
                    inspectionCode.setMajorCode(major.getString("majorCode"));
                    if (ObjectUtil.isNotEmpty(model.getSubsystemCode())) {
                        JSONObject systemName = sysBaseApi.getSystemName(major.getString("majorCode"), model.getSubsystemCode());
                        if (ObjectUtil.isNotEmpty(systemName)) {
                            inspectionCode.setSubsystemCode(systemName.getString("systemCode"));
                        } else {
                            stringBuilder.append("系统不存在该专业下的子系统，");
                        }
                    }
                    orgNameList.forEach(t -> {
                        JSONObject departByName = sysBaseApi.getDepartByName(t);
                        if (ObjectUtil.isEmpty(departByName)) {
                            stringBuilder.append("系统不存在该部门：" + t + StrUtil.COMMA);
                        } else {
                            SysDepartModel sysDepartModel = departByName.toJavaObject(SysDepartModel.class);
                            OrgVO orgVO = new OrgVO();
                            orgVO.setLabel(sysDepartModel.getOrgCode());
                            orgVO.setValue(sysDepartModel.getDepartName());
                            orgVOS.add(orgVO);
                        }
                    });
                    inspectionCode.setOrgCodeList(orgVOS);
                    if (!(repairTypeNames.contains(repairTypeName))) {
                        stringBuilder.append("系统不存在该标准表类型：" + repairTypeName + StrUtil.COMMA);
                    } else {
                        repairTypes.forEach(t -> {
                            if (StrUtil.equals(t.getText(), repairTypeName)) {
                                inspectionCode.setRepairType(Integer.valueOf(t.getValue()));
                            }
                        });
                    }
                    if (!InspectionConstant.IS_APPOINT_DEVICE.equals(isDeviceCode) && !InspectionConstant.NO_ISAPPOINT_DEVICE.equals(isDeviceCode)) {
                        stringBuilder.append("是否与设备类型相关填写不规范，");
                    } else {
                        inspectionCode.setIsAppointDevice(isDeviceCode.equals(InspectionConstant.IS_APPOINT_DEVICE) ? 1 : 0);
                        if (inspectionCode.getIsAppointDevice() == 1 && StrUtil.isNotEmpty(deviceTypeName)) {
                            String systemCode = null;
                            if (StrUtil.isNotEmpty(model.getSubsystemCode())) {
                                JSONObject systemName = sysBaseApi.getSystemName(major.getString("majorCode"), model.getSubsystemCode());
                                if (ObjectUtil.isNotEmpty(systemName)) {
                                    systemCode = systemName.getString("systemCode");
                                }
                            }
                            DeviceType d = sysBaseApi.getCsMajorByCodeTypeName(major.getString("majorCode"), deviceTypeName, systemCode);
                            if (ObjectUtil.isNull(d)) {
                                stringBuilder.append("系统不存在该专业下的设备类型，");
                            } else {
                                inspectionCode.setDeviceTypeCode(d.getCode());
                            }
                        }
                        if (inspectionCode.getIsAppointDevice() == 1 && StrUtil.isEmpty(deviceTypeName)) {
                            stringBuilder.append("设备类型未填写，");
                        }
                        if (inspectionCode.getIsAppointDevice() == 0 && StrUtil.isNotEmpty(deviceTypeName)) {
                            stringBuilder.append("设备类型不用填写，");
                        }
                    }
                    if (!InspectionConstant.IS_EFFECT.equals(statusCode) && !InspectionConstant.NO_IS_EFFECT.equals(statusCode)) {
                        stringBuilder.append("生效状态填写不规范，");
                    } else {
                        inspectionCode.setStatus(statusCode.equals(InspectionConstant.IS_EFFECT) ? 1 : 0);
                    }
                } else {
                    stringBuilder.append("系统不存在该专业，");
                    orgNameList.forEach(t -> {
                        JSONObject departByName = sysBaseApi.getDepartByName(t);
                        if (ObjectUtil.isEmpty(departByName)) {
                            stringBuilder.append("系统不存在该部门：" + t + StrUtil.COMMA);
                        } else {
                            SysDepartModel sysDepartModel = departByName.toJavaObject(SysDepartModel.class);
                            OrgVO orgVO = new OrgVO();
                            orgVO.setLabel(sysDepartModel.getOrgCode());
                            orgVO.setValue(sysDepartModel.getDepartName());
                            orgVOS.add(orgVO);
                        }
                    });
                    inspectionCode.setOrgCodeList(orgVOS);
                    if (!(repairTypeNames.contains(repairTypeName))) {
                        stringBuilder.append("系统不存在该标准表类型：" + repairTypeName + StrUtil.COMMA);
                    } else {
                        repairTypes.forEach(t -> {
                            if (StrUtil.equals(t.getText(), repairTypeName)) {
                                inspectionCode.setRepairType(Integer.valueOf(t.getValue()));
                            }
                        });
                    }
                    if (!InspectionConstant.IS_APPOINT_DEVICE.equals(isDeviceCode) || !InspectionConstant.NO_ISAPPOINT_DEVICE.equals(isDeviceCode)) {
                        stringBuilder.append("是否与设备类型相关填写不规范，");
                    } else {
                        inspectionCode.setIsAppointDevice(isDeviceType.equals(InspectionConstant.IS_APPOINT_DEVICE) ? 1 : 0);
                    }
                    if (!InspectionConstant.IS_EFFECT.equals(statusCode) || !InspectionConstant.NO_IS_EFFECT.equals(statusCode)) {
                        stringBuilder.append("生效状态填写不规范，");
                    } else {
                        inspectionCode.setStatus(statusCode.equals(InspectionConstant.IS_EFFECT) ? 1 : 0);
                    }
                }
            } else {
                stringBuilder.append("检修标准名称、适用专业、适用部门、标准表类型、是否与设备类型相关、生效状态不能为空;");
            }
        }

    }
    private Boolean itemsModel(InspectionCode inspectionCode, int errorLines,Boolean errorSign) {
        List<InspectionCodeContent> standardItems = inspectionCode.getInspectionCodeContentList();
        errorSign = false;
        if (CollUtil.isNotEmpty(standardItems)) {
            int i = 0;
            Map<Object, Integer> duplicateData = new HashMap<>(16);
            for (InspectionCodeContent items : standardItems) {
                StringBuilder contentStringBuilder = new StringBuilder();
                boolean isNull = XlsUtil.checkObjAllFieldsIsNull(items);
                if(isNull) {
                    items.setIsNUll(true);
                }
                else {
                    items.setIsNUll(false);
                }
                String hierarchyTypeName = items.getHasChild();
                String itemsCode = items.getCode();
                String checkName = items.getIsType();
                String content = items.getName();
                // 转换层级
                HashMap<String, String> checkMap = CollUtil.newHashMap();
                checkMap.put("一级", "0");
                checkMap.put("子级", "1");

                HashMap<String, Integer> checkMap2 = CollUtil.newHashMap();
                checkMap2.put("是", 1);
                checkMap2.put("否", 0);

                HashMap<String, Integer> checkMap3 = CollUtil.newHashMap();
                checkMap3.put("无", 1);
                checkMap3.put("选择项", 2);
                checkMap3.put("输入项", 3);

                //层级转换
                String  hierarchyType= checkMap.get(hierarchyTypeName);

                Integer checkCode = checkMap2.get(checkName);

                if("0".equals(hierarchyType)){
                    items.setPid("0");
                }
                //重复数据校验
                Integer s = duplicateData.get(items.getCode());
                if (s == null) {
                    duplicateData.put(items.getCode(), i);
                } else {
                    contentStringBuilder.append("该数据存在相同数据，");
                }
                if (StrUtil.isNotEmpty(hierarchyTypeName) && StrUtil.isNotEmpty(itemsCode) && StrUtil.isNotEmpty(checkName) && StrUtil.isNotEmpty(content)) {
                    List<InspectionCodeContent> itemsList = new ArrayList<>();
                    if(!InspectionConstant.HAS_CHILD_1.equals(hierarchyType) && !InspectionConstant.TREE_ROOT_0.equals(hierarchyType)){
                        contentStringBuilder.append("层级类型填写不规范，");
                    } else {
                        items.setHasChild(InspectionConstant.TREE_ROOT_0.equals(hierarchyType) ? "0" : "1");
                        if (items.getHasChild() == "0") {
                            if (!items.getPid().equals("0")) {
                                contentStringBuilder.append("层级为一级(父级填写无)，");
                            }
                        } else {
                            if(ObjectUtil.isEmpty(items.getPid())) {
                                contentStringBuilder.append("子级要有父级，");
                            }
                            else {
                                itemsList = standardItems.stream().filter(e -> e.getName()!=null&&e.getName().equals(items.getPid())&& !e.equals(items) && e.getHasChild().equals(InspectionConstant.TREE_ROOT_0)).collect(Collectors.toList());
                                if (itemsList.size() == 0 && items.getHasChild().equals(InspectionConstant.HAS_CHILD_1)) {
                                    contentStringBuilder.append("父级不存在，");
                                }
                            }
                        }
                    }
                    if (ObjectUtil.isNotEmpty(items.getIsSortNo())) {
                        String regular = "^[0-9]*$";
                        Pattern pattern = Pattern.compile(regular);
                        Matcher matcher = pattern.matcher(items.getIsSortNo());
                        if (matcher.find()) {
                            items.setSortNo(Integer.valueOf(items.getIsSortNo()));
                        } else {
                            contentStringBuilder.append("内容排序(填写必须是数字)，");
                        }
                    }else{
                        items.setSortNo(1);
                    }
                    if (!InspectionConstant.IS_APPOINT_DEVICE.equals(checkCode) && !InspectionConstant.NO_ISAPPOINT_DEVICE.equals(checkCode)) {
                        contentStringBuilder.append("是否为检查项填写不规范，");
                    } else {
                        items.setType(InspectionConstant.IS_APPOINT_DEVICE.equals(checkCode) ? 1 : 0);
                    }
                    if (items.getType() == 0 && StrUtil.equals(items.getHasChild(), "0")) {
                        if (ObjectUtil.isNotEmpty(items.getDataCheck()) || ObjectUtil.isNotEmpty(items.getQualityStandard()) || ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getSStatusItem()) || ObjectUtil.isNotEmpty(items.getIsInspectionType())) {
                            contentStringBuilder.append("质量标准、检查值类型、检查值是否必填、关联数据字典、数据校验表达式不用填写，");
                        }
                    }
                    if (items.getType() == 1 && StrUtil.equals(items.getHasChild(), "0")) {
                        List<InspectionCodeContent> sonList = standardItems.stream().filter(e -> StrUtil.equals(e.getPid(), items.getName())).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(sonList)) {
                            contentStringBuilder.append("不能有子级，");
                        }
                        if (ObjectUtil.isNotEmpty(items.getIsInspectionType())) {
                            Integer inspectionType = checkMap2.get(items.getIsInspectionType());
                            if (!InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionType) && !InspectionConstant.NO_ISAPPOINT_DEVICE.equals(inspectionType)) {
                                contentStringBuilder.append("检查值是否必填选择不正确，");
                            } else {
                                items.setInspectionType(inspectionType.equals(InspectionConstant.IS_APPOINT_DEVICE) ? 1 : 0);
                            }
                        }
                        if (ObjectUtil.isNotEmpty(items.getSStatusItem())) {
                            Integer statusItem = checkMap3.get(items.getSStatusItem());
                            if(!InspectionConstant.NO_STATUS_ITEM.equals(statusItem) && !InspectionConstant.STATUS_ITEM_CHOICE.equals(statusItem) && !InspectionConstant.STATUS_ITEM_INPUT.equals(statusItem)){
                                contentStringBuilder.append("检查值类型选择不正确，");
                            } else {
                                if (statusItem.equals(InspectionConstant.STATUS_ITEM_INPUT)) {
                                    items.setStatusItem(3);
                                } else {
                                    items.setStatusItem(statusItem.equals(InspectionConstant.STATUS_ITEM_CHOICE) ? 2 : 1);
                                }
                            }
                            if (items.getStatusItem() == 1) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) && ObjectUtil.isNotEmpty(items.getDataCheck())) {
                                    contentStringBuilder.append("关联数据字典、数据校验表达式不用填写，");
                                }
                            }
                            if (items.getStatusItem() == 2) {
                                if (ObjectUtil.isNotEmpty(items.getDataCheck())) {
                                    contentStringBuilder.append("数据校验表达式不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getDictCode())) {
                                        String dictCode = inspectionCodeContentMapper.getDictCode(items.getDictCode());
                                        if (ObjectUtil.isNotEmpty(dictCode)) {
                                            items.setDictCode(dictCode);
                                        } else {
                                            contentStringBuilder.append("关联数据字典选择不正确，");
                                        }
                                    }
                                }
                            }
                            if (items.getStatusItem() == 3) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode())) {
                                    contentStringBuilder.append("关联数据字典不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getDataCheck())) {
                                        List<DictModel> regex = sysBaseApi.getDictItems("regex");
                                        if (CollUtil.isNotEmpty(regex)) {
                                            String dictValue = regex.stream().filter(t -> StrUtil.equals(t.getText(), items.getDataCheck())).map(DictModel::getValue).limit(1).collect(Collectors.joining());
                                            if (StrUtil.isNotEmpty(dictValue)) {
                                                items.setDataCheck(dictValue);
                                            } else {
                                                contentStringBuilder.append("数据校验表达式选择不正确，");
                                            }
                                        } else {
                                            contentStringBuilder.append("数据校验表达式选择不正确，");
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(ObjectUtil.isNotEmpty(items.getDataCheck())||ObjectUtil.isNotEmpty(items.getDictCode())) {
                                contentStringBuilder.append("关联数据字典、数据校验表达式不用填写，");
                            }
                        }
                    }
                    if (items.getType() == 0 && StrUtil.equals(items.getHasChild(), "1")) {
                        contentStringBuilder.append("是否为检查项(要选择为：是)，");
                    }
                    if (items.getType() == 1 && StrUtil.equals(items.getHasChild(), "1")) {
                        if (ObjectUtil.isNotEmpty(items.getIsInspectionType())) {
                            Integer inspectionType = checkMap2.get(items.getIsInspectionType());
                            if (!InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionType) && !InspectionConstant.NO_ISAPPOINT_DEVICE.equals(inspectionType)) {
                                contentStringBuilder.append("检查值是否必填选择不正确，");
                            } else {
                                items.setInspectionType(inspectionType.equals(InspectionConstant.IS_APPOINT_DEVICE) ? 1 : 0);
                            }
                        }
                        if (ObjectUtil.isNotEmpty(items.getSStatusItem())) {
                            Integer statusItem = checkMap3.get(items.getSStatusItem());
                            if(!InspectionConstant.NO_STATUS_ITEM.equals(statusItem) && !InspectionConstant.STATUS_ITEM_CHOICE.equals(statusItem) && !InspectionConstant.STATUS_ITEM_INPUT.equals(statusItem)){
                                contentStringBuilder.append("检查值类型选择不正确，");
                            } else {
                                if (statusItem.equals(InspectionConstant.STATUS_ITEM_INPUT)) {
                                    items.setStatusItem(3);
                                } else {
                                    items.setStatusItem(statusItem.equals(InspectionConstant.STATUS_ITEM_CHOICE) ? 2 : 1);
                                }
                            }
                            if (items.getStatusItem() == 1) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) && ObjectUtil.isNotEmpty(items.getDataCheck())) {
                                    contentStringBuilder.append("关联数据字典、数据校验表达式不用填写，");
                                }
                            }
                            if (items.getStatusItem() == 2) {
                                if (ObjectUtil.isNotEmpty(items.getDataCheck())) {
                                    contentStringBuilder.append("数据校验表达式不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getDictCode())) {
                                        String dictCode = inspectionCodeContentMapper.getDictCode(items.getDictCode());

                                        if (ObjectUtil.isNotEmpty(dictCode)) {
                                            items.setDictCode(dictCode);
                                        } else {
                                            contentStringBuilder.append("关联数据字典选择不正确，");
                                        }
                                    }
                                }
                            }
                            if (items.getStatusItem() == 3) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode())) {
                                    contentStringBuilder.append("关联数据字典不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getDataCheck())) {
                                        List<DictModel> regex = sysBaseApi.getDictItems("regex");
                                        if (CollUtil.isNotEmpty(regex)) {
                                            String dictValue = regex.stream().filter(t -> StrUtil.equals(t.getText(), items.getDataCheck())).map(DictModel::getValue).limit(1).collect(Collectors.joining());
                                            if (StrUtil.isNotEmpty(dictValue)) {
                                                items.setDataCheck(dictValue);
                                            } else {
                                                contentStringBuilder.append("数据校验表达式选择不正确，");
                                            }
                                        } else {
                                            contentStringBuilder.append("数据校验表达式选择不正确，");
                                        }
                                        /*String dictCode = inspectionCodeContentMapper.getDictCode(items.getDataCheck());
                                        if (ObjectUtil.isNotEmpty(dictCode)) {
                                            items.setDataCheck(dictCode);
                                        } else {
                                            contentStringBuilder.append("数据校验表达式选择不正确，");
                                        }*/
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(ObjectUtil.isNotEmpty(items.getDataCheck())||ObjectUtil.isNotEmpty(items.getDictCode())) {
                                contentStringBuilder.append("关联数据字典、数据校验表达式不用填写，");
                            }
                        }
                    }
                } else {
                    contentStringBuilder.append("层级类型、检修项内容、检修项编号、是否为检查项要必填，");
                }
                if(contentStringBuilder.length()>0){
                    contentStringBuilder = contentStringBuilder.deleteCharAt(contentStringBuilder.length() - 1);
                    items.setCodeContentErrorReason(contentStringBuilder.toString());
                    errorSign = true;
                }
            }
        }
        return errorSign;
    }

    private Result<?> getErrorExcel(int errorLines, List<InspectionCodeImportDTO> list, List<InspectionCodeErrorDTO> deviceAssemblyErrorModels, List<String> errorMessage, int successLines, String url, String type) throws IOException {
        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/InspectionCodeError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/InspectionCodeError.xlsx");
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
        for (int i = 0; i < deviceAssemblyErrorModels.size(); i++) {
            InspectionCodeErrorDTO inspectionCodeErrorDto = deviceAssemblyErrorModels.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("title", inspectionCodeErrorDto.getTitle());
            lm.put("type", inspectionCodeErrorDto.getCycleType());
            lm.put("majorName", inspectionCodeErrorDto.getMajorCode());
            lm.put("systemName", inspectionCodeErrorDto.getSubsystemCode());
            lm.put("isdeviceType", inspectionCodeErrorDto.getIsAppointDevice());
            lm.put("statusName", inspectionCodeErrorDto.getStatus());
            lm.put("deviceTypeName", inspectionCodeErrorDto.getDeviceTypeCode());
            lm.put("standMistake", inspectionCodeErrorDto.getInspectionCodeErrorReason());

            lm.put("levelType", inspectionCodeErrorDto.getHasChild());
            lm.put("parent", inspectionCodeErrorDto.getPid());
            lm.put("standradDetail", inspectionCodeErrorDto.getName());
            lm.put("code", inspectionCodeErrorDto.getCode());
            lm.put("detailOrc", inspectionCodeErrorDto.getIsSortNo());
            lm.put("isStandard", inspectionCodeErrorDto.getIsType());
            lm.put("qualityStandard", inspectionCodeErrorDto.getQualityStandard());
            lm.put("checkValue", inspectionCodeErrorDto.getSStatusItem());
            lm.put("isCheck", inspectionCodeErrorDto.getIsInspectionType());
            List<DictModel> regex = sysBaseApi.getDictItems("regex");
            if (StrUtil.isNotEmpty(inspectionCodeErrorDto.getDictCode())) {
                String dictName = inspectionCodeContentMapper.getDictName(inspectionCodeErrorDto.getDictCode());
                if (StrUtil.isNotEmpty(dictName)) {
                    lm.put("dictCode", dictName);
                }
            }
            if (CollUtil.isNotEmpty(regex)) {
                String dictText = regex.stream().filter(t -> StrUtil.equals(t.getValue(), inspectionCodeErrorDto.getDataCheck())).map(DictModel::getText).limit(1).collect(Collectors.joining());
                if (StrUtil.isNotEmpty(dictText)) {
                    inspectionCodeErrorDto.setDataCheck(dictText);
                }
            }
            lm.put("regular", inspectionCodeErrorDto.getDataCheck());
            lm.put("itemParentMistake", inspectionCodeErrorDto.getCodeContentErrorReason());
            lm.put("repairType",inspectionCodeErrorDto.getRepairTypeName());
            lm.put("orgName", inspectionCodeErrorDto.getOrgName());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        int size = 4;
        int length = 7;
        for (InspectionCodeImportDTO deviceModel : list) {
            for (int i = 0; i <= length; i++) {
                //合并单元格
                PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + deviceModel.getInspectionCodeContentList().size() - 1, i, i);
            }
            size = size + deviceModel.getInspectionCodeContentList().size();
        }

        try {
            String fileName = "检修标准数据导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tipMessage = "导入失败，文件类型不对。";
        return imporReturnRes(errorLines, successLines, tipMessage, true, url);
    }


    public static Result<?> imporReturnRes(int errorLines,int successLines,String tipMessage,boolean isType,String failReportUrl) throws IOException {
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
