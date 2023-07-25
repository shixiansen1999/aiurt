package com.aiurt.modules.outsourcingpersonnel.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.result.OutsourcingPersonnelResult;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.outsourcingpersonnel.dto.OutsourcingPersonnelInput;
import com.aiurt.modules.outsourcingpersonnel.entity.OutsourcingPersonnel;
import com.aiurt.modules.outsourcingpersonnel.mapper.OutsourcingPersonnelMapper;
import com.aiurt.modules.outsourcingpersonnel.param.OutsourcingPersonnelParam;
import com.aiurt.modules.outsourcingpersonnel.service.IOutsourcingPersonnelService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : sbx
 * @Classname : OutsourcingPersonnelServiceImpl
 * @Description : 委外人员
 * @Date : 2023/7/24 8:53
 */
@Service
public class OutsourcingPersonnelServiceImpl extends ServiceImpl<OutsourcingPersonnelMapper, OutsourcingPersonnel> implements IOutsourcingPersonnelService {

    @Resource
    private OutsourcingPersonnelMapper personnelMapper;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;

    /**
     * 新增委外人员
     *
     * @param personnel
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result add(OutsourcingPersonnel personnel, HttpServletRequest req) {
        OutsourcingPersonnel outsourcingPersonnel = new OutsourcingPersonnel();
        outsourcingPersonnel.setName(personnel.getName());
        outsourcingPersonnel.setCertificateCode(personnel.getCertificateCode());
        outsourcingPersonnel.setCompany(personnel.getCompany());
        outsourcingPersonnel.setPosition(personnel.getPosition());
        outsourcingPersonnel.setSystemCode(personnel.getSystemCode());
        outsourcingPersonnel.setConnectionWay(personnel.getConnectionWay());
        outsourcingPersonnel.setDelFlag(0);
        //String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        //outsourcingPersonnel.setCreateBy(userId);
        personnelMapper.insert(outsourcingPersonnel);
        return Result.ok("新增成功");
    }

    /**
     * 查询委外人员
     *
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<OutsourcingPersonnelResult> pageList(IPage<OutsourcingPersonnelResult> page, OutsourcingPersonnelParam param) {
        IPage<OutsourcingPersonnelResult> resultIPage = personnelMapper.queryOutsourcingPersonnel(page, param);
        return resultIPage;
    }

    /**
     * 委外人员导出
     *
     * @param param
     * @return
     */
    @Override
    public List<OutsourcingPersonnelResult> exportXls(OutsourcingPersonnelParam param) {
        List<OutsourcingPersonnelResult> list = personnelMapper.exportXls(param);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
        }
        return list;
    }

    /**
     * excel导入
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // String userId = TokenUtils.getUserId(request, iSysBaseAPI);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        // 错误信息
        int  errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
            }
            ImportParams params = new ImportParams();
            //这里要注意一下
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                //读取Excel中的数据进行转换
                List<OutsourcingPersonnelInput> list = ExcelImportUtil.importExcel(file.getInputStream(), OutsourcingPersonnelInput.class, params);
//                if (CollUtil.isEmpty(list)) {
//                    return Result.error("Excel转换异常");
//                }
                errorLines = check(list, errorLines);
                if (errorLines > 0) {
                    return getErrorExcel(errorLines, errorMessage, list, successLines, null, type);
                }
                ArrayList<OutsourcingPersonnel> result = new ArrayList<>();
                for (OutsourcingPersonnelInput input : list) {
                    OutsourcingPersonnel outsourcingPersonnel = new OutsourcingPersonnel();
                    outsourcingPersonnel.setDelFlag(CommonConstant.DEL_FLAG_0);
                    BeanUtils.copyProperties(input, outsourcingPersonnel);
                    result.add(outsourcingPersonnel);
                }
                this.saveBatch(result);
                return XlsUtil.importReturnRes(errorLines, result.size(), errorMessage, true, null);
            } catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if(msg!=null && msg.indexOf("Duplicate entry")>=0){
                    return Result.error("文件导入失败:有重复数据！");
                }else{
                    return Result.error("文件导入失败:" + e.getMessage());
                }
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入成功");
    }

    public int check(List<OutsourcingPersonnelInput> list, int errorLines) {
        List<DictModel> belongUnitDict = iSysBaseAPI.queryDictItemsByCode("belong_unit");
        Map<String, String> map = ObjectUtil.isNotEmpty(belongUnitDict) ? belongUnitDict.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue)) : new HashMap<String, String>(16);
        for (OutsourcingPersonnelInput input : list) {
            StringBuilder error = new StringBuilder();
            if (StrUtil.isBlank(input.getName())) {
                error.append("人员名称不能为空;");
            }
            if (StrUtil.isBlank(input.getCompanyName())) {
                error.append("所属单位不能为空;");
            } else {
                if (map.containsKey(input.getCompanyName())) {
                    input.setCompany(map.get(input.getCompanyName()));
                } else {
                    error.append("输入所属单位不规范;");
                }
            }
            if (StrUtil.isBlank(input.getPosition())) {
                error.append("职位名称不能为空;");
            }
            if (StrUtil.isBlank(input.getSystemName())) {
                error.append("所属专业系统不能为空;");
            } else {
                JSONObject systemByName = iSysBaseAPI.getSystemName(null, input.getSystemName());
                if (ObjectUtil.isEmpty(systemByName)) {
                    error.append("输入所属专业系统不规范;");
                } else {
                    String systemCode = systemByName.getString("systemCode");
                    input.setSystemCode(systemCode);
                }
            }
            /*if (StringUtils.isBlank(input.getConnectionWay())) {
                throw new AiurtBootException("联系方式不能为空");
            }*/
            if (StringUtils.isBlank(input.getCertificateCode())) {
                error.append("施工证编号不能为空;");
            }
            if (ObjectUtil.isNotEmpty(error)) {
                errorLines++;
                input.setMistake(error.toString());
            }
        }
        return errorLines;
    }

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<OutsourcingPersonnelInput> list, int successLines, String url, String type) {

        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/outsourcingPersonnelError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>(16);

            List<Map<String, String>> listMap = new ArrayList<>();
            for (OutsourcingPersonnelInput input : list) {
                Map<String, String> lm = new HashMap<>(5);
                lm.put("name", input.getName());
                lm.put("companyName", input.getCompanyName());
                lm.put("position", input.getPosition());
                lm.put("systemName", input.getSystemName());
                lm.put("connectionWay", input.getConnectionWay());
                lm.put("certificateCode", input.getCertificateCode());
                lm.put("mistake", input.getMistake());
                listMap.add(lm);
            }
            errorMap.put("maplist", listMap);
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(1);
            sheetsMap.put(0, errorMap);
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

            String fileName = "委外人员导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
            url = File.separator+"errorExcelFiles"+ File.separator+fileName;
            workbook.write(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage,true,url);
    }
}
