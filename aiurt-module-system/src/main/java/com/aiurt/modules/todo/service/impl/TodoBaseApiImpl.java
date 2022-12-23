package com.aiurt.modules.todo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.modules.message.websocket.WebSocket;
import com.aiurt.modules.todo.dto.BpmnTodoDTO;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.service.ISysTodoListService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description 待办任务具体实现类
 * @Author MrWei
 * @Date 2022/12/21 11:15
 **/
@Service
@Slf4j
public class TodoBaseApiImpl implements ISTodoBaseAPI {
    @Autowired
    private ISysTodoListService sysTodoListService;
    @Autowired
    private WebSocket webSocket;

    @Override
    public void createBbmnTodoTask(BpmnTodoDTO bpmnTodoDTO) {
        SysTodoList sysTodoList = new SysTodoList();
        sysTodoList.setTaskType(TodoTaskTypeEnum.BPMN.getType());
        BeanUtil.copyProperties(bpmnTodoDTO, sysTodoList, "");
        doCreateTodoTask(sysTodoList);
    }

    @Override
    public void createTodoTask(TodoDTO todoDTO) {
        SysTodoList sysTodoList = new SysTodoList();
        BeanUtil.copyProperties(todoDTO, sysTodoList, "");
        doCreateTodoTask(sysTodoList);
    }

    @Override
    public void updateTodoTaskState(String todoId, String businessKey, String username, String todoType) {
        boolean update = false;
        if (StrUtil.isNotEmpty(todoId)) {
            SysTodoList sysTodoList = sysTodoListService.getById(todoId);
            if (ObjectUtil.isEmpty(sysTodoList)) {
                log.error("未查询到相关待办任务信息");
                return;
            }
            sysTodoList.setTodoType(todoType);
            sysTodoList.setActualUserName(username);
            update = sysTodoListService.updateById(sysTodoList);
        }

        if (StrUtil.isNotEmpty(businessKey)) {
            LambdaUpdateWrapper<SysTodoList> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SysTodoList::getTodoType, todoType)
                    .set(SysTodoList::getActualUserName, username)
                    .eq(SysTodoList::getBusinessKey, businessKey);
            update = sysTodoListService.update(updateWrapper);
        }

        if (update) {
            webSocket.pushMessage("please update the to-do list");
        }

    }

    /**
     * 更新流程待办任务
     *
     * @param taskId
     * @param processInstanceId
     * @param username
     * @param todoType
     */
    @Override
    public void updateBpmnTaskState(String taskId, String processInstanceId, String username, String todoType) {
        LambdaUpdateWrapper<SysTodoList> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysTodoList::getTodoType, todoType)
                .set(SysTodoList::getActualUserName, username)
                .eq(SysTodoList::getTaskId, taskId)
                .eq(SysTodoList::getProcessInstanceId, processInstanceId);

        boolean update = sysTodoListService.update(updateWrapper);
        if (update) {
            webSocket.pushMessage("please update the to-do list");
        }
    }

    private void doCreateTodoTask(SysTodoList sysTodoList) {
        if (ObjectUtil.isEmpty(sysTodoList)) {
            log.error("待办任务创建失败");
            return;
        }
        // 补充所属部门信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isNotEmpty(sysUser)) {
            sysTodoList.setSysOrgCode(sysUser.getOrgCode());
        }
        sysTodoList.setCreateTime(new Date());
        // 保存
        boolean save = sysTodoListService.save(sysTodoList);

        // 通过webSocket推送消息给前端刷新列表
        if (save) {
            webSocket.pushMessage("please update the to-do list");
        }

    }


}
