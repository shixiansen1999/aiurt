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
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.standard.dto.*;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.entity.PatrolStandardDeviceType;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.entity.PatrolStandardOrg;
import com.aiurt.boot.standard.mapper.PatrolStandardDeviceTypeMapper;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.standard.mapper.PatrolStandardOrgMapper;
import com.aiurt.boot.standard.service.IPatrolStandardOrgService;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.aiurt.boot.utils.PatrolCodeUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.device.entity.DeviceType;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.util.SpringContextUtils;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolStandardServiceImpl extends ServiceImpl<PatrolStandardMapper, PatrolStandard> implements IPatrolStandardService {
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;

    @Autowired
    private PatrolStandardItemsMapper patrolStandardItemsMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Autowired
    private PatrolStandardOrgMapper standardOrgMapper;
    @Autowired
    private PatrolStandardDeviceTypeMapper patrolStandardDeviceTypeMapper;
    @Autowired
    private IPatrolStandardOrgService patrolStandardOrgService;
    @Override
    public IPage<PatrolStandardDto> pageList(Page page, PatrolStandard patrolStandard) {
        // 数据权限过滤
       /* List<String> stadardCode = new ArrayList<>();
        try {
            stadardCode = this.standardDataPermissionFilter();
            patrolStandard.setCodes(stadardCode);
        } catch (Exception e) {
            return page;
        }*/

        List<PatrolStandardDto> page1 = patrolStandardMapper.pageList(page, patrolStandard);
        List<DictModel> standardTypes = sysBaseApi.getDictItems("patrol_standard_type");
        // 以下包含的代码权限拦截局部过滤
        boolean filter = GlobalThreadLocal.setDataFilter(false);
        page1.forEach(a -> {
            List<PatrolStandardOrg> orgRelList = standardOrgMapper.selectList(new LambdaQueryWrapper<PatrolStandardOrg>().eq(PatrolStandardOrg::getStandardCode, a.getCode()));
            if(CollUtil.isNotEmpty(orgRelList)){
                List<OrgVO> orgCodeList = standardOrgMapper.getOrgList(orgRelList);
                a.setOrgCodeList(orgCodeList);
                String orgNames = orgCodeList.stream().map(OrgVO::getLabel).collect(Collectors.joining(";"));
                a.setOrgName(orgNames);
            }
            getDeviceTypeName(a);

            String username = baseMapper.selectUserName(a.getCreateBy());
            a.setCreateByName(null == username ? a.getCreateBy() : username);
            a.setNumber(baseMapper.number(a.getCode()));
            standardTypes.forEach(t -> {
                if (Integer.valueOf(t.getValue()).equals(a.getStandardType())) {
                    a.setStandardTypeName(t.getText());
                }
            });
        });
        // 以上包含的代码权限拦截局部过滤
        GlobalThreadLocal.setDataFilter(filter);
        return page.setRecords(page1);
    }

    @Override
    public void getDeviceTypeName(PatrolStandardDto patrolStandardDto) {
        List<PatrolStandardDeviceType> patrolStandardDeviceTypes = patrolStandardDeviceTypeMapper.selectList(new LambdaQueryWrapper<PatrolStandardDeviceType>().eq(PatrolStandardDeviceType::getStandardCode, patrolStandardDto.getCode()).select(PatrolStandardDeviceType::getDeviceTypeCode));
        if (CollUtil.isNotEmpty(patrolStandardDeviceTypes)) {
            Set<String> deviceTypeCodes = patrolStandardDeviceTypes.stream().map(PatrolStandardDeviceType::getDeviceTypeCode).collect(Collectors.toSet());
            patrolStandardDto.setDeviceTypeCodeList(new ArrayList<>(deviceTypeCodes));
            List<DeviceType> typeList = sysBaseApi.selectDeviceTypeByCodes(deviceTypeCodes);
            String deviceTypeNames = typeList.stream().map(DeviceType::getName).collect(Collectors.joining(";"));
            patrolStandardDto.setDeviceTypeName(deviceTypeNames);
        }
    }

    private List<String> standardDataPermissionFilter() {
        List<String> standardCodesByOrg = standardOrgMapper.getStandardCodeByUserOrg();
       /* if (CollectionUtil.isEmpty(standardCodesByOrg)) {
            throw new AiurtBootException("无组织机构数据！");
        }*/
        return standardCodesByOrg;
    }

    @Override
    public IPage<PatrolStandardDto> pageLists(Page page, PatrolStandardDto patrolStandard) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> list = new ArrayList<>();
        if (!sysUser.getRoleCodes().contains("admin")) {
            list = sysBaseApi.getMajorByUserId(sysUser.getId());
        }
        List<PatrolStandardDto> page1 = patrolStandardMapper.pageLists(page, patrolStandard, patrolStandard.getStations(), list.stream().map(s -> s.getMajorCode()).collect(Collectors.toList()));
        page1.forEach(a -> {
            String username = baseMapper.selectUserName(a.getCreateBy());
            a.setCreateByName(null == username ? a.getCreateBy() : username);
            a.setNumber(baseMapper.number(a.getCode()));
            getDeviceTypeName(a);
        });
        return page.setRecords(page1);
    }

    @Override
    public List<InspectionStandardDto> lists(String professionCode, String subsystemCode) {
        List<InspectionStandardDto> list = patrolStandardMapper.list(professionCode, subsystemCode);
        return list;
    }

    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, PatrolStandard patrolStandard) {
        List<PatrolStandard> patrolStandardList = patrolStandardMapper.getList(patrolStandard);
        boolean filter = GlobalThreadLocal.setDataFilter(false);
        for (PatrolStandard standard : patrolStandardList) {
            translate(standard, null);
            List<PatrolStandardItems> patrolStandardItemsList = patrolStandardItemsMapper.selectList(new LambdaQueryWrapper<PatrolStandardItems>()
                    .eq(PatrolStandardItems::getStandardId, standard.getId()).eq(PatrolStandardItems::getDelFlag, CommonConstant.DEL_FLAG_0));
            List<PatrolStandardItems> parentItem = patrolStandardItemsList.stream().filter(e -> 0 == e.getHierarchyType()).collect(Collectors.toList());
            List<PatrolStandardItems> allItem = new ArrayList<>();
            parentItem.sort(Comparator.comparing(PatrolStandardItems::getOrder, Comparator.nullsFirst(Integer::compareTo)));
            for (PatrolStandardItems parent : parentItem) {
                allItem.add(parent);
                List<PatrolStandardItems> sonItem = patrolStandardItemsList.stream().filter(e -> e.getParentId().equals(parent.getId())).collect(Collectors.toList());
                sonItem.sort(Comparator.comparing(PatrolStandardItems::getOrder, Comparator.nullsFirst(Integer::compareTo)));
                if (CollUtil.isEmpty(sonItem)) {
                    parent.setParent("无");
                    translate(null, parent);
                } else {
                    parent.setParent("无");
                    translate(null, parent);
                    for (PatrolStandardItems son : sonItem) {
                        allItem.add(son);
                        translate(null, son);
                        son.setParent(parent.getContent());
                    }
                }
            }
            standard.setPatrolStandardItemsList(allItem);
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "巡检表数据";
        ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), ExcelType.XSSF);
        //调用ExcelExportUtil.exportExcel方法生成workbook
        Workbook wb = ExcelExportUtil.exportExcel(exportParams, PatrolStandard.class, patrolStandardList);
        String fileName = "巡检表数据";
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
        GlobalThreadLocal.setDataFilter(filter);
    }

    /**
     * 配置项翻译
     *
     * @param items
     */
    private void translate(PatrolStandard standard, PatrolStandardItems items) {
        CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);
        //标准表翻译
        if (ObjectUtil.isNotEmpty(standard)) {
            // 适用部门
            List<PatrolStandardOrg> patrolStandardOrgs = standardOrgMapper.selectList(new LambdaQueryWrapper<PatrolStandardOrg>().eq(PatrolStandardOrg::getStandardCode, standard.getCode()).eq(PatrolStandardOrg::getDelFlag, CommonConstant.DEL_FLAG_0));
            ArrayList<String> orgNamelist = new ArrayList<>();
            if (CollUtil.isNotEmpty(patrolStandardOrgs)) {
                patrolStandardOrgs.forEach(t -> {
                    String departNameByOrgCode = sysBaseApi.getDepartNameByOrgCode(t.getOrgCode());
                    if (StrUtil.isNotEmpty(departNameByOrgCode)) {
                        orgNamelist.add(departNameByOrgCode);
                    }
                });
            }
            if (CollUtil.isNotEmpty(orgNamelist)) {
                String orgName = orgNamelist.stream().collect(Collectors.joining(";"));
                standard.setOrgName(orgName);
            }
            JSONObject csMajor = sysBaseApi.getCsMajorByCode(standard.getProfessionCode());
            List<DictModel> deviceType = sysBaseApi.getDictItems("patrol_device_type");
            deviceType = deviceType.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getDeviceType()))).collect(Collectors.toList());
            String isDeviceTypeName = deviceType.stream().map(DictModel::getText).collect(Collectors.joining());
            List<DictModel> status = sysBaseApi.getDictItems("patrol_standard_status");
            status = status.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getStatus()))).collect(Collectors.toList());
            String statusName = status.stream().map(DictModel::getText).collect(Collectors.joining());
            List<DictModel> standardType = sysBaseApi.getDictItems("patrol_standard_type");
            standardType = standardType.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getStandardType()))).collect(Collectors.toList());
            String standardTypeName = standardType.stream().map(DictModel::getText).collect(Collectors.joining());
            standard.setStandardTypeName(StrUtil.isNotEmpty(standardTypeName) ? standardTypeName:"");
            standard.setStatusName(statusName);
            standard.setDeviceTypeNames(isDeviceTypeName);
            standard.setProfessionCode(csMajor.getString("majorName"));

            List<DictModel> subsystemModels = bean.queryTableDictItemsByCode("cs_subsystem", "system_name", "system_code");
            subsystemModels = subsystemModels.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getSubsystemCode()))).collect(Collectors.toList());
            String subsystemName = subsystemModels.stream().map(DictModel::getText).collect(Collectors.joining());
            standard.setSubsystemCode(subsystemName);
            List<DictModel> deviceTypeModels = bean.queryTableDictItemsByCode("device_type", "name", "code");
            deviceTypeModels = deviceTypeModels.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getDeviceTypeCode()))).collect(Collectors.toList());
            String deviceTypeName = deviceTypeModels.stream().map(DictModel::getText).collect(Collectors.joining());
            standard.setDeviceTypeCode(deviceTypeName);
        }
        //配置项翻译
        Integer modules = 2;
        if (ObjectUtil.isNotEmpty(items)) {
            List<DictModel> hierarchyType = sysBaseApi.getDictItems("patrol_hierarchy_type");
            List<DictModel> patrolCheck = sysBaseApi.getDictItems("patrol_check");
            hierarchyType = hierarchyType.stream().filter(e -> e.getValue().equals(String.valueOf(items.getHierarchyType()))).collect(Collectors.toList());
            String hierarchyTypeName = hierarchyType.stream().map(DictModel::getText).collect(Collectors.joining());
            patrolCheck = patrolCheck.stream().filter(e -> e.getValue().equals(String.valueOf(items.getCheck()))).collect(Collectors.toList());
            String patrolCheckName = patrolCheck.stream().map(DictModel::getText).collect(Collectors.joining());
            items.setHierarchyTypeName(hierarchyTypeName);
            items.setCheckName(patrolCheckName);
            items.setDetailOrder(items.getOrder() == null ? "" : String.valueOf(items.getOrder()));
            List<DictModel> requiredDictModels = bean.queryDictItemsByCode("patrol_input_type");
            requiredDictModels = requiredDictModels.stream().filter(e -> e.getValue().equals(String.valueOf(items.getInputType()))).collect(Collectors.toList());
            String requiredDictName = requiredDictModels.stream().map(DictModel::getText).collect(Collectors.joining());
            items.setInputTypeName(requiredDictName);
            List<DictModel> requiredModels = bean.queryDictItemsByCode("patrol_item_required");
            requiredModels = requiredModels.stream().filter(e -> e.getValue().equals(String.valueOf(items.getRequired()))).collect(Collectors.toList());
            String requiredName = requiredModels.stream().map(DictModel::getText).collect(Collectors.joining());
            items.setRequiredDictName(requiredName);
            List<DictModel> modelList = patrolStandardMapper.querySysDict(modules);
            List<DictModel> dictModels = modelList.stream().filter(e -> e.getValue().equals(String.valueOf(items.getDictCode()))).collect(Collectors.toList());
            List<DictModel> resultDictModels = modelList.stream().filter(e -> e.getValue().equals(String.valueOf(items.getResultDictCode()))).collect(Collectors.toList());
            String modelName = dictModels.stream().map(DictModel::getText).collect(Collectors.joining());
            String resultModelName = resultDictModels.stream().map(DictModel::getText).collect(Collectors.joining());
            items.setDictCode(modelName);
            items.setResultDictCode(resultModelName);
            String regular = sysBaseApi.translateDict("regex", items.getRegular());
            items.setRegular(regular);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
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
            params.setNeedSave(true);
            List<PatrolStandardErrorModel> deviceAssemblyErrorModels = new ArrayList<>();
            List<PatrolStandard> standardList = new ArrayList<>();
            try {
                List<PatrolStandardModel> list = ExcelImportUtil.importExcel(file.getInputStream(), PatrolStandardModel.class, params);
                if (CollUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                if (CollUtil.isNotEmpty(list)) {
                    Iterator<PatrolStandardModel> iterator = list.iterator();
                    if (CollUtil.isNotEmpty(iterator)) {
                        while (iterator.hasNext()) {
                            PatrolStandardModel model = iterator.next();
                            boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                            if (a) {
                                iterator.remove();
                            }
                        }
                    }
                }
                for (PatrolStandardModel model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        StringBuilder standMistake = new StringBuilder();
                        PatrolStandard patrolStandard = new PatrolStandard();
                        List<PatrolStandardItems> patrolStandardItemsList = model.getPatrolStandardItemsList();
                        if (CollUtil.isNotEmpty(patrolStandardItemsList)) {
                            //判断配置项是否读取空数据
                            Iterator<PatrolStandardItems> iterator = patrolStandardItemsList.iterator();
                            if (CollUtil.isNotEmpty(iterator)) {
                                while (iterator.hasNext()) {
                                    PatrolStandardItems item = iterator.next();
                                    boolean a = XlsUtil.checkObjAllFieldsIsNull(item);
                                    if (a) {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                        //信息数据校
                        standard(model, patrolStandard, standMistake);
                        //配置项数据校验
                        itemsModel(model,patrolStandard);
                        if (standMistake.length() > 0 || model.getStandItemMistakeNumber()>0) {
                            errorLines++;
                        }
                        for (PatrolStandardItems patrolStandardItems : patrolStandardItemsList) {
                            PatrolStandardErrorModel errorModel = new PatrolStandardErrorModel();
                            BeanUtils.copyProperties(model, errorModel);
                            BeanUtils.copyProperties(patrolStandardItems, errorModel);
                            deviceAssemblyErrorModels.add(errorModel);
                        }
                        standardList.add(patrolStandard);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, deviceAssemblyErrorModels, errorMessage, successLines, url, type);
                } else {
                    LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    for (PatrolStandard patrolStandard : standardList) {
                        String standardCode = PatrolCodeUtil.getStandardCode();
                        List<OrgVO> orgCodeList = patrolStandard.getOrgCodeList();
                        for (OrgVO s : orgCodeList) {
                            PatrolStandardOrg patrolStandardOrg = new PatrolStandardOrg();
                            patrolStandardOrg.setOrgCode(s.getLabel());
                            patrolStandardOrg.setStandardCode(standardCode);
                            patrolStandardOrgService.save(patrolStandardOrg);
                        }
                        patrolStandard.setCode(standardCode);
                        patrolStandard.setUserId(user.getId());
                        patrolStandardMapper.insert(patrolStandard);
                        List<PatrolStandardItems> items = patrolStandard.getPatrolStandardItemsList();
                        if (CollUtil.isNotEmpty(items)) {
                            List<PatrolStandardItems> parents = items.stream().filter(e -> e.getHierarchyType() != null && e.getHierarchyType() == 0).collect(Collectors.toList());
                            List<PatrolStandardItems> sons = items.stream().filter(e -> e.getHierarchyType() != null && e.getHierarchyType() == 1).collect(Collectors.toList());
                            for (PatrolStandardItems item : parents) {
                                item.setParentId("0");
                                item.setStandardId(patrolStandard.getId());
                                patrolStandardItemsMapper.insert(item);
                                List<PatrolStandardItems> standardItems = sons.stream().filter(e -> e.getParent().equals(item.getContent())).collect(Collectors.toList());
                                if (CollUtil.isNotEmpty(standardItems)) {
                                    for (PatrolStandardItems standardItem : standardItems) {
                                        standardItem.setParentId(item.getId());
                                        standardItem.setStandardId(patrolStandard.getId());
                                        patrolStandardItemsMapper.insert(standardItem);
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

    @Override
    public void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/patrolstandardTemplate.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/patrolstandardTemplate.xlsx");
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
        ExcelSelectListUtil.selectList(workbook, "适用专业", 1, 1, majorModels);
        List<DictModel> subsystemModels = bean.queryTableDictItemsByCode("cs_subsystem", "system_name", "system_code");
        ExcelSelectListUtil.selectList(workbook, "适用子系统", 2, 2, subsystemModels);
        List<DictModel> standardTypes = bean.queryDictItemsByCode("patrol_standard_type");
        ExcelSelectListUtil.selectList(workbook, "标准表类型", 4, 4, standardTypes);
        List<DictModel> isDeviceTypeModels = bean.queryDictItemsByCode("patrol_device_type");
        ExcelSelectListUtil.selectList(workbook, "是否与设备类型相关", 5, 5, isDeviceTypeModels);
        List<DictModel> statusModels = bean.queryDictItemsByCode("patrol_standard_status");
        ExcelSelectListUtil.selectList(workbook, "生效状态", 6, 6, statusModels);
        List<DictModel> deviceTypeModels = bean.queryTableDictItemsByCode("device_type", "name", "code");
        ExcelSelectListUtil.selectList(workbook, "设备类型", 7, 7, deviceTypeModels);
        List<DictModel> hierarchyTypeModels = bean.queryDictItemsByCode("patrol_hierarchy_type");
        ExcelSelectListUtil.selectList(workbook, "层级类型", 8, 8, hierarchyTypeModels);
        List<DictModel> isStandardModels = bean.queryDictItemsByCode("patrol_check");
        ExcelSelectListUtil.selectList(workbook, "是否为巡视项目", 13, 13, isStandardModels);
        List<DictModel> requiredDictModels = bean.queryDictItemsByCode("patrol_input_type");
        ExcelSelectListUtil.selectList(workbook, "检查值类型", 15, 15, requiredDictModels);
        List<DictModel> requiredModels = bean.queryDictItemsByCode("patrol_item_required");
        ExcelSelectListUtil.selectList(workbook, "检查值是否必填", 16, 16, requiredModels);
        Integer modules = 2;
        List<DictModel> modelList = patrolStandardMapper.querySysDict(modules);
        ExcelSelectListUtil.selectList(workbook, "关联数据字典", 17, 17, modelList);
        List<DictModel> regularModels = bean.queryDictItemsByCode("regex");
        ExcelSelectListUtil.selectList(workbook, "数据校验表达式", 18, 18, regularModels);
        ExcelSelectListUtil.selectList(workbook, "巡检结果关联的数据字典", 19, 19, modelList);
        String fileName = "巡检标准导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "巡检标准导入模板.xlsx");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
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

    private Result<?> getErrorExcel(int errorLines, List<PatrolStandardModel> list, List<PatrolStandardErrorModel> deviceAssemblyErrorModels, List<String> errorMessage, int successLines, String url, String type) throws IOException {
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/patrolstandardError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/patrolstandardError.xlsx");
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
            PatrolStandardErrorModel deviceAssemblyErrorModel = deviceAssemblyErrorModels.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("standardName", deviceAssemblyErrorModel.getName());
            lm.put("majorName", deviceAssemblyErrorModel.getProfessionCode());
            lm.put("systemName", deviceAssemblyErrorModel.getSubsystemCode());
            lm.put("isdeviceType", deviceAssemblyErrorModel.getIsDeviceType());
            lm.put("statusName", deviceAssemblyErrorModel.getStatusName());
            lm.put("deviceTypeName", deviceAssemblyErrorModel.getDeviceTypeName());
            lm.put("standMistake", deviceAssemblyErrorModel.getStandMistake());

            lm.put("levelType", deviceAssemblyErrorModel.getHierarchyTypeName());
            lm.put("parent", deviceAssemblyErrorModel.getParent());
            lm.put("standradDetail", deviceAssemblyErrorModel.getContent());
            lm.put("code", deviceAssemblyErrorModel.getCode());
            lm.put("detailOrc", deviceAssemblyErrorModel.getDetailOrder());
            lm.put("isStandard", deviceAssemblyErrorModel.getCheckName());
            lm.put("qualityStandard", deviceAssemblyErrorModel.getQualityStandard());
            lm.put("checkValue", deviceAssemblyErrorModel.getInputTypeName());
            lm.put("isCheck", deviceAssemblyErrorModel.getRequiredDictName());
            if (StrUtil.isNotEmpty(deviceAssemblyErrorModel.getDictCode())) {
                String dictName = patrolStandardMapper.getDictName(deviceAssemblyErrorModel.getDictCode());
                if (StrUtil.isNotEmpty(dictName)) {
                    deviceAssemblyErrorModel.setDictCode(dictName);
                }
            }
            lm.put("dictCode", deviceAssemblyErrorModel.getDictCode());
            lm.put("resultDictCode", deviceAssemblyErrorModel.getResultDictCode());
            List<DictModel> regex = sysBaseApi.getDictItems("regex");
            if (CollUtil.isNotEmpty(regex)) {
                String dictText = regex.stream().filter(t -> StrUtil.equals(t.getValue(), deviceAssemblyErrorModel.getRegular())).map(DictModel::getText).limit(1).collect(Collectors.joining());
                if (StrUtil.isNotEmpty(dictText)) {
                    deviceAssemblyErrorModel.setRegular(dictText);
                }
            }
            lm.put("regular", deviceAssemblyErrorModel.getRegular());
            lm.put("specialCharacters", deviceAssemblyErrorModel.getSpecialCharacters());
            lm.put("procMethods", deviceAssemblyErrorModel.getProcMethods());
            lm.put("itemParentMistake", deviceAssemblyErrorModel.getItemParentMistake());
            lm.put("orgName", deviceAssemblyErrorModel.getOrgName());
            lm.put("standardTypeName", deviceAssemblyErrorModel.getStandardTypeName());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        int size = 4;
        int length = 8;
        for (PatrolStandardModel deviceModel : list) {
            for (int i = 0; i <= length; i++) {
                //合并单元格
                PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + deviceModel.getPatrolStandardItemsList().size() - 1, i, i);
            }
            size = size + deviceModel.getPatrolStandardItemsList().size();
        }

        try {
            String fileName = "巡检标准数据导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
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

    private void standard(PatrolStandardModel model, PatrolStandard patrolStandard, StringBuilder stringBuilder) {
        BeanUtils.copyProperties(model, patrolStandard);
        String name = model.getName();
        String majorName = model.getProfessionCode();
        String isDeviceType = model.getIsDeviceType();
        String orgName = model.getOrgName();
        String standardTypeName = model.getStandardTypeName();
        String statusName = model.getStatusName();
        String deviceTypeName = model.getDeviceTypeName();
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
        List<DictModel> standardTypes = sysBaseApi.getDictItems("patrol_standard_type");
        String standardTypeNames = null;
        if (CollUtil.isNotEmpty(standardTypes)) {
            standardTypeNames = standardTypes.stream().map(e -> e.getText()).collect(Collectors.joining());
        }
        if (StrUtil.isNotEmpty(majorName) && StrUtil.isNotEmpty(isDeviceType) && CollUtil.isNotEmpty(orgNameList) && StrUtil.isNotEmpty(standardTypeName) && StrUtil.isNotEmpty(statusName) && StrUtil.isNotEmpty(name)) {
            JSONObject major = sysBaseApi.getCsMajorByName(majorName);
            ArrayList<OrgVO> orgVOS = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(major)) {
                patrolStandard.setProfessionCode(major.getString("majorCode"));
                if (ObjectUtil.isNotEmpty(model.getSubsystemCode())) {
                    JSONObject systemName = sysBaseApi.getSystemName(major.getString("majorCode"), model.getSubsystemCode());
                    if (ObjectUtil.isNotEmpty(systemName)) {
                        patrolStandard.setSubsystemCode(systemName.getString("systemCode"));
                        if (ObjectUtil.isNotEmpty(deviceTypeName)) {
                            DeviceType d = sysBaseApi.getCsMajorByCodeTypeName(major.getString("majorCode"), deviceTypeName, systemName.getString("systemCode"));
                            if (ObjectUtil.isNull(d)) {
                                stringBuilder.append("系统不存在该专业的子系统的设备类型，");
                            } else {
                                patrolStandard.setDeviceTypeCode(d.getCode());
                            }
                        }

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
                patrolStandard.setOrgCodeList(orgVOS);
                if (!(standardTypeNames.contains(standardTypeName))) {
                    stringBuilder.append("系统不存在该标准表类型：" + standardTypeName + StrUtil.COMMA);
                } else {
                    standardTypes.forEach(t -> {
                        if (StrUtil.equals(t.getText(), standardTypeName)) {
                            patrolStandard.setStandardType(Integer.valueOf(t.getValue()));
                        }
                    });
                }
                if (!(PatrolConstant.IS_DEVICE_TYPE + PatrolConstant.IS_NOT_DEVICE_TYPE).contains(isDeviceType)) {
                    stringBuilder.append("是否与设备类型相关填写不规范，");
                } else {
                    patrolStandard.setDeviceType(isDeviceType.equals(PatrolConstant.IS_DEVICE_TYPE) ? 1 : 0);
                    if (patrolStandard.getDeviceType() == 1 && StrUtil.isNotEmpty(deviceTypeName)) {
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
                            patrolStandard.setDeviceTypeCode(d.getCode());
                        }
                    }
                    if (patrolStandard.getDeviceType() == 1 && StrUtil.isEmpty(deviceTypeName)) {
                        stringBuilder.append("设备类型未填写，");
                    }
                    if (patrolStandard.getDeviceType() == 0 && StrUtil.isNotEmpty(deviceTypeName)) {
                        stringBuilder.append("设备类型不用填写，");
                    }
                }
                if (!(PatrolConstant.ACTIVE + PatrolConstant.NOT_ACTIVE).contains(statusName)) {
                    stringBuilder.append("生效状态填写不规范，");
                } else {
                    patrolStandard.setStatus(statusName.equals(PatrolConstant.ACTIVE) ? 1 : 0);
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
                patrolStandard.setOrgCodeList(orgVOS);
                if (!(standardTypeNames.contains(standardTypeName))) {
                    stringBuilder.append("系统不存在该标准表类型：" + standardTypeName + StrUtil.COMMA);
                } else {
                    standardTypes.forEach(t -> {
                        if (StrUtil.equals(t.getText(), standardTypeName)) {
                            patrolStandard.setStandardType(Integer.valueOf(t.getValue()));
                        }
                    });
                }
                if (!(PatrolConstant.IS_DEVICE_TYPE + PatrolConstant.IS_NOT_DEVICE_TYPE).contains(isDeviceType)) {
                    stringBuilder.append("是否与设备类型相关填写不规范，");
                } else {
                    patrolStandard.setDeviceType(isDeviceType.equals(PatrolConstant.IS_DEVICE_TYPE) ? 1 : 0);
                }
                if (!(PatrolConstant.ACTIVE + PatrolConstant.NOT_ACTIVE).contains(statusName)) {
                    stringBuilder.append("生效状态填写不规范，");
                } else {
                    patrolStandard.setStatus(statusName.equals(PatrolConstant.ACTIVE) ? 1 : 0);
                }
            }
        } else {
            stringBuilder.append("巡视标准表名称、适用专业、适用部门、标准表类型、是否与设备类型相关、生效状态不能为空;");
        }
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            model.setStandMistake(stringBuilder.toString());
        }
    }
    private void itemsModel(PatrolStandardModel model,PatrolStandard patrolStandard) {
        List<PatrolStandardItems> standardItems = patrolStandard.getPatrolStandardItemsList();
        if (CollUtil.isNotEmpty(standardItems)) {
            int i = 0;
            Integer wrongNumber = 0;
            Map<Object, Integer> duplicateData = new HashMap<>(16);
            for (PatrolStandardItems items : standardItems) {
                StringBuilder stringBuilder = new StringBuilder();
                String hierarchyTypeName = items.getHierarchyTypeName();
                String itemsCode = items.getCode();
                String checkName = items.getCheckName();
                String content = items.getContent();
                String resultDictCode = items.getResultDictCode();
                //重复数据校验
                Integer s = duplicateData.get(items.getCode());
                if (s == null) {
                    duplicateData.put(items.getCode(), i);
                } else {
                    stringBuilder.append("该数据存在相同数据,");
                }
                if (StrUtil.isNotEmpty(hierarchyTypeName) && StrUtil.isNotEmpty(itemsCode) && StrUtil.isNotEmpty(checkName) && StrUtil.isNotEmpty(content) && StrUtil.isNotEmpty(resultDictCode)) {

                    List<PatrolStandardItems> itemsList = new ArrayList<>();
                    if (!(PatrolConstant.ONE_LEVEL + PatrolConstant.SON_LEVEL).contains(hierarchyTypeName)) {
                        stringBuilder.append("层级类型填写不规范,");
                    } else {
                        items.setHierarchyType(PatrolConstant.ONE_LEVEL.equals(hierarchyTypeName) ? PatrolConstant.TASK_UNDISPOSE : PatrolConstant.INPUT_TYPE_1);
                        if (items.getHierarchyType() == 0) {
                            if (ObjectUtil.isEmpty(items.getParent())) {
                                stringBuilder.append("层级为一级(父级填写无),");
                            } else {
                                if (!items.getParent().equals(PatrolConstant.NO_PARENT)) {
                                    stringBuilder.append("层级为一级(父级填写无),");
                                }
                            }
                        } else {
                            if (ObjectUtil.isEmpty(items.getParent())) {
                                stringBuilder.append("子级要有父级,");
                            } else {
                                itemsList = standardItems.stream().filter(e -> e.getContent() != null && e.getContent().equals(items.getParent()) && !e.equals(items) && e.getHierarchyTypeName().equals(PatrolConstant.ONE_LEVEL)).collect(Collectors.toList());
                                List<PatrolStandardItems> parentItems = standardItems.stream().filter(e -> e.getContent() != null && e.getContent().equals(items.getParent()) && e.getHierarchyTypeName().equals(PatrolConstant.ONE_LEVEL)).collect(Collectors.toList());
                                if (itemsList.size() == 0 && items.getHierarchyTypeName().equals(PatrolConstant.SON_LEVEL)) {
                                    stringBuilder.append("父级不存在,");
                                }
                                if (parentItems.size() != 1) {
                                    stringBuilder.append("存在相同的父级,");
                                }
                            }
                        }
                    }
                    if (ObjectUtil.isNotEmpty(items.getDetailOrder())) {
                        String regular = "^[0-9]*$";
                        Pattern pattern = Pattern.compile(regular);
                        Matcher matcher = pattern.matcher(items.getDetailOrder());
                        if (matcher.find()) {
                            items.setOrder(Integer.valueOf(items.getDetailOrder()));
                        } else {
                            stringBuilder.append("内容排序(填写必须是数字)，");
                        }
                    }
                    if (!(PatrolConstant.IS_DEVICE_TYPE + PatrolConstant.IS_NOT_DEVICE_TYPE).contains(checkName)) {
                        stringBuilder.append("是否为巡视项目填写不规范，");
                    } else {
                        items.setCheck(PatrolConstant.IS_DEVICE_TYPE.equals(checkName) ? 1 : 0);
                    }
                    if (items.getCheck() == 0 && items.getHierarchyType() == 0) {
                        if (ObjectUtil.isNotEmpty(items.getRegular()) || ObjectUtil.isNotEmpty(items.getQualityStandard()) || ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getInputTypeName()) || ObjectUtil.isNotEmpty(items.getRequiredDictName()) || ObjectUtil.isNotEmpty(items.getSpecialCharacters())) {
                            stringBuilder.append("质量标准、检查值类型、检查值是否必填、关联数据字典、数据校验表达式、特殊字符不用填写，");
                        }
                    }
                    if (items.getCheck() == 1 && items.getHierarchyType() == 0) {
                        List<PatrolStandardItems> sonList = standardItems.stream().filter(e -> e.getParent().equals(items.getContent())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(sonList)) {
                            stringBuilder.append("不能有子级，");
                        }
                        if (ObjectUtil.isNotEmpty(items.getRequiredDictName())) {
                            if (!(PatrolConstant.IS_DEVICE_TYPE + PatrolConstant.IS_NOT_DEVICE_TYPE).contains(items.getRequiredDictName())) {
                                stringBuilder.append("检查值是否必填选择不正确，");
                            } else {
                                items.setRequired(items.getRequiredDictName().equals(PatrolConstant.IS_DEVICE_TYPE) ? 1 : 0);
                            }
                        }
                        if (ObjectUtil.isNotEmpty(items.getInputTypeName())) {
                            List<DictModel> inputType = sysBaseApi.queryEnableDictItemsByCode("patrol_input_type");
                            if (CollUtil.isNotEmpty(inputType)) {
                                Map<String, String> collect = inputType.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
                                String value = collect.get(items.getInputTypeName());
                                if (StrUtil.isEmpty(value)) {
                                    stringBuilder.append("检查值类型选择不正确，");
                                } else {
                                    items.setInputType(Integer.valueOf(value));
                                }
                            } else {
                                stringBuilder.append("系统没有启用的检查值类型数据字典，");
                            }

                            if (items.getInputType() == 1) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getRegular()) || ObjectUtil.isNotEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值类型为无时：关联数据字典、数据检验表达式、检查值不用填写");
                                }
                            }
                            if (items.getInputType() == 2) {
                                if (ObjectUtil.isNotEmpty(items.getRegular()) || ObjectUtil.isNotEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值类型为选择项时：数据校验表达式、检查值不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getDictCode())) {
                                        String dictCode = patrolStandardMapper.getDictCode(items.getDictCode());
                                        if (ObjectUtil.isNotEmpty(dictCode)) {
                                            items.setDictCode(dictCode);
                                        } else {
                                            stringBuilder.append("关联数据字典选择不正确，");
                                        }
                                    } else {
                                        stringBuilder.append("关联数据字典不能为空，");
                                    }
                                }
                            }
                            if (items.getInputType() == 3) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值类型为输入项时：关联数据字典、检查值不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getRegular())) {
                                        List<DictModel> regex = sysBaseApi.getDictItems("regex");
                                        if (CollUtil.isNotEmpty(regex)) {
                                            String dictValue = regex.stream().filter(t -> StrUtil.equals(t.getText(), items.getRegular())).map(DictModel::getValue).limit(1).collect(Collectors.joining());
                                            if (StrUtil.isNotEmpty(dictValue)) {
                                                items.setRegular(dictValue);
                                            } else {
                                                stringBuilder.append("数据校验表达式选择不正确，");
                                            }
                                        } else {
                                            stringBuilder.append("数据校验表达式选择不正确，");
                                        }
                                    } else {
                                        stringBuilder.append("数据校验表达式不能为空，");
                                    }
                                }
                            }
                            if (items.getInputType() == 4) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getRegular())) {
                                    stringBuilder.append("检查值类型为特殊字符输入时：关联数据字典、数据检验表达式不用填写，");
                                }
                                if (ObjectUtil.isEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值不能为空，");
                                }
                            }
                        } else {
                            if (ObjectUtil.isNotEmpty(items.getRegular()) || ObjectUtil.isNotEmpty(items.getDictCode())) {
                                stringBuilder.append("关联数据字典、数据校验表达式、检查值不用填写，");
                            }
                        }
                    }
                    if (items.getCheck() == 0 && items.getHierarchyType() == 1) {
                        stringBuilder.append("是否为巡视项目(要选择为：是)，");
                    }
                    if (items.getCheck() == 1 && items.getHierarchyType() == 1) {
                        if (ObjectUtil.isNotEmpty(items.getRequiredDictName())) {
                            if (!(PatrolConstant.IS_DEVICE_TYPE + PatrolConstant.IS_NOT_DEVICE_TYPE).contains(items.getRequiredDictName())) {
                                stringBuilder.append("检查值是否必填选择不正确，");
                            } else {
                                items.setRequired(items.getRequiredDictName().equals(PatrolConstant.IS_DEVICE_TYPE) ? 1 : 0);
                            }
                        }
                        if (ObjectUtil.isNotEmpty(items.getInputTypeName())) {
                            List<DictModel> inputType = sysBaseApi.queryEnableDictItemsByCode("patrol_input_type");
                            if (CollUtil.isNotEmpty(inputType)) {
                                Map<String, String> collect = inputType.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
                                String value = collect.get(items.getInputTypeName());
                                if (StrUtil.isEmpty(value)) {
                                    stringBuilder.append("检查值类型选择不正确，");
                                } else {
                                    items.setInputType(Integer.valueOf(value));
                                }
                            } else {
                                stringBuilder.append("系统没有启用的检查值类型数据字典，");
                            }

                            if (items.getInputType() == 1) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getRegular()) || ObjectUtil.isNotEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值类型为无时：关联数据字典、数据检验表达式、检查值不用填写");
                                }
                            }
                            if (items.getInputType() == 2) {
                                if (ObjectUtil.isNotEmpty(items.getRegular()) || ObjectUtil.isNotEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值类型为选择项时：数据校验表达式、检查值不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getDictCode())) {
                                        String dictCode = patrolStandardMapper.getDictCode(items.getDictCode());
                                        if (ObjectUtil.isNotEmpty(dictCode)) {
                                            items.setDictCode(dictCode);
                                        } else {
                                            stringBuilder.append("关联数据字典选择不正确，");
                                        }
                                    } else {
                                        stringBuilder.append("关联数据字典不能为空，");
                                    }
                                }
                            }
                            if (items.getInputType() == 3) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值类型为输入项时：关联数据字典、检查值不用填写，");
                                } else {
                                    if (ObjectUtil.isNotEmpty(items.getRegular())) {
                                        List<DictModel> regex = sysBaseApi.getDictItems("regex");
                                        if (CollUtil.isNotEmpty(regex)) {
                                            String dictValue = regex.stream().filter(t -> StrUtil.equals(t.getText(), items.getRegular())).map(DictModel::getValue).limit(1).collect(Collectors.joining());
                                            if (StrUtil.isNotEmpty(dictValue)) {
                                                items.setRegular(dictValue);
                                            } else {
                                                stringBuilder.append("数据校验表达式选择不正确，");
                                            }
                                        } else {
                                            stringBuilder.append("数据校验表达式选择不正确，");
                                        }
                                    } else {
                                        stringBuilder.append("数据校验表达式不能为空，");
                                    }
                                }
                            }
                            if (items.getInputType() == 4) {
                                if (ObjectUtil.isNotEmpty(items.getDictCode()) || ObjectUtil.isNotEmpty(items.getRegular())) {
                                    stringBuilder.append("检查值类型为特殊字符输入时：关联数据字典、数据检验表达式不用填写，");
                                }
                                if (ObjectUtil.isEmpty(items.getSpecialCharacters())) {
                                    stringBuilder.append("检查值不能为空，");
                                }
                            }
                        } else {
                            if (ObjectUtil.isNotEmpty(items.getRegular()) || ObjectUtil.isNotEmpty(items.getDictCode())) {
                                stringBuilder.append("关联数据字典、数据校验表达式、检查值不用填写，");
                            }
                        }
                    }
                    //巡检结果数据字典
                    String dictCode = patrolStandardMapper.getDictCode(resultDictCode);
                    if (ObjectUtil.isNotEmpty(dictCode)) {
                        items.setResultDictCode(dictCode);
                    } else {
                        stringBuilder.append("巡检结果关联数据字典选择不正确，");
                    }
                } else {
                    stringBuilder.append("层级类型、巡视项内容、巡视项编号、是否为巡视项目、巡检结果关联数据字典要必填，");
                }
                if (stringBuilder.length() > 0) {
                    // 截取字符
                    wrongNumber++;
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    items.setItemParentMistake(stringBuilder.toString());
                }
            }
            model.setStandItemMistakeNumber(wrongNumber);
        }
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

}
