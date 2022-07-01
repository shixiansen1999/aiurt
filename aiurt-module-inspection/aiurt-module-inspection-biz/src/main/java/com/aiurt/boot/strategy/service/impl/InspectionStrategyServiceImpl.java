package com.aiurt.boot.strategy.service.impl;

import com.aiurt.boot.strategy.dto.InspectionStrategyDTO;
import com.aiurt.boot.strategy.entity.*;
import com.aiurt.boot.strategy.mapper.*;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.boot.strategy.mapper.InspectionStrategyMapper;
import com.aiurt.boot.strategy.service.IInspectionStrategyService;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aiurt.common.constant.InspectionContant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.List;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class InspectionStrategyServiceImpl extends ServiceImpl<InspectionStrategyMapper, InspectionStrategy> implements IInspectionStrategyService {
    @Autowired
    private InspectionStrStaRelMapper inspectionStrStaRelMapper;
    @Autowired
    private InspectionStrRelMapper inspectionStrRelMapper;
    @Autowired
    private InspectionStrDeviceRelMapper inspectionStrDeviceRelMapper;
    @Autowired
    private InspectionStrOrgRelMapper inspectionStrOrgRelMapper;
    @Override
    public IPage<InspectionStrategyDTO> pageList(Page<InspectionStrategyDTO> page, InspectionStrategyDTO inspectionStrategyDTO) {
        IPage<InspectionStrategyDTO> list = baseMapper.selectPageList(page,inspectionStrategyDTO);
        return list;
    }

    @Override
    public void add(InspectionStrategyDTO inspectionStrategyDTO) {
        InspectionStrategy inspectionStrategy =new InspectionStrategy();
        inspectionStrategy.setCode(inspectionStrategyDTO.getCode());
        inspectionStrategy.setName(inspectionStrategyDTO.getName());
        inspectionStrategy.setYear(inspectionStrategyDTO.getYear());
        inspectionStrategy.setDelFlag(0);
        inspectionStrategy.setType(inspectionStrategyDTO.getType());
        inspectionStrategy.setIsReceipt(inspectionStrategyDTO.getIsReceipt());
        inspectionStrategy.setIsConfirm(inspectionStrategyDTO.getIsConfirm());
        inspectionStrategy.setWorkType(inspectionStrategyDTO.getWorkType());
        inspectionStrategy.setStatus(inspectionStrategyDTO.getStatus());
        inspectionStrategy.setGenerateStatus(0);
        baseMapper.insert(inspectionStrategy);
        List<String> codes =inspectionStrategyDTO.getSiteCodes();
        for (String code:codes){
        InspectionStrStaRel inspectionStrStaRel=new InspectionStrStaRel();
        inspectionStrStaRel.setStationCode(code);
        inspectionStrStaRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
        inspectionStrStaRelMapper.insert(inspectionStrStaRel);
        }
        for (String w:inspectionStrategyDTO.getMechanismCodes()){
            InspectionStrOrgRel inspectionStrOrgRel =new InspectionStrOrgRel();
            inspectionStrOrgRel.setOrgCode(w);
            inspectionStrOrgRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
            inspectionStrOrgRelMapper.insert(inspectionStrOrgRel);
        }
       for(String f:inspectionStrategyDTO.getInspectionCodes()){
           InspectionStrRel inspectionStrRel =new InspectionStrRel();
           inspectionStrRel.setInspectionStaCode(f);
           inspectionStrRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
           inspectionStrRelMapper.insert(inspectionStrRel);
       }
       if(inspectionStrategyDTO.getDevices().size()>0){
         List<Device> devices=  inspectionStrategyDTO.getDevices();
           for (Device device:devices) {
               InspectionStrRel i = inspectionStrRelMapper.selectOne(Wrappers.<InspectionStrRel>lambdaQuery().eq(InspectionStrRel::getInspectionStaCode, device.getInspectionCode()));
               InspectionStrDeviceRel inspectionStrDeviceRel =new InspectionStrDeviceRel();
               inspectionStrDeviceRel.setDeviceCode(device.getCode());
               inspectionStrDeviceRel.setInspectionStrRelId(i.getId());
               inspectionStrDeviceRelMapper.insert(inspectionStrDeviceRel);
           }
       }
    }

    @Override
    public void updateId(InspectionStrategyDTO inspectionStrategyDTO) {
        baseMapper.deleteIDorCode(inspectionStrategyDTO.getId(),inspectionStrategyDTO.getCode());
        this.add(inspectionStrategyDTO);
    }

    @Override
    public void removeId(String id) {
        baseMapper.removeId(id);
    }

    @Override
    public InspectionStrategyDTO getId(String id) {
        InspectionStrategyDTO inspectionStrategyDTO=baseMapper.getId(id);
        return inspectionStrategyDTO;
    }


    @Resource
    private StrategyService strategyService;

    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    @Override
    public Result addAnnualPlan(String id) {
        // 校验
        InspectionStrategy ins = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(ins)) {
            return Result.error("非法操作");
        }
        if (ins.getYear() < DateUtil.year(new Date())) {
            return Result.error("只能生成当前往后年份的计划");
        }

        List<InspectionCode> arr = new ArrayList<>();
        String code = ins.getCode();

        // 根据检修类型查询调用不同的方法
        Integer type = ins.getType();
        arr.forEach(inspectionCode -> {
            //周检
            if (type.equals(InspectionContant.WEEK)) {
                strategyService.weekPlan(ins, inspectionCode);
            }

            //月检
            if (type.equals(InspectionContant.MONTH)) {
                strategyService.monthPlan(ins, inspectionCode);
            }

            //双月检
            if (type.equals(InspectionContant.DOUBLEMONTH)) {
                strategyService.doubleMonthPlan(ins, inspectionCode);
            }

            //季检
            if (type.equals(InspectionContant.QUARTER)) {
                strategyService.quarterPlan(ins, inspectionCode);
            }

            //半年检
            if (type.equals(InspectionContant.SEMIANNUAL)) {
                strategyService.semiAnnualPlan(ins, inspectionCode);
            }

            //年检
            if (type.equals(InspectionContant.ANNUAL)) {
                strategyService.annualPlan(ins, inspectionCode);
            }
        });

        // 更新是否生成年计划状态
        return Result.OK("年计划生成成功");
    }

    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    @Override
    public Result addAnnualNewPlan(String id) {
        InspectionStrategy inspectionStrategy = new InspectionStrategy();
        return null;
    }
}
