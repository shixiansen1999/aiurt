package com.aiurt.modules.modeler.service;

import com.aiurt.modules.modeler.dto.ModelInfoVo;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.jeecg.common.system.vo.LoginUser;

import java.io.ByteArrayInputStream;

/**
 * @program: flow
 * @description: 流程引擎服务接口
 * @author: fgw
 * @create: 2022-07-10 17:51
 **/
public interface IFlowableBpmnService {

    /**
     * 创建初始化BPMN模型
     *
     * @param modelInfo 参数
     * @return
     */
    Model createInitBpmn(ActCustomModelInfo modelInfo, LoginUser user);

    /**
     * 加载bpmn的xml文件
     *
     * @param modelId 流程模型id
     * @return
     */
    ModelInfoVo loadBpmnXmlByModelId(String modelId);


    /**
     * 导入bpmn模型
     *
     * @param modelId     模型ID
     * @param fileName    文件名称
     * @param modelStream 模型文件流
     * @param user        登录用户
     * @return
     */
    String importBpmnModel(String modelId, String fileName, ByteArrayInputStream modelStream, LoginUser user);

    /**
     * 部署流程
     * @param modelId
     */
    void publishBpmn(String modelId);

    /**
     * 根据流程定义id读取
     * @param processDefinitionId
     * @return
     */
    BpmnModel getBpmnModelByDefinitionId(String processDefinitionId);
}
