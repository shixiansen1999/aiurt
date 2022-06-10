package com.aiurt.boot.modules.manage.service;

import com.aiurt.boot.modules.manage.entity.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: cs_collection
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface ICollectionService extends IService<Collection> {

    @Transactional(rollbackFor = Exception.class)
    void recovery(ApplicationContext ctx, String ids);
}
