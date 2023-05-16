package com.aiurt.modules.sensorinformation.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.category.dto.FixedAssetsCategoryImport;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.sensorinformation.entity.SensorInformation;
import com.aiurt.modules.sensorinformation.mapper.SensorInformationMapper;
import com.aiurt.modules.sensorinformation.service.ISensorInformationService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: sensor_information
 * @Author: aiurt
 * @Date: 2023-05-15
 * @Version: V1.0
 */
@Service
public class SensorInformationServiceImpl extends ServiceImpl<SensorInformationMapper, SensorInformation> implements ISensorInformationService {
    @Autowired
    private SensorInformationMapper informationMapper;
    @Autowired
    private CsStationMapper csStationMapper;

    @Override
    public Page<SensorInformation> queryPageList(Page<SensorInformation> page, SensorInformation sensorInformation) {
        List<SensorInformation> list = informationMapper.queryPageList(page,sensorInformation);
        return page.setRecords(list);
    }

    @Override
    public void add(SensorInformation sensorInformation) {
        SensorInformation information = informationMapper.selectOne(new LambdaQueryWrapper<SensorInformation>().eq(SensorInformation::getStationCode, sensorInformation.getStationCode()).eq(SensorInformation::getDelFlag, CommonConstant.DEL_FLAG_0));
       if(ObjectUtil.isNotEmpty(information)){
           throw new AiurtBootException("该线路下的站点已被添加！");
       }
        SensorInformation informationIP = informationMapper.selectOne(new LambdaQueryWrapper<SensorInformation>().eq(SensorInformation::getStationIp, sensorInformation.getStationIp()).eq(SensorInformation::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(ObjectUtil.isNotEmpty(informationIP)){
            throw new AiurtBootException("该ip已被添加！");
        }
       CsStation csStation = csStationMapper.selectOne(new LambdaQueryWrapper<CsStation>().eq(CsStation::getStationCode, sensorInformation.getStationCode()).eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (ObjectUtil.isNotEmpty(csStation.getLineName())) {
            sensorInformation.setLineName(csStation.getLineName());
        }
        sensorInformation.setLineCode(csStation.getLineCode());
        sensorInformation.setStationName(csStation.getStationName());
        informationMapper.insert(sensorInformation);
    }

    @Override
    public void edit(SensorInformation sensorInformation) {
        SensorInformation information = informationMapper.selectOne(new LambdaQueryWrapper<SensorInformation>().ne(SensorInformation::getId,sensorInformation.getId()).eq(SensorInformation::getStationCode, sensorInformation.getStationCode()).eq(SensorInformation::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(ObjectUtil.isNotEmpty(information)){
            throw new AiurtBootException("该线路下的站点已被添加！");
        }
        SensorInformation informationIP = informationMapper.selectOne(new LambdaQueryWrapper<SensorInformation>().ne(SensorInformation::getId,sensorInformation.getId()).eq(SensorInformation::getStationIp, sensorInformation.getStationIp()).eq(SensorInformation::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(ObjectUtil.isNotEmpty(informationIP)){
            throw new AiurtBootException("该ip已被添加！");
        }
        informationMapper.updateById(sensorInformation);
    }

    @Override
    public List<SensorInformation> getList(SensorInformation sensorInformation) {
        return informationMapper.getList(sensorInformation);
    }

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
                tipMessage = "导入失败，文件类型不对。";
                return imporReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                //List<> categoryList = new ArrayList<>();
                List<FixedAssetsCategoryImport> list = ExcelImportUtil.importExcel(file.getInputStream(), FixedAssetsCategoryImport.class, params);
                list = list.stream().filter(l -> l.getPidName() != null || l.getCategoryCode() != null || l.getCategoryName() != null || l.getRemark() != null).collect(Collectors.toList());
                if (CollUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                for (FixedAssetsCategoryImport model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        FixedAssetsCategory category = new FixedAssetsCategory();
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        //examine(model, category, stringBuilder, list);
                        if (stringBuilder.length() > 0) {
                            errorLines++;
                        }
                       // categoryList.add(category);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    //return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                } else {
                    //todo
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
