package com.aiurt.boot.strategy.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.*;
import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCode;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.strategy.dto.DeviceExcelDTO;
import com.aiurt.boot.strategy.dto.InspectionExcelDTO;
import com.aiurt.boot.strategy.dto.InspectionStrategyDTO;
import com.aiurt.boot.strategy.dto.InspectionStrategyExcelDTO;
import com.aiurt.boot.strategy.entity.*;
import com.aiurt.boot.strategy.mapper.*;
import com.aiurt.boot.strategy.service.IInspectionStrategyService;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.UpdateHelperUtils;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
    @Resource
    private InspectionManager manager;
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Override
    public IPage<InspectionStrategyDTO> pageList(Page<InspectionStrategyDTO> page, InspectionStrategyDTO inspectionStrategyDTO) {
        if (Objects.nonNull(inspectionStrategyDTO.getSiteCode())) {
            List<String> strings = baseMapper.selectBySite(inspectionStrategyDTO.getSiteCode());
            if (CollUtil.isNotEmpty(strings)) {
                inspectionStrategyDTO.setSiteCode(String.join("|", strings));
            }
        }

        IPage<InspectionStrategyDTO> list = baseMapper.selectPageList(page, inspectionStrategyDTO);

        if (ObjectUtil.isNotEmpty(list)) {
            List<InspectionStrategyDTO> records = list.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                records.forEach(re -> {
                    // 站点
                    String siteName = manager.translateStation(inspectionStrStaRelMapper.selectStationList(re.getCode()));
                    re.setSiteName(siteName);
                });
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(InspectionStrategyDTO inspectionStrategyDTO) {
        // 校验必填信息
        check(inspectionStrategyDTO);

        // 保存策略
        InspectionStrategy inspectionStrategy = new InspectionStrategy();
        inspectionStrategy.setCode(inspectionStrategyDTO.getCode());
        inspectionStrategy.setName(inspectionStrategyDTO.getName());
        inspectionStrategy.setYear(inspectionStrategyDTO.getYear());
        inspectionStrategy.setType(inspectionStrategyDTO.getType());
        inspectionStrategy.setTactics(inspectionStrategyDTO.getTactics());
        inspectionStrategy.setIsReceipt(inspectionStrategyDTO.getIsReceipt());
        inspectionStrategy.setIsConfirm(inspectionStrategyDTO.getIsConfirm());
        inspectionStrategy.setWorkType(inspectionStrategyDTO.getWorkType());
        inspectionStrategy.setStatus(inspectionStrategyDTO.getStatus());
        inspectionStrategy.setIsOutsource(inspectionStrategyDTO.getIsOutsource());
        inspectionStrategy.setGenerateStatus(InspectionConstant.NO_GENERATE);
        baseMapper.insert(inspectionStrategy);

        // 保存站点
        List<StationDTO> siteCodes = inspectionStrategyDTO.getSiteCodes();
        if (CollUtil.isNotEmpty(siteCodes)) {
            siteCodes.forEach(station -> {
                InspectionStrStaRel inspectionStrStaRel = new InspectionStrStaRel();
                inspectionStrStaRel.setStationCode(station.getStationCode());
                inspectionStrStaRel.setLineCode(station.getLineCode());
                inspectionStrStaRel.setPositionCode(station.getPositionCode());
                inspectionStrStaRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
                inspectionStrStaRelMapper.insert(inspectionStrStaRel);
            });
        }

        // 保存组织机构
        List<String> mechanismCodes = inspectionStrategyDTO.getMechanismCodes();
        for (String w : mechanismCodes) {
            InspectionStrOrgRel inspectionStrOrgRel = new InspectionStrOrgRel();
            inspectionStrOrgRel.setOrgCode(w);
            inspectionStrOrgRel.setInspectionStrCode(inspectionStrategyDTO.getCode());
            inspectionStrOrgRelMapper.insert(inspectionStrOrgRel);
        }

        // 检修标准
        if (ObjectUtil.isNotNull(inspectionStrategyDTO.getInspectionCodeDtoList())) {
            for (InspectionCodeDTO ins : inspectionStrategyDTO.getInspectionCodeDtoList()) {
                InspectionStrRel inspectionStrRel = new InspectionStrRel();
                inspectionStrRel.setInspectionStaCode(ins.getCode());
                inspectionStrRel.setInspectionStrCode(inspectionStrategyDTO.getCode());

                // 保存标准关联信息
                inspectionStrRelMapper.insert(inspectionStrRel);

                List<Device> devices = ins.getDevices();
                if (CollUtil.isNotEmpty(devices)) {
                    for (Device device : devices) {
                        InspectionStrDeviceRel inspectionStrDeviceRel = new InspectionStrDeviceRel();
                        inspectionStrDeviceRel.setDeviceCode(device.getCode());
                        inspectionStrDeviceRel.setInspectionStrRelId(inspectionStrRel.getId());

                        // 保存检修标准对应的设备信息
                        inspectionStrDeviceRelMapper.insert(inspectionStrDeviceRel);
                    }
                }
            }
        }
    }

    /**
     * 校验必填信息
     *
     * @param inspectionStrategyDTO
     */
    private void check(InspectionStrategyDTO inspectionStrategyDTO) {

        if (ObjectUtil.isEmpty(inspectionStrategyDTO)) {
            throw new AiurtBootException("必填参数为空");
        }

        if (!InspectionConstant.WEEK.equals(inspectionStrategyDTO.getType()) && inspectionStrategyDTO.getTactics() == null) {
            throw new AiurtBootException("检修策略没有配置对应的策略周数");
        }

        if (CollUtil.isEmpty(inspectionStrategyDTO.getSiteCodes())) {
            throw new AiurtBootException("站点信息为空");
        }

        if (inspectionStrategyDTO.getYear() == null) {
            throw new AiurtBootException("年份不能为空");
        }

        if (inspectionStrategyDTO.getType() == null) {
            throw new AiurtBootException("检修周期类型不能为空");
        }

        if (CollUtil.isEmpty(inspectionStrategyDTO.getMechanismCodes())) {
            throw new AiurtBootException("组织机构为空");
        }

        List<InspectionCodeDTO> inspectionCodeDto = inspectionStrategyDTO.getInspectionCodeDtoList();
        if (CollUtil.isNotEmpty(inspectionCodeDto)) {
            // 跟设备类型相关的是否选择了设备
            inspectionCodeDto.forEach(re -> {
                InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                        new LambdaQueryWrapper<InspectionCode>()
                                .eq(InspectionCode::getCode, re.getCode())
                                .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
                if (ObjectUtil.isEmpty(inspectionCode)) {
                    throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
                }
                if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice()) && CollUtil.isEmpty(re.getDevices())) {
                    throw new AiurtBootException(String.format("名字为%s需要指定设备", ObjectUtil.isNotEmpty(inspectionCode) ? inspectionCode.getTitle() : ""));
                }
            });
        }
    }

    @Override
    public void updateId(InspectionStrategyDTO inspectionStrategyDTO) {
        // 校验必填信息
        check(inspectionStrategyDTO);

        // 策略id
        String strategyId = inspectionStrategyDTO.getId();

        // 策略编号
        String strategyCode = inspectionStrategyDTO.getCode();

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
        strategy.setWorkType(inspectionStrategyDTO.getWorkType());
        inspectionStrategyMapper.updateById(strategy);

        // 清除对应的缓存
        redisTemplate.delete(String.format("sys:cache:dictTable::SimpleKey [%s,%s]", "inspection_strategy,name,code", strategy.getCode()));

        // 把原来的组织机构信息删除
        inspectionStrOrgRelMapper.delete(new LambdaQueryWrapper<InspectionStrOrgRel>()
                .eq(InspectionStrOrgRel::getInspectionStrCode, strategy.getCode())
                .eq(InspectionStrOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        // 更新检修策略的组织机构信息
        List<String> mechanismCodes = inspectionStrategyDTO.getMechanismCodes();
        Optional.ofNullable(mechanismCodes).orElseGet(Collections::emptyList).stream().forEach(l -> {
            InspectionStrOrgRel strOrgRel = new InspectionStrOrgRel();
            strOrgRel.setInspectionStrCode(strategyCode);
            strOrgRel.setOrgCode(l);
            inspectionStrOrgRelMapper.insert(strOrgRel);
        });

        // 把原来的站所信息删除
        inspectionStrStaRelMapper.delete(new LambdaQueryWrapper<InspectionStrStaRel>()
                .eq(InspectionStrStaRel::getInspectionStrCode, strategy.getCode())
                .eq(InspectionStrStaRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        // 更新检修策略站所关联表信息
        List<StationDTO> siteCodes = inspectionStrategyDTO.getSiteCodes();
        Optional.ofNullable(siteCodes).orElseGet(Collections::emptyList).stream().forEach(l -> {
            InspectionStrStaRel strStaRelRel = new InspectionStrStaRel();
            strStaRelRel.setInspectionStrCode(strategyCode);
            strStaRelRel.setStationCode(l.getStationCode());
            strStaRelRel.setLineCode(l.getLineCode());
            strStaRelRel.setPositionCode(l.getPositionCode());
            inspectionStrStaRelMapper.insert(strStaRelRel);
        });

        // 把原来的检修计划策略标准关联表信息删除
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStrCode, strategy.getCode())
                        .eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(inspectionStrRels)) {
            List<String> collect = inspectionStrRels.stream().map(InspectionStrRel::getId).collect(Collectors.toList());
            inspectionStrDeviceRelMapper.delete(new LambdaQueryWrapper<InspectionStrDeviceRel>().in(InspectionStrDeviceRel::getInspectionStrRelId, collect));
            inspectionStrRelMapper.deleteBatchIds(inspectionStrRels);
        }

        // 更新检修计划策略标准关联表信息
        List<InspectionCodeDTO> inspectionCode = inspectionStrategyDTO.getInspectionCodeDtoList();
        Optional.ofNullable(inspectionCode).orElseGet(Collections::emptyList).stream().forEach(l -> {
            InspectionStrRel strRel = new InspectionStrRel();
            strRel.setInspectionStrCode(strategyCode);
            strRel.setInspectionStaCode(l.getCode());
            inspectionStrRelMapper.insert(strRel);
            if (CollUtil.isNotEmpty(l.getDevices())) {
                l.getDevices().forEach(deviceCode -> {
                    InspectionStrDeviceRel inspectionStrDeviceRel = new InspectionStrDeviceRel();
                    inspectionStrDeviceRel.setDeviceCode(deviceCode.getCode());
                    inspectionStrDeviceRel.setInspectionStrRelId(strRel.getId());

                    // 保存检修标准对应的设备信息
                    inspectionStrDeviceRelMapper.insert(inspectionStrDeviceRel);
                });
            }
        });
    }

    @Override
    public void removeId(String id) {
        InspectionStrategy strategy = inspectionStrategyMapper.selectById(id);
        if (ObjectUtil.isEmpty(strategy)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 修改删除状态，已经删除
        strategy.setDelFlag(CommonConstant.DEL_FLAG_1);
        inspectionStrategyMapper.updateById(strategy);

        if (ObjectUtil.isNotEmpty(strategy.getCode())) {
            // 策略编号
            String strategyCode = strategy.getCode();

            // 删除策略站所关联表数据
            new UpdateWrapper<InspectionStrStaRel>().lambda()
                    .eq(InspectionStrStaRel::getInspectionStrCode, strategyCode)
                    .set(InspectionStrStaRel::getDelFlag, CommonConstant.DEL_FLAG_1);

            // 删除策略组织机构关联表数据
            new UpdateWrapper<InspectionStrOrgRel>().lambda()
                    .eq(InspectionStrOrgRel::getInspectionStrCode, strategyCode)
                    .set(InspectionStrOrgRel::getDelFlag, CommonConstant.DEL_FLAG_1);

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

    @Override
    public InspectionStrategyDTO getId(String id) {
        InspectionStrategyDTO ins = baseMapper.getId(id);

        if (ObjectUtil.isEmpty(ins)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 站点信息
        List<InspectionStrStaRel> repairPoolStationRels = inspectionStrStaRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrStaRel>()
                        .eq(InspectionStrStaRel::getInspectionStrCode, ins.getCode())
                        .eq(InspectionStrStaRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolStationRels)) {
            List<StationDTO> arr = new ArrayList<>();
            repairPoolStationRels.forEach(re -> {
                StationDTO st = new StationDTO();
                st.setStationCode(re.getStationCode());
                st.setPositionCode(re.getPositionCode());
                st.setLineCode(re.getLineCode());
                arr.add(st);
            });
            ins.setAddSiteCode(repairPoolStationRels.stream().map(InspectionStrStaRel::getStationCode).collect(Collectors.toList()));
            ins.setSiteCodes(arr);
        }

        // 组织机构
        if (ObjectUtil.isNotEmpty(ins.getMechanismCode())) {
            ins.setMechanismCodes(Arrays.asList(ins.getMechanismCode().split(",")));
        }

        // 检修标准
        if (ObjectUtil.isNotNull(ins.getCodes())) {
            List<String> codes = Arrays.asList(ins.getCodes().split(","));
            ins.setInspectionCodeDtoList(baseMapper.selectbyCodes(codes));
        }

        // 检修计划关联检修标准信息
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStrCode, ins.getCode())
                        .eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        // 检修标准信息
        if (CollUtil.isNotEmpty(inspectionStrRels)) {
            List<InspectionCodeDTO> temp = new ArrayList<>();
            inspectionStrRels.forEach(sl -> {
                InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                        new LambdaQueryWrapper<InspectionCode>()
                                .eq(InspectionCode::getCode, sl.getInspectionStaCode())
                                .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));

                if (ObjectUtil.isNotEmpty(inspectionCode)) {
                    // 判断是否指定了设备
                    List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                            new LambdaQueryWrapper<InspectionStrDeviceRel>()
                                    .eq(InspectionStrDeviceRel::getInspectionStrRelId, sl.getId()));
                    inspectionCode.setSpecifyDevice(CollUtil.isNotEmpty(inspectionStrDeviceRels) ? "是" : "否");

                    InspectionCodeDTO inspectionCodeDTO = new InspectionCodeDTO();
                    List<Device> devices = new ArrayList<>();
                    //查询对应设备
                    inspectionStrDeviceRels.stream().forEach(f -> {
                        Device device = baseMapper.viewDetail(f.getId());
                        //线路
                        String lineCode = device.getLineCode() == null ? "" : device.getLineCode();
                        //站点
                        String stationCode = device.getStationCode() == null ? "" : device.getStationCode();
                        //位置
                        String positionCode = device.getPositionCode() == null ? "" : device.getPositionCode();
                        String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
                        String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
                        String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
                        String positionCodeCcName = lineCodeName;
                        if (stationCodeName != null && !"".equals(stationCodeName)) {
                            positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + stationCodeName;
                        }
                        if (!"".equals(positionCodeName) && positionCodeName != null) {
                            positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
                        }
                        device.setPositionCodeCcName(positionCodeCcName);
                        device.setStatusDesc(baseMapper.statusDesc(device.getStatus()));
                        device.setTemporaryName(baseMapper.temporaryName(device.getTemporary()));
                        device.setMajorCodeName(baseMapper.translateMajor(device.getMajorCode()));
                        device.setSystemCodeName(baseMapper.systemCodeName(device.getSystemCode()));
                        device.setDeviceTypeCodeName(baseMapper.deviceTypeCodeName(device.getDeviceTypeCode()));

                        devices.add(device);
                    });
                    inspectionCodeDTO.setDevices(devices);
                    UpdateHelperUtils.copyNullProperties(inspectionCode, inspectionCodeDTO);

                    // 专业
                    inspectionCodeDTO.setMajorName(manager.translateMajor(Arrays.asList(inspectionCode.getMajorCode()), InspectionConstant.MAJOR));
                    // 子系统
                    inspectionCodeDTO.setSubsystemName(manager.translateMajor(Arrays.asList(inspectionCode.getSubsystemCode()), InspectionConstant.SUBSYSTEM));
                    // 设备类型
                    inspectionCodeDTO.setDeviceTypeName(manager.queryNameByCode(inspectionCode.getDeviceTypeCode()));
                    temp.add(inspectionCodeDTO);
                }
            });

            ins.setInspectionCodeDtoList(temp);
        }

        return ins;
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
                        .eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(inspectionStrRels)) {
            List<String> collect = inspectionStrRels.stream().map(InspectionStrRel::getInspectionStaCode).collect(Collectors.toList());
            inspectionCodes = inspectionCodeMapper.selectList(
                    new LambdaQueryWrapper<InspectionCode>()
                            .in(InspectionCode::getCode, collect)
                            .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
        }

        // 组织结构
        List<InspectionStrOrgRel> orgList = strategyService.getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationList = strategyService.getInspectionStrStaRels(ins.getCode());

        // 保存检修标准与检修项目
        List<RepairPoolCode> newStaIds = strategyService.saveInspection(inspectionCodes);

        // 根据检修类型查询调用不同的方法
        Integer type = ins.getType();

        // 周检
        if (type.equals(InspectionConstant.WEEK)) {
            strategyService.weekPlan(ins, newStaIds, orgList, stationList);
        }

        // 月检
        if (type.equals(InspectionConstant.MONTH)) {
            strategyService.monthPlan(ins, newStaIds, orgList, stationList);
        }

        // 双月检
        if (type.equals(InspectionConstant.DOUBLEMONTH)) {
            strategyService.doubleMonthPlan(ins, newStaIds, orgList, stationList);
        }

        // 季检
        if (type.equals(InspectionConstant.QUARTER)) {
            strategyService.quarterPlan(ins, newStaIds, orgList, stationList);
        }

        // 半年检
        if (type.equals(InspectionConstant.SEMIANNUAL)) {
            strategyService.semiAnnualPlan(ins, newStaIds, orgList, stationList);
        }

        // 年检
        if (type.equals(InspectionConstant.ANNUAL)) {
            strategyService.annualPlan(ins, newStaIds, orgList, stationList);
        }

        // 更新是否生成年计划状态
        ins.setGenerateStatus(InspectionConstant.GENERATED);
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
        // 生效了才能生成
        if (InspectionConstant.NO_IS_EFFECT.equals(ins.getStatus())) {
            throw new AiurtBootException("当前策略未生效");
        }

        if (ins.getYear() == null) {
            throw new AiurtBootException("检修策略年份为空无法生成计划");
        }
        if (ins.getYear() < DateUtil.year(new Date())) {
            throw new AiurtBootException("只能生成当前往后年份的计划");
        }

        // 设置周期策略
        if (!InspectionConstant.WEEK.equals(ins.getType()) && ins.getTactics() == null) {
            throw new AiurtBootException("请先设置周期策略");
        }

        // 检修标准
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(
                new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStrCode, ins.getCode())
                        .eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(inspectionStrRels)) {
            throw new AiurtBootException("请先配置检修标准");
        }

        inspectionStrRels.forEach(re -> {
            InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                    new LambdaQueryWrapper<InspectionCode>()
                            .eq(InspectionCode::getCode, re.getInspectionStaCode())
                            .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (ObjectUtil.isEmpty(inspectionCode)) {
                throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
            }
            if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice())) {
                List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                        new LambdaQueryWrapper<InspectionStrDeviceRel>()
                                .eq(InspectionStrDeviceRel::getInspectionStrRelId, re.getId()));
                if (CollUtil.isEmpty(inspectionStrDeviceRels)) {
                    throw new AiurtBootException(String.format("名字为%s的检修标准需要指定设备", ObjectUtil.isNotEmpty(inspectionCode) ? inspectionCode.getTitle() : ""));
                }
            }
        });
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
                .eq("del_flag", CommonConstant.DEL_FLAG_0)
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
        InspectionStrRel inspectionstrRel = inspectionStrRelMapper.selectOne(Wrappers.<InspectionStrRel>lambdaQuery()
                .eq(InspectionStrRel::getInspectionStaCode, code));
        List<Device> list = baseMapper.viewDetails(inspectionstrRel.getId());
        return list;
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

    /**
     * 根据检修策略code和检修标准id查询检修标准对应的设备
     *
     * @param page
     * @param inspectionStrCode 检修策略code
     * @param inspectionStaCode 检修标准code
     * @return
     */
    @Override
    public IPage<RepairDeviceDTO> queryDeviceByCodeAndId(Page<RepairDeviceDTO> page, String inspectionStrCode, String inspectionStaCode) {
        InspectionStrRel inspectionStrRel = inspectionStrRelMapper.selectOne(
                new LambdaQueryWrapper<InspectionStrRel>()
                        .eq(InspectionStrRel::getInspectionStrCode, inspectionStrCode)
                        .eq(InspectionStrRel::getInspectionStaCode, inspectionStaCode)
                        .eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (ObjectUtil.isNotEmpty(inspectionStrRel)) {
            // 查询对应的设备
            List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(
                    new LambdaQueryWrapper<InspectionStrDeviceRel>()
                            .eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRel.getId()));

            // 分页处理设备信息
            if (CollUtil.isNotEmpty(inspectionStrDeviceRels)) {
                List<String> deviceCodeList = inspectionStrDeviceRels.stream().map(InspectionStrDeviceRel::getDeviceCode).collect(Collectors.toList());
                List<RepairDeviceDTO> repairDeviceDto = manager.queryDeviceByCodesPage(deviceCodeList, page);
                page.setRecords(repairDeviceDto);
            }
        }
        return page;
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
            majorDTOList.forEach(a -> {
                List<SubsystemDTO> subsystemDTOList = baseMapper.translateSubsystem(a.getMajorCode(), systemCode);
                a.setSubsystemDTOList(subsystemDTOList);
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

    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, InspectionStrategyDTO inspectionStrategyDTO) {

        // 封装数据
        List<InspectionStrategyExcelDTO> pageList = this.getinspectionStrategyList(inspectionStrategyDTO);

        // 封装excel表格
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("检修策略列表", "检修策略列表"),
                InspectionStrategyExcelDTO.class, pageList);

        // 从response中获取输出流
        try (OutputStream os = response.getOutputStream();) {
            // 文件名
            String fileName = new String("检修策略列表.xls".getBytes(), "ISO8859-1");
            // 返回类型
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            // 设置响应头
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            workbook.write(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        String url = null;

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(3);
            params.setNeedSave(true);

//            List<DeviceAssemblyErrorModel> deviceAssemblyErrorModels = new ArrayList<>();
            List<Device> deviceList = new ArrayList<Device>();
            List<InspectionStrategyExcelDTO> list = null;
            try {
                list = ExcelImportUtil.importExcel(file.getInputStream(), InspectionStrategyExcelDTO.class, params);
                System.out.println(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, String> duplicateData = new HashMap<>();
        }
        return null;
    }

    /**
     * 获取excel表格数据
     *
     * @param inspectionStrategyDTO
     * @return
     */
    private List<InspectionStrategyExcelDTO> getinspectionStrategyList(InspectionStrategyDTO inspectionStrategyDTO) {
        if (Objects.nonNull(inspectionStrategyDTO.getSiteCode())) {
            List<String> strings = baseMapper.selectBySite(inspectionStrategyDTO.getSiteCode());
            if (CollUtil.isNotEmpty(strings)) {
                inspectionStrategyDTO.setSiteCode(String.join("|", strings));
            }
        }
        List<InspectionStrategyExcelDTO> result = baseMapper.selectListNoPage(inspectionStrategyDTO);
        if (CollUtil.isEmpty(result)) {
            return result;
        }

        // 检修标准
        for (InspectionStrategyExcelDTO inspectionStrategyExcelDTO : result) {
            if (ObjectUtil.isEmpty(inspectionStrategyDTO)) {
                continue;
            }

            List<InspectionExcelDTO> inspectionExcelDTOList = baseMapper.selectInspectionCode(inspectionStrategyExcelDTO.getCode());
            if (CollUtil.isEmpty(inspectionExcelDTOList)) {
                continue;
            }

            // 所选设备
            for (InspectionExcelDTO inspectionExcelDTO : inspectionExcelDTOList) {
                List<DeviceExcelDTO> deviceExcelDTOS = baseMapper.selectDevice(inspectionExcelDTO.getId());
                if (CollUtil.isNotEmpty(deviceExcelDTOS)) {
                    inspectionExcelDTO.setDeviceExcelDTOS(deviceExcelDTOS);
                }
            }

            inspectionStrategyExcelDTO.setInspectionExcelDTOList(inspectionExcelDTOList);
        }
        return result;
    }
}
