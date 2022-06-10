package com.aiurt.boot.modules.manage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.manage.entity.SpecialSituation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: cs_special_situation
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface ISpecialSituationService extends IService<SpecialSituation> {
    public IPage<SpecialSituation> queryByCondition(Page<SpecialSituation> page, QueryWrapper<SpecialSituation> queryWrapper, SpecialSituation specialSituation);

    List<SpecialSituation> getSpecialSituationsByUserId(Map<String, Object> map);
}
