package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.fault.entity.FaultKnowledgeBaseType;
import com.aiurt.boot.modules.fault.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.boot.modules.fault.service.IFaultKnowledgeBaseTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

/**
 * @Description: 故障知识库类型
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultKnowledgeBaseTypeServiceImpl extends ServiceImpl<FaultKnowledgeBaseTypeMapper, FaultKnowledgeBaseType> implements IFaultKnowledgeBaseTypeService {

    @Resource
    private FaultKnowledgeBaseTypeMapper baseTypeMapper;

    @Resource
    private ISysBaseAPI iSysBaseAPI;


    /**
     * 添加故障知识库分类
     * @param baseType
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result add(FaultKnowledgeBaseType baseType, HttpServletRequest req) {
        FaultKnowledgeBaseType type = new FaultKnowledgeBaseType();
        type.setSystemCode(baseType.getSystemCode());
        if (baseType.getParentId() != null) {
            type.setParentId(baseType.getParentId());
        }
        type.setName(baseType.getName());
        type.setDelFlag(0);
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        type.setCreateBy(userId);
        baseTypeMapper.insert(type);
        return Result.ok();
    }
}
