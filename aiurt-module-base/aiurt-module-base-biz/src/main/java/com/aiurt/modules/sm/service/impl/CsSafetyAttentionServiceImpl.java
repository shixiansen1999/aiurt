package com.aiurt.modules.sm.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;

import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.entity.CsSafetyAttentionType;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionMapper;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionTypeMapper;
import com.aiurt.modules.sm.service.ICsSafetyAttentionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;

import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @Description: 安全事项
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
@Service
public class CsSafetyAttentionServiceImpl extends ServiceImpl<CsSafetyAttentionMapper, CsSafetyAttention> implements ICsSafetyAttentionService {

    @Autowired
    private CsSafetyAttentionTypeMapper csSafetyAttentionTypeMapper;
    @Override
    public ModelAndView exportXls(HttpServletRequest request, String ids) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<CsSafetyAttention>safetyAttentions = baseMapper.selectList(new LambdaQueryWrapper<CsSafetyAttention>()
                .in(CsSafetyAttention::getId,Arrays.asList(ids.split(",")))
                .eq(CsSafetyAttention::getDelFlag,0));
        if (CollectionUtil.isNotEmpty(safetyAttentions)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "安全事项管理");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, CsSafetyAttention.class);
            //自定义导出字段 暂时用不上
            //mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportField);
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

        for (int i = 0; i < listMaterial.size(); i++) {
            try {
                CsSafetyAttention csSafetyAttention = listMaterial.get(i);
                //专业
                String majorCodeName = csSafetyAttention.getMajorName() == null ? "" : csSafetyAttention.getMajorName();
                if ("".equals(majorCodeName)) {
                    errorStrs.add("第 " + i + " 行：专业名称为空，忽略导入。");
                    continue;
                }
                CsUserMajorModel csMajor = baseMapper.selectCsMajor(majorCodeName);
                if (csMajor == null) {
                    errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
                    continue;
                } else {
                    csSafetyAttention.setMajorCode(csMajor.getMajorCode());
                    //安全事项分类
                    String baseTypeCodeName = csSafetyAttention.getAttentionTypeName() == null ? "" : csSafetyAttention.getAttentionTypeName();
                    if ("".equals(baseTypeCodeName)) {
                        errorStrs.add("第 " + i + " 行：安全事项为空，忽略导入。");
                        continue;
                    }
                    CsSafetyAttentionType csSafetyAttentionType = csSafetyAttentionTypeMapper.selectOne(new LambdaQueryWrapper<CsSafetyAttentionType>()
                            .like(CsSafetyAttentionType::getName, baseTypeCodeName)
                            .eq(CsSafetyAttentionType::getDelFlag, 0));
                    if (csSafetyAttentionType == null) {
                        errorStrs.add("第 " + i + " 行：无法根据安全事项分类找到对应数据，忽略导入。");
                        continue;
                    } else {
                        csSafetyAttention.setAttentionTypeCode(csSafetyAttentionType.getCode());
                        csSafetyAttention.setAttentionType(csSafetyAttentionType.getId());
                    }
                    //安全事项内容
                    String attentionContent = csSafetyAttention.getAttentionContent() == null ? "" : csSafetyAttention.getAttentionContent();
                    if ("".equals(attentionContent)) {
                        errorStrs.add("第 " + i + " 行：安全事项内容为空，忽略导入。");
                        continue;
                    }
                    //安全事项措施
                    String attentionMeasures = csSafetyAttention.getAttentionMeasures() == null ? "" : csSafetyAttention.getAttentionMeasures();
                    if ("".equals(attentionMeasures)) {
                        errorStrs.add("第 " + i + " 行：安全事项措施为空，忽略导入。");
                        continue;
                    }
                    //状态
                    String stateName = csSafetyAttention.getStateName()==null?"": csSafetyAttention.getStateName();
                    if ("".equals(stateName)){
                        errorStrs.add("第 " + i + " 行：安全状态为空，忽略导入。");
                        continue;
                    }else  if("有效".equals(stateName)){
                        csSafetyAttention.setState(1);
                    }else if ("失效".equals(stateName)){
                        csSafetyAttention.setState(0);
                    }else {
                        errorStrs.add("第 " + i + " 行：安全状态识别不出，忽略导入。");
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
            errorLines += errorStrs.size();
            successLines += (listMaterial.size() - errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs);
        }
}
