package com.aiurt.modules.usageconfig.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.usageconfig.dto.UsageConfigDTO;
import com.aiurt.modules.usageconfig.dto.UsageConfigParamDTO;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.aiurt.modules.usageconfig.mapper.UsageConfigMapper;
import com.aiurt.modules.usageconfig.service.UsageConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
@Service
public class UsageConfigImpl extends ServiceImpl<UsageConfigMapper, UsageConfig> implements UsageConfigService {

    @Autowired
    private UsageConfigMapper usageConfigMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Override
    public Page<UsageConfigDTO> pageList(Page<UsageConfigDTO> pageList, UsageConfigDTO usageConfigDTO) {
        return usageConfigMapper.getList(pageList, usageConfigDTO);
    }

    @Override
    public UsageConfig getBusinessDataStatistics(UsageConfigParamDTO usageConfigParamDTO) {
        UsageConfig usageConfig = new UsageConfig();

        //基础数据
        if (usageConfigParamDTO.getSign() == 0) {
            LambdaQueryWrapper<UsageConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UsageConfig::getState, 1);
            lambdaQueryWrapper.eq(UsageConfig::getPid, 0);
            lambdaQueryWrapper.eq(UsageConfig::getHasChild, 1);
            lambdaQueryWrapper.like(UsageConfig::getName, "基础");

            usageConfig = usageConfigMapper.selectOne(lambdaQueryWrapper);
            List<UsageConfig> children = new ArrayList<>();
            this.getBasicData(usageConfig.getId(), usageConfigParamDTO.getStartTime(), usageConfigParamDTO.getEndTime(), children);
            usageConfig.setChildren(children);
        }
        //业务数据
        else {
            LambdaQueryWrapper<UsageConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UsageConfig::getState, 1);
            lambdaQueryWrapper.eq(UsageConfig::getPid, 0);
            lambdaQueryWrapper.eq(UsageConfig::getHasChild, 1);
            lambdaQueryWrapper.like(UsageConfig::getName, "业务");

            usageConfig = usageConfigMapper.selectOne(lambdaQueryWrapper);
            List<UsageConfig> children = new ArrayList<>();
            this.getBusinessData(usageConfig.getId(), usageConfigParamDTO.getStartTime(), usageConfigParamDTO.getEndTime(), children);
            usageConfig.setChildren(children);
        }

        return usageConfig;
    }



    /**
     * 递归统计所有的基础数据
     */
    private void getBasicData(String id, String startTime, String endTime, List<UsageConfig> children) {
        LambdaQueryWrapper<UsageConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UsageConfig::getPid, id);
        List<UsageConfig> usageConfigs = usageConfigMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtil.isNotEmpty(usageConfigs)) {
            for (UsageConfig e : usageConfigs) {
                if (StrUtil.isNotBlank(e.getTableName()) && e.getHasChild() == 1) {
                    Integer total = usageConfigMapper.getTotal(e.getTableName(), e.getStaCondition());
                    e.setTotal(total);
                    Integer newNumber = usageConfigMapper.getNewNumber(e.getTableName(), startTime, endTime, e.getStaCondition());
                    e.setNewlyAdded(newNumber);
                }
                children.add(e);
                List<UsageConfig> children1 = new ArrayList<>();
                this.getBasicData(e.getId(), startTime, endTime, children1);
                e.setChildren(children1);
            }
        }
    }

    /**
     * 统计每个层级的总数和新增数量
     * @param usageConfigs
     */
    private void setNumber(List<UsageConfig> usageConfigs){
        if (CollectionUtil.isNotEmpty(usageConfigs)){
            int count = 0;
            usageConfigs.stream().filter(e -> e.getHasChild() == 0).map(UsageConfig::getId).collect(Collectors.toList());


        }
    }

    /**
     * 递归统计所有的业务数据
     * @param id
     * @param startTime
     * @param endTime
     * @param children
     */
    private void getBusinessData(String id, String startTime, String endTime, List<UsageConfig> children){
        LambdaQueryWrapper<UsageConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UsageConfig::getPid, id);
        List<UsageConfig> usageConfigs = usageConfigMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtil.isNotEmpty(usageConfigs)) {
            for (UsageConfig e : usageConfigs) {
                children.add(e);
                List<UsageConfig> children1 = new ArrayList<>();
                this.getBasicData(e.getId(), startTime, endTime, children1);
                e.setChildren(children1);
            }
        }
    }


    @Override
    public List<UsageConfigDTO> tree(String name) {
        List<UsageConfigDTO> usageConfigDTOList =  usageConfigMapper.getAllList();
        List<UsageConfigDTO> treeList =  new ArrayList<>();
        if(CollUtil.isNotEmpty(usageConfigDTOList)){
            List<UsageConfigDTO> parentList = usageConfigDTOList.stream().filter(u -> "0".equals(u.getPid())).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(parentList))
            {
                for (UsageConfigDTO dto : parentList) {
                    UsageConfigDTO configDTO = buildTree(dto, usageConfigDTOList);
                    treeList.add(configDTO);
                }
            }
        }
        return treeList;
    }

    private UsageConfigDTO buildTree(UsageConfigDTO parentDTO, List<UsageConfigDTO> list) {
        List<UsageConfigDTO> sonList = new ArrayList<>();
        for (UsageConfigDTO configDTO : list) {
            if(configDTO.getPid().equals(parentDTO.getId())){
                sonList.add(configDTO);
                buildTree(configDTO,list);
            }
        }
        parentDTO.setSonList(sonList);
        return parentDTO;
    }
}
