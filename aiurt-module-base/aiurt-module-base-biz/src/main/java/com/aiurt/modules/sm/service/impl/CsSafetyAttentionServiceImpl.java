package com.aiurt.modules.sm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionMapper;
import com.aiurt.modules.sm.service.ICsSafetyAttentionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
}
