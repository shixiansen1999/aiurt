package com.aiurt.modules.floodpreventioninformation.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.floodpreventioninformation.entity.FloodPreventionInformation;
import com.aiurt.modules.floodpreventioninformation.mapper.FloodPreventionInformationMapper;
import com.aiurt.modules.floodpreventioninformation.model.FloodPreventionInformationModel;
import com.aiurt.modules.floodpreventioninformation.service.IFloodPreventionInformationService;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.SysDepartMapper;
import com.aiurt.modules.system.mapper.SysDictMapper;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
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
import java.util.stream.Collectors;

/**
 * @Description: flood_prevention_information
 * @Author: zwl
 * @Date:   2023-04-24
 * @Version: V1.0
 */
@Service
public class FloodPreventionInformationServiceImpl extends ServiceImpl<FloodPreventionInformationMapper, FloodPreventionInformation> implements IFloodPreventionInformationService {

    @Autowired
    private CsStationMapper  csStationMapper;

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysDepartMapper sysDepartMapper;

    @Autowired
    private FloodPreventionInformationMapper floodPreventionInformationMapper;

    @Autowired
    private ISysBaseAPI baseApi;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

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
                tipMessage = "导入失败，文件类型错误！";
                return imporReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            try {
                List<FloodPreventionInformation> floodPreventionInformationArrayList = new ArrayList<>();

                List<FloodPreventionInformationModel> list = ExcelImportUtil.importExcel(file.getInputStream(), FloodPreventionInformationModel.class, params);
                Iterator<FloodPreventionInformationModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    FloodPreventionInformationModel model = iterator.next();
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
                for (FloodPreventionInformationModel model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        FloodPreventionInformation em = new FloodPreventionInformation();
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        examine(model, em, stringBuilder, list);
                        if (stringBuilder.length() > 0) {
                            // 截取字符
                            model.setMistake(stringBuilder.toString());
                            errorLines++;
                        }else{
                            floodPreventionInformationArrayList.add(em);
                        }
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                }else {
                    successLines = list.size();
                    for (FloodPreventionInformation floodPreventionInformation : floodPreventionInformationArrayList) {
                        //插入数据库
                        floodPreventionInformationMapper.insert(floodPreventionInformation);
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
        return null;
    }

    private void examine(FloodPreventionInformationModel floodPreventionInformationModel, FloodPreventionInformation floodPreventionInformation, StringBuilder stringBuilder, List<FloodPreventionInformationModel> list) {
        if (StrUtil.isBlank(floodPreventionInformationModel.getStationName())){
            stringBuilder.append("站点名称必填，");
        }else {
            CsStation csStation = csStationMapper.selectOne(new LambdaQueryWrapper<CsStation>()
                    .eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(CsStation::getStationName, floodPreventionInformationModel.getStationName()));
            if(ObjectUtil.isNotNull(csStation)){
                floodPreventionInformation.setStationCode(csStation.getStationCode());
                floodPreventionInformation.setStationName(csStation.getStationName());
                floodPreventionInformation.setLineCode(csStation.getLineCode());
            }else {
                stringBuilder.append("站点名称不存在，");
            }
        }
        if (StrUtil.isBlank(floodPreventionInformationModel.getOrgName())){
            stringBuilder.append("所属部门必填，");
        }else {
            LambdaQueryWrapper<SysDepart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(SysDepart::getDepartName,floodPreventionInformationModel.getOrgName())
                    .last("limit 1");
            SysDepart sysDepart = sysDepartMapper.selectOne(lambdaQueryWrapper);
            if (ObjectUtil.isNotNull(sysDepart)){
                floodPreventionInformation.setOrgCode(sysDepart.getOrgCode());
            }else {
                stringBuilder.append("所属部门不存在，");
            }
        }

        if (StrUtil.isNotBlank(floodPreventionInformationModel.getMasterName())){
            floodPreventionInformation.setMasterName(floodPreventionInformationModel.getMasterName());
        }

        if (StrUtil.isNotBlank(floodPreventionInformationModel.getInsuranceName())){
            floodPreventionInformation.setInsuranceName(floodPreventionInformationModel.getInsuranceName());
        }

        if (ObjectUtil.isNull(floodPreventionInformationModel.getEmergencyPersonnel())){
            stringBuilder.append("应急队伍人数必填，");
        }else {
            floodPreventionInformation.setEmergencyPersonnel(Long.valueOf(floodPreventionInformationModel.getEmergencyPersonnel()));
        }

        if (StrUtil.isBlank(floodPreventionInformationModel.getEmergencyPeopleName())){
            stringBuilder.append("应急队伍负责人必填，");
        }
        if (StrUtil.isBlank(floodPreventionInformationModel.getWorkNo())){
            stringBuilder.append("工号必填，");
        }
        if(StrUtil.isNotBlank(floodPreventionInformationModel.getEmergencyPeopleName()) && StrUtil.isNotBlank(floodPreventionInformationModel.getWorkNo())){
            String emergencyPeopleName = floodPreventionInformationModel.getEmergencyPeopleName();
            String workNo = floodPreventionInformationModel.getWorkNo();

            List<String> stringList = Arrays.asList(emergencyPeopleName.split(","));
            List<String> stringList1 = Arrays.asList(workNo.split(","));

            List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(SysUser::getRealname, stringList)
                    .in(SysUser::getWorkNo, stringList1));
            if (CollectionUtil.isNotEmpty(sysUsers)){
                List<String> collect = sysUsers.stream().map(SysUser::getId).collect(Collectors.toList());
                String join = CollectionUtil.join(collect, ",");
                floodPreventionInformation.setEmergencyPeople(join);
            }else {
                stringBuilder.append("无法找到数据，工号或者应急队伍负责人输入错误！，");
            }
            if (sysUsers.size()<stringList.size() || sysUsers.size()<stringList1.size()){
                stringBuilder.append("无法找到数据，工号或者应急队伍负责人输入错误！，");
            }
        }

        if(ObjectUtil.isNull(floodPreventionInformationModel.getReservePersonnel())){
            stringBuilder.append("后备人数必填，");
        }else {
            floodPreventionInformation.setReservePersonnel(Long.valueOf(floodPreventionInformationModel.getReservePersonnel()));
        }

        if (StrUtil.isBlank(floodPreventionInformationModel.getPeripheryWaterName())){
            stringBuilder.append("周边是否存在排水不畅必填，");
        }else {
            List<DictModel> peripheryWater = sysDictMapper.queryDictItemsByCode("periphery_water");
            DictModel dictModel1 = Optional.ofNullable(peripheryWater).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(floodPreventionInformationModel.getPeripheryWaterName())).findFirst().orElse(null);
               if(ObjectUtil.isNotNull(dictModel1)){
                   floodPreventionInformation.setPeripheryWater(Long.valueOf(dictModel1.getValue()));
               }else {
                   stringBuilder.append("周边是否存在排水输入错误，");
               }
        }

        if (StrUtil.isBlank(floodPreventionInformationModel.getPeripheryGroundsName())){
            stringBuilder.append("周边是否存在工地必填，");
        }else {
            List<DictModel> peripheryGrounds = sysDictMapper.queryDictItemsByCode("periphery_grounds");
            DictModel dictModel1 = Optional.ofNullable(peripheryGrounds).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(floodPreventionInformationModel.getPeripheryGroundsName())).findFirst().orElse(null);
            if(ObjectUtil.isNotNull(dictModel1)){
                    floodPreventionInformation.setPeripheryGrounds(Long.valueOf(dictModel1.getValue()));
                }else {
                    stringBuilder.append("该周边是否存在工地输入错误，");
                }
         }

