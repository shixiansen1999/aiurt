package com.aiurt.modules.sensorinformation.service.impl;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.sensorinformation.dto.SensorInformationDTO;
import com.aiurt.modules.sensorinformation.entity.SensorInformation;
import com.aiurt.modules.sensorinformation.mapper.SensorInformationMapper;
import com.aiurt.modules.sensorinformation.service.ISensorInformationService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
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
    private CsLineMapper csLineMapper;
    @Autowired
    private CsStationMapper csStationMapper;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    public Page<SensorInformation> queryPageList(Page<SensorInformation> page, SensorInformation sensorInformation) {
        List<SensorInformation> list = informationMapper.queryPageList(page,sensorInformation);
        return page.setRecords(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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
                List<SensorInformation> informationList = new ArrayList<>();
                List<SensorInformationDTO> list = ExcelImportUtil.importExcel(file.getInputStream(), SensorInformationDTO.class, params);
                list = list.stream().filter(l -> l.getLineCode() != null || l.getStationCode() != null || l.getStationIp() != null || l.getGatewayAddress() != null|| l.getSubnetMask() != null|| l.getRemark() != null)
                        .collect(Collectors.toList());
                if (CollUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                for (SensorInformationDTO dto : list) {
                    if (ObjectUtil.isNotEmpty(dto)) {
                        SensorInformation information = new SensorInformation();
                        BeanUtils.copyProperties(dto,information);
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        examine(information, dto, stringBuilder,list);
                        if (stringBuilder.length() > 0) {
                            errorLines++;
                        }
                        informationList.add(information);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                } else {
                    this.saveBatch(informationList);
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

    private void examine(SensorInformation information, SensorInformationDTO informationDTO, StringBuilder stringBuilder, List<SensorInformationDTO> list) {
        //查询列表是否有相同的数据
        List<SensorInformationDTO> informationList = list.stream().filter(l -> l.equals(information)).collect(Collectors.toList());
        if(informationList.size()>1){
            stringBuilder.append("文件存在相同的数据，");
        }
        if(ObjectUtil.isNotEmpty(information.getLineCode())&&ObjectUtil.isNotEmpty(information.getStationCode())&&ObjectUtil.isNotEmpty(information.getStationIp())
                &&ObjectUtil.isNotEmpty(information.getGatewayAddress())&&ObjectUtil.isNotEmpty(information.getSubnetMask())&&ObjectUtil.isNotEmpty(information.getRemark())){
            //查询是否有相同的线路下的站点添加
            //1.文件
            List<SensorInformationDTO> fileStationLineInformationList = list.stream().filter(l -> l.getLineCode().equals(information.getLineCode())&&l.getStationCode().equals(information.getStationCode())).collect(Collectors.toList());
            if(fileStationLineInformationList.size()>1){
                stringBuilder.append("文件中存在相同的线路站点，");
            }
            //2.数据库
            List<SensorInformation> dateInformationLis = informationMapper.selectList(new LambdaQueryWrapper<SensorInformation>().eq(SensorInformation::getLineCode, information.getLineCode()).
                    eq(SensorInformation::getStationCode, information.getStationCode()).eq(SensorInformation::getDelFlag,CommonConstant.DEL_FLAG_0));
            if(CollUtil.isNotEmpty(dateInformationLis)){
                stringBuilder.append("系统已添加相同的线路站点，");
            }
            //查询ip是否唯一
            //1.文件
            List<SensorInformationDTO> ipFileInformationList = list.stream().filter(l -> l.getStationIp().equals(information.getStationIp())).collect(Collectors.toList());
            if(ipFileInformationList.size()>1){
                stringBuilder.append("文件存在相同的ip，");
            }
            //2.数据库
            List<SensorInformation> ipDateInformationList = informationMapper.selectList(new LambdaQueryWrapper<SensorInformation>().eq(SensorInformation::getStationIp, information.getStationIp()).eq(SensorInformation::getDelFlag,CommonConstant.DEL_FLAG_0));
            if(CollUtil.isNotEmpty(ipDateInformationList)){
                stringBuilder.append("系统已添加相同的ip，");
            }
            //线路是否存在
            CsLine csLine = csLineMapper.selectOne(new LambdaQueryWrapper<CsLine>().eq(CsLine::getLineCode, information.getLineCode()).eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0));
            if(ObjectUtil.isEmpty(csLine)){
                stringBuilder.append("系统不存在该线路，");
            }else {
                //线路下的站点是否存在
                CsStation csStation = csStationMapper.selectOne(new LambdaQueryWrapper<CsStation>().eq(CsStation::getStationCode, information.getStationCode()).eq(CsStation::getLineCode, csLine.getLineCode()).eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0));
                if(ObjectUtil.isEmpty(csStation)){
                    stringBuilder.append("系统不存在该线路下的站点，");
                }else {
                    information.setLineName(csLine.getLineName());
                    information.setStationName(csStation.getStationName());
                }
            }
        }else {
            stringBuilder.append("线路、站点、对应IP地址、子网掩码、网关地址、备注为必填字段，");
        }

        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            informationDTO.setWrongReason(stringBuilder.toString());
        }
    }

    private Result<?> getErrorExcel(int errorLines, List<SensorInformationDTO> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/sensorinformationerror.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/sensorinformationerror.xlsx");
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
            SensorInformationDTO dto = list.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("lineCode", dto.getLineCode());
            lm.put("stationCode", dto.getStationCode());
            lm.put("stationIp", dto.getStationIp());
            lm.put("subnetMask", dto.getSubnetMask());
            lm.put("gatewayAddress", dto.getGatewayAddress());
            lm.put("remark", dto.getRemark());
            lm.put("wrongReason", dto.getWrongReason());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "传感器信息导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
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
