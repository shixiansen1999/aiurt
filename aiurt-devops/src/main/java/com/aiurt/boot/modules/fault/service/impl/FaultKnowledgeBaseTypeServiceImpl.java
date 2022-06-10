package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.common.system.util.JwtUtil;
import com.swsc.copsms.common.util.TokenUtils;
import com.swsc.copsms.modules.fault.entity.FaultKnowledgeBaseType;
import com.swsc.copsms.modules.fault.mapper.FaultKnowledgeBaseTypeMapper;
import com.swsc.copsms.modules.fault.service.IFaultKnowledgeBaseTypeService;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;

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
    private SysUserMapper userMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result add(FaultKnowledgeBaseType baseType, HttpServletRequest req) {

        FaultKnowledgeBaseType type = new FaultKnowledgeBaseType();
        if (baseType.getSystemCode() == null || "".equals(baseType.getSystemCode())) {
            throw new SwscException("系统编号不能为空");
        }
        type.setSystemCode(baseType.getSystemCode());

        if (baseType.getParentId() == null) {
            throw new SwscException("父ID不能为空");
        }
        type.setParentId(baseType.getParentId());

        if (baseType.getName() == null || "".equals(baseType.getName())) {
            throw new SwscException("类型名称不能为空");
        }
        type.setName(baseType.getName());

        if (baseType.getDelFlag() == null) {
            throw new SwscException("删除状态不能为空");
        }
        type.setDelFlag(baseType.getDelFlag());

        if (baseType.getCreateBy() == null) {
            throw new SwscException("创建人不能为空");
        }
        // 解密获得username，用于和数据库进行对比
        String token = TokenUtils.getTokenByRequest(req);

        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token非法无效!");
        }
        // 查询用户信息
        SysUser name = userMapper.getUserByName(username);
        if (name==null){
            throw new AuthenticationException("用户不存在!");
        }
        String id = name.getId();
        type.setCreateBy(id);
        if (baseType.getUpdateBy()!= null) {
            type.setUpdateBy(baseType.getUpdateBy());
        }
        type.setCreateTime(new Date());
        type.setUpdateTime(new Date());
        baseTypeMapper.insert(type);
        return Result.ok();
    }

    /**
     * 根据id假删除
     * @param id
     */
    @Override
    public Result deleteById(Integer id) {
        baseTypeMapper.deleteOne(id);
        return Result.ok();
    }
}
