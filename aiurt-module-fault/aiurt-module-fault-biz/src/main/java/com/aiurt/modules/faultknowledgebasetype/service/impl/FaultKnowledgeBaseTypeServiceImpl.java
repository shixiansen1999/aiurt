package com.aiurt.modules.faultknowledgebasetype.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faultknowledgebasetype.dto.SelectTableDTO;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.faultknowledgebasetype.service.IFaultKnowledgeBaseTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Lazy
    @Autowired
    private CommonAPI commonApi;

    @Override
    public List<MajorDTO> faultKnowledgeBaseTypeTreeList(String majorCode,String systemCode) {
        LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultKnowledgeBaseType::getDelFlag, "0").orderByDesc(FaultKnowledgeBaseType::getCreateTime);
        List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes = faultKnowledgeBaseTypeMapper.selectList(queryWrapper);

        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        //用户用的专业和子系统
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = commonApi.getMajorByUserId(sysUser.getId());
        List<CsUserSubsystemModel> subsystemByUserId = commonApi.getSubsystemByUserId(sysUser.getId());
        if (StringUtils.isNotBlank(majorCode) && StringUtils.isNotBlank(systemCode)) {
            List<CsUserMajorModel> majorModels = majorByUserId.stream().filter(m -> !m.getMajorCode().equals(majorCode)).collect(Collectors.toList());
            List<CsUserSubsystemModel> subsystemModels = subsystemByUserId.stream().filter(s -> !s.getSystemCode().equals(systemCode)).collect(Collectors.toList());
            majorByUserId.removeAll(majorModels);
            subsystemByUserId.removeAll(subsystemModels);
        }
        if (CollectionUtil.isNotEmpty(majorByUserId)) {
            List<MajorDTO> allMajor = new ArrayList<>();
            for (CsUserMajorModel major: majorByUserId) {
                MajorDTO majorDTO = new MajorDTO();
                BeanUtils.copyProperties(major, majorDTO);
                allMajor.add(majorDTO);
            }
            //用户拥有的专业
            for (MajorDTO majorDTO:allMajor) {
                majorDTO.setKey(majorDTO.getId());
                majorDTO.setLabel(majorDTO.getMajorName());
                majorDTO.setValue(majorDTO.getMajorCode());
                majorDTO.setIsBaseType(false);
                List<SelectTableDTO> selectTableDTOList = new ArrayList<>();
                //用户拥有的专业的子系统
                List<CsUserSubsystemModel> collect = subsystemByUserId.stream().filter(s -> s.getMajorCode().equals(majorDTO.getMajorCode())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    for (CsUserSubsystemModel csUserSubsystemModel : collect) {
                        SelectTableDTO selectTableDTO = new SelectTableDTO();
                        selectTableDTO.setKey(csUserSubsystemModel.getId());
                        selectTableDTO.setLabel(csUserSubsystemModel.getSystemName());
                        selectTableDTO.setValue(csUserSubsystemModel.getSystemCode());
                        selectTableDTO.setIsBaseType(false);
                        selectTableDTO.setSystemCode(csUserSubsystemModel.getSystemCode());
                        selectTableDTO.setMajorCode(majorDTO.getMajorCode());
                        List<SelectTableDTO> treeRes = new ArrayList<>();
                        //获取子节点
                        List<FaultKnowledgeBaseType> baseTypeList = faultKnowledgeBaseTypes.stream().filter(f -> f.getSystemCode().equals(csUserSubsystemModel.getSystemCode()) && f.getMajorCode().equals(majorDTO.getMajorCode())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(baseTypeList)) {
                            List<SelectTableDTO> childrenTress = new ArrayList<>();
                            baseTypeList.forEach(f->{
                                SelectTableDTO selectTable = new SelectTableDTO();
                                selectTable.setId(f.getId());
                                selectTable.setKey(f.getId().toString());
                                selectTable.setLabel(f.getName());
                                selectTable.setValue(f.getCode());
                                selectTable.setPid(f.getPid());
                                selectTable.setIsBaseType(true);
                                selectTable.setSystemCode(f.getSystemCode());
                                selectTable.setMajorCode(majorDTO.getMajorCode());
                                childrenTress.add(selectTable);
                            });
                            treeRes.addAll(getTreeRes(childrenTress, "0"));
                        }
                        selectTableDTO.setChildren(treeRes);
                        selectTableDTOList.add(selectTableDTO);

                    }
                    majorDTO.setChildren(selectTableDTOList);
                } else {
                    //获取子节点
                    List<FaultKnowledgeBaseType> baseTypeList = faultKnowledgeBaseTypes.stream().filter(f -> f.getMajorCode().equals(majorDTO.getMajorCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(baseTypeList)) {
                        List<SelectTableDTO> childrenTress = new ArrayList<>();
                        baseTypeList.forEach(f->{
                            SelectTableDTO selectTable = new SelectTableDTO();
                            selectTable.setId(f.getId());
                            selectTable.setKey(f.getId().toString());
                            selectTable.setLabel(f.getName());
                            selectTable.setValue(f.getCode());
                            selectTable.setPid(f.getPid());
                            selectTable.setIsBaseType(true);
                            selectTable.setSystemCode(f.getSystemCode());
                            selectTable.setMajorCode(majorDTO.getMajorCode());
                            childrenTress.add(selectTable);
                        });
                        List<SelectTableDTO> treeRes = getTreeRes(childrenTress, "0");
                        majorDTO.setChildren(treeRes);
                    }
                }
            }
            return allMajor;
        }
        GlobalThreadLocal.setDataFilter(b);
        return null;
    }

    List<SelectTableDTO> getTreeRes(List<SelectTableDTO> children, String pid){
        List<SelectTableDTO> childList = children.stream().filter(f -> f.getPid().equals(pid)).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(childList)){
            for (SelectTableDTO selectTableDTO : childList) {
                selectTableDTO.setChildren(getTreeRes(children,selectTableDTO.getId()));
            }
        }
        return childList;
    }

    @Override
    public void add(FaultKnowledgeBaseType faultKnowledgeBaseType) {
        String pid = "0";
        if ((pid).equals(faultKnowledgeBaseType.getPid())) {
            faultKnowledgeBaseType.setCodeCc("/" + faultKnowledgeBaseType.getCode() + "/");
        } else {
            FaultKnowledgeBaseType f = faultKnowledgeBaseTypeMapper.selectById(faultKnowledgeBaseType.getPid());
            faultKnowledgeBaseType.setCodeCc(f.getCodeCc() + faultKnowledgeBaseType.getCode() + "/");
        }
        faultKnowledgeBaseType.setDelFlag(0);
        faultKnowledgeBaseTypeMapper.insert(faultKnowledgeBaseType);
    }
}
