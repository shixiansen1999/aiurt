package com.aiurt.modules.modeler.service;

import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.jeecg.common.system.vo.LoginUser;

/**
 * @program: flow
 * @description: 流程引擎服务接口
 * @author: fgw
 * @create: 2022-07-10 17:51
 **/
public interface IFlowableBpmnModelService {

    /**
     * 创建初始化BPMN模型
     *
     * @param modelInfo 参数
     * @return
     */
    ActCustomModelInfo createInitBpmn(ActCustomModelInfo modelInfo, LoginUser user);

    /**
     * 创建模型初始化
     * @param modelRepresentation 参数
     * @param user
     * @return
     */
    Model creatModel(ModelRepresentation modelRepresentation, LoginUser user);
}
