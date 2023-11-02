package com.aiurt.modules.modeler.service;

import com.aiurt.modules.modeler.dto.ConnectionConditionConfigDTO;
import com.aiurt.modules.modeler.entity.ActCustomVariable;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 流程变量
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
public interface IActCustomVariableService extends IService<ActCustomVariable> {

    /**
     * 获取连接条件配置的字段名称下拉列表
     * @param modelId 模板id
     * @return
     */
    List<ConnectionConditionConfigDTO> getFilterFieldNamesDropdown(String modelId);

}
