package com.aiurt.boot.weeklyplan.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.weeklyplan.entity.ConstructionTemplate;
import com.aiurt.boot.weeklyplan.mapper.ConstructionTemplateMapper;
import com.aiurt.boot.weeklyplan.service.IConstructionTemplateService;
import com.aiurt.boot.weeklyplan.vo.ConstructionTemplateVO;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: construction_template
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Service
public class ConstructionTemplateServiceImpl extends ServiceImpl<ConstructionTemplateMapper, ConstructionTemplate> implements IConstructionTemplateService {

    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private ConstructionTemplateMapper constructionTemplateMapper;

    @Override
    public IPage<ConstructionTemplateVO> queryPageList(Page<ConstructionTemplateVO> page, ConstructionTemplate constructionTemplate) {
        IPage<ConstructionTemplateVO> pageList = constructionTemplateMapper.queryPageList(page, constructionTemplate);
        pageList.getRecords().forEach(template -> {
            ConstructionTemplateVO templateVO = new ConstructionTemplateVO();
            BeanUtils.copyProperties(template, templateVO);
            String userId = template.getUserId();
            if (StrUtil.isNotEmpty(userId)) {
                LoginUser user = iSysBaseApi.getUserById(userId);
                templateVO.setUserName(user.getRealname());
            }
        });
        return pageList;
    }

    @Override
    public List<ConstructionTemplateVO> selectAll(ConstructionTemplate constructionTemplate) {
        List<ConstructionTemplateVO> list = new ArrayList<>();
        List<ConstructionTemplate> templateList = this.list();
        templateList.forEach(template -> {
            ConstructionTemplateVO templateVO = new ConstructionTemplateVO();
            BeanUtils.copyProperties(template, templateVO);
            String userId = template.getUserId();
            if (StrUtil.isNotEmpty(userId)) {
                LoginUser user = iSysBaseApi.getUserById(userId);
                templateVO.setUserName(user.getRealname());
            }
            list.add(templateVO);
        });
        return list;
    }

    @Override
    public ConstructionTemplateVO queryById(String id) {
        ConstructionTemplate constructionTemplate = this.getById(id);
        if (ObjectUtil.isEmpty(constructionTemplate)) {
            throw new AiurtBootException("未找到对应数据!");
        }
        ConstructionTemplateVO templateVO = new ConstructionTemplateVO();
        BeanUtils.copyProperties(constructionTemplate, templateVO);
        if (ObjectUtil.isNotEmpty(constructionTemplate.getUserId())) {
            LoginUser user = iSysBaseApi.getUserById(constructionTemplate.getUserId());
            templateVO.setUserName(user.getRealname());
        }
        return templateVO;
    }
}
