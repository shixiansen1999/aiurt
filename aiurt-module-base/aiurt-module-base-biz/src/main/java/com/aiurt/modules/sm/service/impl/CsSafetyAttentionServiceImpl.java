package com.aiurt.modules.sm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.entity.SafetyRelatedForm;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionMapper;
import com.aiurt.modules.sm.mapper.SafetyRelatedFormMapper;
import com.aiurt.modules.sm.service.ICsSafetyAttentionService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private SafetyRelatedFormMapper safetyRelatedFormMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
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
         safetyAttentions.forEach(s->{
             String htmlStr  = s.getAttentionContent();
             //定义script的正则表达式
             String regExScript = "<script[^>]*?>[\\s\\S]*?</script>";
             //定义style的正则表达式
             String regExStyle = "<style[^>]*?>[\\s\\S]*?</style>";
             //定义HTML标签的正则表达式
             String regExHtml = "<[^>]+>";

             Pattern pScript = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
             Matcher mScript = pScript.matcher(htmlStr);
             //过滤script标签
             htmlStr = mScript.replaceAll("");

             Pattern pStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
             Matcher mStyle = pStyle.matcher(htmlStr);
             //过滤style标签
             htmlStr = mStyle.replaceAll("");

             Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
             Matcher mHtml = pHtml.matcher(htmlStr);
             //过滤html标签
             htmlStr = mHtml.replaceAll( "");
             htmlStr = htmlStr.replace("\n","");
             s.setAttentionContent(htmlStr);

         });
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
                    list.add(csSafetyAttention.setText("专业名称为空，忽略导入"));
                    continue;
                }
                CsUserMajorModel csMajor = baseMapper.selectCsMajor(majorCodeName);
                if (csMajor == null) {
                    errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
                    list.add(csSafetyAttention.setText("无法根据专业名称找到对应数据，忽略导入"));
                    continue;
                } else {
                    csSafetyAttention.setMajorCode(csMajor.getMajorCode());
                    //安全事项分类
                    String systemName = csSafetyAttention.getSystemName() == null ? "" : csSafetyAttention.getSystemName();
                    if (StrUtil.isNotEmpty(systemName)) {
                      String systemCode = baseMapper.selectSystemCode(systemName,csMajor.getMajorCode());
                      if (StrUtil.isNotEmpty(systemCode)){
                          csSafetyAttention.setSystemCode(systemCode);
                      }else {
                          errorStrs.add("第 " + i + " 行：输入的子系统找不到！请核对后输出，忽略导入。");
                          list.add(csSafetyAttention.setText("输入的子系统找不到！请核对后输出，忽略导入"));
                          continue;
                      }
                    }

                    //安全事项内容
                    String attentionContent = csSafetyAttention.getAttentionContent() == null ? "" : csSafetyAttention.getAttentionContent();
                    if ("".equals(attentionContent)) {
                        errorStrs.add("第 " + i + " 行：安全事项内容为空，忽略导入。");
                        list.add(csSafetyAttention.setText("安全事项内容为空，忽略导入"));
                        continue;
                    }
                    //状态
                    String stateName = csSafetyAttention.getStateName()==null?"": csSafetyAttention.getStateName();
                    if ("".equals(stateName)){
                        errorStrs.add("第 " + i + " 行：安全状态为空，忽略导入。");
                        list.add(csSafetyAttention.setText("安全状态为空，忽略导入"));
                        continue;
                    }else  if("有效".equals(stateName)){
                        csSafetyAttention.setState(1);
                    }else if ("无效".equals(stateName)){
                        csSafetyAttention.setState(0);
                    }else {
                        errorStrs.add("第 " + i + " 行：安全状态识别不出，忽略导入。");
                        list.add(csSafetyAttention.setText("安全状态识别不出，忽略导入"));
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
                lm.put("attentionMeasures",l.getAttentionMeasures());
                lm.put("stateName",l.getStateName());
                lm.put("text",l.getText());
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
    @Override
    public List<CsSafetyAttention> isFirstByCode(String code, Integer status, String majorCode, String systemCode) {
        List<CsSafetyAttention> csSafetyAttentions = new ArrayList<>();
        LambdaQueryWrapper<SafetyRelatedForm> wrapper = new LambdaQueryWrapper<>();
        if(0==status){
            wrapper.eq(SafetyRelatedForm::getPatrolStandardCode,code);
        }
        if (1==status){
            wrapper.eq(SafetyRelatedForm::getInspectionCode,code);
        }
        List<SafetyRelatedForm> safetyRelatedForms = safetyRelatedFormMapper.selectList(wrapper);
        //判断是否修改过关联表
        if (CollectionUtil.isNotEmpty(safetyRelatedForms)){
            //如果修改 查询已经保存的数据
            wrapper.eq(SafetyRelatedForm::getDelFlag,0);
            List<SafetyRelatedForm> list = safetyRelatedFormMapper.selectList(wrapper);
            List<String> str = list.stream().map(l-> l.getSafetyAttentionId()).collect(Collectors.toList());
            csSafetyAttentions = baseMapper.selectList(new LambdaQueryWrapper<CsSafetyAttention>().in(CsSafetyAttention::getId,str));
        }else {
            //没有修改按照专业子系统查询
            LambdaQueryWrapper<CsSafetyAttention> wrapper1 = new LambdaQueryWrapper<CsSafetyAttention>();
            wrapper1.eq(CsSafetyAttention::getMajorCode,majorCode);
            if (StrUtil.isNotEmpty(systemCode)){
                wrapper1.eq(CsSafetyAttention::getSystemCode,systemCode);
            }
            //需要查询启动和未删除
            wrapper1.eq(CsSafetyAttention::getState,1).eq(CsSafetyAttention::getDelFlag,0);
            csSafetyAttentions = baseMapper.selectList(wrapper1);
        }
        csSafetyAttentions.forEach(c->{
            JSONObject major = sysBaseAPI.getCsMajorByCode(c.getMajorCode());
            c.setMajorName(major != null ? major.getString("majorName") : null);
            String systemName = baseMapper.getSystemName(c.getMajorCode(),c.getSystemCode());
            c.setSystemName(systemName);
        });
        return csSafetyAttentions;
    }
}
