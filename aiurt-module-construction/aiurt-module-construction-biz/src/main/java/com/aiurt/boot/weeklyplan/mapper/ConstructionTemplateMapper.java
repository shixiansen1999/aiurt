package com.aiurt.boot.weeklyplan.mapper;

import com.aiurt.boot.weeklyplan.entity.ConstructionTemplate;
import com.aiurt.boot.weeklyplan.vo.ConstructionTemplateVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: construction_template
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
public interface ConstructionTemplateMapper extends BaseMapper<ConstructionTemplate> {
    /**
     * 施工供电模板分页查询
     *
     * @param page
     * @param constructionTemplate
     * @return
     */
    IPage<ConstructionTemplateVO> queryPageList(@Param("page") Page<ConstructionTemplateVO> page, @Param("condition") ConstructionTemplate constructionTemplate);
}
