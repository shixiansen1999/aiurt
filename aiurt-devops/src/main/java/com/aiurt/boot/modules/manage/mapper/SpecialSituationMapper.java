package com.aiurt.boot.modules.manage.mapper;

import java.util.List;
import java.util.Map;

import com.aiurt.boot.modules.manage.entity.SpecialSituation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: cs_special_situation
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface SpecialSituationMapper extends BaseMapper<SpecialSituation> {

    public List<SpecialSituation> queryByCondition(Map<String,Object> param);
    public Integer queryPageTotal(Map<String,Object> param);
    List<SpecialSituation> queryByUserId(Map<String,Object> param);
}
