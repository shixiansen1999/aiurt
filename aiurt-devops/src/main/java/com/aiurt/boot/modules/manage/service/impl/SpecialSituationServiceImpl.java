package com.aiurt.boot.modules.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.manage.entity.SpecialSituation;
import com.aiurt.boot.modules.manage.mapper.SpecialSituationMapper;
import com.aiurt.boot.modules.manage.service.ISpecialSituationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: cs_special_situation
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class SpecialSituationServiceImpl extends ServiceImpl<SpecialSituationMapper, SpecialSituation> implements ISpecialSituationService {

    @Autowired
    private SpecialSituationMapper specialSituationMapper;

    @Override
    public IPage<SpecialSituation> queryByCondition(Page<SpecialSituation> page, QueryWrapper<SpecialSituation> queryWrapper, SpecialSituation specialSituation) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("cotent",specialSituation.getCotent());
        condition.put("creater_name",specialSituation.getCreaterName());
        if(StringUtils.isNotEmpty(specialSituation.getSTime())&&StringUtils.isNotEmpty(specialSituation.getETime())) {
            condition.put("stime", specialSituation.getSTime());
            condition.put("etime", specialSituation.getETime());
        }
        Integer total = specialSituationMapper.queryPageTotal(condition);
        condition.put("page",page);
        List<SpecialSituation> list =specialSituationMapper.queryByCondition(condition);
        IPage<SpecialSituation> situationIPage = page;
        situationIPage.setRecords(list);
        situationIPage.setTotal(total);
        situationIPage.setCurrent(page.getCurrent());
        return situationIPage;
    }

    @Override
    public List<SpecialSituation> getSpecialSituationsByUserId(Map<String, Object> map) {
        return specialSituationMapper.queryByUserId(map);
    }
}
