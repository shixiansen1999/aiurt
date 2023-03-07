package com.aiurt.modules.todo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.MessageTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.HTMLUtils;
import com.aiurt.common.util.dynamic.db.FreemarkerParseFactory;
import com.aiurt.modules.message.entity.SysMessageTemplate;
import com.aiurt.modules.message.handle.impl.DdSendMsgHandle;
import com.aiurt.modules.message.handle.impl.EmailSendMsgHandle;
import com.aiurt.modules.message.handle.impl.QywxSendMsgHandle;
import com.aiurt.modules.message.service.ISysMessageTemplateService;
import com.aiurt.modules.message.websocket.WebSocket;
import com.aiurt.modules.todo.dto.BpmnTodoDTO;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.service.ISysTodoListService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    @Autowired
    private ISysMessageTemplateService sysMessageTemplateService;
    @Autowired
    private QywxSendMsgHandle qywxSendMsgHandle;

    @Autowired
    private EmailSendMsgHandle emailSendMsgHandle;

    @Autowired
    private DdSendMsgHandle ddSendMsgHandle;


    @Override
    public void createBbmnTodoTask(BpmnTodoDTO bpmnTodoDTO) {
        SysTodoList sysTodoList = new SysTodoList();
        BeanUtil.copyProperties(bpmnTodoDTO, sysTodoList, "");
        sysTodoList.setTaskType(TodoTaskTypeEnum.BPMN.getType());
        doCreateTodoTask(sysTodoList);
    }

    @Override
    public void createTodoTask(TodoDTO todoDTO) {
        SysTodoList sysTodoList = new SysTodoList();
        BeanUtil.copyProperties(todoDTO, sysTodoList, "");
        doCreateTodoTask(sysTodoList);
    }

    @Override
    public void updateTodoTaskState(String businessType, String businessKey, String username, String todoType) {
        boolean update = false;
        if (StrUtil.isNotEmpty(businessKey)) {
            LambdaUpdateWrapper<SysTodoList> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SysTodoList::getTodoType, todoType)
                    .set(SysTodoList::getActualUserName, username)
                    .set(SysTodoList::getUpdateTime, new Date())
                    .eq(SysTodoList::getBusinessKey, businessKey)
                    .eq(SysTodoList::getBusinessType, businessType);
            update = sysTodoListService.update(updateWrapper);
        }

        if (update) {
            log.info("推送消息：businessKey：{},businessType:{},username:{}", businessKey, businessType, username);
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
                .set(SysTodoList::getUpdateTime, new Date())
                .eq(SysTodoList::getTaskId, taskId)
                .eq(SysTodoList::getProcessInstanceId, processInstanceId);

        boolean update = sysTodoListService.update(updateWrapper);
        if (update) {
            log.info("更新流程待办任务->推送消息：processInstanceId：{},taskId:{},username:{}", processInstanceId, taskId, username);
            webSocket.pushMessage("please update the to-do list");
        }
    }

    private void doCreateTodoTask(SysTodoList sysTodoList) {
        if (ObjectUtil.isEmpty(sysTodoList)) {
            log.error("待办任务创建失败");
            return;
        }
        //处理消息模板
        String content = null;
        String templateCode = sysTodoList.getTemplateCode();
        if(StrUtil.isNotBlank(templateCode)){
            SysMessageTemplate templateEntity = getTemplateEntity(templateCode);
            boolean isMarkdown = CommonConstant.MSG_TEMPLATE_TYPE_MD.equals(templateEntity.getTemplateType());
            content = templateEntity.getTemplateContent();
            if(StrUtil.isNotBlank(content) && null!=sysTodoList.getData()){
                content = FreemarkerParseFactory.parseTemplateContent(content, sysTodoList.getData(), isMarkdown);
            }
            sysTodoList.setMarkdown(isMarkdown);
            sysTodoList.setMsgContent(content);
        }

        // 定时任务下发送消息则跳过补充信息
        Boolean timedTask = ObjectUtil.isNotEmpty(sysTodoList.getTimedTask()) && sysTodoList.getTimedTask() ? true : false;
        if (!timedTask) {
            // 补充所属部门信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (ObjectUtil.isNotEmpty(sysUser)) {
                sysTodoList.setSysOrgCode(sysUser.getOrgCode());
            }
        }
        sysTodoList.setCreateTime(new Date());
        if (sysTodoList.getMarkdown()) {
            // 系统消息要解析Markdown
            sysTodoList.setMsgContent(HTMLUtils.parseMarkdown(sysTodoList.getMsgContent()));
        }
        // 保存
        boolean save = sysTodoListService.save(sysTodoList);

        // 通过webSocket推送消息给前端刷新列表
        if (save) {
            log.info("新增代办->推送消息：businessKey：{}", JSONObject.toJSONString(sysTodoList));
            webSocket.pushMessage("please update the to-do list");
        }
        String type = sysTodoList.getType();
        if(StrUtil.isBlank(type)){
            throw new AiurtBootException("发送消息失败,消息发送渠道没有配置！");
        }
        List<String> messageTypes = StrUtil.splitTrim(type, ",");
        MessageDTO messageDTO = new MessageDTO();
        BeanUtil.copyProperties(sysTodoList, messageDTO);
        messageDTO.setToUser(sysTodoList.getCurrentUserName());
        messageDTO.setIsMarkdown(sysTodoList.getMarkdown());
        for (String messageType : messageTypes) {
            //update-end-author:taoyan date:2022-7-9 for: 将模板解析代码移至消息发送, 而不是调用的地方
            if(MessageTypeEnum.XT.getType().equals(messageType)){
                webSocket.pushMessage("please update the to-do list");
            }else if(MessageTypeEnum.YJ.getType().equals(messageType)){
                if (messageDTO.isMarkdown()) {
                    // 邮件消息要解析Markdown
                    messageDTO.setContent(HTMLUtils.parseMarkdown(messageDTO.getContent()));
                }
                emailSendMsgHandle.sendMessage(messageDTO);
            }else if(MessageTypeEnum.DD.getType().equals(messageType)){
                ddSendMsgHandle.sendMessage(messageDTO);
            }else if(MessageTypeEnum.QYWX.getType().equals(messageType)){
                if (messageDTO.isMarkdown()) {
                    // 系统消息要解析Markdown
                    messageDTO.setContent(HTMLUtils.parseMarkdown(sysTodoList.getMsgContent()));
                }
                messageDTO.setBusKey(sysTodoList.getBusinessKey());
                if (sysTodoList.getBusinessType() != null) {
                    messageDTO.setBusType(sysTodoList.getBusinessType());
                } else {
                    messageDTO.setBusType(sysTodoList.getProcessCode());
                }
                messageDTO.setBusKey(sysTodoList.getBusinessKey());
                if (sysTodoList.getBusinessType() != null) {
                    messageDTO.setBusType(sysTodoList.getBusinessType());
                } else {
                    messageDTO.setBusType(sysTodoList.getProcessCode());
                }
                qywxSendMsgHandle.sendMessage(messageDTO);
            }
        }
    }

    /**
     * 获取模板内容，解析markdown
     *
     * @param code
     * @return
     */
    private SysMessageTemplate getTemplateEntity(String code) {
        List<SysMessageTemplate> list = sysMessageTemplateService.selectByCode(code);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }


}
