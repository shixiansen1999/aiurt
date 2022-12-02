package com.aiurt.modules.sm.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;

import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.entity.CsSafetyAttentionType;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionMapper;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionTypeMapper;
import com.aiurt.modules.sm.service.ICsSafetyAttentionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;

import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description: 安全事项
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
@Service
public class CsSafetyAttentionServiceImpl extends ServiceImpl<CsSafetyAttentionMapper, CsSafetyAttention> implements ICsSafetyAttentionService {
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Autowired
    private CsSafetyAttentionTypeMapper csSafetyAttentionTypeMapper;
    @Override
    public ModelAndView exportXls(HttpServletRequest request, String ids) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<CsSafetyAttention> safetyAttentions = new ArrayList<>();
        if (StrUtil.isNotEmpty(ids)){
            List<CsSafetyAttention>safetyAttentions1 = baseMapper.selectList(new LambdaQueryWrapper<CsSafetyAttention>()
                .in(CsSafetyAttention::getSystemCode,Arrays.asList(ids.split(",")))
                .eq(CsSafetyAttention::getDelFlag,0));
            safetyAttentions.addAll(safetyAttentions1);
        }
        if (StrUtil.isNotEmpty(ids)){
            List<CsSafetyAttention>safetyAttentions1 = baseMapper.selectList(new LambdaQueryWrapper<CsSafetyAttention>()
                    .in(CsSafetyAttention::getMajorCode,Arrays.asList(ids.split(",")))
                    .eq(CsSafetyAttention::getDelFlag,0));
            safetyAttentions.addAll(safetyAttentions1);
        }
        if (StrUtil.isNotEmpty(ids)){
            List<CsSafetyAttention>safetyAttentions1 = baseMapper.selectList(new LambdaQueryWrapper<CsSafetyAttention>()
                    .in(CsSafetyAttention::getId,Arrays.asList(ids.split(",")))
                    .eq(CsSafetyAttention::getDelFlag,0));
            safetyAttentions.addAll(safetyAttentions1);
        }
         safetyAttentions =  safetyAttentions.stream().distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(safetyAttentions)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "安全事项管理");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, CsSafetyAttention.class);
            //自定义导出字段
            String exportField = "majorCode,systemCode,attentionContent,state";
            mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportField);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("安全事项管理", "安全事项管理"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, safetyAttentions);
        }
        return mv;
    }

    @Override
    public Result importExcelMaterial(MultipartFile file, ImportParams params) throws Exception {
        List<CsSafetyAttention> listMaterial = ExcelImportUtil.importExcel(file.getInputStream(), CsSafetyAttention.class, params);
        List<String> errorStrs = new ArrayList<>();
        // 去掉 sql 中的重复数据
        Integer errorLines = 0;
        Integer successLines = 0;
        List<CsSafetyAttention> list = new ArrayList<>();
        for (int i = 0; i < listMaterial.size(); i++) {
            try {
                CsSafetyAttention csSafetyAttention = listMaterial.get(i);
                //专业
                String majorCodeName = csSafetyAttention.getMajorName() == null ? "" : csSafetyAttention.getMajorName();
                if ("".equals(majorCodeName)) {
                    errorStrs.add("第 " + i + " 行：专业名称为空，忽略导入。");
                    list.add(csSafetyAttention);
                    continue;
                }
                CsUserMajorModel csMajor = baseMapper.selectCsMajor(majorCodeName);
                if (csMajor == null) {
                    errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
                    list.add(csSafetyAttention);
                    continue;
                } else {
                    csSafetyAttention.setMajorCode(csMajor.getMajorCode());
                    //安全事项分类
                    String systemName = csSafetyAttention.getSystemName() == null ? "" : csSafetyAttention.getSystemName();
                    if (StrUtil.isNotEmpty(systemName)) {
                      String systemCode = baseMapper.selectSystemCode(systemName);
                      if (StrUtil.isNotEmpty(systemCode)){
                          csSafetyAttention.setSystemCode(systemCode);
                      }else {
                          errorStrs.add("第 " + i + " 行：输入的子系统找不到！请核对后输出，忽略导入。");
                          list.add(csSafetyAttention);
                          continue;
                      }
                    }

                    //安全事项内容
                    String attentionContent = csSafetyAttention.getAttentionContent() == null ? "" : csSafetyAttention.getAttentionContent();
                    if ("".equals(attentionContent)) {
                        errorStrs.add("第 " + i + " 行：安全事项内容为空，忽略导入。");
                        list.add(csSafetyAttention);
                        continue;
                    }
                    //状态
                    String stateName = csSafetyAttention.getStateName()==null?"": csSafetyAttention.getStateName();
                    if ("".equals(stateName)){
                        errorStrs.add("第 " + i + " 行：安全状态为空，忽略导入。");
                        list.add(csSafetyAttention);
                        continue;
                    }else  if("有效".equals(stateName)){
                        csSafetyAttention.setState(1);
                    }else if ("无效".equals(stateName)){
                        csSafetyAttention.setState(0);
                    }else {
                        errorStrs.add("第 " + i + " 行：安全状态识别不出，忽略导入。");
                        list.add(csSafetyAttention);
                        continue;
                    }
                    int save = baseMapper.insert(csSafetyAttention);
                    if (save <= 0) {
                        throw new Exception(CommonConstant.SQL_INDEX_UNIQ_MATERIAL_BASE_CODE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (list.size()>0){
            //创建导入失败错误报告,进行模板导出
            Resource resource = new ClassPathResource("templates/csSafetyAttentionError.xlsx");
            InputStream resourceAsStream = resource.getInputStream();
            //2.获取临时文件
            File fileTemp= new File("templates/csSafetyAttentionError.xlsx");
            try {
                //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);
            List<Map<String, Object>> mapList = new ArrayList<>();
            list.forEach(l->{
                Map<String, Object> lm = new HashMap<String, Object>();
                lm.put("majorName",l.getMajorName());
                lm.put("systemName",l.getSystemName());
                lm.put("attentionContent",l.getAttentionContent());
                lm.put("stateName",l.getStateName());
                mapList.add(lm);
            });
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("maplist", mapList);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams,errorMap);
            String fileName = "安全事项导入错误模板"+"_" + System.currentTimeMillis()+".xlsx";
            FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
            String  url = fileName;
            workbook.write(out);
            errorLines+=errorStrs.size();
            successLines+=(listMaterial.size()-errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs,url);
        }
            errorLines += errorStrs.size();
            successLines += (listMaterial.size() - errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs);
        }
}