        if (StrUtil.isBlank(floodPreventionInformationModel.getMaterialLocation())){
            stringBuilder.append("防汛物资配备所在位置必填，");
        }else {
            floodPreventionInformation.setMaterialLocation(floodPreventionInformationModel.getMaterialLocation());
        }

        if (StrUtil.isBlank(floodPreventionInformationModel.getEntrance())){
            stringBuilder.append("防汛出入口必填，");
        }else {
            floodPreventionInformation.setEntrance(floodPreventionInformationModel.getEntrance());
        }

        if (StrUtil.isBlank(floodPreventionInformationModel.getGradeName())){
            stringBuilder.append("防汛等级必填，");
        }else {
            List<DictModel> floodPreventionLevel = sysDictMapper.queryDictItemsByCode("floodPrevention_level");
            DictModel dictModel1 = Optional.ofNullable(floodPreventionLevel).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(floodPreventionInformationModel.getGradeName())).findFirst().orElse(null);
            if(ObjectUtil.isNotNull(dictModel1)){
                floodPreventionInformation.setGrade(Long.valueOf(dictModel1.getValue()));
            }else {
                stringBuilder.append("防汛等级输入错误，");
            }
        }
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            floodPreventionInformationModel.setMistake(stringBuilder.toString());
        }
    }

    private Result<?> getErrorExcel(int errorLines, List<FloodPreventionInformationModel> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/floodInformationError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/floodInformationError.xlsx");
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
            FloodPreventionInformationModel floodPreventionInformationModel = list.get(i);
            Map<String, String> map = new HashMap<>(16);
            //错误报告获取信息
            map.put("stationName",floodPreventionInformationModel.getStationName());
            map.put("orgName",floodPreventionInformationModel.getOrgName());
            map.put("masterName",floodPreventionInformationModel.getMasterName());
            map.put("insuranceName",floodPreventionInformationModel.getInsuranceName());
            map.put("emergencyPersonnel",floodPreventionInformationModel.getEmergencyPersonnel());
            map.put("emergencyPeopleName",floodPreventionInformationModel.getEmergencyPeopleName());
            map.put("reservePersonnel",floodPreventionInformationModel.getReservePersonnel());
            map.put("peripheryWaterName",floodPreventionInformationModel.getPeripheryWaterName());
            map.put("peripheryGroundsName",floodPreventionInformationModel.getPeripheryGroundsName());
            map.put("materialLocation",floodPreventionInformationModel.getMaterialLocation());
            map.put("entrance",floodPreventionInformationModel.getEntrance());
            map.put("gradeName",floodPreventionInformationModel.getGradeName());
            map.put("workNo",floodPreventionInformationModel.getWorkNo());
            map.put("mistake",floodPreventionInformationModel.getMistake());
            listMap.add(map);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "防汛信息管理错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url =fileName;
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

    @Override
    public void exportTemplateXl(HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/floodInformation.xlsx");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp= new File("/templates/floodInformation.xlsx");
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
        String fileName = "防汛信息导入模板.xlsx";

        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename="+"防汛信息导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IPage<FloodPreventionInformation> getList(Page<FloodPreventionInformation> page, FloodPreventionInformation floodPreventionInformation) {

        LambdaQueryWrapper<FloodPreventionInformation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FloodPreventionInformation::getDelFlag,CommonConstant.DEL_FLAG_0);
        if (StrUtil.isNotBlank(floodPreventionInformation.getStationName())){
            lambdaQueryWrapper.like(FloodPreventionInformation::getStationName,floodPreventionInformation.getStationName());
        }
        if (StrUtil.isNotBlank(floodPreventionInformation.getLineCode())){
            //由于没保存线路，所以线路转成站点
            List<String> stationCodes = baseApi.getStationCodeByLineCode(floodPreventionInformation.getLineCode());
            lambdaQueryWrapper.in(FloodPreventionInformation::getStationCode,stationCodes);
        }
        if(StrUtil.isNotBlank(floodPreventionInformation.getCodeCc())){
            if(floodPreventionInformation.getCodeCc().contains(CommonConstant.SYSTEM_SPLIT_STR)){
                String[] split = floodPreventionInformation.getCodeCc().split(CommonConstant.SYSTEM_SPLIT_STR);
                int length = split.length;
                switch (length){
                    case 2:
                        lambdaQueryWrapper.eq(FloodPreventionInformation::getLineCode, split[0]);
                        lambdaQueryWrapper.eq(FloodPreventionInformation::getStationCode, split[1]);
                        break;
                    default:
                        lambdaQueryWrapper.eq(FloodPreventionInformation::getLineCode, split[0]);
                }
            }else{
                lambdaQueryWrapper.eq(FloodPreventionInformation::getLineCode, floodPreventionInformation.getCodeCc());
            }
        }
        if (StrUtil.isNotBlank(floodPreventionInformation.getStationCode())){
            lambdaQueryWrapper.eq(FloodPreventionInformation::getStationCode,floodPreventionInformation.getStationCode());
        }
        if (StrUtil.isNotBlank(floodPreventionInformation.getScreenStationName())){
            lambdaQueryWrapper.eq(FloodPreventionInformation::getStationName,floodPreventionInformation.getScreenStationName());
        }
        lambdaQueryWrapper.orderByDesc(FloodPreventionInformation::getCreateTime);

        Page<FloodPreventionInformation> pageList = this.page(page, lambdaQueryWrapper);
        GlobalThreadLocal.setDataFilter(false);
        pageList.getRecords().forEach(e->{
            if (ObjectUtil.isNotNull(e.getPeripheryWater())){
                e.setPeripheryWaterName(baseApi.translateDict("periphery_water",String.valueOf(e.getPeripheryWater())));
            }
            if (ObjectUtil.isNotNull(e.getPeripheryGrounds())){
                e.setPeripheryGroundsName(baseApi.translateDict("periphery_grounds",String.valueOf(e.getPeripheryGrounds())));
            }
            if (StrUtil.isNotBlank(e.getOrgCode())){
                e.setOrgName( baseApi.getDepartNameByOrgCode(e.getOrgCode()));
            }
            if(ObjectUtil.isNotNull(e.getGrade())){
                e.setGradeName(baseApi.translateDict("floodPrevention_level",String.valueOf(e.getGrade())));
            }
            if (StrUtil.isNotBlank(e.getEntrance()) && ObjectUtil.isNotNull(e.getGrade())){
                e.setEntranceGrade(e.getEntrance()+baseApi.translateDict("floodPrevention_level",String.valueOf(e.getGrade())));
            }else {
                if (StrUtil.isNotBlank(e.getEntrance())){
                    e.setEntranceGrade(e.getEntrance());
                }
                if(ObjectUtil.isNotNull(e.getGrade())){
                    e.setEntranceGrade(baseApi.translateDict("floodPrevention_level",String.valueOf(e.getGrade())));
                }
            }
            if(StrUtil.isNotBlank(e.getEmergencyPeople())){
                StringBuilder stringBuilder = new StringBuilder();
                String[] split = e.getEmergencyPeople().split(",");
                List<String> stringList = Arrays.asList(split);
                stringList.forEach(str->{
                    LoginUser userById = baseApi.getUserById(str);
                    stringBuilder.append(userById.getRealname()+",");
                });
                e.setEmergencyPeopleName(stringBuilder.deleteCharAt(stringBuilder.length()-1).toString());
            }
        });
        return pageList;
    }

}
