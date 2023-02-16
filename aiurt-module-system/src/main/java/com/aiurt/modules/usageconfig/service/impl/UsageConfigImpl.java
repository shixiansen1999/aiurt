package com.aiurt.modules.usageconfig.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.usageconfig.dto.UsageConfigDTO;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.aiurt.modules.usageconfig.mapper.UsageConfigMapper;
import com.aiurt.modules.usageconfig.service.UsageConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        return usageConfigMapper.getList(pageList,usageConfigDTO);
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
