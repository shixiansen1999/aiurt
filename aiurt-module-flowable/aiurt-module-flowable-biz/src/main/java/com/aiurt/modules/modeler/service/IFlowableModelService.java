package com.aiurt.modules.modeler.service;

import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.jeecg.common.system.vo.LoginUser;

/**
 * @program: flow
 * @description: 模型创建
 * @author fgw
 * @create 2022-07-12
 */
public interface IFlowableModelService {
    /**
     * 创建模型初始化
     * @param modelRepresentation 参数
     * @param user 创建人
     * @return
     */
    Model creatModel(ModelRepresentation modelRepresentation, LoginUser user);
}
