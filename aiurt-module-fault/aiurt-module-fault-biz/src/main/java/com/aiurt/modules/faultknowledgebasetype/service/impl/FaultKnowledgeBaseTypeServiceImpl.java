package com.aiurt.modules.faultknowledgebasetype.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faultknowledgebasetype.dto.SubSystemDTO;
import com.aiurt.modules.faultknowledgebasetype.dto.SelectTableDTO;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.faultknowledgebasetype.service.IFaultKnowledgeBaseTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public List<MajorDTO> faultKnowledgeBaseTypeTreeList() {
        LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultKnowledgeBaseType::getDelFlag, "0").orderByDesc(FaultKnowledgeBaseType::getCreateTime);
        List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes = faultKnowledgeBaseTypeMapper.selectList(queryWrapper);
        List<String> majors = faultKnowledgeBaseTypes.stream().map(FaultKnowledgeBaseType::getMajorCode).distinct().collect(Collectors.toList());
        List<String> systems = faultKnowledgeBaseTypes.stream().map(FaultKnowledgeBaseType::getSystemCode).distinct().collect(Collectors.toList());
        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        if (CollectionUtil.isNotEmpty(majors)) {
            //用户拥有的专业
            List<MajorDTO> allMajor = faultKnowledgeBaseTypeMapper.getAllMajor(majors);
            for (MajorDTO majorDTO:allMajor) {
                majorDTO.setKey(majorDTO.getId());
                majorDTO.setLabel(majorDTO.getMajorCode());
                majorDTO.setValue(majorDTO.getMajorName());
                List<SelectTableDTO> selectTableDTOS = new ArrayList<>();
                //用户拥有的专业的子系统
                List<SubSystemDTO> subSystemByUser = faultKnowledgeBaseTypeMapper.getSubSystemByCode(systems);
                if (CollectionUtil.isNotEmpty(subSystemByUser)) {
                    for (SubSystemDTO subSystemDTO : subSystemByUser) {
                        SelectTableDTO selectTableDTO = new SelectTableDTO();
                        selectTableDTO.setKey(subSystemDTO.getId());
                        selectTableDTO.setLabel(subSystemDTO.getSystemCode());
                        selectTableDTO.setValue(subSystemDTO.getSystemName());
                        selectTableDTO.setIsBaseType(false);
                        //获取子节点
                        List<FaultKnowledgeBaseType> baseTypeList = faultKnowledgeBaseTypes.stream().filter(f -> f.getSystemCode().equals(subSystemDTO.getSystemCode()) && f.getMajorCode().equals(majorDTO.getMajorCode())).collect(Collectors.toList());
                        List<SelectTableDTO> childrenTress = new ArrayList<>();
                        baseTypeList.forEach(f->{
                            SelectTableDTO selectTable = new SelectTableDTO();
                            selectTable.setId(f.getId());
                            selectTable.setKey(f.getId().toString());
                            selectTable.setLabel(f.getCode());
                            selectTable.setValue(f.getName());
                            selectTable.setPid(f.getPid());
                            selectTable.setIsBaseType(true);
                            childrenTress.add(selectTable);
                        });
                        List<SelectTableDTO> treeRes = getTreeRes(childrenTress, 0);
                        selectTableDTO.setChildren(treeRes);
                        selectTableDTOS.add(selectTableDTO);
                    }
                    majorDTO.setChildren(selectTableDTOS);
                } else {
                    //获取子节点
                    List<FaultKnowledgeBaseType> baseTypeList = faultKnowledgeBaseTypes.stream().filter(f -> f.getMajorCode().equals(majorDTO.getMajorCode())).collect(Collectors.toList());
                    List<SelectTableDTO> childrenTress = new ArrayList<>();
                    baseTypeList.forEach(f->{
                        SelectTableDTO selectTable = new SelectTableDTO();
                        selectTable.setId(f.getId());
                        selectTable.setKey(f.getId().toString());
                        selectTable.setLabel(f.getCode());
                        selectTable.setValue(f.getName());
                        selectTable.setPid(f.getPid());
                        childrenTress.add(selectTable);
                    });
                    List<SelectTableDTO> treeRes = getTreeRes(childrenTress, 0);
                    majorDTO.setChildren(treeRes);
                }
            }
            return allMajor;
        }
        GlobalThreadLocal.setDataFilter(b);
        return null;
    }

    List<SelectTableDTO> getTreeRes(List<SelectTableDTO> children, Integer pid){
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
        if (faultKnowledgeBaseType.getPid() == 0) {
            faultKnowledgeBaseType.setCodeCc("/" + faultKnowledgeBaseType.getCode() + "/");
        } else {
            FaultKnowledgeBaseType f = faultKnowledgeBaseTypeMapper.selectById(faultKnowledgeBaseType.getPid());
            faultKnowledgeBaseType.setCodeCc(f.getCodeCc() + faultKnowledgeBaseType.getCode() + "/");
        }
        faultKnowledgeBaseType.setDelFlag(0);
        faultKnowledgeBaseTypeMapper.insert(faultKnowledgeBaseType);
    }
}
