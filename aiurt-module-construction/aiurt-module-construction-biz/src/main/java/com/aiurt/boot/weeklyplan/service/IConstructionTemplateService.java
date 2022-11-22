package com.aiurt.boot.weeklyplan.service;

import com.aiurt.boot.weeklyplan.entity.ConstructionTemplate;
import com.aiurt.boot.weeklyplan.vo.ConstructionTemplateVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: construction_template
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
public interface IConstructionTemplateService extends IService<ConstructionTemplate> {
    /**
     * 施工供电模板分页查询
     *
     * @param page
     * @param constructionTemplate
     * @return
     */
    IPage<ConstructionTemplateVO> queryPageList(Page<ConstructionTemplateVO> page, ConstructionTemplate constructionTemplate);

    /**
     * 施工供电模板-查询所有
     *
     * @param constructionTemplate
     * @return
     */
    List<ConstructionTemplateVO> selectAll(ConstructionTemplate constructionTemplate);

    /**
     *
     * @param id
     * @return
     */
    ConstructionTemplateVO queryById(String id);
}
