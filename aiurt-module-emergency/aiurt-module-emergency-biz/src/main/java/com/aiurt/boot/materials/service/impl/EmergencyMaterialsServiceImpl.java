package com.aiurt.boot.materials.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesItemMapper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.constant.MaterialsConstant;
import com.aiurt.boot.materials.dto.*;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsCategoryMapper;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesMapper;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.*;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsServiceImpl extends ServiceImpl<EmergencyMaterialsMapper, EmergencyMaterials> implements IEmergencyMaterialsService {

    @Autowired
    private EmergencyMaterialsMapper emergencyMaterialsMapper;
    @Autowired
    private EmergencyMaterialsCategoryMapper emergencyMaterialsCategoryMapper;
    @Autowired
    private EmergencyMaterialsInvoicesMapper materialsInvoicesMapper;
    @Autowired
    private EmergencyMaterialsInvoicesItemMapper materialsInvoicesItemMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    public Page<MaterialAccountDTO> getMaterialAccountList(Page<MaterialAccountDTO> pageList, MaterialAccountDTO condition) {
        List<MaterialAccountDTO> materialAccountList = emergencyMaterialsMapper.getMaterialAccountList(pageList, condition);
        List<PatrolStandardItemsModel> patrolStandardItemsModels = iSysBaseAPI.patrolStandardList(condition.getPatrolStandardId());
        materialAccountList.forEach(e -> {
            if (StrUtil.isNotBlank(e.getUserId())) {
                //根据负责人id查询负责人名称
                LoginUser userById = iSysBaseAPI.getUserById(e.getUserId());
                if (StrUtil.isNotBlank(userById.getRealname())) {
                    e.setUserName(userById.getRealname());
                }
            }
            if (StrUtil.isNotBlank(e.getPrimaryOrg())) {
                //根据部门编码查询部门名称
                SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(e.getPrimaryOrg());
                if (ObjectUtil.isNotEmpty(departByOrgCode)) {
                    e.setPrimaryName(departByOrgCode.getDepartName());
                }
            }
            if (StrUtil.isNotBlank(e.getLineCode())) {
                //根据线路编码查询线路名称
                String position = iSysBaseAPI.getPosition(e.getLineCode());
                if (StrUtil.isNotBlank(position)) {
                    e.setLineName(position);
                }
            }
            if (StrUtil.isNotBlank(e.getStationCode())) {
                //根据站点编码查询站点名称
                String position = iSysBaseAPI.getPosition(e.getStationCode());
                if (StrUtil.isNotBlank(position)) {
                    e.setStationName(position);
                }
            }
            if (StrUtil.isNotBlank(e.getPositionCode())) {
                //根据位置编码查询位置名称
                String position = iSysBaseAPI.getPosition(e.getPositionCode());
                if (StrUtil.isNotBlank(position)) {
                    e.setPositionName(position);
                }
            }
            //巡检项
            if (CollUtil.isNotEmpty(patrolStandardItemsModels)) {
                e.setPatrolStandardItemsModelList(patrolStandardItemsModels);
            }

            //是否有存在子级，有就查询出子级一起返回
            if (StrUtil.isNotBlank(e.getPid()) && e.getPid().equals("0") && StrUtil.isNotBlank(condition.getCategoryCode())){
                MaterialAccountDTO materialAccountDTO = new MaterialAccountDTO();
                materialAccountDTO.setPid(e.getCategoryId());
                materialAccountDTO.setMaterialsName(condition.getMaterialsName());
                List<MaterialAccountDTO> materialAccountList1 = emergencyMaterialsMapper.getMaterialAccountList(pageList, materialAccountDTO);
                if (CollUtil.isNotEmpty(materialAccountList1)){
                    materialAccountList1.forEach(q->{
                        if (StrUtil.isNotBlank(q.getUserId())) {
                            //根据负责人id查询负责人名称
                            LoginUser userById = iSysBaseAPI.getUserById(q.getUserId());
                            if (StrUtil.isNotBlank(userById.getRealname())) {
                                q.setUserName(userById.getRealname());
                            }
                        }
                        if (StrUtil.isNotBlank(q.getPrimaryOrg())) {
                            //根据部门编码查询部门名称
                            SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(q.getPrimaryOrg());
                            if (ObjectUtil.isNotEmpty(departByOrgCode)) {
                                q.setPrimaryName(departByOrgCode.getDepartName());
                            }
                        }
                        if (StrUtil.isNotBlank(q.getLineCode())) {
                            //根据线路编码查询线路名称
                            String position = iSysBaseAPI.getPosition(q.getLineCode());
                            if (StrUtil.isNotBlank(position)) {
                                q.setLineName(position);
                            }
                        }
                        if (StrUtil.isNotBlank(q.getStationCode())) {
                            //根据站点编码查询站点名称
                            String position = iSysBaseAPI.getPosition(q.getStationCode());
                            if (StrUtil.isNotBlank(position)) {
                                q.setStationName(position);
                            }
                        }
                        if (StrUtil.isNotBlank(q.getPositionCode())) {
                            //根据位置编码查询位置名称
                            String position = iSysBaseAPI.getPosition(q.getPositionCode());
                            if (StrUtil.isNotBlank(position)) {
                                q.setPositionName(position);
                            }
                        }
                    });
                    e.setChildren(materialAccountList1);
                }
            }
        });
        return pageList.setRecords(materialAccountList);
    }

    @Override
    public Page<EmergencyMaterialsInvoicesItem> getInspectionRecord(Page<EmergencyMaterialsInvoicesItem> pageList, EmergencyMaterialsInvoicesItem condition) {
        List<EmergencyMaterialsInvoicesItem> inspectionRecord = emergencyMaterialsMapper.getInspectionRecord(pageList, condition);
        inspectionRecord.forEach(e -> {
            if (StrUtil.isNotBlank(e.getLineCode())) {
                //根据线路编码查询线路名称
                String position = iSysBaseAPI.getPosition(e.getLineCode());
                e.setLineName(position);
            }
            if (StrUtil.isNotBlank(e.getStationCode())) {
                //根据站点编码查询站点名称
                String position = iSysBaseAPI.getPosition(e.getStationCode());
                e.setStationName(position);
            }
            if (StrUtil.isNotBlank(e.getPositionCode())) {
                //根据位置编码查询位置名称
                String position = iSysBaseAPI.getPosition(e.getPositionCode());
                e.setPositionName(position);
            }
            if (StrUtil.isNotBlank(e.getPatrolTeamCode())) {
                //根据巡视班组编码查询巡视班组名称
                String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(e.getPatrolTeamCode());
                e.setPatrolTeamName(departNameByOrgCode);
            }
            if (StrUtil.isNotBlank(e.getPatrolId())) {
                //根据巡视人id查询巡视人名称
                String[] split = e.getPatrolId().split(",");
                List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(split);
                if (CollUtil.isNotEmpty(loginUsers)){
                    String collect = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    e.setPatrolName(collect);
                }
            }
        });
        return pageList.setRecords(inspectionRecord);
    }

    @Override
    public MaterialPatrolDTO getMaterialPatrol() {
        MaterialPatrolDTO materialPatrolDTO = new MaterialPatrolDTO();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        materialPatrolDTO.setPatrolName(sysUser.getRealname());

        //根据用户id查询专业
        List<CsUserMajorModel> majorByUserId = iSysBaseAPI.getMajorByUserId(sysUser.getId());
        List<String> collect = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(collect)) {
            List<PatrolStandardDTO> patrolStandardList = emergencyMaterialsMapper.getPatrolStandardList(collect);
            materialPatrolDTO.setPatrolStandardDTOList(CollUtil.isNotEmpty(patrolStandardList) ? patrolStandardList : null);
        }

        //生成4位英文随机字符串
        String random = org.apache.commons.lang3.RandomStringUtils.random(4, true, false);
        //转换为大写
        String string = random.toUpperCase();

        //生成3位数字随机字符串
        String random1 = RandomStringUtils.random(3, false, true);

        //当前时间
        String replace = DateUtil.formatDate(DateUtil.date()).replace("-", "");

        //拼接字符串
        String string1 = String.join("-", string, replace, random1);

        materialPatrolDTO.setMaterialsPatrolCode(string1);
        return materialPatrolDTO;
    }

    @Override
    public Page<EmergencyMaterialsInvoicesItem> getMaterialInspection(Page<EmergencyMaterialsInvoicesItem> pageList, String id) {
        List<EmergencyMaterialsInvoicesItem> materialInspection = emergencyMaterialsMapper.getMaterialInspection(pageList, id,"0");
        LambdaQueryWrapper<EmergencyMaterialsInvoices> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(EmergencyMaterialsInvoices::getDelFlag,0);
        if (StrUtil.isNotBlank(id)){
            lambdaQueryWrapper1.eq(EmergencyMaterialsInvoices::getId,id);
        }
        EmergencyMaterialsInvoices emergencyMaterialsInvoices = materialsInvoicesMapper.selectOne(lambdaQueryWrapper1);
        for (int i = 0; i < materialInspection.size(); i++) {
            EmergencyMaterialsInvoicesItem e = materialInspection.get(i);
            if (StrUtil.isNotBlank(e.getCategoryCode())){
                LambdaQueryWrapper<EmergencyMaterialsCategory> lambdaQueryWrapper= new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(EmergencyMaterialsCategory::getDelFlag,0)
                                  .eq(EmergencyMaterialsCategory::getCategoryCode,e.getCategoryCode());
                EmergencyMaterialsCategory emergencyMaterialsCategory = emergencyMaterialsCategoryMapper.selectOne(lambdaQueryWrapper);
                if (StrUtil.isNotBlank(emergencyMaterialsCategory.getCategoryName())){
                    e.setCategoryName(emergencyMaterialsCategory.getCategoryName());
                }
            }
            if (StrUtil.isNotBlank(emergencyMaterialsInvoices.getLineCode())) {
                //根据线路编码查询线路名称
                String position = iSysBaseAPI.getPosition(emergencyMaterialsInvoices.getLineCode());
                e.setLineName(position);
            }
            if (StrUtil.isNotBlank(emergencyMaterialsInvoices.getStationCode())) {
                //根据站点编码查询站点名称
                String position = iSysBaseAPI.getPosition(emergencyMaterialsInvoices.getStationCode());
                e.setStationName(position);
            }
            if (StrUtil.isNotBlank(emergencyMaterialsInvoices.getPositionCode())) {
                //根据位置编码查询位置名称
                String position = iSysBaseAPI.getPosition(emergencyMaterialsInvoices.getPositionCode());
                e.setPositionName(position);
            }
            if ("0".equals(e.getPid()) && StrUtil.isNotBlank(e.getId())){
                List<EmergencyMaterialsInvoicesItem> materialInspection1 = emergencyMaterialsMapper.getMaterialInspection(pageList, id, e.getId());
                for (int j = 0; j < materialInspection1.size(); j++) {
                    EmergencyMaterialsInvoicesItem q = materialInspection1.get(j);
                    if (StrUtil.isNotBlank(q.getCategoryCode())){
                        LambdaQueryWrapper<EmergencyMaterialsCategory> lambdaQueryWrapper= new LambdaQueryWrapper<>();
                        lambdaQueryWrapper.eq(EmergencyMaterialsCategory::getDelFlag,0)
                                          .eq(EmergencyMaterialsCategory::getCategoryCode,q.getCategoryCode());
                        EmergencyMaterialsCategory emergencyMaterialsCategory = emergencyMaterialsCategoryMapper.selectOne(lambdaQueryWrapper);
                        if (StrUtil.isNotBlank(emergencyMaterialsCategory.getCategoryName())){
                            q.setCategoryName(emergencyMaterialsCategory.getCategoryName());
                        }
                    }
                    if (StrUtil.isNotBlank(emergencyMaterialsInvoices.getLineCode())) {
                        //根据线路编码查询线路名称
                        String position = iSysBaseAPI.getPosition(emergencyMaterialsInvoices.getLineCode());
                        q.setLineName(position);
                    }
                    if (StrUtil.isNotBlank(emergencyMaterialsInvoices.getStationCode())) {
                        //根据站点编码查询站点名称
                        String position = iSysBaseAPI.getPosition(emergencyMaterialsInvoices.getStationCode());
                        q.setStationName(position);
                    }
                    if (StrUtil.isNotBlank(emergencyMaterialsInvoices.getPositionCode())) {
                        //根据位置编码查询位置名称
                        String position = iSysBaseAPI.getPosition(emergencyMaterialsInvoices.getPositionCode());
                        q.setPositionName(position);
                    }
                }
                e.setSubLevel(materialInspection1);
            }
        }
        return pageList.setRecords(materialInspection);
    }

    @Override
    public ModelAndView getMaterialPatrolList(MaterialAccountDTO condition) {
        List<MaterialAccountDTO> materialAccountList = emergencyMaterialsMapper.getMaterialPatrolList(condition);
        List<PatrolStandardItemsModel> patrolStandardItemsModels = iSysBaseAPI.patrolStandardList(condition.getPatrolStandardId());
        materialAccountList.forEach(e -> {
            if (StrUtil.isNotBlank(e.getUserId())) {
                //根据负责人id查询负责人名称
                LoginUser userById = iSysBaseAPI.getUserById(e.getUserId());
                e.setUserName(userById.getRealname());
            }
            if (StrUtil.isNotBlank(e.getPrimaryOrg())) {
                //根据部门编码查询部门名称
                SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(e.getPrimaryOrg());
                e.setPrimaryName(departByOrgCode.getDepartName());
            }
            if (StrUtil.isNotBlank(e.getLineCode())) {
                //根据线路编码查询线路名称
                String position = iSysBaseAPI.getPosition(e.getLineCode());
                e.setLineName(position);
            }
            if (StrUtil.isNotBlank(e.getStationCode())) {
                //根据站点编码查询站点名称
                String position = iSysBaseAPI.getPosition(e.getStationCode());
                e.setStationName(position);
            }
            if (StrUtil.isNotBlank(e.getPositionCode())) {
                //根据位置编码查询位置名称
                String position = iSysBaseAPI.getPosition(e.getPositionCode());
                e.setPositionName(position);
            }
            //巡检项
            if (CollUtil.isNotEmpty(patrolStandardItemsModels)) {
                e.setPatrolStandardItemsModelList(patrolStandardItemsModels);
            }
        });
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "应急物资台账");
        //excel注解对象Class
        mv.addObject(NormalExcelConstants.CLASS, MaterialAccountDTO.class);
        //自定义表格参数
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("应急物资台账", "应急物资台账"));
        //导出数据列表
        mv.addObject(NormalExcelConstants.DATA_LIST, materialAccountList);
        return mv;
    }

    @Override
    public void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/emergencyMaterials.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/emergencyMaterials.xlsx");
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
        ExcelSelectListUtil.selectList(workbook, "应急物资分类", 2, 2, categoryList);
        List<DictModel> isDeviceTypeModels = bean.queryDictItemsByCode("flood_protection");
        //ExcelSelectListUtil.selectList(workbook, "是否与设备类型相关", 3, 3, isDeviceTypeModels);
        String fileName = "应急物资台账导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "应急物资台账导入模板.xlsx");
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
                tipMessage = "导入失败，文件类型不对。";
                return imporReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<EmergencyMaterials> materials = new ArrayList<>();
                List<EmergencyMaterialsModel> list = ExcelImportUtil.importExcel(file.getInputStream(), EmergencyMaterialsModel.class, params);
                Iterator<EmergencyMaterialsModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    EmergencyMaterialsModel model = iterator.next();
                    boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
                    if (b) {
                        iterator.remove();
                    }
                }
                if (CollUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                for (EmergencyMaterialsModel model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        EmergencyMaterials em = new EmergencyMaterials();
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        examine(model, em, stringBuilder, list);
                        if (stringBuilder.length() > 0) {
                            // 截取字符
                            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                            model.setWrongReason(stringBuilder.toString());
                            errorLines++;
                        }
                        materials.add(em);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                } else {
                    successLines = list.size();
                    for (EmergencyMaterials material : materials) {
                        emergencyMaterialsMapper.insert(material);
                    }
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
    @Override
    public void getInspectionRecordExportExcel(EmergencyMaterialsInvoicesDTO condition, HttpServletRequest request, HttpServletResponse response) throws IOException {
        XSSFWorkbook wb = getInspectionRecordExportExcelTemplate(condition, request, response);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos);
        byte[] brray = bos.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(brray);
        //拿到临时文件
        File fileTemp = new File("/templates/inspectionRecordExportExcel.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(inputStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String path = fileTemp.getAbsolutePath();
        TemplateExportParams params = new TemplateExportParams(path);
        Map<String, Object> map = new HashMap<String, Object>();
        //将数据写进自定义模板中
        List<String> selections = condition.getSelections();
        String s = selections.stream().findFirst().get();
        LambdaQueryWrapper<EmergencyMaterialsInvoices> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyMaterialsInvoices::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.eq(EmergencyMaterialsInvoices::getId, s);
        EmergencyMaterialsInvoices invoice = materialsInvoicesMapper.selectOne(queryWrapper);
        EmergencyMaterialsInvoicesDTO e = new EmergencyMaterialsInvoicesDTO();
        BeanUtils.copyProperties(invoice, e);
        //为了拿到巡检位置|存放地点
        String inspectionPosition = null;
        String storageLocation = null;
        if (StrUtil.isNotBlank(e.getLineCode())) {
            //根据线路编码查询线路名称
            String position = iSysBaseAPI.getPosition(e.getLineCode());
            e.setLineName(position);
            inspectionPosition = position;
        }
        if (StrUtil.isNotBlank(e.getStationCode())) {
            //根据站点编码查询站点名称
            String position = iSysBaseAPI.getPosition(e.getStationCode());
            if (ObjectUtil.isNotEmpty(inspectionPosition)) {
                inspectionPosition = inspectionPosition + position;
                storageLocation = position;
            } else {
                inspectionPosition = position;
                storageLocation = position;
            }
            e.setStationName(position);
        }
        if (StrUtil.isNotBlank(e.getPositionCode())) {
            //根据位置编码查询位置名称
            String position = iSysBaseAPI.getPosition(e.getPositionCode());
            e.setPositionName(position);
            if (ObjectUtil.isNotEmpty(inspectionPosition)) {
                inspectionPosition = inspectionPosition + position;
                storageLocation = position;
            } else {
                inspectionPosition = position;
                storageLocation = position;
            }
        }
        if (StrUtil.isNotBlank(e.getDepartmentCode())) {
            String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(e.getDepartmentCode());
            e.setPatrolTeamName(departNameByOrgCode);
        }
        if (StrUtil.isNotBlank(e.getUserId())) {
            LoginUser userById = iSysBaseAPI.getUserById(e.getUserId());
            e.setPatrolName(userById.getRealname());
        }
        List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        boolean isGroup = true;
        //根据物资编码分类，拿到数据
        List<EmergencyMaterialsInvoicesItemDTO> items = emergencyMaterialsMapper.getMaterialInspectionList(null, invoice.getId(), isGroup);
        Integer serialNumber = 1;
        //一级表头存放数据
        map.put("patrolDate", DateUtil.format(e.getPatrolDate(), "yyyy-MM-dd"));
        map.put("position", inspectionPosition);
        map.put("patrolName", e.getPatrolName());
        map.put("patrolTeamName", e.getPatrolTeamName());
        map.put("patrolResult", e.getInspectionResults() == 0 ? "异常" : "正常");
        //内容存放数据
        for (EmergencyMaterialsInvoicesItemDTO item : items) {
            List<EmergencyMaterialsInvoicesItemDTO> materialInspectionList = emergencyMaterialsMapper.getMaterialInspectionList(item.getMaterialsCode(), invoice.getId(), false);
            Map<String, String> lm = new HashMap<String, String>();
            lm.put("serialNumber", String.valueOf(serialNumber));
            EmergencyMaterialsCategory category = emergencyMaterialsCategoryMapper.selectOne(new LambdaQueryWrapper<EmergencyMaterialsCategory>()
                    .eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(EmergencyMaterialsCategory::getCategoryCode, item.getCategoryCode())
            );
            lm.put("categoryName", category.getCategoryName());
            lm.put("materialsCode", item.getMaterialsCode());
            lm.put("materialsName", item.getMaterialsName());
            lm.put("specification", item.getSpecification());
            lm.put("storageLocation", storageLocation);
            lm.put("number", item.getNumber());
            lm.put("abnormalCondition", item.getAbnormalCondition());
            listMap.add(lm);
            serialNumber++;
            //动态表头的表达式，与创建表头导入公式对应
            Integer t = 7;
            for (EmergencyMaterialsInvoicesItemDTO dto : materialInspectionList) {
                List<EmergencyMaterialsInvoicesItemDTO> sons = materialInspectionList.stream().filter(m -> m.getPid().equals(dto.getId())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(sons)) {
                    for (EmergencyMaterialsInvoicesItemDTO son : sons) {
                        lm.put(String.valueOf(t), son.getCheckResult() == 1 ? "√" : "×");
                        t++;
                    }
                } else {
                    lm.put(String.valueOf(t), dto.getCheckResult() == 1 ? "√" : "×");
                    t++;
                }
            }

        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        String fileName = "应急物资巡检记录.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + e.getPatrolDate() + inspectionPosition + "应急物资巡检记录.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        //删除文件
        fileTemp.delete();
    }

    @Override
    public void getInspectionRecordExportZip(EmergencyMaterialsInvoicesDTO condition, HttpServletRequest request, HttpServletResponse response) throws IOException {

        OutputStream outputStream = response.getOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        List<String> selections = condition.getSelections();
        LambdaQueryWrapper<EmergencyMaterialsInvoices> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyMaterialsInvoices::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.in(EmergencyMaterialsInvoices::getId, selections);
        List<EmergencyMaterialsInvoices> invoices = materialsInvoicesMapper.selectList(queryWrapper);
        response.setContentType("application/zip");
        response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(DateUtil.format(new Date(), "yyyy-MM-dd") + "应急物资巡检记录导出模板.zip", "UTF-8"));
        for (EmergencyMaterialsInvoices invoice : invoices) {
            List<String> invoiceId = new ArrayList<>();
            invoiceId.add(invoice.getId()) ;
            condition.setSelections(invoiceId);
            XSSFWorkbook wb = getInspectionRecordExportExcelTemplate(condition, request, response);
            //wb转输入流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            byte[] brray = bos.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(brray);
            File fileTemp = new File("/templates/inspectionRecordExportExcel.xlsx");
            try {
                //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                FileUtils.copyInputStreamToFile(inputStream, fileTemp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams params = new TemplateExportParams(path);
            Map<String, Object> map = new HashMap<String, Object>();
            EmergencyMaterialsInvoicesDTO e = new EmergencyMaterialsInvoicesDTO();
            BeanUtils.copyProperties(invoice, e);
            String inspectionPosition = null;
            String storageLocation = null;
            if (StrUtil.isNotBlank(e.getLineCode())) {
                //根据线路编码查询线路名称
                String position = iSysBaseAPI.getPosition(e.getLineCode());
                e.setLineName(position);
                inspectionPosition = position;
            }
            if (StrUtil.isNotBlank(e.getStationCode())) {
                //根据站点编码查询站点名称
                String position = iSysBaseAPI.getPosition(e.getStationCode());
                if (ObjectUtil.isNotEmpty(inspectionPosition)) {
                    inspectionPosition = inspectionPosition + position;
                    storageLocation = position;
                } else {
                    inspectionPosition = position;
                    storageLocation = position;
                }
                e.setStationName(position);
            }
            if (StrUtil.isNotBlank(e.getPositionCode())) {
                //根据位置编码查询位置名称
                String position = iSysBaseAPI.getPosition(e.getPositionCode());
                e.setPositionName(position);
                if (ObjectUtil.isNotEmpty(inspectionPosition)) {
                    inspectionPosition = inspectionPosition + position;
                    storageLocation = position;
                } else {
                    inspectionPosition = position;
                    storageLocation = position;
                }
            }
            if (StrUtil.isNotBlank(e.getDepartmentCode())) {
                String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(e.getDepartmentCode());
                e.setPatrolTeamName(departNameByOrgCode);
            }
            if (StrUtil.isNotBlank(e.getUserId())) {
                LoginUser userById = iSysBaseAPI.getUserById(e.getUserId());
                e.setPatrolName(userById.getRealname());
            }
            List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
            boolean isGroup = true;
            List<EmergencyMaterialsInvoicesItemDTO> items = emergencyMaterialsMapper.getMaterialInspectionList(null, invoice.getId(), isGroup);
            Integer a = 1;
            map.put("patrolDate", DateUtil.format(e.getPatrolDate(), "yyyy-MM-dd"));
            map.put("position", inspectionPosition);
            map.put("patrolName", e.getPatrolName());
            map.put("patrolTeamName", e.getPatrolTeamName());
            map.put("patrolResult", e.getInspectionResults() == 0 ? "异常" : "正常");
            for (EmergencyMaterialsInvoicesItemDTO item : items) {
                List<EmergencyMaterialsInvoicesItemDTO> materialInspectionList = emergencyMaterialsMapper.getMaterialInspectionList(item.getMaterialsCode(), invoice.getId(), false);
                Map<String, String> lm = new HashMap<String, String>();
                lm.put("serialNumber", String.valueOf(a));
                EmergencyMaterialsCategory category = emergencyMaterialsCategoryMapper.selectOne(new LambdaQueryWrapper<EmergencyMaterialsCategory>()
                        .eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(EmergencyMaterialsCategory::getCategoryCode, item.getCategoryCode())
                );
                lm.put("categoryName", category.getCategoryName());
                lm.put("materialsCode", item.getMaterialsCode());
                lm.put("materialsName", item.getMaterialsName());
                lm.put("specification", item.getSpecification());
                lm.put("storageLocation", storageLocation);
                lm.put("number", item.getNumber());
                lm.put("abnormalCondition", item.getAbnormalCondition());
                listMap.add(lm);
                a++;
                Integer t = 7;
                for (EmergencyMaterialsInvoicesItemDTO dto : materialInspectionList) {
                    List<EmergencyMaterialsInvoicesItemDTO> sons = materialInspectionList.stream().filter(m -> m.getPid().equals(dto.getId())).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(sons)) {
                        for (EmergencyMaterialsInvoicesItemDTO son : sons) {
                            lm.put(String.valueOf(t), son.getCheckResult() == 1 ? "√" : "×");
                            t++;
                        }
                    } else {
                        lm.put(String.valueOf(t), dto.getCheckResult() == 1 ? "√" : "×");
                        t++;
                    }
                }

            }
            map.put("maplist", listMap);
            Workbook workbook = ExcelExportUtil.exportExcel(params, map);
            String fileName = "应急物资巡检记录.xlsx";
            try {
                Random random = new Random();
                int c = random.nextInt(10);
                ZipEntry entry = new ZipEntry(c + " " + DateUtil.format(e.getPatrolDate(), "yyyy-MM-dd") + inspectionPosition + fileName);
                zipOutputStream.putNextEntry(entry);
                workbook.write(zipOutputStream);
                zipOutputStream.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            fileTemp.delete();
        }
        zipOutputStream.close();
        outputStream.flush();
        outputStream.close();

    }

    /**
     * 创建自定义模板
     *
     * @param condition
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    public XSSFWorkbook getInspectionRecordExportExcelTemplate(EmergencyMaterialsInvoicesDTO condition, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //创建workbook,即创建一个excel
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建excel里对应的sheet
        XSSFSheet sheet = wb.createSheet("应急物资巡检记录导出");
        /** 第三步，设置样式以及字体样式*/
        XSSFCellStyle titleStyle = createTitleCellStyle(wb);
        XSSFCellStyle firstHeadCellStyle = createFirstHeadCellStyle(wb);
        XSSFCellStyle secondHeaderStyle = createSecondHeadCellStyle(wb);
        XSSFCellStyle contentStyle = createContentCellStyle(wb);
        // 行号
        int rowNum = 0;
        // 创建第一页的第一行，索引从0开始
        XSSFRow row0 = sheet.createRow(rowNum++);
        // 设置行高
        row0.setHeight((short) 800);
        String title = "应急物资巡检记录导出";
        List<String> selections = condition.getSelections();
        LambdaQueryWrapper<EmergencyMaterialsInvoices> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyMaterialsInvoices::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.in(EmergencyMaterialsInvoices::getId, selections);
        List<EmergencyMaterialsInvoices> invoices = materialsInvoicesMapper.selectList(queryWrapper);
        //创建表头
        createHeadCell(wb, row0, title, invoices, titleStyle, contentStyle, sheet, rowNum, firstHeadCellStyle, secondHeaderStyle);
        return wb;
    }

    /**
     * 创建一级标题样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle createTitleCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //垂直对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 创建字体样式
        XSSFFont headerFont1 = (XSSFFont) wb.createFont();
        //字体加粗
        headerFont1.setBold(true);
        // 设置字体类型
        headerFont1.setFontName("宋体");
        // 设置字体大小
        headerFont1.setFontHeightInPoints((short) 16);
        // 为标题样式设置字体样式
        cellStyle.setFont(headerFont1);
        return cellStyle;
    }

    /**
     * 创建表头
     */
    public void createHeadCell(XSSFWorkbook wb, XSSFRow row0, String title, List<EmergencyMaterialsInvoices> invoices, XSSFCellStyle titleStyle, XSSFCellStyle contentStyle, XSSFSheet sheet, int rowNum, XSSFCellStyle firstHeaderStyle, XSSFCellStyle secondHeaderStyle) {
        XSSFCell c00 = row0.createCell(0);
        c00.setCellValue(title);
        c00.setCellStyle(titleStyle);
        // 第二行，设置一级表头名，cellRangeAddressList 合并表格集合
        List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
        String[] rowSeconds = {"巡检位置：{{position}}", "", "巡检人：{{patrolName}}", "", "巡检部门：{{patrolTeamName}}", "", "巡检结果：{{patrolResult}}"};
        XSSFRow rowSecond = sheet.createRow(rowNum++);
        rowSecond.setHeight((short) 700);
        //将 rowSeconds 的名称，即一级表头名 ，写入单元格，每个空一格，然后合并
        for (int i = 0; i < rowSeconds.length; i++) {
            XSSFCell tempCell = rowSecond.createCell(i);
            tempCell.setCellValue(rowSeconds[i]);
            tempCell.setCellStyle(firstHeaderStyle);
            Integer column = i + 1;
            sheet.addMergedRegion(new CellRangeAddress(1, 1, i, column));
            i++;
        }
        // 第三行，设置二级表头名，固定了七个，其他的是动态表头
        String[] row_first = {"序号", "应急物资分类", "应急物资编号", "应急物资名称", "规格型号", "存放地点", "数量"};
        List<String> titles = new LinkedList<>(Arrays.asList(row_first));
        List<EmergencyMaterialsInvoicesItemDTO> allList = new ArrayList<>();
        XSSFRow row1 = sheet.createRow(rowNum++);
        row1.setHeight((short) 350);
        for (EmergencyMaterialsInvoices invoice : invoices) {
            EmergencyMaterialsInvoicesItem emergencyMaterialsInvoicesItem = materialsInvoicesItemMapper.selectOne(new LambdaQueryWrapper<EmergencyMaterialsInvoicesItem>().eq(EmergencyMaterialsInvoicesItem::getInvoicesId, invoice.getId()).last("limit 1"));
            List<EmergencyMaterialsInvoicesItemDTO> items = emergencyMaterialsMapper.getMaterialInspectionList(emergencyMaterialsInvoicesItem.getMaterialsCode(), invoice.getId(), false);
            if (CollUtil.isNotEmpty(items)) {
                allList.addAll(items);
            }
        }
        if (CollUtil.isNotEmpty(allList)) {
            List<EmergencyMaterialsInvoicesItemDTO> parent = allList.stream().filter(i -> i.getPid().equals("0")).collect(Collectors.toList());
            for (EmergencyMaterialsInvoicesItemDTO s : parent) {
                titles.add(s.getContent());
                List<EmergencyMaterialsInvoicesItemDTO> son = allList.stream().filter(e -> e.getPid().equals(s.getId())).collect(Collectors.toList());
                //有子级的则用空格代替，要减一，空格代替是为了下面做合并，即第三、第四行合并
                if (CollUtil.isNotEmpty(son)) {
                    for (int i = 0; i < son.size() - 1; i++) {
                        titles.add("");
                    }
                }
            }
            //第三行，最后一列的表头名
            titles.add("异常情况记录");
            //标题合并单元格操作，参数依次为起始行，结束行，起始列，结束列 （索引0开始）
            CellRangeAddress cellAddresses1 = new CellRangeAddress(0, 0, 0, titles.size() - 1);
            cellRangeAddressList.add(cellAddresses1);
            //第三行，将二级表头写入单元格中
            for (int i = 0; i < titles.size(); i++) {
                XSSFCell tempCell = row1.createCell(i);
                tempCell.setCellValue(titles.get(i));
                tempCell.setCellStyle(secondHeaderStyle);
            }
            for (int i = 0; i < 7; i++) {
                CellRangeAddress cellAddresses = new CellRangeAddress(2, 3, i, i);
                cellRangeAddressList.add(cellAddresses);
            }
            //第四行，设置三级表头名，即二级中动态表头的子级
            XSSFRow row2 = sheet.createRow(rowNum++);
            row2.setHeight((short) 350);
            //row_second 第四行表头，只存子级，其他的用空格代替第三行对应的列，为了第三行中的父级合并列
            List<String> row_second = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                row_second.add("");
            }
            Integer size = row_second.size();
            Integer eigthLines = 7;
            for (EmergencyMaterialsInvoicesItemDTO s : parent) {
                List<EmergencyMaterialsInvoicesItemDTO> son = allList.stream().filter(e -> e.getPid().equals(s.getId())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(son) && son.size() > 1) {
                    CellRangeAddress cellAddresses = new CellRangeAddress(2, 2, size, size + son.size() - 1);
                    size = size + son.size();
                    cellRangeAddressList.add(cellAddresses);
                    for (EmergencyMaterialsInvoicesItemDTO itemDTO : son) {
                        row_second.add(eigthLines, itemDTO.getContent());
                        eigthLines++;
                    }
                } else {
                    if (son.size() == 1) {
                        for (EmergencyMaterialsInvoicesItemDTO itemDTO : son) {
                            row_second.add(eigthLines, itemDTO.getContent());
                        }
                    } else {
                        CellRangeAddress cellAddresses = new CellRangeAddress(2, 3, size, size);
                        cellRangeAddressList.add(cellAddresses);
                        row_second.add(eigthLines, "");
                    }
                    size++;
                    eigthLines++;
                }
            }
            row_second.add("");
            createCell(sheet, rowNum, contentStyle, row_second.size());
            //最后第三、第四行的最后一列合并
            CellRangeAddress addresses = new CellRangeAddress(2, 3, row_second.size() - 1, row_second.size() - 1);
            cellRangeAddressList.add(addresses);
            style(cellRangeAddressList, sheet);
            //将数据写入第四行
            for (int i = 0; i < row_second.size(); i++) {
                XSSFCell tempCell = row2.createCell(i);
                tempCell.setCellValue(row_second.get(i));
                tempCell.setCellStyle(secondHeaderStyle);
                sheet.setColumnWidth(i, 4500);
                if (i == row_second.size() - 1) {
                    //第一个表头的最后一格设置右边框
                    XSSFCellStyle cellStyle = wb.createCellStyle();
                    //右边框
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    XSSFCell a = rowSecond.createCell(i);
                    a.setCellStyle(cellStyle);
                }
            }
        }
    }

    /**
     * 创建一级表头样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle createFirstHeadCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //垂直对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 创建字体样式
        XSSFFont headerFont1 = (XSSFFont) wb.createFont();
        //字体加粗
        headerFont1.setBold(true);
        // 设置字体类型
        headerFont1.setFontName("宋体");
        // 设置字体大小
        headerFont1.setFontHeightInPoints((short) 11);
        // 为标题样式设置字体样式
        cellStyle.setFont(headerFont1);
        return cellStyle;
    }

    /**
     * 创建二级表头样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle createSecondHeadCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        // 设置自动换行
        cellStyle.setWrapText(true);
        //背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //垂直对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        //下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        //左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        //上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 创建字体样式
        XSSFFont headerFont = (XSSFFont) wb.createFont();
        //字体加粗
        headerFont.setBold(true);
        // 设置字体类型
        headerFont.setFontName("宋体");
        // 设置字体大小
        headerFont.setFontHeightInPoints((short) 11);
        // 为标题样式设置字体样式
        cellStyle.setFont(headerFont);

        return cellStyle;
    }

    /**
     * 合并，并设置样式
     *
     * @param cellRangeAddressList
     * @param sheet
     */
    private void style(List<CellRangeAddress> cellRangeAddressList, Sheet sheet) {
        for (CellRangeAddress cellAddresses : cellRangeAddressList) {
            sheet.addMergedRegion(cellAddresses);
            //合并后设置下边框
            RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
        }
    }


    /**
     * 创建内容样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle createContentCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        // 垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置自动换行
        cellStyle.setWrapText(true);
        //下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        //左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        //上边框
        cellStyle.setBorderTop(BorderStyle.THIN);

        // 生成12号字体
        XSSFFont font = wb.createFont();
        font.setColor((short) 8);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * 导入公式
     *
     * @param sheet
     * @param rowNum
     * @param contentStyle
     * @param rowSumNum
     */
    public void createCell(XSSFSheet sheet, int rowNum, XSSFCellStyle contentStyle, int rowSumNum) {

        //第三行，索引2，录入数据
        XSSFRow tempRow = sheet.createRow(rowNum++);
        tempRow.setHeight((short) 700);
        String[] row_third = new String[rowSumNum];
        String[] row_first = {"{{$fe:maplist t.serialNumber", "t.categoryName", "t.materialsCode", "t.materialsName", "t.specification", "t.storageLocation", "t.number"};
        for (int j = 0; j < row_first.length; j++) {
            XSSFCell tempCell = tempRow.createCell(j);
            tempCell.setCellValue(row_first[j]);
        }
        for (int j = 7; j < row_third.length - 1; j++) {
            XSSFCell tempCell = tempRow.createCell(j);
            tempCell.setCellValue("t." + j);
        }
        XSSFCell tempCell = tempRow.createCell(rowSumNum - 1);
        tempCell.setCellValue("t.abnormalCondition }}");
    }


    private Result<?> getErrorExcel(int errorLines, List<EmergencyMaterialsModel> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/emergencyMaterialsError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/emergencyMaterialsError.xlsx");
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
            EmergencyMaterialsModel categoryModel = list.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("emCode", categoryModel.getMaterialsCode());
            lm.put("emName", categoryModel.getMaterialsName());
            lm.put("categoryName", categoryModel.getCategoryName());
            lm.put("floodProtection", categoryModel.getFloodProtection());
            lm.put("number", categoryModel.getNumber());
            lm.put("stationCodeName", categoryModel.getStationName());
            lm.put("primaryName", categoryModel.getPrimaryName());
            lm.put("userName", categoryModel.getUserName());
            lm.put("unit", categoryModel.getUserName());
            lm.put("phone", categoryModel.getUnit());
            lm.put("wrongReason", categoryModel.getWrongReason());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "应急物资台账导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
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

    private void examine(EmergencyMaterialsModel model, EmergencyMaterials em, StringBuilder stringBuilder, List<EmergencyMaterialsModel> list) {
        BeanUtils.copyProperties(model, em);
        if (ObjectUtil.isEmpty(em.getMaterialsCode())) {
            stringBuilder.append("应急物资编号必填，");

        } else {
            if (ObjectUtil.isNotEmpty(em.getMaterialsName())) {
                if (ObjectUtil.isNotEmpty(model.getDepositPositionName())) {
                    List<EmergencyMaterialsModel> modelList = list.stream().filter(l -> !l.equals(model) && l.getMaterialsCode().equals(model.getMaterialsCode()) && l.getDepositPositionName().equals(model.getDepositPositionName())).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(modelList)) {
                        stringBuilder.append("导入的数据已有同一个物资、同一个位置的编号，");
                    }
                    List<String> depositPositionName = StrUtil.splitTrim(model.getDepositPositionName(), "/");
                    String lineCode = emergencyMaterialsMapper.getLineCode(depositPositionName.get(0));
                    String stationCode = emergencyMaterialsMapper.getStationCode(lineCode, depositPositionName.get(1));
                    String positionCode = null;
                    String[] split = model.getDepositPositionName().split("");
                    Integer count = 0;
                    for (String s : split) {
                        if (s.equals("/")) {
                            count++;
                        }
                    }
                    if (count == 3 || count == 2) {
                        if (depositPositionName.size() > 2) {
                            int i = model.getDepositPositionName().indexOf("/", 2);
                            positionCode = emergencyMaterialsMapper.getPositionCode(lineCode, stationCode, model.getDepositPositionName().substring(i));
                        }
                        if (ObjectUtil.isNotEmpty(lineCode) && ObjectUtil.isNotEmpty(stationCode)) {
                            LambdaQueryWrapper<EmergencyMaterials> queryWrapper = new LambdaQueryWrapper<>();
                            queryWrapper.eq(EmergencyMaterials::getDelFlag, CommonConstant.DEL_FLAG_0);
                            queryWrapper.eq(EmergencyMaterials::getMaterialsCode, model.getMaterialsCode());
                            queryWrapper.eq(EmergencyMaterials::getLineCode, lineCode);
                            queryWrapper.eq(EmergencyMaterials::getStationCode, stationCode);
                            queryWrapper.last("limit 1");
                            if (ObjectUtil.isNotEmpty(positionCode)) {
                                queryWrapper.eq(EmergencyMaterials::getPositionCode, positionCode);
                            }
                            EmergencyMaterials materials = emergencyMaterialsMapper.selectOne(queryWrapper);
                            if (ObjectUtil.isNotEmpty(materials)) {
                                stringBuilder.append("已添加同一个物资、同一个位置的编号，");
                            }
                        }
                    }
                }
            }
        }
        if (ObjectUtil.isEmpty(em.getMaterialsName())) {
            stringBuilder.append("应急物资名称必填，");
        } else {
            if (ObjectUtil.isNotEmpty(em.getMaterialsCode())) {
                MaterialBaseDTO materialBaseDTO = emergencyMaterialsMapper.getMaterials(em.getMaterialsCode(), em.getMaterialsName());
                if (ObjectUtil.isNotEmpty(materialBaseDTO)) {
                    em.setMaterialsCode(materialBaseDTO.getCode());
                    em.setMaterialsName(materialBaseDTO.getName());
                    em.setSpecification(materialBaseDTO.getSpecifications());
                    em.setUnit(materialBaseDTO.getUnit());
                } else {
                    stringBuilder.append("应急物资名称、应急物资编号这两个不是同一类，");
                }
            }
        }
        if (ObjectUtil.isEmpty(em.getCategoryName())) {
            stringBuilder.append("应急物资分类必填，");
        } else {
            EmergencyMaterialsCategory categoryFatherName = emergencyMaterialsCategoryMapper.selectOne(new LambdaQueryWrapper<EmergencyMaterialsCategory>().eq(EmergencyMaterialsCategory::getCategoryName, model.getCategoryName()).eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1"));
            if (ObjectUtil.isEmpty(categoryFatherName)) {
                stringBuilder.append("应急物资分类不存在，");
            }
            if (categoryFatherName.getStatus() == 0) {
                stringBuilder.append("该应急物资分类已被禁用，");
            } else {
                List<EmergencyMaterialsCategory> deptAll = emergencyMaterialsCategoryMapper.selectList(new LambdaQueryWrapper<EmergencyMaterialsCategory>().eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0));
                Set<EmergencyMaterialsCategory> deptUpList = getDeptUpList(deptAll, categoryFatherName);
                List<EmergencyMaterialsCategory> disabledList = deptUpList.stream().filter(e -> e.getStatus() == 0).collect(Collectors.toList());
                if (disabledList.size() > 0) {
                    stringBuilder.append("该应急物资分类已被禁用，");
                } else {
                    em.setCategoryCode(categoryFatherName.getCategoryCode());
                }

            }
        }
        if (ObjectUtil.isEmpty(model.getFloodProtection())) {
            stringBuilder.append("是否为防汛物资必填，");
        } else {
            if (!(MaterialsConstant.IS_FLOOD_P + MaterialsConstant.NO_FLOOD_P).contains(model.getFloodProtection())) {
                stringBuilder.append("是否为防汛物资填写不规范，");
            } else {
                em.setFloodProtection(MaterialsConstant.NO_FLOOD_P.equals(model.getFloodProtection()) ? 0 : 1);
            }

        }
        if (ObjectUtil.isEmpty(model.getNumber())) {
            stringBuilder.append("数量必填，");
        } else {
            String regular = "^[0-9]*$";
            Pattern pattern = Pattern.compile(regular);
            Matcher matcher = pattern.matcher(em.getPhone());
            if (!matcher.find()) {
                stringBuilder.append("数量填写必须是数字，");
            }
            em.setNumber(Integer.valueOf(model.getNumber()));
        }
        if (ObjectUtil.isEmpty(model.getDepositPositionName())) {
            stringBuilder.append("存放位置必填，");
        } else {
            String[] split = model.getDepositPositionName().split("");
            Integer count = 0;
            for (String s : split) {
                if (s.equals("/")) {
                    count++;
                }
            }
            if (count < 2 || count > 3) {
                stringBuilder.append("存放位置填写不规范，");
            } else {
                List<String> depositPositionName = StrUtil.splitTrim(model.getDepositPositionName(), "/");
                String lineCode = emergencyMaterialsMapper.getLineCode(depositPositionName.get(0));
                String stationCode = emergencyMaterialsMapper.getStationCode(lineCode, depositPositionName.get(1));
                String positionCode = null;
                //线路/站点/位置
                if (depositPositionName.size() > 2) {
                    positionCode = emergencyMaterialsMapper.getPositionCode(lineCode, stationCode, depositPositionName.get(2));
                }
                if (ObjectUtil.isNotEmpty(lineCode)) {
                    em.setLineCode(lineCode);
                    String lineStation = emergencyMaterialsMapper.getStationCode(lineCode, depositPositionName.get(1));
                    if (ObjectUtil.isEmpty(lineStation)) {
                        stringBuilder.append("该线路下的站点不存在，");
                    } else {
                        em.setStationCode(stationCode);
                        if (ObjectUtil.isEmpty(positionCode)) {
                            stringBuilder.append("该线路下的站点的位置不存在，");
                        } else {
                            em.setPositionCode(positionCode);
                        }

                    }
                } else {
                    stringBuilder.append("线路不存在，");
                }
                if (ObjectUtil.isNotEmpty(stationCode)) {
                    em.setStationCode(stationCode);
                } else {
                    stringBuilder.append("站点不存在，");
                }
                if (ObjectUtil.isNotEmpty(positionCode)) {
                    em.setPositionCode(stationCode);
                } else {
                    stringBuilder.append("位置不存在，");
                }
            }
        }
        if (ObjectUtil.isEmpty(em.getPrimaryName())) {
            stringBuilder.append("主管部门必填，");
        } else {
            String orgCode = emergencyMaterialsMapper.getOrgCode(em.getPrimaryName());
            if (ObjectUtil.isEmpty(orgCode)) {
                stringBuilder.append("填写的主管部门不存在，");
            } else {
                em.setPrimaryOrg(orgCode);
                if (ObjectUtil.isNotEmpty(em.getUserName())) {
                    String userId = emergencyMaterialsMapper.getUserId(em.getUserName(), orgCode);
                    if (ObjectUtil.isEmpty(userId)) {
                        stringBuilder.append("该部门下不存在该用户，");
                    } else {
                        em.setUserId(userId);
                    }
                }
            }
        }
        if (ObjectUtil.isEmpty(em.getUserName())) {
            stringBuilder.append("负责人必填，");
        }
        if (ObjectUtil.isEmpty(em.getPhone())) {
            stringBuilder.append("联系电话必填，");
        } else {
            String regular = "^\\d{11}$";
            Pattern pattern = Pattern.compile(regular);
            Matcher matcher = pattern.matcher(em.getPhone());
            if (!matcher.find()) {
                stringBuilder.append("联系电话填写不规范，");
            }
        }
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            model.setWrongReason(stringBuilder.toString());
        }
    }

    public static Set<EmergencyMaterialsCategory> getDeptUpList(List<EmergencyMaterialsCategory> deptAll, EmergencyMaterialsCategory categoryFatherName) {
        Set<EmergencyMaterialsCategory> set = new HashSet<>();
        if (ObjectUtil.isNotEmpty(categoryFatherName)) {
            String parentId = categoryFatherName.getPid();
            List<EmergencyMaterialsCategory> parentDepts = deptAll.stream().filter(item -> item.getId().equals(parentId)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(parentDepts)) {
                EmergencyMaterialsCategory parentDept = parentDepts.get(0);
                set.add(parentDept);
                Set<EmergencyMaterialsCategory> deptUpTree = getDeptUpList(deptAll, parentDept);
                if (deptUpTree != null) {
                    set.addAll(deptUpTree);
                }
            }
        }
        return set;
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
