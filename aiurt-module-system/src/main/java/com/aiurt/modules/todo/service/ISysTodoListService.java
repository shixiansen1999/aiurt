package com.aiurt.modules.todo.service;

import com.aiurt.modules.todo.dto.SysTodoCountDTO;
import com.aiurt.modules.todo.dto.TaskModuleDTO;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date:   2022-12-21
 * @Version: V1.0
 */
public interface ISysTodoListService extends IService<SysTodoList> {

    /**
    /**
     * 分页查询待办列表
     * @param page
     * @param sysTodoList
     * @return
     */
    IPage<SysTodoList> queryPageList(Page<SysTodoList> page, SysTodoList sysTodoList);

    /**
     * 查询各个类型待办数量
     * @param sysTodoList
     * @return
     */
    List<TaskModuleDTO> queryTaskModuleList(SysTodoList sysTodoList);

    /**
     * 查询流程的代办统计
     * @param userName
     * @return
     */
    List<SysTodoCountDTO> queryBpmn(String userName);

    /**
     *
     * @param list
     * @return
     */
    SysTodoList queryBpmnLast(List<String> list, String userName);



}
