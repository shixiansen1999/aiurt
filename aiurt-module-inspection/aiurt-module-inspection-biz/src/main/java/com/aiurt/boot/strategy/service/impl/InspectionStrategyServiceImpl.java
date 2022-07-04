package com.aiurt.boot.strategy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.strategy.dto.InspectionStrategyDTO;
import com.aiurt.boot.strategy.entity.*;
import com.aiurt.boot.strategy.mapper.*;
import com.aiurt.boot.strategy.service.IInspectionStrategyService;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class InspectionStrategyServiceImpl extends ServiceImpl<InspectionStrategyMapper, InspectionStrategy> implements IInspectionStrategyService {
    @Resource
    private InspectionStrStaRelMapper inspectionStrStaRelMapper;
    @Resource
    private InspectionStrRelMapper inspectionStrRelMapper;
    @Resource
    private InspectionStrDeviceRelMapper inspectionStrDeviceRelMapper;
    @Resource
    private InspectionStrOrgRelMapper inspectionStrOrgRelMapper;
    @Resource
    private InspectionCodeMapper inspectionCodeMapper;

    @Override
    public IPage<InspectionStrategyDTO> pageList(Page<InspectionStrategyDTO> page, InspectionStrategyDTO inspectionStrategyDTO) {
        IPage<InspectionStrategyDTO> list = baseMapper.selectPageList(page, inspectionStrategyDTO);
        return list;
    }

    @Override
    public void add(InspectionStrategyDTO inspectionStrategyDTO) {
        InspectionStrategy inspectionStrategy = new InspectionStrategy();
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
        List<String> codes = inspectionStrategyDTO.getSiteCodes();
        for (String code : codes) {
            InspectionStrStaRel inspectionStrStaRel = new InspectionStrStaRel();
            inspectionStrStaRel.setStationCode(code);
            inspectionStrStaRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
            inspectionStrStaRelMapper.insert(inspectionStrStaRel);
        }
        for (String w : inspectionStrategyDTO.getMechanismCodes()) {
            InspectionStrOrgRel inspectionStrOrgRel = new InspectionStrOrgRel();
            inspectionStrOrgRel.setOrgCode(w);
            inspectionStrOrgRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
            inspectionStrOrgRelMapper.insert(inspectionStrOrgRel);
        }
        for (String f : inspectionStrategyDTO.getInspectionCodes()) {
            InspectionStrRel inspectionStrRel = new InspectionStrRel();
            inspectionStrRel.setInspectionStaCode(f);
            inspectionStrRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
            inspectionStrRelMapper.insert(inspectionStrRel);
        }
        if (inspectionStrategyDTO.getDevices().size() > 0) {
            List<Device> devices = inspectionStrategyDTO.getDevices();
            for (Device device : devices) {
                InspectionStrRel i = inspectionStrRelMapper.selectOne(Wrappers.<InspectionStrRel>lambdaQuery().eq(InspectionStrRel::getInspectionStaCode, device.getInspectionCode()));
                InspectionStrDeviceRel inspectionStrDeviceRel = new InspectionStrDeviceRel();
                inspectionStrDeviceRel.setDeviceCode(device.getCode());
                inspectionStrDeviceRel.setInspectionStrRelId(i.getId());
                inspectionStrDeviceRelMapper.insert(inspectionStrDeviceRel);
            }
        }
    }

    @Override
    public void updateId(InspectionStrategyDTO inspectionStrategyDTO) {
        baseMapper.deleteIDorCode(inspectionStrategyDTO.getId(), inspectionStrategyDTO.getCode());
        this.add(inspectionStrategyDTO);
    }

    @Override
    public void removeId(String id) {
        baseMapper.removeId(id);
    }

    @Override
    public InspectionStrategyDTO getId(String id) {
        InspectionStrategyDTO inspectionStrategyDTO=baseMapper.getId(id);
        if(ObjectUtil.isNotEmpty(inspectionStrategyDTO.getSiteCode())){
            inspectionStrategyDTO.setSiteCodes(Arrays.asList(inspectionStrategyDTO.getSiteCode().split(",")));}
        if (ObjectUtil.isNotEmpty(inspectionStrategyDTO.getMechanismCode())){
            inspectionStrategyDTO.setMechanismCodes(Arrays.asList(inspectionStrategyDTO.getMechanismCode().split(",")));
        }
        List<String>codes= Arrays.asList(inspectionStrategyDTO.getCodes().split(","));
        inspectionStrategyDTO.setInspectionCodeDTOS(baseMapper.selectbyCodes(codes));
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

        // 检修标准
        List<InspectionCode> inspectionCodes = new ArrayList<>();
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStrCode, ins.getCode())
                        .eq(InspectionStrRel::getDelFlag, 0));
        if (CollUtil.isNotEmpty(inspectionStrRels)) {
            List<String> collect = inspectionStrRels.stream().map(InspectionStrRel::getInspectionStaCode).collect(Collectors.toList());
            inspectionCodes = inspectionCodeMapper.selectList(
                    new LambdaQueryWrapper<InspectionCode>()
                            .in(InspectionCode::getCode, collect)
                            .eq(InspectionCode::getDelFlag, 0));
        }


        // 组织结构
        List<InspectionStrOrgRel> orgList = strategyService.getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationList = strategyService.getInspectionStrStaRels(ins.getCode());

        // 根据检修类型查询调用不同的方法
        Integer type = ins.getType();
        inspectionCodes.forEach(inspectionCode -> {

            // 保存检修标准与检修项目
            String newStaId = strategyService.saveInspection(inspectionCode);

            // 查询检修策略对应的检修标准绑定的设备
            List<String> deviceList = strategyService.getDeviceList(ins.getCode(), inspectionCode.getCode());

            //周检
            if (type.equals(InspectionConstant.WEEK)) {
                strategyService.weekPlan(ins,newStaId,orgList,stationList,deviceList);
            }

            //月检
            if (type.equals(InspectionConstant.MONTH)) {
                strategyService.monthPlan(ins,newStaId,orgList,stationList,deviceList);
            }

            //双月检
            if (type.equals(InspectionConstant.DOUBLEMONTH)) {
                strategyService.doubleMonthPlan(ins,newStaId,orgList,stationList,deviceList);
            }

            //季检
            if (type.equals(InspectionConstant.QUARTER)) {
                strategyService.quarterPlan(ins,newStaId,orgList,stationList,deviceList);
            }

            //半年检
            if (type.equals(InspectionConstant.SEMIANNUAL)) {
                strategyService.semiAnnualPlan(ins,newStaId,orgList,stationList,deviceList);
            }

            //年检
            if (type.equals(InspectionConstant.ANNUAL)) {
                strategyService.annualPlan(ins,newStaId,orgList,stationList,deviceList);
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
        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
        // 当前策略生成的计划、当前结束时间往后的，并且没有被执行的的检修计划将会删除
//        wrapper.eq("inspection_code_id", inspectionCode.getId()).eq("del_flag", 0).ge("end_time", DateUtil.now());
//        List<RepairPool> list = this.baseMapper.selectList(wrapper);
        return null;
    }



    @Override
    public List<Device> viewDetails(String code) {
        InspectionStrRel inspectionstrRel= inspectionStrRelMapper.selectOne(Wrappers.<InspectionStrRel>lambdaQuery().eq(InspectionStrRel::getInspectionStaCode, code));
        List<Device> list= baseMapper.viewDetails(inspectionstrRel.getId());
        return null;
    }
}
