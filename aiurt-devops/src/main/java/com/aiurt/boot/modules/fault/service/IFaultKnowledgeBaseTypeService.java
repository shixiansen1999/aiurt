package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.fault.entity.FaultKnowledgeBaseType;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 故障知识库类型
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IFaultKnowledgeBaseTypeService extends IService<FaultKnowledgeBaseType> {

    /**
     * 添加故障知识分类
     * @param baseType
     * @return
     */
    public Result add(FaultKnowledgeBaseType baseType, HttpServletRequest req);

    /**
     * 根据id假删除
     * @param id
     */
    public Result deleteById(Integer id);

}
