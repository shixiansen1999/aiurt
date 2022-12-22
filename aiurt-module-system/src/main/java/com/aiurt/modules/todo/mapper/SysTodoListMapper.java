package com.aiurt.modules.todo.mapper;


import com.aiurt.modules.todo.entity.SysTodoList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
public interface SysTodoListMapper extends BaseMapper<SysTodoList> {

    /**
     * 分页查询
     * @param page
     * @param sysTodoList
     * @param todoTypeList
     * @return
     */
    List<SysTodoList> getTodoList(@Param("page") Page<SysTodoList> page, @Param("sysTodoList") SysTodoList sysTodoList, @Param("todoTypeList") List<String> todoTypeList);
}
