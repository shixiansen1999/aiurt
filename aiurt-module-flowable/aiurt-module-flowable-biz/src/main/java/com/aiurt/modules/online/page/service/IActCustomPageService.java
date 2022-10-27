package com.aiurt.modules.online.page.service;


import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 设计表单
 * @Author: aiurt
 * @Date:   2022-10-26
 * @Version: V1.0
 */
public interface IActCustomPageService extends IService<ActCustomPage> {

    /**
     * 编辑菜单
     * @param actCustomPage
     */
    void edit(ActCustomPage actCustomPage);
}
