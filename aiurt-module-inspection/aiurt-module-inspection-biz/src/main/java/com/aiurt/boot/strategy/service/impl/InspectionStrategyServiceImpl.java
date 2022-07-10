package com.aiurt.boot.strategy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.dto.*;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.strategy.dto.InspectionStrategyDTO;
import com.aiurt.boot.strategy.entity.*;
import com.aiurt.boot.strategy.mapper.*;
import com.aiurt.boot.strategy.service.IInspectionStrategyService;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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
    @Resource
    private RepairPoolMapper repairPoolMapper;
    @Resource
    private StrategyService strategyService;
    @Resource
    private InspectionStrategyMapper inspectionStrategyMapper;
    @Autowired
    private RepairTaskMapper repairTaskMapper;


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
        List<String> mechanismCodes = inspectionStrategyDTO.getMechanismCodes();
        for (String w : mechanismCodes) {
            InspectionStrOrgRel inspectionStrOrgRel = new InspectionStrOrgRel();
            inspectionStrOrgRel.setOrgCode(w);
            inspectionStrOrgRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
            inspectionStrOrgRelMapper.insert(inspectionStrOrgRel);
        }
        if (ObjectUtil.isNotNull(inspectionStrategyDTO.getInspectionCodes())) {
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
    }

    @Override
    public void updateId(InspectionStrategyDTO inspectionStrategyDTO) {
//        baseMapper.deleteIDorCode(inspectionStrategyDTO.getId(), inspectionStrategyDTO.getCode());
//        this.add(inspectionStrategyDTO);
        // 策略id
        String strategyId = inspectionStrategyDTO.getId();
        // 策略编号
        String strategyCode = inspectionStrategyDTO.getCode();
        // 已删除状态
        final Integer isDelFlag = 1;

        if (ObjectUtil.isEmpty(strategyId)) {
            throw new AiurtBootException("检修策略的ID为空!");
        }
        if (ObjectUtil.isEmpty(strategyCode)) {
            throw new AiurtBootException("检修策略编号为空！");
        }
        // 更新检修策略基本信息
        InspectionStrategy strategy = inspectionStrategyMapper.selectById(strategyId);
        strategy.setYear(inspectionStrategyDTO.getYear());
        strategy.setName(inspectionStrategyDTO.getName());
        strategy.setIsOutsource(inspectionStrategyDTO.getIsOutsource());
        strategy.setType(inspectionStrategyDTO.getType());
        strategy.setTactics(inspectionStrategyDTO.getTactics());
        strategy.setIsConfirm(inspectionStrategyDTO.getIsConfirm());
        strategy.setIsReceipt(inspectionStrategyDTO.getIsReceipt());
        inspectionStrategyMapper.updateById(strategy);

        // 更新检修策略的组织机构信息
        // 把原来的组织机构信息删除，改为已删除状态
        LambdaUpdateWrapper<InspectionStrOrgRel> strOrgRelWrapper = new UpdateWrapper<InspectionStrOrgRel>().lambda()
                .eq(InspectionStrOrgRel::getInspectionStrCode, strategyCode)
                .set(InspectionStrOrgRel::getDelFlag, isDelFlag);
        inspectionStrOrgRelMapper.update(new InspectionStrOrgRel(), strOrgRelWrapper);

        List<String> mechanismCodes = inspectionStrategyDTO.getMechanismCodes();
        Optional.ofNullable(mechanismCodes).orElseGet(Collections::emptyList).stream().forEach(l -> {
            InspectionStrOrgRel strOrgRel = new InspectionStrOrgRel();
            strOrgRel.setInspectionStrCode(strategyCode);
            strOrgRel.setOrgCode(l);
            inspectionStrOrgRelMapper.insert(strOrgRel);
        });

        // 更新检修策略站所关联表信息
        // 把原来的站所信息删除，改为已删除状态
        LambdaUpdateWrapper<InspectionStrStaRel> strStaRelWrapper = new UpdateWrapper<InspectionStrStaRel>().lambda()
                .eq(InspectionStrStaRel::getInspectionStrCode, strategyCode)
                .set(InspectionStrStaRel::getDelFlag, isDelFlag);
        inspectionStrStaRelMapper.update(new InspectionStrStaRel(), strStaRelWrapper);

        List<String> siteCodes = inspectionStrategyDTO.getSiteCodes();
        Optional.ofNullable(siteCodes).orElseGet(Collections::emptyList).stream().forEach(l -> {
            InspectionStrStaRel strStaRelRel = new InspectionStrStaRel();
            strStaRelRel.setInspectionStrCode(strategyCode);
            strStaRelRel.setStationCode(l);
            inspectionStrStaRelMapper.insert(strStaRelRel);
        });

        // 更新检修计划策略标准关联表信息
        // 把原来的检修计划策略标准关联表信息删除，改为已删除状态
        LambdaUpdateWrapper<InspectionStrRel> strRelwrapper = new UpdateWrapper<InspectionStrRel>().lambda()
                .eq(InspectionStrRel::getInspectionStrCode, strategyCode)
                .set(InspectionStrRel::getDelFlag, isDelFlag);
        inspectionStrRelMapper.update(new InspectionStrRel(), strRelwrapper);

        List<InspectionCodeDTO> inspectionCode = inspectionStrategyDTO.getInspectionCodeDTOS();
        Optional.ofNullable(inspectionCode).orElseGet(Collections::emptyList).stream().forEach(l -> {
            InspectionStrRel strRel = new InspectionStrRel();
            strRel.setInspectionStrCode(strategyCode);
            strRel.setInspectionStaCode(l.getCode());
            inspectionStrRelMapper.insert(strRel);
        });

    }

    @Override
    public void removeId(String id) {
//        baseMapper.removeId(id);
        InspectionStrategy strategy = inspectionStrategyMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(strategy)) {
            inspectionStrategyMapper.deleteById(strategy);
            // 删除状态
            Integer delFlag = 1;

            if (ObjectUtil.isNotEmpty(strategy.getCode())) {
                // 策略编号
                String strategyCode = strategy.getCode();

                // 删除策略站所关联表数据
                new UpdateWrapper<InspectionStrStaRel>().lambda()
                        .eq(InspectionStrStaRel::getInspectionStrCode, strategyCode)
                        .set(InspectionStrStaRel::getDelFlag, delFlag);

                // 删除策略组织机构关联表数据
                new UpdateWrapper<InspectionStrOrgRel>().lambda()
                        .eq(InspectionStrOrgRel::getInspectionStrCode, strategyCode)
                        .set(InspectionStrOrgRel::getDelFlag, delFlag);

                // 查询检修计划策略标准关联表数据
                QueryWrapper<InspectionStrRel> strRelWrapper = new QueryWrapper<>();
                strRelWrapper.lambda().eq(InspectionStrRel::getInspectionStrCode, strategyCode);
                List<InspectionStrRel> strRelList = Optional.ofNullable(inspectionStrRelMapper.selectList(strRelWrapper)).orElseGet(Collections::emptyList);
                String[] strRelId = strRelList.stream().map(InspectionStrRel::getId).toArray(String[]::new);
                if (ObjectUtil.isNotEmpty(strRelId) && strRelId.length > 0) {
                    // 删除检修策略-关联设备表数据
                    QueryWrapper<InspectionStrDeviceRel> strDeviceRelQueryWrapper = new QueryWrapper<>();
                    strDeviceRelQueryWrapper.lambda().in(InspectionStrDeviceRel::getInspectionStrRelId, strRelId);
                    inspectionStrDeviceRelMapper.delete(strDeviceRelQueryWrapper);

                    // 删除检修计划策略标准关联表数据
                    inspectionStrRelMapper.deleteBatchIds(Arrays.asList(strRelId));
                }

            }
        }

    }

    @Override
    public InspectionStrategyDTO getId(String id) {
        InspectionStrategyDTO inspectionStrategyDTO = baseMapper.getId(id);
        if (ObjectUtil.isNotEmpty(inspectionStrategyDTO.getSiteCode())) {
            inspectionStrategyDTO.setSiteCodes(Arrays.asList(inspectionStrategyDTO.getSiteCode().split(",")));
        }
        if (ObjectUtil.isNotEmpty(inspectionStrategyDTO.getMechanismCode())) {
            inspectionStrategyDTO.setMechanismCodes(Arrays.asList(inspectionStrategyDTO.getMechanismCode().split(",")));
        }
        if (ObjectUtil.isNotNull(inspectionStrategyDTO.getCodes())) {
            List<String> codes = Arrays.asList(inspectionStrategyDTO.getCodes().split(","));
            inspectionStrategyDTO.setInspectionCodeDTOS(baseMapper.selectbyCodes(codes));
        }
        return inspectionStrategyDTO;
    }


    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    @Override
    public Result addAnnualPlan(String id) {
        // 校验
        InspectionStrategy ins = checkInspectionStrategy(id);

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

            // 周检
            if (type.equals(InspectionConstant.WEEK)) {
                strategyService.weekPlan(ins, newStaId, orgList, stationList, deviceList);
            }

            // 月检
            if (type.equals(InspectionConstant.MONTH)) {
                strategyService.monthPlan(ins, newStaId, orgList, stationList, deviceList);
            }

            // 双月检
            if (type.equals(InspectionConstant.DOUBLEMONTH)) {
                strategyService.doubleMonthPlan(ins, newStaId, orgList, stationList, deviceList);
            }

            // 季检
            if (type.equals(InspectionConstant.QUARTER)) {
                strategyService.quarterPlan(ins, newStaId, orgList, stationList, deviceList);
            }

            // 半年检
            if (type.equals(InspectionConstant.SEMIANNUAL)) {
                strategyService.semiAnnualPlan(ins, newStaId, orgList, stationList, deviceList);
            }

            // 年检
            if (type.equals(InspectionConstant.ANNUAL)) {
                strategyService.annualPlan(ins, newStaId, orgList, stationList, deviceList);
            }
        });

        // 更新是否生成年计划状态
        ins.setGenerateStatus(1);
        this.baseMapper.updateById(ins);

        return Result.OK("年计划生成成功");
    }

    /**
     * 校验检修策略数据合法性
     *
     * @param id
     * @return
     */
    @NotNull
    public InspectionStrategy checkInspectionStrategy(String id) {
        InspectionStrategy ins = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(ins)) {
            throw new AiurtBootException("非法操作");
        }

        if (ins.getYear() == null) {
            throw new AiurtBootException("检修策略年份为空无法生成计划");
        }
        if (ins.getYear() < DateUtil.year(new Date())) {
            throw new AiurtBootException("只能生成当前往后年份的计划");
        }

        // 生效了才能生成
        if (InspectionConstant.NO_IS_EFFECT.equals(ins.getStatus())) {
            throw new AiurtBootException("当前策略未生效");
        }
        return ins;
    }

    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    @Override
    public Result addAnnualNewPlan(String id) {
        InspectionStrategy ins = checkInspectionStrategy(id);
        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
        // 当前策略生成的计划、当前结束时间往后的，并且待指派的的检修计划将会删除
        wrapper.eq("inspection_str_code", ins.getCode())
                .eq("del_flag", 0)
                .eq("status", InspectionConstant.TO_BE_ASSIGNED)
                .ge("end_time", DateUtil.now());
        List<RepairPool> list = repairPoolMapper.selectList(wrapper);
        if (CollUtil.isNotEmpty(list)) {
            repairPoolMapper.deleteBatchIds(list.stream().map(RepairPool::getId).collect(Collectors.toList()));
        }
        this.addAnnualPlan(id);
        return Result.OK("重新生成年计划成功");
    }


    @Override
    public List<Device> viewDetails(String code) {
        InspectionStrRel inspectionstrRel = inspectionStrRelMapper.selectOne(Wrappers.<InspectionStrRel>lambdaQuery().eq(InspectionStrRel::getInspectionStaCode, code));
        List<Device> list = baseMapper.viewDetails(inspectionstrRel.getId());
        return null;
    }

    /**
     * 修改生效状态
     *
     * @param id@return
     */
    @Override
    public void modify(String id) {
        InspectionStrategy ins = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(ins)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        ins.setStatus(ins.getStatus() ^ 1);
        baseMapper.updateById(ins);
    }

    @Override
    public List<MajorDTO> selectMajorCodeList(String id) {
        List<InspectionStrategyDTO> inspectionStrategyDTOList = inspectionStrategyMapper.selectCodeList(id, null, null);
        List<InspectionStrategyDTO> collect = inspectionStrategyDTOList.stream().distinct().collect(Collectors.toList());
        List<String> majorCodes1 = new ArrayList<>();
        List<String> systemCode = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(collect)) {
            collect.forEach(e -> {
                String majorCode = e.getProfessionCode();
                String systemCode1 = e.getSubsystemCode();
                majorCodes1.add(majorCode);
                systemCode.add(systemCode1);
            });
        }
        //根据专业编码查询对应的专业子系统
        List<MajorDTO> majorDTOList = repairTaskMapper.translateMajor(majorCodes1);
        if (CollectionUtil.isNotEmpty(majorDTOList)) {
            majorDTOList.forEach(q -> {
                systemCode.forEach(o -> {
                    List<SubsystemDTO> subsystemDTOList = repairTaskMapper.translateSubsystem(q.getMajorCode(), o);
                    q.setSubsystemDTOList(subsystemDTOList);
                });
            });
        }
        return majorDTOList;
    }

    @Override
    public EquipmentOverhaulDTO selectEquipmentOverhaulList(String strategyId, String majorCode, String subsystemCode) {
        //根据检修策略id查询检修标准名称
        List<InspectionStrategyDTO> inspectionStrategyDTOList = inspectionStrategyMapper.selectCodeList(strategyId, majorCode, subsystemCode);
        List<InspectionStrategyDTO> collect = inspectionStrategyDTOList.stream().distinct().collect(Collectors.toList());
        List<OverhaulDTO> overhaulDTOList = new ArrayList<>();
        collect.forEach(e -> {
            OverhaulDTO overhaulDTO = new OverhaulDTO();
            overhaulDTO.setStandardId(e.getStandardId());
            overhaulDTO.setOverhaulStandardName(e.getStandardName());
            overhaulDTOList.add(overhaulDTO);
        });
        EquipmentOverhaulDTO equipmentOverhaulDTO = new EquipmentOverhaulDTO();
        equipmentOverhaulDTO.setOverhaulDTOList(overhaulDTOList);
        return equipmentOverhaulDTO;
    }
}
