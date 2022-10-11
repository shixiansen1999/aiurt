package com.aiurt.modules.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.utils.ReflectionService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.delegate.event.impl.FlowableSequenceFlowTakenEventImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 流向线监听事件
 * @author fgw
 */
public class SequenceFlowTakenListener implements FlowableEventListener {

    private Logger logger = LoggerFactory.getLogger(SequenceFlowTakenListener.class);

    private static final String PROPERTY = "property";

    @Override
    public void onEvent(FlowableEvent event) {
        if (!(event instanceof FlowableSequenceFlowTakenEventImpl)) {
            return;
        }

        FlowableSequenceFlowTakenEventImpl activitiEntityEvent = (FlowableSequenceFlowTakenEventImpl) event;

        String id = activitiEntityEvent.getId();

        String processInstanceId = activitiEntityEvent.getProcessInstanceId();

        String processDefinitionId = activitiEntityEvent.getProcessDefinitionId();

        FlowElementUtil flowElementUtil = SpringContextUtils.getBean(FlowElementUtil.class);

        // 获取当前节点定义
        FlowElement flowElement = flowElementUtil.getFlowElement(processDefinitionId, id);

        Map<String, List<ExtensionElement>> extensionElements = flowElement.getExtensionElements();

        // 解析属性
        List<ExtensionElement> propertyElements = extensionElements.get(PROPERTY);
        List<ExtensionElement> serviceElements = extensionElements.get("service");

        String serviceName = "";
        if (CollUtil.isNotEmpty(serviceElements)) {
            ExtensionElement extensionElement = serviceElements.get(0);
            serviceName = extensionElement.getAttributeValue(null, "name");
        }

        if (CollUtil.isNotEmpty(propertyElements)) {

            ExtensionElement e = propertyElements.get(0);
            String value = e.getAttributeValue(null, "value");
            String service = e.getAttributeValue(null, "service");
            if (StrUtil.isBlank(service)) {
                service = serviceName;
            }
            if (StrUtil.isAllNotBlank(value, service)) {
                List<String> list = StrUtil.splitTrim(service, '.');
                if (list.size() == 2) {
                    // 更新状态
                    String className = StrUtil.lowerFirst(list.get(0));

                    // 更新状态
                    ReflectionService reflectionService = SpringContextUtils.getBean(ReflectionService.class);
                    try {
                        ProcessInstance processInstance = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                                .createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                        // 封装数据
                        JSONObject data = new JSONObject();
                        data.put("businessKey", processInstance.getBusinessKey());
                        data.put("states", Integer.valueOf(value));
                        // 更新状态
                        reflectionService.invokeService(className, list.get(1), data);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
     }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
