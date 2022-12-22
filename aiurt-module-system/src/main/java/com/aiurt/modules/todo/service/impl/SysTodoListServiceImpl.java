package com.aiurt.modules.todo.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.mapper.SysTodoListMapper;
import com.aiurt.modules.todo.service.ISysTodoListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
@Service
public class SysTodoListServiceImpl extends ServiceImpl<SysTodoListMapper, SysTodoList> implements ISysTodoListService {

    @Override
    public IPage<SysTodoList> getTodoList(Page<SysTodoList> page, SysTodoList sysTodoList) {
        List<String> todoTypeList = null;
        if (ObjectUtil.isNotEmpty(sysTodoList) && StrUtil.isNotEmpty(sysTodoList.getCurrentUserName())) {
            todoTypeList = StrUtil.split(sysTodoList.getCurrentUserName(), ',');
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isNotEmpty(sysUser)) {
            sysTodoList.setCurrentUserName(sysUser.getUsername());
        }
        List<SysTodoList> todoList = baseMapper.getTodoList(page, sysTodoList, todoTypeList);
        return page.setRecords(todoList);
    }

    @Override
    public IPage<SysTodoList> queryPageList(Page<SysTodoList> page, SysTodoList sysTodoList) {
        LambdaQueryWrapper queryWrapper = doQuery(sysTodoList);
        return this.page(page,queryWrapper);
    }

    /**
     * 构建查询条件
     *
     * @param sysTodoList
     * @return
     */
    private LambdaQueryWrapper<SysTodoList> doQuery(SysTodoList sysTodoList) {

        LambdaQueryWrapper<SysTodoList> sysTodoListLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 任务状态
        if (ObjectUtil.isNotEmpty(sysTodoList) && StrUtil.isNotEmpty(sysTodoList.getTodoType())) {
            List<String> sysTodoStr= StrUtil.split(sysTodoList.getCurrentUserName(), ',');
            if(CollUtil.isNotEmpty(sysTodoStr)) {
                sysTodoListLambdaQueryWrapper.in(SysTodoList::getTodoType, sysTodoStr);
            }
        }

        // 任务名称
        if(StrUtil.isNotEmpty(sysTodoList.getTaskName())){
            sysTodoListLambdaQueryWrapper.like(SysTodoList::getTaskName, sysTodoList.getTaskName());
        }

        // 流程名称
        if(StrUtil.isNotEmpty(sysTodoList.getProcessDefinitionName())){
            sysTodoListLambdaQueryWrapper.like(SysTodoList::getProcessDefinitionName, sysTodoList.getProcessDefinitionName());
        }

        // 任务类型
        if(StrUtil.isNotEmpty(sysTodoList.getTaskType())){
            sysTodoListLambdaQueryWrapper.eq(SysTodoList::getTaskType, sysTodoList.getTaskType());
        }

        // 我发起的
        if(StrUtil.isNotEmpty(sysTodoList.getCreateBy())){
            sysTodoListLambdaQueryWrapper.eq(SysTodoList::getCreateBy, sysTodoList.getCreateBy());
        }

        // 当前办理的用户账号包含有我的账号
        if (StrUtil.isNotEmpty(sysTodoList.getCurrentUserName())) {
            sysTodoListLambdaQueryWrapper.apply("FIND_IN_SET({0},current_user_name)", sysTodoList.getCurrentUserName());
        }

        sysTodoListLambdaQueryWrapper.orderByDesc(SysTodoList::getCreateTime);
        return sysTodoListLambdaQueryWrapper;
    }
}
