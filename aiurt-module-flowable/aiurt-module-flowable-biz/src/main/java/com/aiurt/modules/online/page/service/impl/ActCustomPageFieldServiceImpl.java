package com.aiurt.modules.online.page.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.online.page.entity.ActCustomPageField;
import com.aiurt.modules.online.page.mapper.ActCustomPageFieldMapper;
import com.aiurt.modules.online.page.service.IActCustomPageFieldService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: act_custom_page_field
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
@Service
public class ActCustomPageFieldServiceImpl extends ServiceImpl<ActCustomPageFieldMapper, ActCustomPageField> implements IActCustomPageFieldService {

    @Override
    public List<String> listPageFieldCode(String pageId) {
        if (StrUtil.isBlank(pageId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ActCustomPageField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomPageField::getPageId, pageId);
        List<ActCustomPageField> actCustomPageFieldList = baseMapper.selectList(wrapper);
        if (CollUtil.isEmpty(actCustomPageFieldList)) {
            return Collections.emptyList();
        }
        return actCustomPageFieldList.stream().map(ActCustomPageField::getField).collect(Collectors.toList());
    }
}
