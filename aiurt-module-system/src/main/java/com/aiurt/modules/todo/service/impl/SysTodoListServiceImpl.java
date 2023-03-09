package com.aiurt.modules.todo.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.system.mapper.SysDictMapper;
import com.aiurt.modules.todo.dto.SysTodoCountDTO;
import com.aiurt.modules.todo.dto.TaskModuleDTO;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.mapper.SysTodoListMapper;
import com.aiurt.modules.todo.service.ISysTodoListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.flowable.task.api.Task;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
@Service
public class SysTodoListServiceImpl extends ServiceImpl<SysTodoListMapper, SysTodoList> implements ISysTodoListService {

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private FlowApiService flowApiService;


    @Override
    public IPage<SysTodoList> queryPageList(Page<SysTodoList> page, SysTodoList sysTodoList) {
        LambdaQueryWrapper<SysTodoList> queryWrapper = doQuery(sysTodoList);
        Page<SysTodoList> listPage = this.page(page, queryWrapper);
        // 流程需要已办，待办的需要处理
        List<SysTodoList> records = listPage.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            records.forEach(todoList->{
                String taskType = todoList.getTaskType();
                if (StrUtil.equalsIgnoreCase("bpmn", taskType) && StrUtil.equalsIgnoreCase("1,3", sysTodoList.getTodoType())) {
                    Task activeTask = flowApiService.getProcessInstanceActiveTask(todoList.getProcessInstanceId(), null);
                    if (Objects.nonNull(activeTask)) {
                        todoList.setTaskId(activeTask.getId());
                    }
                }
            });
        }
        return listPage;
    }

    @Override
    public List<TaskModuleDTO> queryTaskModuleList(SysTodoList sysTodoList) {
        List<TaskModuleDTO> result = CollUtil.newArrayList();
        LambdaQueryWrapper<SysTodoList> queryWrapper = doQuery(sysTodoList);
        List<SysTodoList> list = this.list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return result;
        }

        Map<String, List<SysTodoList>> map = list.stream().collect(Collectors.groupingBy(SysTodoList::getTaskType));
        // 字典翻译
        Map<String, String> dictMap = CollUtil.newHashMap(8);
        List<DictModel> todoType = sysDictMapper.queryEnableDictItemsByCode("todo_type");
        if (CollUtil.isNotEmpty(todoType)) {
            dictMap = todoType.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        }

        // 封装数据
        for (Map.Entry<String, List<SysTodoList>> entry : map.entrySet()) {
            TaskModuleDTO temp = new TaskModuleDTO();
            temp.setField(entry.getKey());
            temp.setCount(CollUtil.isNotEmpty(entry.getValue()) ? entry.getValue().size() : 0);
            if (StrUtil.isNotEmpty(entry.getKey()) && MapUtil.isNotEmpty(dictMap)) {
                temp.setName(dictMap.get(entry.getKey()));
            }
            result.add(temp);
        }
        return result;
    }

    /**
     * 查询流程的代办统计
     *
     * @param userName
     * @return
     */
    @Override
    public List<SysTodoCountDTO> queryBpmn(String userName) {
        return baseMapper.queryBpmn(userName);
    }

    /**
     * @param list
     * @return
     */
    @Override
    public SysTodoList queryBpmnLast(List<String> list) {
        return baseMapper.queryBpmnLast(list);
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
            List<String> sysTodoStr = StrUtil.split(sysTodoList.getTodoType(), ',');
            if (CollUtil.isNotEmpty(sysTodoStr)) {
                sysTodoListLambdaQueryWrapper.in(SysTodoList::getTodoType, sysTodoStr);
            }
        }

        // 任务名称
        if (StrUtil.isNotEmpty(sysTodoList.getTaskName())) {
            sysTodoListLambdaQueryWrapper.like(SysTodoList::getTaskName, sysTodoList.getTaskName());
        }

        // 流程名称
        if (StrUtil.isNotEmpty(sysTodoList.getProcessDefinitionName())) {
            sysTodoListLambdaQueryWrapper.like(SysTodoList::getProcessDefinitionName, sysTodoList.getProcessDefinitionName());
        }

        // 任务类型
        if (StrUtil.isNotEmpty(sysTodoList.getTaskType())) {
            List<String> sysTaskStr = StrUtil.split(sysTodoList.getTaskType(), ',');
            if (CollUtil.isNotEmpty(sysTaskStr)) {
                sysTodoListLambdaQueryWrapper.in(SysTodoList::getTaskType, sysTaskStr);
            }
        }

        // 我发起的
        if (StrUtil.isNotEmpty(sysTodoList.getCreateBy())) {
            sysTodoListLambdaQueryWrapper.eq(SysTodoList::getCreateBy, sysTodoList.getCreateBy());
        }

        // 当前办理的用户账号包含有我的账号
        if (StrUtil.isNotEmpty(sysTodoList.getCurrentUserName())) {
            sysTodoListLambdaQueryWrapper.apply("FIND_IN_SET({0},current_user_name)", sysTodoList.getCurrentUserName());
        }
        sysTodoListLambdaQueryWrapper.orderByAsc(SysTodoList::getTodoType);
        sysTodoListLambdaQueryWrapper.orderByDesc(SysTodoList::getCreateTime);
        return sysTodoListLambdaQueryWrapper;
    }
}
