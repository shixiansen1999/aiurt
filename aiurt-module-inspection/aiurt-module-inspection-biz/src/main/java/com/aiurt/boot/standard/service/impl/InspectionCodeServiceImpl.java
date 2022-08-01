package com.aiurt.boot.standard.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.standard.service.IInspectionCodeService;
import com.aiurt.boot.strategy.entity.InspectionStrDeviceRel;
import com.aiurt.boot.strategy.entity.InspectionStrRel;
import com.aiurt.boot.strategy.mapper.InspectionStrDeviceRelMapper;
import com.aiurt.boot.strategy.mapper.InspectionStrRelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class InspectionCodeServiceImpl extends ServiceImpl<InspectionCodeMapper, InspectionCode> implements IInspectionCodeService {
    @Resource
    private InspectionStrDeviceRelMapper inspectionStrDeviceRelMapper;
    @Resource
    private InspectionStrRelMapper inspectionStrRelMapper;
    @Override
    public IPage<InspectionCodeDTO> pageList(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO) {

        // todo 数据权限过滤
        List<InspectionCodeDTO> inspectionCodeDTOS = baseMapper.pageList(page,inspectionCodeDTO);
        inspectionCodeDTOS.forEach(i->{
            i.setNumber(baseMapper.number(i.getCode()));
        });
        if (ObjectUtils.isNotEmpty(inspectionCodeDTO.getInspectionStrCode())) {
            for (InspectionCodeDTO il : inspectionCodeDTOS) {
                InspectionStrRel inspectionStrRel = inspectionStrRelMapper.selectOne(new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStaCode, il.getCode())
                        .eq(InspectionStrRel::getInspectionStrCode,inspectionCodeDTO.getInspectionStrCode()));
                // 判断是否指定了设备
                List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                        new LambdaQueryWrapper<InspectionStrDeviceRel>()
                                .eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRel.getId()));
                il.setSpecifyDevice(CollUtil.isNotEmpty(inspectionStrDeviceRels) ? "是" : "否");
            }
        }
        return page.setRecords(inspectionCodeDTOS);
    }

    @Override
    public void updateDelFlag(String id) {
       InspectionCode inspectionCode =baseMapper.selectById(id);
       inspectionCode.setDelFlag(1);
       baseMapper.updateById(inspectionCode);
    }

    @Override
    public IPage<InspectionCodeDTO> pageLists(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO) {

        // todo 数据权限过滤
        List<InspectionCodeDTO> inspectionCodeDTOS = baseMapper.pageList(page,inspectionCodeDTO);
        inspectionCodeDTOS.forEach(i->{
            i.setNumber(baseMapper.number1(i.getCode()));
        });
        inspectionCodeDTOS.removeIf(i-> i.getNumber().equals(0));
        if (ObjectUtils.isNotEmpty(inspectionCodeDTO.getInspectionStrCode())) {
            for (InspectionCodeDTO il : inspectionCodeDTOS) {
                InspectionStrRel inspectionStrRel = inspectionStrRelMapper.selectOne(new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStaCode, il.getCode())
                        .eq(InspectionStrRel::getInspectionStrCode,inspectionCodeDTO.getInspectionStrCode()));
                // 判断是否指定了设备
                List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                        new LambdaQueryWrapper<InspectionStrDeviceRel>()
                                .eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRel.getId()));
                il.setSpecifyDevice(CollUtil.isNotEmpty(inspectionStrDeviceRels) ? "是" : "否");
            }
        }
        return page.setRecords(inspectionCodeDTOS);
    }
}
