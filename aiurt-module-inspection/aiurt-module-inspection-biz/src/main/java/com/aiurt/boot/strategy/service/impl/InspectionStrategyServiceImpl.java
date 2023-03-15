package com.aiurt.boot.strategy.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
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
import com.aiurt.boot.strategy.dto.*;
import com.aiurt.boot.strategy.entity.*;
import com.aiurt.boot.strategy.mapper.*;
import com.aiurt.boot.strategy.service.IInspectionStrategyService;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.UpdateHelperUtils;
import com.aiurt.modules.device.entity.Device;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    public IPage<InspectionStrategyDTO> pageList(Page<InspectionStrategyDTO> page, InspectionStrategyDTO inspectionStrategyDTO) {
        if (Objects.nonNull(inspectionStrategyDTO.getSiteCode())) {
            List<String> strings = baseMapper.selectBySite(inspectionStrategyDTO.getSiteCode());
            if (CollUtil.isNotEmpty(strings)) {
                inspectionStrategyDTO.setSiteCode(String.join("|", strings));
            }
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Set<String> userRoleSet = sysBaseApi.getUserRoleSet(sysUser.getUsername());
        List<CsUserMajorModel> list2 = new ArrayList<>();
        List<CsUserDepartModel> list1 = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userRoleSet)) {
            if (!userRoleSet.contains("admin")) {
                list1 = sysBaseApi.getDepartByUserId(sysUser.getId());
                list2 = sysBaseApi.getMajorByUserId(sysUser.getId());
            }
        }
        List<String> orgCodes = list1.stream().map(s -> s.getOrgCode()).collect(Collectors.toList());
        List<String> majorCodes = list2.stream().map(s -> s.getMajorCode()).collect(Collectors.toList());
        IPage<InspectionStrategyDTO> list = baseMapper.selectPageList(page, inspectionStrategyDTO, orgCodes, majorCodes, sysUser.getUsername());

        if (ObjectUtil.isNotEmpty(list)) {
            List<InspectionStrategyDTO> records = list.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                records.forEach(re -> {
                    // 站点
                    String siteName = manager.translateStation(inspectionStrStaRelMapper.selectStationList(re.getCode()));
                    re.setSiteName(siteName);
                    //专业
                    String s = inspectionStrStaRelMapper.selectMajorList(re.getCode());
                    re.setProfessionName(s);
                    //子系统
                    String s1 = inspectionStrStaRelMapper.selectSystemList(re.getCode());
                    re.setSubsystemName(s1);
                    //组织机构
                    String s2 = inspectionStrStaRelMapper.selectDepartList(re.getCode());
                    re.setMechanismName(s2);
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
                InspectionCode inspectionCode = inspectionCodeMapper.selectOne(new LambdaQueryWrapper<InspectionCode>().eq(InspectionCode::getCode, re.getCode()).eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
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
        inspectionStrOrgRelMapper.delete(new LambdaQueryWrapper<InspectionStrOrgRel>().eq(InspectionStrOrgRel::getInspectionStrCode, strategy.getCode()).eq(InspectionStrOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        // 更新检修策略的组织机构信息
        List<String> mechanismCodes = inspectionStrategyDTO.getMechanismCodes();
        Optional.ofNullable(mechanismCodes).orElseGet(Collections::emptyList).stream().forEach(l -> {
            InspectionStrOrgRel strOrgRel = new InspectionStrOrgRel();
            strOrgRel.setInspectionStrCode(strategyCode);
            strOrgRel.setOrgCode(l);
            inspectionStrOrgRelMapper.insert(strOrgRel);
        });

        // 把原来的站所信息删除
        inspectionStrStaRelMapper.delete(new LambdaQueryWrapper<InspectionStrStaRel>().eq(InspectionStrStaRel::getInspectionStrCode, strategy.getCode()).eq(InspectionStrStaRel::getDelFlag, CommonConstant.DEL_FLAG_0));

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
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(new LambdaQueryWrapper<InspectionStrRel>().eq(InspectionStrRel::getInspectionStrCode, strategy.getCode()).eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));

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
            new UpdateWrapper<InspectionStrStaRel>().lambda().eq(InspectionStrStaRel::getInspectionStrCode, strategyCode).set(InspectionStrStaRel::getDelFlag, CommonConstant.DEL_FLAG_1);

            // 删除策略组织机构关联表数据
            new UpdateWrapper<InspectionStrOrgRel>().lambda().eq(InspectionStrOrgRel::getInspectionStrCode, strategyCode).set(InspectionStrOrgRel::getDelFlag, CommonConstant.DEL_FLAG_1);

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
        List<InspectionStrStaRel> repairPoolStationRels = inspectionStrStaRelMapper.selectList(new LambdaQueryWrapper<InspectionStrStaRel>().eq(InspectionStrStaRel::getInspectionStrCode, ins.getCode()).eq(InspectionStrStaRel::getDelFlag, CommonConstant.DEL_FLAG_0));
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
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(new LambdaQueryWrapper<InspectionStrRel>().eq(InspectionStrRel::getInspectionStrCode, ins.getCode()).eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        // 检修标准信息
        if (CollUtil.isNotEmpty(inspectionStrRels)) {
            List<InspectionCodeDTO> temp = new ArrayList<>();
            inspectionStrRels.forEach(sl -> {
                InspectionCode inspectionCode = inspectionCodeMapper.selectOne(new LambdaQueryWrapper<InspectionCode>().eq(InspectionCode::getCode, sl.getInspectionStaCode()).eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));

                if (ObjectUtil.isNotEmpty(inspectionCode)) {
                    // 判断是否指定了设备
                    List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(new LambdaQueryWrapper<InspectionStrDeviceRel>().eq(InspectionStrDeviceRel::getInspectionStrRelId, sl.getId()));
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
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(new LambdaQueryWrapper<InspectionStrRel>().eq(InspectionStrRel::getInspectionStrCode, ins.getCode()).eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(inspectionStrRels)) {
            List<String> collect = inspectionStrRels.stream().map(InspectionStrRel::getInspectionStaCode).collect(Collectors.toList());
            inspectionCodes = inspectionCodeMapper.selectList(new LambdaQueryWrapper<InspectionCode>().in(InspectionCode::getCode, collect).eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
        }

        // 组织结构
        List<InspectionStrOrgRel> orgList = strategyService.getInspectionStrOrgRels(ins.getCode());

        // 站点
        List<InspectionStrStaRel> stationList = strategyService.getInspectionStrStaRels(ins.getCode());

        // 保存检修标准与检修项目
        List<RepairPoolCode> newStaIds = strategyService.saveInspection(inspectionCodes);

        // 根据检修类型查询调用不同的方法
        Integer type = ins.getType();

        // 根据类型生成计划
        strategyService.macth(type, ins, newStaIds, orgList, stationList);

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
        List<InspectionStrRel> inspectionStrRels = inspectionStrRelMapper.selectList(new LambdaQueryWrapper<InspectionStrRel>().eq(InspectionStrRel::getInspectionStrCode, ins.getCode()).eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(inspectionStrRels)) {
            throw new AiurtBootException("请先配置检修标准");
        }

        // 组织机构
        Long orgCount = inspectionStrOrgRelMapper.selectCount(new LambdaQueryWrapper<InspectionStrOrgRel>().eq(InspectionStrOrgRel::getInspectionStrCode, ins.getCode()).eq(InspectionStrOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (orgCount < 1) {
            throw new AiurtBootException("请先配置组织机构");
        }

        inspectionStrRels.forEach(re -> {
            InspectionCode inspectionCode = inspectionCodeMapper.selectOne(new LambdaQueryWrapper<InspectionCode>().eq(InspectionCode::getCode, re.getInspectionStaCode()).eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (ObjectUtil.isEmpty(inspectionCode)) {
                throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
            }
            if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice())) {
                List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(new LambdaQueryWrapper<InspectionStrDeviceRel>().eq(InspectionStrDeviceRel::getInspectionStrRelId, re.getId()));
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
        wrapper.eq("inspection_str_code", ins.getCode()).eq("del_flag", CommonConstant.DEL_FLAG_0).eq("status", InspectionConstant.TO_BE_ASSIGNED).ge("end_time", DateUtil.now());
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
        InspectionStrRel inspectionStrRel = inspectionStrRelMapper.selectOne(new LambdaQueryWrapper<InspectionStrRel>().eq(InspectionStrRel::getInspectionStrCode, inspectionStrCode).eq(InspectionStrRel::getInspectionStaCode, inspectionStaCode).eq(InspectionStrRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (ObjectUtil.isNotEmpty(inspectionStrRel)) {
            // 查询对应的设备
            List<InspectionStrDeviceRel> inspectionStrDeviceRels = inspectionStrDeviceRelMapper.selectList(new LambdaQueryWrapper<InspectionStrDeviceRel>().eq(InspectionStrDeviceRel::getInspectionStrRelId, inspectionStrRel.getId()));

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
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("检修策略列表", "检修策略列表"), InspectionStrategyExcelDTO.class, pageList);

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

        // 失败条数
        Integer errorLines = 0;
        // 标记是否有错误信息
        Boolean errorSign = false;
        // 失败导出的excel下载地址
        String failReportUrl = "";

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();

            // 判断是否xls、xlsx两种类型的文件，不是则直接返回
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, false, failReportUrl, "文件导入失败，文件类型不对");
            }

            // 设置excel参数
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(3);
            params.setNeedSave(true);

            // 需要保存的数据
            List<InspectionStrategyDTO> saveData = CollUtil.newArrayList();
            // excel表格数据
            List<InspectionStyImportExcelDTO> list = null;
            try {
                list = ExcelImportUtil.importExcel(file.getInputStream(), InspectionStyImportExcelDTO.class, params);

                // 空表格直接返回
                if (CollUtil.isEmpty(list)) {
                    return imporReturnRes(errorLines, false, failReportUrl, "暂无导入数据");
                }
                // 校验数据
                for (InspectionStyImportExcelDTO inspectionStyImportExcelDTO : list) {
                    // 记录校验得到的错误信息
                    StringBuilder errorMessage = new StringBuilder();
                    InspectionStrategyDTO inspectionStrategyDTO = new InspectionStrategyDTO();
                    // 校验检修策略
                    this.checkData(errorMessage, inspectionStyImportExcelDTO, inspectionStrategyDTO);
                    // 校验检修标准
                    errorSign = this.requiredInspectionCodeCheck(errorSign, inspectionStyImportExcelDTO, inspectionStrategyDTO);

                    if (errorMessage.length() > 0 || errorSign) {
                        if (errorMessage.length() > 0) {
                            errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                            inspectionStyImportExcelDTO.setInspectionStyErrorReason(errorMessage.toString());
                        }
                        errorLines++;
                    } else {
                        saveData.add(inspectionStrategyDTO);
                    }
                }

                // 存在错误，错误报告下载
                if (errorLines > 0) {
                    return getErrorExcel(errorLines, list, failReportUrl, type);
                }

                // 保存到系统
                if (CollUtil.isNotEmpty(saveData)) {
                    for (InspectionStrategyDTO saveDatum : saveData) {
                        this.add(saveDatum);
                    }
                    return imporReturnRes(errorLines, true, failReportUrl, "文件导入成功");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imporReturnRes(errorLines, false, failReportUrl, "暂无导入数据");
    }

    /**
     * 校验excel数据
     *
     * @param errorMessage                错误信息
     * @param inspectionStyImportExcelDTO excel数据
     * @param inspectionStrategyDTO       转换成要保存的实体数据
     */
    private void checkData(StringBuilder errorMessage, InspectionStyImportExcelDTO inspectionStyImportExcelDTO, InspectionStrategyDTO inspectionStrategyDTO) {
        // 空数据不处理
        if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO)) {
            return;
        }
        // 检修策略必填校验
        requiredCheck(errorMessage, inspectionStyImportExcelDTO, inspectionStrategyDTO);

        // 检验检修策略正确性
        checkValidity(errorMessage, inspectionStyImportExcelDTO, inspectionStrategyDTO);
    }

    /**
     * 检验检修策略正确性
     *
     * @param errorMessage
     * @param inspectionStyImportExcelDTO
     */
    private void checkValidity(StringBuilder errorMessage, InspectionStyImportExcelDTO inspectionStyImportExcelDTO, InspectionStrategyDTO inspectionStrategyDTO) {
        // 年份是否合法
        if (ObjectUtil.isNotEmpty(inspectionStyImportExcelDTO.getYear())) {
            String strPattern = "^(19[4-9]\\d|20\\d{2})";
            Pattern pattern = Pattern.compile(strPattern);
            Matcher isNum = pattern.matcher(inspectionStyImportExcelDTO.getYear() + "");
            if (!isNum.matches()) {
                errorMessage.append("年份格式不合法，");
            } else {
                inspectionStrategyDTO.setYear(inspectionStyImportExcelDTO.getYear());
            }
        }

        // 站点
        if (StrUtil.isNotEmpty(inspectionStyImportExcelDTO.getStationName())) {
            List<String> stations = StrUtil.split(inspectionStyImportExcelDTO.getStationName(), '|');
            if (CollUtil.isEmpty(stations)) {
                errorMessage.append("站点名称格式错误，");
                return;
            }
            Set<StationDTO> count = CollUtil.newHashSet();
            for (String station : stations) {
                List<String> stationList = StrUtil.split(station, '-');
                if (stationList.size() != 2) {
                    errorMessage.append("站点名称格式错误，");
                    break;
                }

                // 查询站点是否在系统中存在
                String lineName = stationList.get(0);
                String stationName = stationList.get(1);
                List<StationDTO> stationTemp = baseMapper.getStation(lineName, stationName);
                if (CollUtil.isEmpty(stationTemp)) {
                    errorMessage.append("有站点在系统中不存在，");
                    break;
                }
                count.addAll(stationTemp);
            }
            if (CollUtil.isNotEmpty(count)) {
                inspectionStrategyDTO.setSiteCodes(CollUtil.newArrayList(count));
            }

            // 组织机构
            if (StrUtil.isNotEmpty(inspectionStyImportExcelDTO.getOrgName())) {
                List<String> orgs = StrUtil.split(inspectionStyImportExcelDTO.getOrgName(), '|');
                if (CollUtil.isEmpty(orgs)) {
                    errorMessage.append("组织结构格式错误，");
                    return;
                }

                Set<String> orgCount = CollUtil.newHashSet();
                // 查询组织机构是否在系统中存在
                for (String org : orgs) {
                    List<String> orgTemp = baseMapper.getOrgs(org);
                    if (CollUtil.isEmpty(orgTemp)) {
                        errorMessage.append("有组织机构在系统中不存在，");
                        break;
                    }
                    orgCount.addAll(orgTemp);
                }

                if (CollUtil.isNotEmpty(orgCount)) {
                    inspectionStrategyDTO.setMechanismCodes(CollUtil.newArrayList(orgCount));
                }
            }
        }
    }

    /**
     * 校验检修标准
     *
     * @param inspectionStyImportExcelDTO
     * @param inspectionStrategyDTO
     */
    private Boolean requiredInspectionCodeCheck(Boolean errorSign, InspectionStyImportExcelDTO inspectionStyImportExcelDTO, InspectionStrategyDTO inspectionStrategyDTO) {
        if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO)
                || CollUtil.isEmpty(inspectionStyImportExcelDTO.getInspectionExcelDTOList())) {
            return false;
        }
        List<InspectionImportExcelDTO> inspectionExcelDTOList = inspectionStyImportExcelDTO.getInspectionExcelDTOList();
        // 封装检修标准到inspectionStrategyDTO检修策略实体
        List<InspectionCodeDTO> inspectionCodeDtoList = CollUtil.newArrayList();

        // 检修标准是否存在系统
        for (InspectionImportExcelDTO inspectionImportExcelDTO : inspectionExcelDTOList) {
            // 封装设备信息
            List<Device> deviceList = CollUtil.newArrayList();

            // 错误信息
            StringBuilder errorMessage = new StringBuilder();
            List<InspectionCode> inspectionCodes = inspectionCodeMapper.selectList(new LambdaQueryWrapper<InspectionCode>().eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0).eq(InspectionCode::getTitle, inspectionImportExcelDTO.getTitle()).eq(InspectionCode::getCode, inspectionImportExcelDTO.getCode()));
            if (CollUtil.isEmpty(inspectionCodes)) {
                errorMessage.append("该检修标准在系统中不存在或没有填写检修标准，");
            }

            // 周期类型必须跟检修策略保持一致
            InspectionCode inspectionCode = new InspectionCode();
            if (ObjectUtil.isNotEmpty(inspectionStyImportExcelDTO.getType()) && CollUtil.isNotEmpty(inspectionCodes)) {
                 inspectionCode = inspectionCodes.get(0);
                if (ObjectUtil.isNotEmpty(inspectionStrategyDTO.getType()) && !inspectionStrategyDTO.getType().equals(inspectionCode.getType())) {
                    errorMessage.append("该检修标准的检修周期与检修策略的检修周期不一致，");
                }

                // 如果该策略跟设备类型相关，必须选设备
                String deviceExcelDTOS = inspectionImportExcelDTO.getDeviceExcelDTOS();
                if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice()) && StrUtil.isEmpty(deviceExcelDTOS)) {
                    errorMessage.append("检修标准与设备相关，请填写设备，");
                }

                // 所选设备校验
                if (StrUtil.isNotEmpty(deviceExcelDTOS)) {
                    List<String> devices = StrUtil.split(deviceExcelDTOS, '|');
                    if (CollUtil.isNotEmpty(devices)) {

                        for (String device : devices) {
                            // 设备是否存在系统校验
                            Device deviceCodes = baseMapper.getIsExistDevice(device);
                            deviceList.add(deviceCodes);
                            if (ObjectUtil.isEmpty(deviceCodes)) {
                                String errormessage = String.format("编码为%s的设备在系统不存在，", device);
                                errorMessage.append(errormessage);
                                continue;
                            }

                            // 如果设备存在系统，校验设备=是否是检修标准的设备类型+是否是该检修策略的站点中的
                            if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice()) && ObjectUtil.isNotEmpty(inspectionCode.getType()) && CollUtil.isNotEmpty(inspectionStrategyDTO.getSiteCodes())) {
                                List<String> stationList = inspectionStrategyDTO.getSiteCodes().stream().map(StationDTO::getStationCode).collect(Collectors.toList());
                                if (!inspectionCode.getDeviceTypeCode().equals(deviceCodes.getDeviceTypeCode()) || !stationList.contains(deviceCodes.getStationCode())) {
                                    String errormessage = String.format("编码为%s的设备的类型不属于检修标准中的设备类型或设备的站点不属于检修策略的站点，", device);
                                    errorMessage.append(errormessage);
                                }
                            }

                        }
                    }
                }
            }

            if (errorMessage.length() > 0) {
                // 截取字符
                errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                inspectionImportExcelDTO.setErrorReason(errorMessage.toString());
                errorSign = true;
            } else {
                InspectionCodeDTO inspectionCodeDTO = new InspectionCodeDTO();
                inspectionCodeDTO.setCode(inspectionCode.getCode());
                inspectionCodeDTO.setDevices(deviceList);
                inspectionCodeDtoList.add(inspectionCodeDTO);
            }
        }
        inspectionStrategyDTO.setInspectionCodeDtoList(inspectionCodeDtoList);
        return errorSign;
    }

    /**
     * excel数据必填校验
     *
     * @param errorMessage                错误信息
     * @param inspectionStyImportExcelDTO excel数据
     */
    private void requiredCheck(StringBuilder errorMessage, InspectionStyImportExcelDTO inspectionStyImportExcelDTO, InspectionStrategyDTO inspectionStrategyDTO) {
        // 生成检修策略编码
        inspectionStrategyDTO.setCode("JX" + System.currentTimeMillis());
        if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO.getYear())) {
            errorMessage.append("年份必须填写，");
        }

        if (StrUtil.isEmpty(inspectionStyImportExcelDTO.getName())) {
            errorMessage.append("策略名称必须填写，");
        } else {
            inspectionStrategyDTO.setName(inspectionStyImportExcelDTO.getName());
        }

        if (StrUtil.isEmpty(inspectionStyImportExcelDTO.getStationName())) {
            errorMessage.append("站点名称必须填写，");
        }

        if (StrUtil.isEmpty(inspectionStyImportExcelDTO.getOrgName())) {
            errorMessage.append("组织机构必须填写，");
        }

        if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO.getType())) {
            errorMessage.append("检修周期类型必须填写，");
        } else {
            Map<String, String> inspectionCycleTypeMap = Optional.ofNullable(sysBaseApi.getDictItems(DictConstant.INSPECTION_CYCLE_TYPE)).orElse(CollUtil.newArrayList()).stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
            if (StrUtil.isEmpty(inspectionCycleTypeMap.get(inspectionStyImportExcelDTO.getType()))) {
                errorMessage.append("检修周期类型格式错误，");
            } else {
                inspectionStrategyDTO.setType(Integer.parseInt(inspectionCycleTypeMap.get(inspectionStyImportExcelDTO.getType())));
            }

            // 校验
            if (!InspectionConstant.WEEK.equals(inspectionStrategyDTO.getType())) {
                Integer tactics = inspectionStyImportExcelDTO.getTactics();
                if (ObjectUtil.isEmpty(tactics)) {
                    errorMessage.append("周期策略必须填写或格式错误，");
                } else {
                    switch (inspectionStyImportExcelDTO.getType()) {
                        case "半月检":
                            if (tactics < 1 || tactics > 2) {
                                errorMessage.append("检修周期类型半月检的周期策略范围是1~2");
                            } else {
                                inspectionStrategyDTO.setTactics(tactics);
                            }
                            break;
                        case "月检":
                            if (tactics < 1 || tactics > 4) {
                                errorMessage.append("检修周期类型月检的周期策略范围是1~4");
                            } else {
                                inspectionStrategyDTO.setTactics(tactics);
                            }
                            break;
                        case "双月检":
                            if (tactics < 1 || tactics > 8) {
                                errorMessage.append("检修周期类型双月检的周期策略范围是1~8");
                            } else {
                                inspectionStrategyDTO.setTactics(tactics);
                            }
                            break;
                        case "季检":
                            if (tactics < 1 || tactics > 12) {
                                errorMessage.append("检修周期类型季检的周期策略范围是1~12");
                            } else {
                                inspectionStrategyDTO.setTactics(tactics);
                            }
                            break;
                        case "半年检":
                            if (tactics < 1 || tactics > 24) {
                                errorMessage.append("检修周期类型半年检的周期策略范围是1~24");
                            } else {
                                inspectionStrategyDTO.setTactics(tactics);
                            }
                            break;
                        case "年检":
                            if (tactics < 1 || tactics > 48) {
                                errorMessage.append("检修周期类型年检的周期策略范围是1~48");
                            } else {
                                inspectionStrategyDTO.setTactics(tactics);
                            }
                            break;
                    }
                }
            }
        }

        // 转换是否值
        HashMap<String, Integer> checkMap = CollUtil.newHashMap();
        checkMap.put("是", 1);
        checkMap.put("否", 0);

        if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO.getIsConfirm())) {
            errorMessage.append("是否需要审核必须填写，");
        } else {
            Integer isConfirm = checkMap.get(inspectionStyImportExcelDTO.getIsConfirm());
            if (ObjectUtil.isNotEmpty(isConfirm)) {
                inspectionStrategyDTO.setIsConfirm(isConfirm);
            } else {
                errorMessage.append("是否需要审核格式错误，");
            }
        }

        if (ObjectUtil.isNotEmpty(inspectionStyImportExcelDTO.getIsConfirm()) && ObjectUtil.isEmpty(inspectionStyImportExcelDTO.getIsReceipt())) {
            errorMessage.append("是否需要验收必须填写，");
        } else {
            Integer isReceipt = checkMap.get(inspectionStyImportExcelDTO.getIsConfirm());
            if (ObjectUtil.isNotEmpty(isReceipt)) {
                inspectionStrategyDTO.setIsReceipt(isReceipt);
            } else {
                errorMessage.append("是否需要验收格式错误，");
            }
        }

        if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO.getIsOutsource())) {
            errorMessage.append("是否委外必须填写，");
        } else {
            Integer isOutsource = checkMap.get(inspectionStyImportExcelDTO.getIsConfirm());
            if (ObjectUtil.isNotEmpty(isOutsource)) {
                inspectionStrategyDTO.setIsOutsource(isOutsource);
            } else {
                errorMessage.append("是否委外格式错误，");
            }
        }

        if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO.getWorkType())) {
            errorMessage.append("作业类型必须填写，");
        } else {
            Map<String, String> workTypeMap = Optional.ofNullable(sysBaseApi.getDictItems(DictConstant.WORK_TYPE)).orElse(CollUtil.newArrayList()).stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
            if (ObjectUtil.isNotEmpty(workTypeMap.get(inspectionStyImportExcelDTO.getWorkType()))) {
                inspectionStrategyDTO.setWorkType(Integer.parseInt(workTypeMap.get(inspectionStyImportExcelDTO.getWorkType())));
            } else {
                errorMessage.append("作业类型格式错误，");
            }
        }
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

    /**
     * 检修策略导入统一返回格式
     *
     * @param errorLines    错误条数
     * @param isSucceed     是否成功
     * @param failReportUrl 错误报告下载地址
     * @param message       提示信息
     * @return
     */
    public static Result<?> imporReturnRes(int errorLines, boolean isSucceed, String failReportUrl, String message) {
        JSONObject result = new JSONObject(5);
        result.put("isSucceed", isSucceed);
        result.put("errorCount", errorLines);
        result.put("failReportUrl", failReportUrl);
        Result res = Result.ok(result);
        res.setMessage(message);
        res.setCode(200);
        return res;
    }

    private Result<?> getErrorExcel(int errorLines, List<InspectionStyImportExcelDTO> list, String url, String type) throws IOException {
        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/InspectionStyError.xls");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp = new File("/templates/InspectionStyError.xls");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);

            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);

            // 封装数据
            Map<String, Object> errorMap = handleData(list);

            // 将数据填入表格
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);

            // 合并数据
            // size从第6行开始合并，对应模板
            int size = 5;
            for (InspectionStyImportExcelDTO deviceModel : list) {
                for (int i = 0; i <= 9; i++) {
                    //合并单元格
                    PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + deviceModel.getInspectionExcelDTOList().size() - 1, i, i);
                }
                PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + deviceModel.getInspectionExcelDTOList().size() - 1, 14, 14);
                size = size + deviceModel.getInspectionExcelDTOList().size();
            }

            String fileName = "检修策略数据导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imporReturnRes(errorLines, false, url, "文件导入失败，数据有错误");
    }

    @NotNull
    private Map<String, Object> handleData(List<InspectionStyImportExcelDTO> list) {
        Map<String, Object> errorMap = CollUtil.newHashMap();
        List<Map<String, Object>> listMap = CollUtil.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> lm = CollUtil.newHashMap();
            InspectionStyImportExcelDTO inspectionStyImportExcelDTO = list.get(i);
            if (ObjectUtil.isEmpty(inspectionStyImportExcelDTO)) {
                continue;
            }

            if (CollUtil.isNotEmpty(inspectionStyImportExcelDTO.getInspectionExcelDTOList())) {
                List<InspectionImportExcelDTO> inspectionExcelDTOList = inspectionStyImportExcelDTO.getInspectionExcelDTOList();
                for (InspectionImportExcelDTO inspectionImportExcelDTO : inspectionExcelDTOList) {
                    lm = CollUtil.newHashMap();
                    //错误报告获取信息
                    lm.put("year", inspectionStyImportExcelDTO.getYear());
                    lm.put("name", inspectionStyImportExcelDTO.getName());
                    lm.put("stationName", inspectionStyImportExcelDTO.getStationName());
                    lm.put("orgName", inspectionStyImportExcelDTO.getOrgName());
                    lm.put("type", inspectionStyImportExcelDTO.getType());
                    lm.put("tactics", inspectionStyImportExcelDTO.getTactics());
                    lm.put("isConfirm", inspectionStyImportExcelDTO.getIsConfirm());
                    lm.put("isReceipt", inspectionStyImportExcelDTO.getIsReceipt());
                    lm.put("workType", inspectionStyImportExcelDTO.getWorkType());
                    lm.put("isOutsource", inspectionStyImportExcelDTO.getIsOutsource());
                    lm.put("InspectionStyErrorReason", inspectionStyImportExcelDTO.getInspectionStyErrorReason());
                    lm.put("code", inspectionImportExcelDTO.getCode());
                    lm.put("title", inspectionImportExcelDTO.getTitle());
                    lm.put("deviceExcelDTOS", inspectionImportExcelDTO.getDeviceExcelDTOS());
                    lm.put("errorReason", inspectionImportExcelDTO.getErrorReason());
                    listMap.add(lm);
                }
            } else {
                //错误报告获取信息
                lm.put("year", inspectionStyImportExcelDTO.getYear());
                lm.put("name", inspectionStyImportExcelDTO.getName());
                lm.put("stationName", inspectionStyImportExcelDTO.getStationName());
                lm.put("orgName", inspectionStyImportExcelDTO.getOrgName());
                lm.put("type", inspectionStyImportExcelDTO.getType());
                lm.put("tactics", inspectionStyImportExcelDTO.getTactics());
                lm.put("isConfirm", inspectionStyImportExcelDTO.getIsConfirm());
                lm.put("isReceipt", inspectionStyImportExcelDTO.getIsReceipt());
                lm.put("workType", inspectionStyImportExcelDTO.getWorkType());
                lm.put("isOutsource", inspectionStyImportExcelDTO.getIsOutsource());
                lm.put("InspectionStyErrorReason", inspectionStyImportExcelDTO.getInspectionStyErrorReason());
                listMap.add(lm);
            }

        }
        errorMap.put("maplist", listMap);
        return errorMap;
    }
}
