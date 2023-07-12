package com.aiurt.modules.faultexternallinestarel.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.faultexternallinestarel.dto.FaultExternalLineStaRelImport;
import com.aiurt.modules.faultexternallinestarel.entity.FaultExternalLineStaRel;
import com.aiurt.modules.faultexternallinestarel.mapper.FaultExternalLineStaRelMapper;
import com.aiurt.modules.faultexternallinestarel.service.IFaultExternalLineStaRelService;
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
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: fault_external_line_sta_rel
 * @Author: aiurt
 * @Date: 2023-06-13
 * @Version: V1.0
 */
@Service
public class FaultExternalLineStaRelServiceImpl extends ServiceImpl<FaultExternalLineStaRelMapper, FaultExternalLineStaRel> implements IFaultExternalLineStaRelService {
    @Autowired
    private FaultExternalLineStaRelMapper lineStaRelMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    public IPage<FaultExternalLineStaRel> pageList(Page<FaultExternalLineStaRel> page, FaultExternalLineStaRel faultExternalLineStaRel) {
        List<FaultExternalLineStaRel> list = lineStaRelMapper.pageList(page,faultExternalLineStaRel);
        return page.setRecords(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(FaultExternalLineStaRel staRel) {
        FaultExternalLineStaRel faultExternalLineStaRel = getStaRel(staRel);
        lineStaRelMapper.insert(faultExternalLineStaRel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(FaultExternalLineStaRel staRel) {
        FaultExternalLineStaRel faultExternalLineStaRel = getStaRel(staRel);
        lineStaRelMapper.updateById(faultExternalLineStaRel);
    }


    private FaultExternalLineStaRel getStaRel(FaultExternalLineStaRel staRel) {
        JSONObject csStation = sysBaseApi.getCsStationByCode(staRel.getStationCode());
        staRel.setLineCode(csStation.getString("lineCode"));
        String lineName = csStation.getString("lineName");
        String stationName = csStation.getString("stationName");
        staRel.setCorrespondenceScc(lineName + "/" + stationName);
        LambdaQueryWrapper<FaultExternalLineStaRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultExternalLineStaRel::getStationCode, staRel.getStationCode());
        if(ObjectUtil.isNotEmpty(staRel.getId())){
            queryWrapper.ne(FaultExternalLineStaRel::getId, staRel.getId());
        }
        List<FaultExternalLineStaRel> list = lineStaRelMapper.selectList(queryWrapper);
        if(CollUtil.isNotEmpty(list)){
            throw new AiurtBootException("该线路下的站点已被添加！");
        }
        return staRel;
    }
    @Override
    public List<FaultExternalLineStaRel> getList(FaultExternalLineStaRel faultExternalLineStaRel) {
        List<FaultExternalLineStaRel> list = lineStaRelMapper.pageList(null, faultExternalLineStaRel);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        int successLines = 0;
        String tipMessage = "";
        int errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()){
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                tipMessage = "导入失败，文件类型不对。";
                return importReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            List<FaultExternalLineStaRel> list = new ArrayList<>();
            List<FaultExternalLineStaRelImport> importExcelList = ExcelImportUtil.importExcel(file.getInputStream(), FaultExternalLineStaRelImport.class, params);
            Iterator<FaultExternalLineStaRelImport> iterator = importExcelList.iterator();
            while (iterator.hasNext()) {
                FaultExternalLineStaRelImport model = iterator.next();
                boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
                if (b) {
                    iterator.remove();
                }
            }
            if (CollUtil.isEmpty(importExcelList)) {
                tipMessage = "导入失败，该文件为空。";
                return importReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            for (FaultExternalLineStaRelImport model : importExcelList){
                FaultExternalLineStaRel staRel = new FaultExternalLineStaRel();
                BeanUtils.copyProperties(model,staRel);
                StringBuilder stringBuilder = new StringBuilder();
                //校验信息
                examine(model, staRel, stringBuilder, importExcelList);
                if (stringBuilder.length() > 0) {
                    errorLines++;
                }
                list.add(staRel);
            }
            if (errorLines > 0) {
                //错误报告下载
                return getErrorExcel(errorLines, importExcelList, successLines, type);
            }
                this.saveBatch(list);
                tipMessage = "文件导入成功！";
                return importReturnRes(errorLines, successLines, tipMessage, true, null);
        }
        return importReturnRes(errorLines, successLines, tipMessage, true, null);
    }

    private void examine(FaultExternalLineStaRelImport model, FaultExternalLineStaRel staRel, StringBuilder stringBuilder, List<FaultExternalLineStaRelImport> importExcelList) {
        if(ObjectUtil.isNotEmpty(model.getStationCode())&&ObjectUtil.isNotEmpty(model.getIline())
                &&ObjectUtil.isNotEmpty(model.getIpos()) &&ObjectUtil.isNotEmpty(model.getScc())){
            JSONObject csStation = sysBaseApi.getCsStationByCode(model.getStationCode());
            if(ObjectUtil.isEmpty(csStation)){
                stringBuilder.append("系统不存在该站点，");
            }else {
                model.setLineCode(csStation.getString("lineCode"));
                String lineName = csStation.getString("lineName");
                String stationName = csStation.getString("stationName");
                model.setCorrespondenceScc(lineName + "/" + stationName);
                List<FaultExternalLineStaRel> list = lineStaRelMapper.selectList(new LambdaQueryWrapper<FaultExternalLineStaRel>().eq(FaultExternalLineStaRel::getStationCode, staRel.getStationCode()));
                if(CollUtil.isNotEmpty(list)){
                    stringBuilder.append("该线路下的站点已被添加，");
                }
                List<FaultExternalLineStaRelImport> fileList = importExcelList.stream().filter(i -> !model.equals(i) && model.getStationCode().equals(i.getStationCode())).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(fileList)){
                    stringBuilder.append("文件中存在相同的站点，");
                }
            }
        }else {
            stringBuilder.append("站点code、对应IP地址、子网掩码、网关地址为必填字段，");
        }
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            model.setWrongReason(stringBuilder.toString());
        }
        BeanUtils.copyProperties(model,staRel);
    }

    private Result<?> importReturnRes(int errorLines, int successLines, String tipMessage, boolean isType, String  failReportUrl) {
        JSONObject result = new JSONObject(5);
        result.put("errorCount", errorLines);
        result.put("successCount", successLines);
        int totalCount = successLines + errorLines;
        result.put("totalCount", totalCount);
        result.put("failReportUrl", failReportUrl);
        result.put("isSucceed", isType);
        Result<JSONObject> res = Result.OK(result);
        res.setMessage(tipMessage);
        res.setCode(200);
        return res;
    }


    private Result<?> getErrorExcel(int errorLines, List<FaultExternalLineStaRelImport> list, int successLines, String type) throws IOException{
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/faultexternallinestarelerror.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/faultexternallinestarelerror.xlsx");
        FileOutputStream out = null;
        String url = null;
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);
            Map<String, Object> errorMap = new HashMap<>(16);
            List<Map<String, String>> listMap = new ArrayList<>();
            for (FaultExternalLineStaRelImport dto : list) {
                Map<String, String> lm = new HashMap<>(16);
                //错误报告获取信息
                lm.put("stationCode", dto.getStationCode());
                lm.put("iline", String.valueOf(dto.getIline()));
                lm.put("ipos", String.valueOf(dto.getIpos()));
                lm.put("scc", dto.getScc());
                lm.put("wrongReason", dto.getWrongReason());
                listMap.add(lm);
            }
            errorMap.put("maplist", listMap);
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
            sheetsMap.put(0, errorMap);
            Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            String fileName = "调度子系统位置导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }  finally {
            assert out != null;
            out.close();
        }
        String tipMessage = "文件失败，数据有错误。";
        return importReturnRes(errorLines, successLines, tipMessage, false, url);
    }
}