package com.aiurt.modules.faultknowledgebasetype.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faultknowledgebasetype.dto.SubSystemDTO;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.faultknowledgebasetype.service.IFaultKnowledgeBaseTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.models.auth.In;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 故障知识分类
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Service
public class FaultKnowledgeBaseTypeServiceImpl extends ServiceImpl<FaultKnowledgeBaseTypeMapper, FaultKnowledgeBaseType> implements IFaultKnowledgeBaseTypeService {
    @Autowired
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;

    @Override
    public List<MajorDTO> faultKnowledgeBaseTypeTreeList() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> majorByUser = faultKnowledgeBaseTypeMapper.getMajorByUser(sysUser.getId());
        if (CollectionUtil.isNotEmpty(majorByUser)) {
            //用户拥有的专业
            List<MajorDTO> allMajor = faultKnowledgeBaseTypeMapper.getAllMajor(majorByUser);
            for (MajorDTO majorDTO:allMajor) {
                //用户拥有的专业的子系统
                List<SubSystemDTO> subSystemByUser = faultKnowledgeBaseTypeMapper.getSubSystemByUser(sysUser.getId(), majorDTO.getMajorCode());
                if (CollectionUtil.isNotEmpty(subSystemByUser)) {
                    for (SubSystemDTO subSystemDTO : subSystemByUser) {
                        //该子系统的全部知识库类型
                        LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
                        List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes = faultKnowledgeBaseTypeMapper.selectList(
                                queryWrapper.eq(FaultKnowledgeBaseType::getDelFlag, 0)
                                        .eq(FaultKnowledgeBaseType::getSystemCode, subSystemDTO.getSystemCode())
                                        .orderByDesc(FaultKnowledgeBaseType::getCreateTime));
                        //获取子节点
                        List<FaultKnowledgeBaseType> treeRes = getTreeRes(faultKnowledgeBaseTypes, 0);
                        subSystemDTO.setFaultKnowledgeBaseTypes(treeRes);
                        majorDTO.setSubSystemDTOS(subSystemByUser);
                    }
                } else {
                    LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
                    List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes = faultKnowledgeBaseTypeMapper.selectList(
                            queryWrapper.eq(FaultKnowledgeBaseType::getDelFlag, 0)
                                    .eq(FaultKnowledgeBaseType::getMajorCode, majorDTO.getMajorCode()));
                    //获取子节点
                    List<FaultKnowledgeBaseType> treeRes = getTreeRes(faultKnowledgeBaseTypes, 0);
                    majorDTO.setFaultKnowledgeBaseTypes(treeRes);
                }
            }
            return allMajor;
        }
        return null;
    }

    List<FaultKnowledgeBaseType> getTreeRes(List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes, Integer pid){
        List<FaultKnowledgeBaseType> childList = faultKnowledgeBaseTypes.stream().filter(f -> f.getPid().equals(pid)).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(childList)){
            for (FaultKnowledgeBaseType faultKnowledgeBaseType : childList) {
                faultKnowledgeBaseType.setFaultKnowledgeBaseTypes(getTreeRes(faultKnowledgeBaseTypes,faultKnowledgeBaseType.getId()));
            }
        }
        return childList;
    }

    @Override
    public void add(FaultKnowledgeBaseType faultKnowledgeBaseType) {
        if (faultKnowledgeBaseType.getPid() == 0) {
            faultKnowledgeBaseType.setCodeCc("/" + faultKnowledgeBaseType.getCode() + "/");
        } else {
            FaultKnowledgeBaseType f = faultKnowledgeBaseTypeMapper.selectById(faultKnowledgeBaseType.getPid());
            faultKnowledgeBaseType.setCodeCc(f.getCodeCc() + faultKnowledgeBaseType.getCode() + "/");
        }
        faultKnowledgeBaseTypeMapper.insert(faultKnowledgeBaseType);
    }
}
