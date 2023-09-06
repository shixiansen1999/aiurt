package com.aiurt.modules.online.page.service;


import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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

    /**
     *表单分页列表
     * @param page
     * @param actCustomPage
     * @return
     */
    IPage<ActCustomPage> queryPageList(Page<ActCustomPage> page,ActCustomPage actCustomPage);

    /**
     * 查询接口名称在数据库是否存在
     *
     * @param name
     * @param id
     * @return
     */
    boolean isNameExists(String name, String id);

    /**
     * 表单新增
     * @param actCustomPage
     * @return
     */
    Result<String> add(ActCustomPage actCustomPage);


}
