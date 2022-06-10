package com.aiurt.boot.modules.manage.service.impl;

import com.aiurt.boot.modules.manage.entity.Collection;
import com.aiurt.boot.modules.manage.mapper.CollectionMapper;
import com.aiurt.boot.modules.manage.service.ICollectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.lang.reflect.Method;

/**
 * @Description: cs_collection
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements ICollectionService {

    @Override
    public void recovery(ApplicationContext ctx,String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            String[] cids = ids.split(",");
            for (String id : cids) {
                Collection collection = this.baseMapper.selectById(id);
                recovery(ctx,collection);
                collection.setDelFlag(1);
                this.baseMapper.updateById(collection);
            }
        }
    }

    private void recovery(ApplicationContext ctx,Collection collection) {
        /**
         * 1、获取恢复的方法名和全类名和参数
         */
        String temp = collection.getMethod();
        String className = temp.substring(0, temp.lastIndexOf("."));
        String methodName = temp.substring(temp.lastIndexOf(".") + 1);
        String parameter = collection.getParams();
        try {
            className =className.substring(0,1).toLowerCase()+ className.substring(1);
            Object bean = ctx.getBean(className);
            Class aClass=bean.getClass();
            Method method = aClass.getMethod(methodName, String.class);
            method.invoke(bean, parameter);
        } catch (Exception e) {
            log.error("恢复异常:{}", e.getMessage());
        }
    }
}
