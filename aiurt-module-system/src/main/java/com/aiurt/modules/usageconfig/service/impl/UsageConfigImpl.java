package com.aiurt.modules.usageconfig.service.impl;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.usageconfig.dto.UsageConfigDTO;
import com.aiurt.modules.usageconfig.dto.UsageConfigParamDTO;
import com.aiurt.modules.usageconfig.dto.UsageStatDTO;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.aiurt.modules.usageconfig.mapper.UsageConfigMapper;
import com.aiurt.modules.usageconfig.service.UsageConfigService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
@Slf4j
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
    public IPage<UsageStatDTO> getBusinessDataStatistics(UsageConfigParamDTO usageConfigParamDTO) {
        Date startTime = usageConfigParamDTO.getStartTime();

        Date endTime = usageConfigParamDTO.getEndTime();
        if (Objects.isNull(startTime) || Objects.isNull(endTime)) {
            startTime = DateUtil.beginOfDay(new Date());
            endTime = DateUtil.endOfDay(new Date());
        }

        Page<UsageStatDTO> pageList = new Page<>(usageConfigParamDTO.getPageNo(),usageConfigParamDTO.getPageSize());

        if (Objects.isNull(usageConfigParamDTO.getSign())) {
            usageConfigParamDTO.setSign(0);
        }
        String code = usageConfigParamDTO.getSign()==0 ? "base" : "business";

        List<UsageStatDTO> dtoList = usageConfigMapper.selectByPage(pageList, code);

        Date finalStartTime = startTime;
        Date finalEndTime = endTime;
        dtoList.stream().forEach(usageStatDTO -> {
            String id = usageStatDTO.getId();

            // 查询下级
            List<UsageConfig> childrenList = usageConfigMapper.getChildrenList(id);
            //
            if (CollUtil.isNotEmpty(childrenList)) {
                AtomicReference<Long> num = new AtomicReference<>(0L);
                AtomicReference<Long> totalNum = new AtomicReference<>(0L);
                List<UsageStatDTO> children = new ArrayList<>();
                for (UsageConfig usageConfig : childrenList) {
                    Long newNumber = this.getNewNumber(usageConfig.getTableName(), finalStartTime, finalEndTime, usageConfig.getStaCondition());
                    Long total = this.getTotal(usageConfig.getTableName(), usageConfig.getStaCondition());
                    num.set(num.get() + newNumber);
                    totalNum.set(totalNum.get() + total);
                    UsageStatDTO dto = new UsageStatDTO();
                    dto.setTileName(usageConfig.getName());
                    dto.setTotal(total);
                    dto.setNewAddNum(newNumber);
                    dto.setId(usageConfig.getId());
                    dto.setPid(usageConfig.getPid());
                    dto.setTableName("");
                    dto.setStaCondition("");
                    children.add(dto);

                }
                usageStatDTO.setNewAddNum(num.get());
                usageStatDTO.setTotal(totalNum.get());
                usageStatDTO.setChildren(children);

            }else {
                Long newNumber = this.getNewNumber(usageStatDTO.getTableName(), finalStartTime, finalEndTime, usageStatDTO.getStaCondition());
                Long total = this.getTotal(usageStatDTO.getTableName(), usageStatDTO.getStaCondition());
                usageStatDTO.setNewAddNum(newNumber);
                usageStatDTO.setTotal(total);
            }
        });
        pageList.setRecords(dtoList);
        return pageList;
    }


    private Long getNewNumber(String tableName, Date startTime, Date endTime, String staCondition) {
        try {
            Long newNumber = usageConfigMapper.getNewNumber(tableName, startTime, endTime, staCondition);
            return newNumber;
        } catch (Exception e) {

        }
        return 0L;
    }

    private Long getTotal(String tableName, String staCondition) {
        try {
            Long total = usageConfigMapper.getTotal(tableName, staCondition);
            return total;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0L;
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
