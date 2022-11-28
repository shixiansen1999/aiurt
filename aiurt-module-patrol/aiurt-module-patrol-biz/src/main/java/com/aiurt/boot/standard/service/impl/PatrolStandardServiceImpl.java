package com.aiurt.boot.standard.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.standard.dto.*;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.aiurt.boot.utils.PatrolCodeUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.device.entity.DeviceType;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ISysBaseAPI iSysBaseAPI;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    public IPage<PatrolStandardDto> pageList(Page page, PatrolStandard patrolStandard) {
        List<PatrolStandardDto> page1 = patrolStandardMapper.pageList(page, patrolStandard);
        // 以下包含的代码权限拦截局部过滤
        boolean filter = GlobalThreadLocal.setDataFilter(false);
        page1.forEach(a -> {
            a.setNumber(baseMapper.number(a.getCode()));
        });
        // 以上包含的代码权限拦截局部过滤
        GlobalThreadLocal.setDataFilter(filter);
        return page.setRecords(page1);
    }

    @Override
    public IPage<PatrolStandardDto> pageLists(Page page, PatrolStandardDto patrolStandard) {
        List<PatrolStandardDto> page1 = patrolStandardMapper.pageLists(page, patrolStandard, patrolStandard.getStations());
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
        for (PatrolStandard standard : patrolStandardList) {
            JSONObject csMajor = iSysBaseAPI.getCsMajorByCode(standard.getProfessionCode());
            List<DictModel> deviceType = iSysBaseAPI.getDictItems("patrol_device_type");
            deviceType = deviceType.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getDeviceType()))).collect(Collectors.toList());
            String deviceTypeName = deviceType.stream().map(DictModel::getText).collect(Collectors.joining());
            List<DictModel> status = iSysBaseAPI.getDictItems("patrol_standard_status");
            status = status.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getStatus()))).collect(Collectors.toList());
            String statusName = status.stream().map(DictModel::getText).collect(Collectors.joining());
            standard.setStatusName(statusName);
            standard.setDeviceTypeNames(deviceTypeName);
            standard.setProfessionCode(csMajor.getString("majorName"));
            List<PatrolStandardItems> patrolStandardItemsList = patrolStandardItemsMapper.selectList(new LambdaQueryWrapper<PatrolStandardItems>().
                    eq(PatrolStandardItems::getStandardId, standard.getId()).eq(PatrolStandardItems::getDelFlag, CommonConstant.DEL_FLAG_0).
                    eq(PatrolStandardItems::getHierarchyType,CommonConstant.DEL_FLAG_0));
            standard.setPatrolStandardItemsList(patrolStandardItemsList);
            for (PatrolStandardItems patrolStandardItems : patrolStandardItemsList) {
                 PatrolStandardItems translate = translate(patrolStandardItems);
                BeanUtils.copyProperties(translate,patrolStandardItems);
                List<PatrolStandardItems> itemsList = patrolStandardItemsMapper.selectList(new LambdaQueryWrapper<PatrolStandardItems>().eq(PatrolStandardItems::getParentId, patrolStandardItems.getId()));
                if (CollUtil.isNotEmpty(itemsList)) {
                    List<PatrolStandardItemsExport> exportList = itemsList.stream().map(
                            todo -> {
                                PatrolStandardItems standardItems = translate(todo);
                                PatrolStandardItemsExport to = new PatrolStandardItemsExport();
                                BeanUtils.copyProperties(standardItems, to);
                                return to;
                            }
                    ).collect(Collectors.toList());
                }


            }
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
    }
    public PatrolStandardItems translate(PatrolStandardItems items) {
        List<DictModel> hierarchyType = iSysBaseAPI.getDictItems("patrol_hierarchy_type");
        hierarchyType = hierarchyType.stream().filter(e -> e.getValue().equals(String.valueOf(items.getHierarchyType()))).collect(Collectors.toList());
        String hierarchyTypeName = hierarchyType.stream().map(DictModel::getText).collect(Collectors.joining());
        List<DictModel> patrolCheck = iSysBaseAPI.getDictItems("patrol_check");
        patrolCheck = patrolCheck.stream().filter(e -> e.getValue().equals(String.valueOf(items.getCheck()))).collect(Collectors.toList());
        String patrolCheckName = patrolCheck.stream().map(DictModel::getText).collect(Collectors.joining());
        items.setHierarchyTypeName(hierarchyTypeName);
        items.setCheckName(patrolCheckName);
        return items;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        String url = null;
        // 错误信息
        int  errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, successLines, errorMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(2);
            params.setNeedSave(true);
            List<PatrolStandardErrorModel> deviceAssemblyErrorModels = new ArrayList<>();
            try {
                List<PatrolStandardModel> list = ExcelImportUtil.importExcel(file.getInputStream(), PatrolStandardModel.class, params);
                for (PatrolStandardModel model : list) {
                    //数据重复校验
                    if (ObjectUtil.isNotEmpty(model)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        PatrolStandard  patrolStandard = new PatrolStandard();
                        //信息数据校验
                        standard(model, patrolStandard, stringBuilder);
                        //配置项数据校验
                        List<PatrolStandardItems> itemsList = itemsModel(model, errorLines);
                        if (stringBuilder.length() > 0) {
                            // 截取字符
                            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                            model.setStandMistake(stringBuilder.toString());
                            errorLines++;
                        }
                        if(errorLines>0)
                        {
                            for (PatrolStandardItems patrolStandardItems : itemsList) {
                                PatrolStandardErrorModel errorModel = new PatrolStandardErrorModel();
                                BeanUtils.copyProperties(model,errorModel);
                                BeanUtils.copyProperties(patrolStandardItems,errorModel);
                                deviceAssemblyErrorModels.add(errorModel);

                            }
                        }
                        patrolStandard.setPatrolStandardItemsList(itemsList);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines,list,deviceAssemblyErrorModels,errorMessage,successLines,url, type);
                }
                else
                {
                    LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    for (PatrolStandardModel patrolStandardModel : list) {
                        PatrolStandard patrolStandard = new PatrolStandard();
                        BeanUtils.copyProperties(patrolStandardModel,patrolStandard);
                        String standardCode = PatrolCodeUtil.getStandardCode();
                        patrolStandard.setCode(standardCode);
                        patrolStandard.setUserId(user.getId());
                        patrolStandardMapper.insert(patrolStandard);
                         List<PatrolStandardItems> items = patrolStandardModel.getPatrolStandardItemsList();
                         if(CollUtil.isNotEmpty(items))
                         {
                              List<PatrolStandardItems> parents = items.stream().filter(e -> e.getHierarchyType() == 0).collect(Collectors.toList());
                              List<PatrolStandardItems> sons = items.stream().filter(e -> e.getHierarchyType() == 1).collect(Collectors.toList());
                             for (PatrolStandardItems item : parents) {
                                 item.setParentId("0");
                                 item.setStandardId(patrolStandard.getId());
                                 patrolStandardItemsMapper.insert(item);
                                 List<PatrolStandardItems> standardItems = sons.stream().filter(e->e.getParent().equals(item.getContent())).collect(Collectors.toList());
                                 if(CollUtil.isNotEmpty(standardItems))
                                 {
                                     for (PatrolStandardItems standardItem : standardItems) {
                                         standardItem.setParentId(item.getId());
                                         standardItem.setStandardId(patrolStandard.getId());
                                         patrolStandardItemsMapper.insert(standardItem);
                                     }
                                 }
                             }
                         }
                    }
                }
                return Result.ok("文件导入成功！");

            } catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if(msg!=null && msg.contains("Duplicate entry")){
                    return Result.error("文件导入失败:有重复数据！");
                }else{
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
        return Result.ok("文件导入失败！");
    }

    private Result<?> getErrorExcel(int errorLines, List<PatrolStandardModel> list,List<PatrolStandardErrorModel> deviceAssemblyErrorModels, List<String> errorMessage, int successLines, String url, String type) throws IOException {
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/patrolstandardError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp= new File("/templates/patrolstandardError.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<String, Object> errorMap = new HashMap<String, Object>();
        List<Map<String, String>> listMap = new ArrayList<>();
        for (int i = 0; i < deviceAssemblyErrorModels.size(); i++) {
            PatrolStandardErrorModel deviceAssemblyErrorModel = deviceAssemblyErrorModels.get(i);
            Map<String, String> lm = new HashMap<>();
            //错误报告获取信息
            lm.put("standardName",deviceAssemblyErrorModel.getName());
            lm.put("majorName",deviceAssemblyErrorModel.getProfessionCode());
            lm.put("isdeviceType",deviceAssemblyErrorModel.getIsDeviceType());
            lm.put("statusName",deviceAssemblyErrorModel.getStatusName());
            lm.put("deviceTypeName",deviceAssemblyErrorModel.getDeviceTypeName());
            lm.put("standMistake",deviceAssemblyErrorModel.getStandMistake());

            lm.put("levelType",deviceAssemblyErrorModel.getHierarchyTypeName());
            lm.put("parent",deviceAssemblyErrorModel.getParent());
            lm.put("standradDetail",deviceAssemblyErrorModel.getContent());
            lm.put("code",deviceAssemblyErrorModel.getCode());
            lm.put("detailOrc",deviceAssemblyErrorModel.getDetailOrder());
            lm.put("isStandard",deviceAssemblyErrorModel.getCheckName());
            lm.put("qualityStandard",deviceAssemblyErrorModel.getQualityStandard());
            lm.put("itemParentMistake",deviceAssemblyErrorModel.getItemParentMistake());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
        sheetsMap.put(0, errorMap);
        Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        int size = 4;
        for (PatrolStandardModel deviceModel : list) {
            for (int i = 0; i <= 5; i++) {
                //合并单元格
                PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0),size,size + deviceModel.getPatrolStandardItemsList().size()-1,i,i);
            }
            size = size + deviceModel.getPatrolStandardItemsList().size();
        }

        try {
            String fileName = "巡检标准数据导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imporReturnRes(errorLines, successLines, errorMessage,true,url);
    }

    private void standard(PatrolStandardModel model, PatrolStandard patrolStandard, StringBuilder stringBuilder) {
        String name  = model.getName();
        String  majorName = model.getProfessionCode();
        String  isDeviceType = model.getIsDeviceType();
        String  statusName = model.getStatusName();
        String  deviceTypeName = model.getDeviceTypeName();
        if (StrUtil.isNotEmpty(majorName) && StrUtil.isNotEmpty(isDeviceType) && StrUtil.isNotEmpty(statusName) && StrUtil.isNotEmpty(name)) {
            JSONObject major = iSysBaseAPI.getCsMajorByName(majorName);
            if(ObjectUtil.isNotEmpty(major))
            {
                patrolStandard.setProfessionCode(major.getString("majorCode"));
                if(!isDeviceType.equals(PatrolConstant.IS_DEVICE_TYPE)&&!isDeviceType.equals(PatrolConstant.IS_NOT_DEVICE_TYPE))
                {
                    stringBuilder.append("是否与设备类型相关填写不规范，");
                }
                if(!statusName.equals(PatrolConstant.ACTIVE)&&!statusName.equals(PatrolConstant.NOT_ACTIVE))
                {
                    stringBuilder.append("生效状态填写不规范，");
                }
                if(StrUtil.isNotEmpty(deviceTypeName))
                {
                    DeviceType d = iSysBaseAPI.getCsMajorByCodeTypeName(major.getString("majorCode"), deviceTypeName);
                    if(ObjectUtil.isNull(d))
                    {
                        stringBuilder.append("系统不存在该专业下的设备类型，");
                    }
                    else
                    {
                        patrolStandard.setDeviceTypeCode(d.getCode());
                    }
                }
            }
            else
            {
                stringBuilder.append("系统不存在该专业，");
                if(!isDeviceType.equals(PatrolConstant.IS_DEVICE_TYPE)&&!isDeviceType.equals(PatrolConstant.IS_NOT_DEVICE_TYPE))
                {
                    stringBuilder.append("是否与设备类型相关填写不规范，");
                }
                if(!statusName.equals(PatrolConstant.ACTIVE)&&!statusName.equals(PatrolConstant.NOT_ACTIVE))
                {
                    stringBuilder.append("生效状态填写不规范，");
                }
            }
        }
        else {
            stringBuilder.append("巡视标准表名称，适用专业，是否与设备类型相关，生效状态，设备类型不能为空;");
        }
    }

    private List<PatrolStandardItems> itemsModel(PatrolStandardModel model, int errorLines) {
        List<PatrolStandardItems> standardItems = model.getPatrolStandardItemsList();
        if (CollUtil.isNotEmpty(standardItems)) {
            int i = 0;
            Map<Object, Integer> duplicateData = new HashMap<>();
            for (PatrolStandardItems items : standardItems) {
                StringBuilder stringBuildera = new StringBuilder();
                 String hierarchyTypeName = items.getHierarchyTypeName();
                 String itemsCode = items.getCode();
                 String checkName = items.getCheckName();
                 String content = items.getContent();
                //重复数据校验
                Integer s = duplicateData.get(items.getCode());
                if (s == null) {
                    duplicateData.put(items.getCode(), i);
                } else {
                    stringBuildera.append("该数据存在相同数据,");
                }
                if (StrUtil.isNotEmpty(hierarchyTypeName) && StrUtil.isNotEmpty(itemsCode) && StrUtil.isNotEmpty(checkName)&& StrUtil.isNotEmpty(content)) {
                   List <PatrolStandardItems> one = patrolStandardItemsMapper.selectList(new LambdaQueryWrapper<PatrolStandardItems>().eq(PatrolStandardItems::getCode, itemsCode));
                   List<PatrolStandardItems> itemsList = new ArrayList<>();
                   if(items.equals(PatrolConstant.SON_LEVEL))
                   {
                       itemsList =standardItems.stream().filter(e -> e.getContent().equals(items.getParent())&&!e.equals(items)&&e.getHierarchyType().equals(PatrolConstant.ONE_LEVEL)).collect(Collectors.toList());
                   }
                    if(itemsList.size()==0&&items.equals(PatrolConstant.SON_LEVEL))
                    {
                        stringBuildera.append("父级不存在,");
                    }
                    if(!hierarchyTypeName.equals(PatrolConstant.ONE_LEVEL)&&!hierarchyTypeName.equals(PatrolConstant.SON_LEVEL))
                    {
                        stringBuildera.append("层级类型填写不规范,");
                    }
                    else
                    {
                        items.setHierarchyType(PatrolConstant.ONE_LEVEL.equals(hierarchyTypeName)?PatrolConstant.TASK_UNDISPOSE:PatrolConstant.INPUT_TYPE_1);
                    }
                    if(!checkName.equals(PatrolConstant.IS_DEVICE_TYPE)&&!checkName.equals(PatrolConstant.IS_NOT_DEVICE_TYPE))
                    {
                        stringBuildera.append("是否为巡视项目填写不规范，");
                    }
                    else
                    {
                        items.setCheck(PatrolConstant.ONE_LEVEL.equals(hierarchyTypeName)?PatrolConstant.TASK_UNDISPOSE:PatrolConstant.INPUT_TYPE_1);
                    }
                    if(CollUtil.isNotEmpty(one))
                    {
                        stringBuildera.append("编码数据库已经存在，");
                    }

                }
                else
                {
                    stringBuildera.append("层级类型，巡视项内容，巡视项编号、是否为巡视项目不能为空");
                }
                if (stringBuildera.length() > 0) {
                    // 截取字符
                    stringBuildera.deleteCharAt(stringBuildera.length() - 1);
                    items.setItemParentMistake(stringBuildera.toString());
                    errorLines++;
                }
            }
        }
        return standardItems;
    }


    public static Result<?> imporReturnRes(int errorLines, int successLines, List<String> errorMessage, boolean isType,String failReportUrl ) throws IOException {
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
            res.setMessage("导入失败，文件类型不对。");
            res.setCode(200);
            return res;
        }

    }

}
