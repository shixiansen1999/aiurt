package com.aiurt.modules.faultknowledgebase.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.google.common.collect.Maps;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.ElasticAPI;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.AsyncThreadPoolExecutorUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.faultanalysisreport.constants.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultcausesolution.dto.FaultCauseProportionNumDTO;
import com.aiurt.modules.faultcausesolution.dto.FaultCauseSolutionDTO;
import com.aiurt.modules.faultcausesolution.entity.FaultCauseSolution;
import com.aiurt.modules.faultcausesolution.service.IFaultCauseSolutionService;
import com.aiurt.modules.faultknowledgebase.dto.*;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.aiurt.modules.faultsparepart.entity.FaultSparePart;
import com.aiurt.modules.faultsparepart.service.IFaultSparePartService;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseMatchDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.aiurt.modules.knowledge.entity.CauseSolution;
import com.aiurt.modules.knowledge.entity.KnowledgeBase;
import com.aiurt.modules.knowledge.entity.SparePart;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.search.service.ISearchRecordsService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.action.bulk.BulkResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import search.entity.SearchRecords;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date: 2022-06-24
 * @Version: V1.0
 */
@Slf4j
@Service
public class FaultKnowledgeBaseServiceImpl extends ServiceImpl<FaultKnowledgeBaseMapper, FaultKnowledgeBase> implements IFaultKnowledgeBaseService, IFlowableBaseUpdateStatusService {

    @Autowired
    private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
    @Autowired
    private FaultAnalysisReportMapper faultAnalysisReportMapper;
    @Autowired
    private ISearchRecordsService searchRecordsService;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Autowired
    private FaultMapper faultMapper;

    @Autowired
    private FlowBaseApi flowBaseApi;

    @Autowired
    private IFaultCauseSolutionService faultCauseSolutionService;

    @Autowired
    private IFaultSparePartService faultSparePartService;

    @Autowired
    private IFaultLevelService faultLevelService;

    @Autowired
    private ElasticAPI elasticApi;



    @Override
    public IPage<FaultKnowledgeBaseBuildDTO> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        String id = faultKnowledgeBase.getId();
        //根据id条件查询时，jeecg前端会传一个id结尾带逗号的id，所以先去掉结尾id
        if (StringUtils.isNotBlank(id)) {
            String substring = id.substring(0, id.length() - 1);
            faultKnowledgeBase.setId(substring);
        }
        List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.readAll(page, faultKnowledgeBase, null, sysUser.getUsername());
        Page<FaultKnowledgeBaseBuildDTO> knowledgeBaseBuildPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        //解决不是审核人去除审核按钮
        if (CollUtil.isNotEmpty(faultKnowledgeBases)) {
            Set<String> deviceTypeCodeSet = faultKnowledgeBases.stream().map(FaultKnowledgeBase::getDeviceTypeCode).collect(Collectors.toSet());
            Map<String, DeviceType> deviceTypeMap = new HashMap<>();
            if (CollUtil.isNotEmpty(deviceTypeCodeSet)) {
                List<DeviceType> typeList = sysBaseApi.selectDeviceTypeByCodes(deviceTypeCodeSet);
                if (CollUtil.isNotEmpty(typeList)) {
                    deviceTypeMap = typeList.stream().collect(Collectors.toMap(DeviceType::getCode, Function.identity()));
                }
            }

            // 组别标识,构造数据前端所需字段
            int group = 1;
            for (FaultKnowledgeBase knowledgeBase : faultKnowledgeBases) {
                knowledgeBase.setHaveButton(false);
                if (StrUtil.isNotBlank(knowledgeBase.getProcessInstanceId()) && StrUtil.isNotBlank(knowledgeBase.getTaskId())) {
                    dealAuthButton(sysUser, knowledgeBase);
                }
                //当前登录人不是创建人，则为false
                if (knowledgeBase.getCreateBy().equals(sysUser.getUsername())) {
                    knowledgeBase.setIsCreateUser(true);
                } else {
                    knowledgeBase.setIsCreateUser(false);
                }
                String faultCodes = knowledgeBase.getFaultCodes();
                if (StrUtil.isNotBlank(faultCodes)) {
                    String[] split = faultCodes.split(",");
                    List<String> list = Arrays.asList(split);
                    knowledgeBase.setFaultCodeList(list);
                }

                DeviceType deviceType = deviceTypeMap.getOrDefault(knowledgeBase.getDeviceTypeCode(), new DeviceType());
                knowledgeBase.setDeviceTypeName(deviceType.getName());
            }
            // 获取故障原因和解决方案
            List<FaultKnowledgeBase> newFaultKnowledgeBases = this.setFaultCauseSolution(faultKnowledgeBases);
            // 列表查询前端单元格需要合并，后端配合构造数据
            List<FaultKnowledgeBaseBuildDTO> faultKnowledgeBaseBuilds = new ArrayList<>();
            FaultKnowledgeBaseBuildDTO faultKnowledgeBaseBuild = null;
            int virId = 1;
            for (FaultKnowledgeBase knowledgeBase : newFaultKnowledgeBases) {
                List<FaultCauseSolutionDTO> solutions = knowledgeBase.getFaultCauseSolutions();
                // 首条标识
                boolean first = true;
                if (CollUtil.isEmpty(solutions)) {
                    faultKnowledgeBaseBuild = new FaultKnowledgeBaseBuildDTO();
                    BeanUtils.copyProperties(knowledgeBase, faultKnowledgeBaseBuild);
                    faultKnowledgeBaseBuild.setVirId(String.valueOf(virId));
                    faultKnowledgeBaseBuild.setFirst(first);
                    faultKnowledgeBaseBuild.setSize(1);
                    faultKnowledgeBaseBuild.setGroup(group);
                    faultKnowledgeBaseBuilds.add(faultKnowledgeBaseBuild);
                    virId++;
                    group++;
                    continue;
                }
                for (FaultCauseSolutionDTO solution : solutions) {
                    faultKnowledgeBaseBuild = new FaultKnowledgeBaseBuildDTO();
                    BeanUtils.copyProperties(knowledgeBase, faultKnowledgeBaseBuild);
                    // 故障原因及解决方案记录ID
                    faultKnowledgeBaseBuild.setCauseId(solution.getId());
                    // 故障原因
                    faultKnowledgeBaseBuild.setFaultCause(solution.getFaultCause());
                    // 解决方案
                    faultKnowledgeBaseBuild.setSolution(solution.getSolution());
                    // 维修视频url
                    faultKnowledgeBaseBuild.setVideoUrl(solution.getVideoUrl());
                    // 百分比
                    faultKnowledgeBaseBuild.setHappenRate(solution.getHappenRate());
                    faultKnowledgeBaseBuild.setFaultCauseSolutions(solutions);
                    faultKnowledgeBaseBuild.setVirId(String.valueOf(virId));
                    faultKnowledgeBaseBuild.setFirst(first);
                    faultKnowledgeBaseBuild.setSize(solutions.size());
                    faultKnowledgeBaseBuild.setGroup(group);
                    faultKnowledgeBaseBuilds.add(faultKnowledgeBaseBuild);
                    virId++;
                    first = false;
                }
                group++;
            }
            knowledgeBaseBuildPage.setRecords(faultKnowledgeBaseBuilds);
        }
        GlobalThreadLocal.setDataFilter(b);
        return knowledgeBaseBuildPage;
    }

    /**
     * 获取故障原因和解决方案
     *
     * @param faultKnowledgeBases
     * @return
     */
    private List<FaultKnowledgeBase> setFaultCauseSolution(List<FaultKnowledgeBase> faultKnowledgeBases) {
        List<String> baseIds = faultKnowledgeBases.stream().map(FaultKnowledgeBase::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(baseIds)) {
            return faultKnowledgeBases;
        }
        List<FaultCauseSolution> faultCauseSolutions = faultCauseSolutionService.lambdaQuery()
                .eq(FaultCauseSolution::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(FaultCauseSolution::getKnowledgeBaseId, baseIds)
                .list();
        if (CollUtil.isEmpty(faultCauseSolutions)) {
            return faultKnowledgeBases;
        }

        Map<String, List<FaultCauseSolution>> faultCauseSolutionMap = faultCauseSolutions.stream()
                .collect(Collectors.groupingBy(FaultCauseSolution::getKnowledgeBaseId));

        // 查询备件
        List<String> causeSolutionIds = faultCauseSolutions.stream()
                .map(FaultCauseSolution::getId)
                .distinct()
                .collect(Collectors.toList());
        List<FaultSparePart> spareParts = faultSparePartService.lambdaQuery()
                .eq(FaultSparePart::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(FaultSparePart::getCauseSolutionId, causeSolutionIds)
                .list();

        Map<String, String> sparePartCodeMap = null;
        if (CollUtil.isNotEmpty(spareParts)) {
            // 备件编码获取备件名
            List<String> sparePartCodes = spareParts.stream()
                    .map(FaultSparePart::getSparePartCode)
                    .distinct()
                    .collect(Collectors.toList());
            sparePartCodeMap = CollUtil.isEmpty(sparePartCodes) ? Collections.emptyMap() : sysBaseApi.getMaterialNameByCode(sparePartCodes);
            for (FaultSparePart sparePart : spareParts) {
                sparePart.setSparePartName(sparePartCodeMap.get(sparePart.getSparePartCode()));
            }
        }

        Map<String, Map<String, FaultCauseProportionNumDTO>> dataMap = this.buildCauseNumberMap(baseIds);
        for (FaultKnowledgeBase knowledgeBase : faultKnowledgeBases) {
            String id = knowledgeBase.getId();
            List<FaultCauseSolution> list = CollUtil.isEmpty(faultCauseSolutionMap.get(id)) ? new ArrayList<>() : faultCauseSolutionMap.get(id);
            List<FaultCauseSolutionDTO> faultCauseSolutionList = this.buildCauseSolutions(list, spareParts);
            // 原因出现率百分比
            Map<String, FaultCauseProportionNumDTO> happenRateMap = CollUtil.isEmpty(dataMap.get(id)) ? Collections.emptyMap() : dataMap.get(id);
            for (FaultCauseSolutionDTO faultCauseSolutionDTO : faultCauseSolutionList) {
                FaultCauseProportionNumDTO causeProportionNum = Optional.ofNullable(happenRateMap.get(faultCauseSolutionDTO.getId()))
                        .orElseGet(FaultCauseProportionNumDTO::new);
                String happenRate = causeProportionNum.getHappenRate();
                Integer causeNum = causeProportionNum.getCauseNum();
//                if (ObjectUtil.isEmpty(happenRate)) {
//                    happenRate = "0%";
//                }
                faultCauseSolutionDTO.setHappenRate(happenRate);
                faultCauseSolutionDTO.setCauseNum(causeNum);
            }
            knowledgeBase.setFaultCauseSolutions(faultCauseSolutionList);
        }
        return faultKnowledgeBases;
    }

    @Override
    public IPage<FaultKnowledgeBase> queryPageList(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        String id = faultKnowledgeBase.getId();
        //根据id条件查询时，jeecg前端会传一个id结尾带逗号的id，所以先去掉结尾id
        if (StringUtils.isNotBlank(id)) {
            String substring = id.substring(0, id.length() - 1);
            faultKnowledgeBase.setId(substring);
        }
        List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.readAll(page, faultKnowledgeBase, null, sysUser.getUsername());
        page.setRecords(faultKnowledgeBases);
        //解决不是审核人去除审核按钮
        if (CollUtil.isNotEmpty(faultKnowledgeBases)) {
            Set<String> deviceTypeCodeSet = faultKnowledgeBases.stream().map(FaultKnowledgeBase::getDeviceTypeCode).collect(Collectors.toSet());
            Map<String, DeviceType> deviceTypeMap = new HashMap<>();
            if (CollUtil.isNotEmpty(deviceTypeCodeSet)) {
                List<DeviceType> typeList = sysBaseApi.selectDeviceTypeByCodes(deviceTypeCodeSet);
                if (CollUtil.isNotEmpty(typeList)) {
                    deviceTypeMap = typeList.stream().collect(Collectors.toMap(DeviceType::getCode, Function.identity()));
                }
            }

            for (FaultKnowledgeBase knowledgeBase : faultKnowledgeBases) {
                String lineCode = faultKnowledgeBaseMapper.selectById(knowledgeBase.getId()).getLineCode();
                knowledgeBase.setLineCode(lineCode);
                knowledgeBase.setLineName(faultKnowledgeBaseMapper.translateLine(lineCode));
                knowledgeBase.setHaveButton(false);
                if (StrUtil.isNotBlank(knowledgeBase.getProcessInstanceId()) && StrUtil.isNotBlank(knowledgeBase.getTaskId())) {
                    dealAuthButton(sysUser, knowledgeBase);
                }
                //当前登录人不是创建人，则为false
                if (knowledgeBase.getCreateBy().equals(sysUser.getUsername())) {
                    knowledgeBase.setIsCreateUser(true);
                } else {
                    knowledgeBase.setIsCreateUser(false);
                }
                String faultCodes = knowledgeBase.getFaultCodes();
                if (StrUtil.isNotBlank(faultCodes)) {
                    String[] split = faultCodes.split(",");
                    List<String> list = Arrays.asList(split);
                    knowledgeBase.setFaultCodeList(list);
                }

                DeviceType deviceType = deviceTypeMap.getOrDefault(knowledgeBase.getDeviceTypeCode(), new DeviceType());
                knowledgeBase.setDeviceTypeName(deviceType.getName());
            }
            // 获取故障原因和解决方案
            List<FaultKnowledgeBase> newFaultKnowledgeBases = this.setFaultCauseSolution(faultKnowledgeBases);
            page.setRecords(faultKnowledgeBases);
        }
        List<FaultKnowledgeBase> collect=new ArrayList<>();
        collect.addAll(faultKnowledgeBases);
        //            筛选站点（暂时）
        String faultLineCode = faultKnowledgeBase.getLineCode();
        if(faultKnowledgeBase.getLineCode()!=null){
            collect = faultKnowledgeBases.stream()
                    .filter(l -> {
                        String lineCode = l.getLineCode();
                        return lineCode != null && faultLineCode != null && lineCode.equals(faultLineCode);
                    })
                    .collect(Collectors.toList());
        }
//            collect = faultKnowledgeBases.stream()
//                    .filter(l -> l.getLineCode().equals(faultKnowledgeBase.getLineCode())||l.getLineCode()!=null).collect(Collectors.toList());
        GlobalThreadLocal.setDataFilter(b);
        return page.setRecords(collect);
    }

    private void dealAuthButton(LoginUser sysUser, FaultKnowledgeBase knowledgeBase) {
        TaskInfoDTO taskInfoDTO = flowBaseApi.viewRuntimeTaskInfoWithCache(knowledgeBase.getProcessInstanceId(), knowledgeBase.getTaskId(), sysUser.getUsername());
        List<ActOperationEntity> operationList = taskInfoDTO.getOperationList();
        //operationList为空，没有审核按钮
        if (CollUtil.isNotEmpty(operationList)) {
            knowledgeBase.setHaveButton(true);
        } else {
            knowledgeBase.setHaveButton(false);
        }

    }

    @Override
    public List<FaultKnowledgeBase> queryAll(FaultKnowledgeBase faultKnowledgeBase) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        String id = faultKnowledgeBase.getId();
        //根据id条件查询时，jeecg前端会传一个id结尾带逗号的id，所以先去掉结尾id
        if (StringUtils.isNotBlank(id)) {
            String substring = id.substring(0, id.length() - 1);
            faultKnowledgeBase.setId(substring);
        }

        List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.queryAll(faultKnowledgeBase, null, sysUser.getUsername());

        GlobalThreadLocal.setDataFilter(b);
        faultKnowledgeBases.forEach(f -> {
            String faultCodes = f.getFaultCodes();
            if (StrUtil.isNotBlank(faultCodes)) {
                String[] split = faultCodes.split(",");
                List<String> list = Arrays.asList(split);
                f.setFaultCodeList(list);
            }
        });
        return faultKnowledgeBases;
    }

    @Override
    public IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO) {
        List<FaultDTO> faults = faultMapper.getFault(page, faultDTO, null);
        if (CollUtil.isNotEmpty(faults)) {
            for (FaultDTO fault : faults) {
                LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(FaultKnowledgeBaseType::getCode, fault.getFaultPhenomenon());
                FaultKnowledgeBaseType faultKnowledgeBaseType = faultKnowledgeBaseTypeMapper.selectOne(queryWrapper);
                fault.setFaultPhenomenon(faultKnowledgeBaseType != null ? faultKnowledgeBaseType.getName() : null);
            }
        }
        return page.setRecords(faults);
    }

    @Override
    public Result<String> approval(String approvedRemark, Integer approvedResult, String id) {
        if (getRole()) {
            return Result.error("没有权限");
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultKnowledgeBase faultKnowledgeBase = new FaultKnowledgeBase();
        faultKnowledgeBase.setId(id);
        faultKnowledgeBase.setApprovedRemark(approvedRemark);
        faultKnowledgeBase.setApprovedResult(approvedResult);
        faultKnowledgeBase.setApprovedTime(new Date());
        faultKnowledgeBase.setApprovedUserName(sysUser.getUsername());
        if (approvedResult.equals(FaultConstant.NO_PASS)) {
            faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
        } else {
            faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
        }
        this.updateById(faultKnowledgeBase);
        return Result.OK("审批成功!");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(String id) {
        FaultKnowledgeBase byId = this.getById(id);
        if (ObjectUtil.isEmpty(byId)) {
            return Result.error("没找到对应实体");
        }
        //获取知识库被使用的次数
        int num = faultKnowledgeBaseMapper.getNum(id);
        if (num > 0) {
            return Result.error("该知识库已经被使用，不能删除");
        } else {
            byId.setDelFlag(1);
            this.updateById(byId);
            List<FaultCauseSolution> faultCauseSolutions = faultCauseSolutionService.lambdaQuery()
                    .eq(FaultCauseSolution::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(FaultCauseSolution::getKnowledgeBaseId, id)
                    .select(FaultCauseSolution::getId)
                    .list();
            if (CollUtil.isNotEmpty(faultCauseSolutions)) {
                List<String> causeSolutionIds = faultCauseSolutions.stream()
                        .map(FaultCauseSolution::getId)
                        .collect(Collectors.toList());
                QueryWrapper<FaultSparePart> wrapper = new QueryWrapper<>();
                wrapper.lambda().in(FaultSparePart::getCauseSolutionId, id);
                faultSparePartService.remove(wrapper);
                faultCauseSolutionService.removeBatchByIds(causeSolutionIds);
            }
//            QueryWrapper<FaultCauseSolution> wrapper = new QueryWrapper<>();
//            wrapper.lambda().eq(FaultCauseSolution::getKnowledgeBaseId, id);
            // 删除ES的数据
            elasticApi.removeKnowledgeBase(id);
        }
        return Result.OK("删除成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteBatch(List<String> ids) {
//        for (String id : ids) {
//            FaultKnowledgeBase byId = this.getById(id);
//            if (ObjectUtil.isEmpty(byId)) {
//                return Result.error("没找到对应实体");
//            }
//            //获取知识库被使用的次数
//            int num = faultKnowledgeBaseMapper.getNum(id);
//            if (num > 0) {
//                return Result.error("所选知识库中有已经被使用的知识库，不能删除");
//            } else {
//                byId.setDelFlag(1);
//                this.updateById(byId);
//            }
//        }
        List<FaultKnowledgeBase> knowledgeBases = this.lambdaQuery()
                .eq(FaultKnowledgeBase::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(FaultKnowledgeBase::getId, ids)
                .list();
        for (FaultKnowledgeBase knowledgeBase : knowledgeBases) {
            int num = faultKnowledgeBaseMapper.getNum(knowledgeBase.getId());
            if (num > 0) {
                return Result.error("所选知识库中有已经被使用的知识库，不能删除");
            }
        }
        this.removeBatchByIds(ids);

        List<FaultCauseSolution> faultCauseSolutions = faultCauseSolutionService.lambdaQuery()
                .eq(FaultCauseSolution::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(FaultCauseSolution::getKnowledgeBaseId, ids)
                .select(FaultCauseSolution::getId)
                .list();
        if (CollUtil.isNotEmpty(faultCauseSolutions)) {
            List<String> causeSolutionIds = faultCauseSolutions.stream().map(FaultCauseSolution::getId).collect(Collectors.toList());
            QueryWrapper<FaultSparePart> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(FaultSparePart::getCauseSolutionId, causeSolutionIds);
            // 删除备件信息
            faultSparePartService.remove(wrapper);
            // 删除解决方案信息
            faultCauseSolutionService.removeBatchByIds(causeSolutionIds);
        }
        // 删除ES的数据
        elasticApi.removeBatchKnowledgeBase(ids);
        return Result.OK("批量删除成功!");
    }

    @Override
    public void exportTemplateXls(HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/knowledgeBase.xlsx");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp = new File("/templates/knowledgeBase.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);

        CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);

        //线路下拉框
        List<DictModel> dictModels5 = bean.queryTableDictItemsByCode("cs_line", "line_name", "line_code");
        Map<String, DictModel> collect10 = dictModels5.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect11 = collect10.values().stream().collect(Collectors.toList());
        selectList(workbook, "线路", 0, 0, collect11);
        //专业下拉框
        List<DictModel> dictModels3 = bean.queryTableDictItemsByCode("cs_major", "major_name", "major_code");
        Map<String, DictModel> collect6 = dictModels3.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect7 = collect6.values().stream().collect(Collectors.toList());
        selectList(workbook, "专业", 1, 1, collect7);

        //子系统下拉框
        List<DictModel> dictModels4 = bean.queryTableDictItemsByCode("cs_subsystem", "system_name", "system_code");
        Map<String, DictModel> collect8 = dictModels4.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect9 = collect8.values().stream().collect(Collectors.toList());
        selectList(workbook, "子系统", 2, 2, collect9);

        //知识库类别下拉框
        List<DictModel> dictModels = bean.queryTableDictItemsByCode("fault_knowledge_base_type", "name", "code");
        Map<String, DictModel> collect = dictModels.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect1 = collect.values().stream().collect(Collectors.toList());
        selectList(workbook, "知识库类别", 3, 3, collect1);

        //设备类型下拉框
        List<DictModel> dictModels1 = bean.queryTableDictItemsByCode("device_Type", "name", "code");
        Map<String, DictModel> collect2 = dictModels1.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect3 = collect2.values().stream().collect(Collectors.toList());
        selectList(workbook, "设备类型", 4, 4, collect3);

        //设备组件下拉框
        List<DictModel> dictModels2 = bean.queryTableDictItemsByCode("device_assembly", "material_name", "material_code");
        Map<String, DictModel> collect4 = dictModels2.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect5 = collect4.values().stream().collect(Collectors.toList());
        selectList(workbook, "设备组件", 5, 5, collect5);

        String fileName = "故障知识库导入模板.xlsx";

        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "故障知识库导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        String tipMessage = null;
        String url = null;
        int errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                tipMessage = "导入失败，文件类型错误！";
                return imporReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);

            try {

                List<FaultKnowledgeBase> faultKnowledgeBaseList = new ArrayList<>();

                List<FaultKnowledgeBaseModel> list = ExcelImportUtil.importExcel(file.getInputStream(), FaultKnowledgeBaseModel.class, params);
                Iterator<FaultKnowledgeBaseModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    FaultKnowledgeBaseModel model = iterator.next();
                    boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
                    if (b) {
                        iterator.remove();
                    }
                }
                if (CollectionUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                //数据校验
                for (FaultKnowledgeBaseModel model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        FaultKnowledgeBase em = new FaultKnowledgeBase();
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        examine(model, em, stringBuilder, list);
                        if (stringBuilder.length() > 0) {
                            // 截取字符
                            model.setDeviceMistake(stringBuilder.toString());
                            errorLines++;
                        } else {
                            faultKnowledgeBaseList.add(em);
                        }
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                } else {
                    successLines = list.size();
                    for (FaultKnowledgeBase faultKnowledgeBase : faultKnowledgeBaseList) {
                        faultKnowledgeBase.setDelFlag(0);
                        faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
                        faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
                        //插入数据库
                        faultKnowledgeBaseMapper.insert(faultKnowledgeBase);
                    }
                    return imporReturnRes(errorLines, successLines, tipMessage, true, null);
                }

            } catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if (msg != null && msg.contains("Duplicate entry")) {
                    return Result.error("文件导入失败:有重复数据！");
                } else {
                    return Result.error("文件导入失败:" + e.getMessage());
                }
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return imporReturnRes(errorLines, successLines, tipMessage, true, null);
    }

    @Override
    public FaultKnowledgeBase readOne(String id) {
        FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseMapper.readOne(id);
        if (faultKnowledgeBase == null) {
            throw new AiurtBootException("未找到相关数据！");
        }
        String faultCodes = faultKnowledgeBase.getFaultCodes();
        if (StrUtil.isNotBlank(faultCodes)) {
            String[] split = faultCodes.split(",");
            List<String> list = Arrays.asList(split);
            faultKnowledgeBase.setFaultCodeList(list);
        }

        // 查询故障原因和解决方案
        List<FaultCauseSolution> faultCauseSolutions = faultCauseSolutionService.lambdaQuery()
                .eq(FaultCauseSolution::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(FaultCauseSolution::getKnowledgeBaseId, id)
                .list();
//        if (CollUtil.isEmpty(faultCauseSolutions)) {
//            return faultKnowledgeBase;
//        }
        if (CollUtil.isNotEmpty(faultCauseSolutions)) {

            // 查询备件
            List<String> causeSolutionIds = faultCauseSolutions.stream()
                    .map(FaultCauseSolution::getId)
                    .distinct()
                    .collect(Collectors.toList());
            List<FaultSparePart> spareParts = faultSparePartService.lambdaQuery()
                    .eq(FaultSparePart::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(FaultSparePart::getCauseSolutionId, causeSolutionIds)
                    .list();

            Map<String, String> sparePartCodeMap = null;
            if (CollUtil.isNotEmpty(spareParts)) {
                // 备件编码获取备件名
                List<String> sparePartCodes = spareParts.stream()
                        .map(FaultSparePart::getSparePartCode)
                        .distinct()
                        .collect(Collectors.toList());
                sparePartCodeMap = CollUtil.isEmpty(sparePartCodes) ? Collections.emptyMap() : sysBaseApi.getMaterialNameByCode(sparePartCodes);
                for (FaultSparePart sparePart : spareParts) {
                    sparePart.setSparePartName(sparePartCodeMap.get(sparePart.getSparePartCode()));
                }
            }

            List<FaultCauseSolutionDTO> faultCauseSolutionList = this.buildCauseSolutions(faultCauseSolutions, spareParts);
            // 原因出现率百分比
            List<String> knowledgeBaseIds = faultCauseSolutionList.stream()
                    .map(FaultCauseSolutionDTO::getKnowledgeBaseId)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(knowledgeBaseIds)) {
                Map<String, Map<String, FaultCauseProportionNumDTO>> dataMap = this.buildCauseNumberMap(knowledgeBaseIds);
                Map<String, FaultCauseProportionNumDTO> happenRateMap = CollUtil.isEmpty(dataMap.get(id)) ? Collections.emptyMap() : dataMap.get(id);
                for (FaultCauseSolutionDTO faultCauseSolutionDTO : faultCauseSolutionList) {
                    FaultCauseProportionNumDTO causeProportionNum = Optional.ofNullable(happenRateMap.get(faultCauseSolutionDTO.getId()))
                            .orElseGet(FaultCauseProportionNumDTO::new);
                    String happenRate = causeProportionNum.getHappenRate();
                    Integer causeNum = causeProportionNum.getCauseNum();
//                    if (ObjectUtil.isEmpty(happenRate)) {
//                        happenRate = "0%";
//                    }
                    faultCauseSolutionDTO.setHappenRate(happenRate);
                    faultCauseSolutionDTO.setCauseNum(causeNum);
                }
            }
            faultKnowledgeBase.setFaultCauseSolutions(faultCauseSolutionList);
        }
        // 更新浏览次数并同步数据到搜索引擎
        AsyncThreadPoolExecutorUtil executor = AsyncThreadPoolExecutorUtil.getExecutor();
        executor.submitTask(() -> {
            this.updateScanNumber(faultKnowledgeBase);
            this.syncPartField(faultKnowledgeBase);
            return null;
        });
        return faultKnowledgeBase;
    }

    /**
     * 更新浏览次数并同步数据
     *
     * @param faultKnowledgeBase
     */
    private void updateScanNumber(FaultKnowledgeBase faultKnowledgeBase) {
        try {
            Integer scanNum = ObjectUtil.isEmpty(faultKnowledgeBase.getScanNum()) ? 1 : (faultKnowledgeBase.getScanNum() + 1);
            faultKnowledgeBase.setScanNum(scanNum);
            this.updateById(faultKnowledgeBase);
        } catch (Exception e) {
            log.error("故障知识库浏览次数更新失败！", e.getMessage());
        }
    }

    /**
     * 同步更新浏览次数字段到搜索引擎
     *
     * @param faultKnowledgeBase
     */
    private void syncPartField(FaultKnowledgeBase faultKnowledgeBase) {
        try {
            Map<String, Object> map = new HashMap<>(16);
            // 浏览次数
            map.put("scanNum", faultKnowledgeBase.getScanNum());
            // 更新时间
            map.put("updateTime", faultKnowledgeBase.getUpdateTime().getTime());
            elasticApi.update(faultKnowledgeBase.getId(), KnowledgeBase.class, map);
        } catch (IOException e) {
            log.error("故障知识库浏览次数字段同步更新异常！", e.getMessage());
        }
    }

    /**
     * 构造故障原因数量Map
     *
     * @param knowledgeBaseIds 知识库ID
     * @return Map<knowledgeBaseId, < causeSolutionId, FaultCauseProportionNumDTO>>
     */
//    private Map<String, Map<String, String>> buildCauseNumberMap(List<String> knowledgeBaseIds) {
    private Map<String, Map<String, FaultCauseProportionNumDTO>> buildCauseNumberMap(List<String> knowledgeBaseIds) {
        // 故障原因（针对录入标准化故障现象的故障工单，各[故障原因&解决方案]被采用的次数，占故障现象所有[故障原因&解决方案]被采用总次数的百分比）
        if (CollUtil.isEmpty(knowledgeBaseIds)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, FaultCauseProportionNumDTO>> dataMap = new HashMap<>(16);
        List<AnalyzeFaultCauseResDTO> analyzeFaultCauses = baseMapper.countFaultCauseByIdSet(knowledgeBaseIds);
        if (CollUtil.isNotEmpty(analyzeFaultCauses)) {
            Map<String, List<AnalyzeFaultCauseResDTO>> baseIdMap = analyzeFaultCauses.stream()
                    .collect(Collectors.groupingBy(AnalyzeFaultCauseResDTO::getKnowledgeBaseId));
            for (String knowledgeBaseId : knowledgeBaseIds) {
                List<AnalyzeFaultCauseResDTO> causes = baseIdMap.get(knowledgeBaseId);
                if (CollUtil.isNotEmpty(causes)) {
                    // 解决方案总数
                    Long sum = causes.stream().filter(Objects::nonNull)
                            .map(AnalyzeFaultCauseResDTO::getNum)
                            .reduce(0L, Long::sum);
                    Map<String, FaultCauseProportionNumDTO> percentageMap = new HashMap<>(16);
                    // 遍历每个解决方案，计算原因出现率百分比
                    causes.forEach(cause -> {
                        Long causeNum = cause.getNum();
                        double number = 1.0 * causeNum / sum * 100;
                        String percentage = String.format("%.2f", number) + "%";
//                        percentageMap.put(cause.getId(), percentage);
                        percentageMap.put(cause.getId(), new FaultCauseProportionNumDTO(percentage, causeNum.intValue()));
                    });
                    dataMap.put(knowledgeBaseId, percentageMap);
                } else {
                    dataMap.put(knowledgeBaseId, Collections.emptyMap());
                }
            }
        }
        return dataMap;
    }


    /**
     * @param faultCauseSolutions 故障原因和解决方案信息
     * @param spareParts          备件信息
     * @return
     */
    private List<FaultCauseSolutionDTO> buildCauseSolutions(List<FaultCauseSolution> faultCauseSolutions,
                                                            List<FaultSparePart> spareParts) {
        Map<String, List<FaultSparePart>> sparePartMap = spareParts.stream()
                .collect(Collectors.groupingBy(FaultSparePart::getCauseSolutionId));
        List<FaultCauseSolutionDTO> faultCauseSolutionList = new ArrayList<>();
        FaultCauseSolutionDTO faultCauseSolutionDTO = null;
        for (FaultCauseSolution faultCauseSolution : faultCauseSolutions) {
            faultCauseSolutionDTO = new FaultCauseSolutionDTO();
            BeanUtils.copyProperties(faultCauseSolution, faultCauseSolutionDTO);
            faultCauseSolutionDTO.setSpareParts(sparePartMap.get(faultCauseSolution.getId()));
            faultCauseSolutionList.add(faultCauseSolutionDTO);
        }
        return faultCauseSolutionList;
    }


    /**
     * 查找故障现象模板
     *
     * @param symptomReqDTO 请求参数
     * @return
     */
    @Override
    public Page<SymptomResDTO> querySymptomTemplate(SymptomReqDTO symptomReqDTO) {
        Page<SymptomResDTO> page = new Page<>(symptomReqDTO.getPageNo(), symptomReqDTO.getPageSize());
        List<SymptomResDTO> symptomResDTOS = baseMapper.querySymptomTemplate(page, symptomReqDTO);
        // app 需要每个故障原因的比例
        Set<String> idSet = symptomResDTOS.stream().map(SymptomResDTO::getId).collect(Collectors.toSet());

        // 查询故障记录使用的记录数
        Map<String, List<AnalyzeFaultCauseResDTO>> dataMap = new HashMap<>();
        if (CollUtil.isNotEmpty(idSet)) {
            List<AnalyzeFaultCauseResDTO> causeResDTOList = baseMapper.countFaultCauseByIdSeV2(new ArrayList<>(idSet));
            if (CollUtil.isNotEmpty(causeResDTOList)) {
                Map<String, List<AnalyzeFaultCauseResDTO>> map = causeResDTOList.stream().collect(Collectors.groupingBy(AnalyzeFaultCauseResDTO::getKnowledgeBaseId));
                map.forEach((knowledgeId, resList) -> {
                    Long sum = resList.stream().filter(Objects::nonNull).map(AnalyzeFaultCauseResDTO::getNum).reduce(0L, Long::sum);
                    resList.stream().forEach(re -> {
                        if (sum != 0L) {
                            re.setPercentage(NumberUtil.div((float) re.getNum(), (float) sum, 2) * 100 + "");
                        } else {
                            re.setPercentage("0");
                        }
                    });
                    dataMap.put(knowledgeId, resList);
                });
            }
        }
        // 拼接
        symptomResDTOS.stream().forEach(symptomResDTO -> {
            String materialName = symptomResDTO.getMaterialName();
            String baseTypeName = symptomResDTO.getBaseTypeName();
            if (StrUtil.isNotBlank(baseTypeName)) {
                symptomResDTO.setMaterialName(baseTypeName + "-" + materialName);
            }
            symptomResDTO.setAnalyzeFaultCauseResDTOList(dataMap.getOrDefault(symptomResDTO.getId(), Collections.emptyList()));
        });

        // 统计总和，以及所有比例
        page.setRecords(symptomResDTOS);
        return page;
    }

    /**
     * 维修建议
     *
     * @param knowledgeId 知识库id
     * @return
     */
    @Override
    public RepairSolRecDTO queryRepairSolRecDTO(String knowledgeId) {
        // 查询故障知识库

        // 查询故障原因及解决方案
        return null;
    }

    /**
     * 导入数据校验
     *
     * @param faultKnowledgeBaseModel
     * @param faultKnowledgeBase
     * @param stringBuilder
     * @param list
     */
    private void examine(FaultKnowledgeBaseModel faultKnowledgeBaseModel, FaultKnowledgeBase faultKnowledgeBase, StringBuilder stringBuilder, List<FaultKnowledgeBaseModel> list) {
        BeanUtils.copyProperties(faultKnowledgeBaseModel, faultKnowledgeBase);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        String majorCode = null;
        String systemCode = null;
        String lineCode = null;
        if(StrUtil.isBlank(faultKnowledgeBaseModel.getLineName())){
            stringBuilder.append("线路必填，");
        }else {
            JSONObject line = sysBaseApi.getLineByName(faultKnowledgeBaseModel.getLineName());
            if (ObjectUtil.isNotNull(line)){
                lineCode = line.getString("lineCode");
                faultKnowledgeBase.setLineCode(lineCode);
            }else {
                stringBuilder.append("系统中不存在该线路，");
            }
        }
        if(StrUtil.isBlank(faultKnowledgeBaseModel.getMajorName())){
            stringBuilder.append("专业必填，");
        } else {
            JSONObject csMajorByName = sysBaseApi.getCsMajorByName(faultKnowledgeBaseModel.getMajorName());
            if (ObjectUtil.isNotNull(csMajorByName)) {
                majorCode = csMajorByName.getString("majorCode");
                faultKnowledgeBase.setMajorCode(majorCode);
            } else {
                stringBuilder.append("系统中不存在该专业，");
            }
        }

        if (StrUtil.isBlank(faultKnowledgeBaseModel.getSystemName())) {
            stringBuilder.append("子系统必填，");
        } else {
            if (StrUtil.isNotBlank(majorCode)) {
                JSONObject systemName = sysBaseApi.getSystemName(majorCode, faultKnowledgeBaseModel.getSystemName());
                if (ObjectUtil.isNotNull(systemName)) {
                    systemCode = systemName.getString("systemCode");
                    faultKnowledgeBase.setSystemCode(systemCode);
                } else {
                    stringBuilder.append("系统中该专业下不存在该子系统，");
                }
            } else {
                stringBuilder.append("请正确填写专业后，再填写子系统，");
            }

        }

        if (StrUtil.isBlank(faultKnowledgeBaseModel.getKnowledgeBaseTypeName())) {
            stringBuilder.append("故障现象分类必填，");
        } else {
            if (StrUtil.isNotBlank(systemCode)) {
                LambdaQueryWrapper<FaultKnowledgeBaseType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(FaultKnowledgeBaseType::getName, faultKnowledgeBaseModel.getKnowledgeBaseTypeName())
                        .eq(FaultKnowledgeBaseType::getSystemCode, systemCode)
                        .eq(FaultKnowledgeBaseType::getDelFlag, 0);
                FaultKnowledgeBaseType faultKnowledgeBaseType = faultKnowledgeBaseTypeMapper.selectOne(lambdaQueryWrapper);
                if (ObjectUtil.isNotNull(faultKnowledgeBaseType)) {
                    faultKnowledgeBase.setKnowledgeBaseTypeCode(faultKnowledgeBaseType.getCode());
                } else {
                    stringBuilder.append("系统中该子系统下不存在该故障现象分类，");
                }
            } else {
                stringBuilder.append("请正确填写子系统后，在填写故障现象分类，");
            }
        }

        if (StrUtil.isBlank(faultKnowledgeBaseModel.getDeviceTypeName())) {
            stringBuilder.append("设备类型名称必填，");
        } else {
            String deviceTypeName = faultKnowledgeBaseModel.getDeviceTypeName();
            List<DeviceType> deviceCodeByName = faultKnowledgeBaseMapper.getDeviceCodeByName(deviceTypeName);
            if (CollUtil.isNotEmpty(deviceCodeByName)) {
                List<String> collect = deviceCodeByName.stream().map(DeviceType::getCode).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)) {
                    String deviceTypeCode = collect.get(0);
                    faultKnowledgeBase.setDeviceTypeCode(deviceTypeCode);
                }
            } else {
                stringBuilder.append("系统中不存在该设备类型，");
            }
        }

        if (StrUtil.isNotBlank(faultKnowledgeBaseModel.getMaterialName())) {
            String materialName = faultKnowledgeBaseModel.getMaterialName();
            List<DeviceAssemblyDTO> deviceAssemblyCode = faultKnowledgeBaseMapper.getDeviceAssemblyCode(materialName);

            deviceAssemblyCode.removeIf(Objects::isNull);
            if (CollUtil.isNotEmpty(deviceAssemblyCode)) {
                List<String> collect = deviceAssemblyCode.stream().map(DeviceAssemblyDTO::getMaterialCode).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)) {
                    String deviceAssCode = collect.get(0);
                    faultKnowledgeBase.setMaterialCode(deviceAssCode);
                }
            } else {
                stringBuilder.append("系统中不存在该设备组件，");
            }
        }

        if (StrUtil.isBlank(faultKnowledgeBaseModel.getFaultPhenomenon())) {
            stringBuilder.append("故障现象必填，");
        } else {
            faultKnowledgeBase.setFaultPhenomenon(faultKnowledgeBaseModel.getFaultPhenomenon());
        }

        if (StrUtil.isBlank(faultKnowledgeBaseModel.getSolution())) {
            stringBuilder.append("解决方案必填，");
        } else {
            faultKnowledgeBase.setSolution(faultKnowledgeBaseModel.getSolution());
        }
        //设置导入流程是工班长还是技术员
        String roleCodes = sysUser.getRoleCodes();
        List<String> roleList = StrUtil.splitTrim(roleCodes, ",");
        if (roleList.contains(RoleConstant.FOREMAN)) {
            faultKnowledgeBase.setProcessInitiator(0);
        } else if (roleList.contains(RoleConstant.TECHNICIAN)) {
            faultKnowledgeBase.setProcessInitiator(1);
        } else {
            faultKnowledgeBase.setProcessInitiator(0);
        }

        faultKnowledgeBase.setFaultReason(faultKnowledgeBaseModel.getFaultReason());
        faultKnowledgeBase.setMethod(faultKnowledgeBaseModel.getMethod());
        faultKnowledgeBase.setTools(faultKnowledgeBaseModel.getTools());

        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            faultKnowledgeBaseModel.setDeviceMistake(stringBuilder.toString());
        }

    }

    private Result<?> getErrorExcel(int errorLines, List<FaultKnowledgeBaseModel> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/knowledgeBaseError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/knowledgeBaseError.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<String, Object> errorMap = new HashMap<String, Object>(16);
        List<Map<String, String>> listMap = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            FaultKnowledgeBaseModel faultKnowledgeBaseModel = list.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("lineName", faultKnowledgeBaseModel.getLineName());
            lm.put("majorName", faultKnowledgeBaseModel.getMajorName());
            lm.put("systemName", faultKnowledgeBaseModel.getSystemName());
            lm.put("knowledgeBaseTypeName", faultKnowledgeBaseModel.getKnowledgeBaseTypeName());
            lm.put("deviceTypeName", faultKnowledgeBaseModel.getDeviceTypeName());
            lm.put("materialName", faultKnowledgeBaseModel.getMaterialName());
            lm.put("faultPhenomenon", faultKnowledgeBaseModel.getFaultPhenomenon());
            lm.put("faultReason", faultKnowledgeBaseModel.getFaultReason());
            lm.put("solution", faultKnowledgeBaseModel.getSolution());
            lm.put("method", faultKnowledgeBaseModel.getMethod());
            lm.put("tools", faultKnowledgeBaseModel.getTools());
            lm.put("deviceMistake", faultKnowledgeBaseModel.getDeviceMistake());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "故障知识库导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imporReturnRes(errorLines, successLines, null, true, url);
    }

    public static Result<?> imporReturnRes(int errorLines, int successLines, String tipMessage, boolean isType, String failReportUrl) throws IOException {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl", failReportUrl);
                Result res = Result.ok(result);
                res.setMessage("文件失败，数据有错误。");
                res.setCode(200);
                return res;
            } else {
                //是否成功
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", true);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                Result res = Result.ok(result);
                res.setMessage("文件导入成功！");
                res.setCode(200);
                return res;
            }
        } else {
            JSONObject result = new JSONObject(5);
            result.put("isSucceed", false);
            result.put("errorCount", errorLines);
            result.put("successCount", successLines);
            int totalCount = successLines + errorLines;
            result.put("totalCount", totalCount);
            Result res = Result.ok(result);
            res.setMessage(tipMessage);
            res.setCode(200);
            return res;
        }

    }

    //下拉框
    private void selectList(Workbook workbook, String name, int firstCol, int lastCol, List<DictModel> modelList) {
        Sheet sheet = workbook.getSheetAt(0);
        if (CollectionUtil.isNotEmpty(modelList)) {
            //将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
            int sheetTotal = workbook.getNumberOfSheets();
            String hiddenSheetName = name + "_hiddenSheet";
            List<String> collect = modelList.stream().map(DictModel::getText).collect(Collectors.toList());
            Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
            if (hiddenSheet == null) {
                hiddenSheet = workbook.createSheet(hiddenSheetName);
                //写入下拉数据到新的sheet页中
                for (int i = 0; i < collect.size(); i++) {
                    Row hiddenRow = hiddenSheet.createRow(i);
                    Cell hiddenCell = hiddenRow.createCell(0);
                    hiddenCell.setCellValue(collect.get(i));
                }
                workbook.setSheetHidden(sheetTotal, true);
            }

            // 下拉数据
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 65535, firstCol, lastCol);
            //  生成下拉框内容名称
            String strFormula = hiddenSheetName + "!$A$1:$A$65535";
            // 根据隐藏页面创建下拉列表
            XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
            DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
            //  对sheet页生效
            sheet.addValidationData(validation);
        }

    }

    public boolean getRole() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> rolesByUsername = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        if (!rolesByUsername.contains(RoleConstant.ADMIN) && !rolesByUsername.contains(RoleConstant.FOREMAN) && !rolesByUsername.contains(RoleConstant.MAJOR_PEOPLE)) {
            return true;
        }
        return false;
    }

    public String startProcess(FaultKnowledgeBase faultKnowledgeBase) {
        String id = faultKnowledgeBase.getId();
        if (StrUtil.isEmpty(id)) {
            //list转string
            getFaultCodeList(faultKnowledgeBase);
            // 获取故障现象编号
            String faultPhenomenonCode = this.getFaultPhenomenonCode("GZXX");
            faultKnowledgeBase.setFaultPhenomenonCode(faultPhenomenonCode);
            faultKnowledgeBase.setStatus(FaultConstant.PENDING);
            faultKnowledgeBase.setScanNum(0);
            faultKnowledgeBase.setDelFlag(0);
            if (StringUtils.isEmpty(faultKnowledgeBase.getDeviceTypeCode()) || StringUtils.isEmpty(faultKnowledgeBase.getMaterialCode())) {
                Result<String> result = new Result<>();
                result.error500("设备或组件不能为空");
            }
            faultKnowledgeBaseMapper.insert(faultKnowledgeBase);
            // 添加故障原因和解决方案
            this.saveFaultData(faultKnowledgeBase);
            String newId = faultKnowledgeBase.getId();
            return newId;
        } else {
            getFaultCodeList(faultKnowledgeBase);
            faultKnowledgeBaseMapper.updateById(faultKnowledgeBase);

            // 删除原先的数据
            List<FaultCauseSolution> solutions = faultCauseSolutionService.lambdaQuery()
                    .eq(FaultCauseSolution::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(FaultCauseSolution::getKnowledgeBaseId, id)
                    .select(FaultCauseSolution::getId)
                    .list();
            if (CollUtil.isNotEmpty(solutions)) {
                List<String> causeSolutionIds = solutions.stream()
                        .map(FaultCauseSolution::getId)
                        .collect(Collectors.toList());
                QueryWrapper<FaultSparePart> wrapper = new QueryWrapper<>();
                wrapper.lambda().in(FaultSparePart::getCauseSolutionId, causeSolutionIds);
                faultSparePartService.remove(wrapper);
            }
            QueryWrapper<FaultCauseSolution> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(FaultCauseSolution::getKnowledgeBaseId, id);
            faultCauseSolutionService.remove(wrapper);
            this.saveFaultData(faultKnowledgeBase);
            return id;
        }

    }

    /**
     * 保存故障原因及解决方案和备件数据
     *
     * @param faultKnowledgeBase
     */
    private void saveFaultData(FaultKnowledgeBase faultKnowledgeBase) {
        /*搜索引擎数据构造*/
        // 需构造同步至搜索引擎的数据
        List<CauseSolution> causeSolutions = new ArrayList<>();
        CauseSolution solution = null;
        /*搜索引擎数据构造*/

        List<FaultCauseSolutionDTO> faultCauseSolutions = faultKnowledgeBase.getFaultCauseSolutions();
        // 备件信息
        List<FaultSparePart> sparePartInfos = new ArrayList<>();
        if (CollUtil.isNotEmpty(faultCauseSolutions)) {
            for (FaultCauseSolutionDTO faultCauseSolution : faultCauseSolutions) {
                FaultCauseSolution causeSolution = new FaultCauseSolution();
                BeanUtils.copyProperties(faultCauseSolution, causeSolution);
                causeSolution.setKnowledgeBaseId(faultKnowledgeBase.getId());
                // 单条添加，为了获取记录的ID
                faultCauseSolutionService.save(causeSolution);

                /*搜索引擎数据构造*/
                solution = new CauseSolution();
                BeanUtils.copyProperties(causeSolution, solution);
                causeSolutions.add(solution);
                /*搜索引擎数据构造*/

                // 构造备件数据批量保存
                List<FaultSparePart> spareParts = faultCauseSolution.getSpareParts();
                if (CollUtil.isNotEmpty(spareParts)) {
                    for (FaultSparePart sparePart : spareParts) {
                        sparePart.setCauseSolutionId(causeSolution.getId());
                    }
                    sparePartInfos.addAll(spareParts);
                }
            }

            // 批量保存备件信息
            if (CollUtil.isNotEmpty(sparePartInfos)) {
                faultSparePartService.saveBatch(sparePartInfos);
            }
        }

        /*搜索引擎数据构造*/
        this.knowledgeBaseElasticData(faultKnowledgeBase, causeSolutions, sparePartInfos);
        /*搜索引擎数据构造*/
    }

    /**
     * 添加或编辑是同步数据至Elasticsearch
     *
     * @param faultKnowledgeBase
     * @param causeSolutions
     * @param sparePartInfos
     */
    private void knowledgeBaseElasticData(FaultKnowledgeBase faultKnowledgeBase,
                                          List<CauseSolution> causeSolutions,
                                          List<FaultSparePart> sparePartInfos) {
        AsyncThreadPoolExecutorUtil executor = AsyncThreadPoolExecutorUtil.getExecutor();
        executor.submitTask(() -> {
            KnowledgeBase knowledgeBase = new KnowledgeBase();
            BeanUtils.copyProperties(faultKnowledgeBase, knowledgeBase);
            knowledgeBase.setUpdateTime(new Date());
            // 故障知识分类编码
            Optional.ofNullable(knowledgeBase.getKnowledgeBaseTypeCode())
                    .ifPresent(knowledgeBaseTypeCode -> {
                        QueryWrapper<FaultKnowledgeBaseType> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(FaultKnowledgeBaseType::getDelFlag, CommonConstant.DEL_FLAG_0)
                                .eq(FaultKnowledgeBaseType::getCode, knowledgeBaseTypeCode)
                                .last("limit 1");
                        FaultKnowledgeBaseType knowledgeBaseType = faultKnowledgeBaseTypeMapper.selectOne(wrapper);
                        knowledgeBase.setKnowledgeBaseTypeName(knowledgeBaseType.getName());
                    });
            // 专业
            Optional.ofNullable(knowledgeBase.getMajorCode())
                    .ifPresent(majorCode -> {
                        JSONObject major = sysBaseApi.getCsMajorByCode(majorCode);
                        CsUserMajorModel csUserMajorModel = JSON.toJavaObject(major, CsUserMajorModel.class);
                        knowledgeBase.setMajorName(csUserMajorModel.getMajorName());
                    });
            // 子系统
            Optional.ofNullable(knowledgeBase.getSystemCode()).ifPresent(systemCode -> {
                List<String> systemNames = sysBaseApi.getSystemNames(Collections.singletonList(systemCode));
                if (CollUtil.isNotEmpty(systemNames)) {
                    knowledgeBase.setSystemName(systemNames.get(0));
                }
            });
            // 组件部位
            Optional.ofNullable(knowledgeBase.getMaterialCode()).ifPresent(materialCode -> {
                Map<String, String> composeMap = sysBaseApi.getDeviceComposeNameByCode(Arrays.asList(materialCode));
                knowledgeBase.setMaterialName(composeMap.get(materialCode));
            });
            // 设备类型
            Optional.ofNullable(knowledgeBase.getDeviceTypeCode()).ifPresent(deviceTypeCode -> {
                List<DeviceType> deviceTypes = sysBaseApi.selectDeviceTypeByCodes(Collections.singleton(deviceTypeCode));
                if (CollUtil.isNotEmpty(deviceTypes)) {
                    DeviceType deviceType = deviceTypes.stream().findFirst().orElseGet(DeviceType::new);
                    knowledgeBase.setDeviceTypeName(deviceType.getName());
                }
            });
            // 故障等级
            Optional.ofNullable(knowledgeBase.getFaultLevelCode()).ifPresent(faultLevelCode -> {
                FaultLevel faultLevel = faultLevelService.lambdaQuery()
                        .eq(FaultLevel::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(FaultLevel::getCode, faultLevelCode).last("limit 1").one();
                if (ObjectUtil.isNotEmpty(faultLevel)) {
                    knowledgeBase.setFaultLevelName(faultLevel.getName());
                }
            });
            if (CollUtil.isNotEmpty(causeSolutions)) {
                if (CollUtil.isNotEmpty(sparePartInfos)) {
                    List<String> sparePartCodes = sparePartInfos.stream().map(FaultSparePart::getSparePartCode).collect(Collectors.toList());
                    Map<String, String> sparePartCodeMap = CollUtil.isEmpty(sparePartCodes) ? Collections.emptyMap() : sysBaseApi.getMaterialNameByCode(sparePartCodes);
                    sparePartInfos.stream().forEach(l -> l.setSparePartName(sparePartCodeMap.get(l.getSparePartCode())));

                    Map<String, List<FaultSparePart>> partMap = sparePartInfos.stream()
                            .collect(Collectors.groupingBy(FaultSparePart::getCauseSolutionId));
                    causeSolutions.forEach(l -> {
                        List<FaultSparePart> spareParts = partMap.get(l.getId());
                        if (CollUtil.isNotEmpty(spareParts)) {
                            List<SparePart> collect = spareParts.stream().map(sparePart -> {
                                SparePart part = new SparePart();
                                BeanUtils.copyProperties(sparePart, part);
                                return part;
                            }).collect(Collectors.toList());
                            l.setSpareParts(collect);
                        }
                    });
                }
                knowledgeBase.setReasonSolutions(causeSolutions);
            }
            boolean exists = elasticApi.exists(knowledgeBase.getId(), KnowledgeBase.class);
            if (exists) {
                elasticApi.removeKnowledgeBase(knowledgeBase.getId());
            }
            elasticApi.saveBatch(Arrays.asList(knowledgeBase));
            return null;
        });
    }

    /**
     * 故障现象编码生成
     *
     * @return
     */
    private String getFaultPhenomenonCode(String prefix) {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String code = String.format("%s%s", prefix, snowflake.nextIdStr());
        return code;
    }

    /**
     * list转string
     */
    private void getFaultCodeList(FaultKnowledgeBase faultKnowledgeBase) {
        List<String> faultCodeList = faultKnowledgeBase.getFaultCodeList();
        if (CollectionUtils.isNotEmpty(faultCodeList)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String faultCode : faultCodeList) {
                stringBuilder.append(faultCode);
                stringBuilder.append(",");
            }
            // 判断字符串长度是否有效
            if (stringBuilder.length() > 0) {
                // 截取字符
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            faultKnowledgeBase.setFaultCodes(stringBuilder.toString());
        }
        faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        FaultKnowledgeBase faultKnowledgeBase = this.getById(businessKey);
        if (ObjectUtil.isEmpty(faultKnowledgeBase)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        int states = updateStateEntity.getStates();
        switch (states) {
            case 0:
                // 技术员或者专业技术负责人审核
                faultKnowledgeBase.setStatus(FaultConstant.PENDING);
                break;
            case 2:
                // 技术员驳回，更新状态为已驳回状态
                faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
                break;
            case 4:
                //专业技术负责人驳回
                faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
                break;
            case 5:
                //已审批
                faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
                break;
            default:
                break;
        }
        this.updateById(faultKnowledgeBase);
        /*搜索引擎数据同步*/
        AsyncThreadPoolExecutorUtil executor = AsyncThreadPoolExecutorUtil.getExecutor();
        executor.submitTask(() -> {
            Map<String, Object> map = new HashMap<>(16);
            // 状态
            map.put("status", faultKnowledgeBase.getStatus());
            elasticApi.update(faultKnowledgeBase.getId(), KnowledgeBase.class, map);
            return null;
        });
        /*搜索引擎数据同步*/
    }

    @Override
    public IPage<KnowledgeBaseResDTO> search(Page<KnowledgeBaseResDTO> page, KnowledgeBaseReqDTO knowledgeBaseReqDTO) {
        // 记录搜索记录,不影响查询业务
        try {
            String keyword = knowledgeBaseReqDTO.getKeyword();
            if (ObjectUtil.isNotEmpty(knowledgeBaseReqDTO) && StrUtil.isNotEmpty(keyword)) {
                Date searchTime = new Date();
                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                SearchRecords searchRecords = searchRecordsService.lambdaQuery()
                        .eq(SearchRecords::getKeyword, keyword).one();
                if (ObjectUtil.isNotEmpty(searchRecords)) {
                    searchRecords.setSearchTime(searchTime);
                    searchRecords.setUserId(loginUser.getId());
                    searchRecords.setResultCount(searchRecords.getResultCount() + 1);
                    searchRecordsService.updateById(searchRecords);
                } else {
                    SearchRecords records = new SearchRecords();
                    records.setKeyword(keyword);
                    records.setUserId(loginUser.getId());
                    records.setSearchTime(searchTime);
                    records.setResultCount(1);
                    searchRecordsService.save(records);
                }
            }
        } catch (Exception e) {
            log.error("保存搜索记录异常：", e.getMessage());
        }

        // 分页搜索
        IPage<KnowledgeBaseResDTO> pageList = null;
        try {
            pageList = elasticApi.search(page, knowledgeBaseReqDTO);
            List<KnowledgeBaseResDTO> records = pageList.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                List<String> knowledgeBaseIds = records.stream().map(KnowledgeBaseResDTO::getId).collect(Collectors.toList());
                Map<String, Map<String, FaultCauseProportionNumDTO>> dataMap = this.buildCauseNumberMap(knowledgeBaseIds);
                // 采用数
                List<AnalyzeFaultCauseResDTO> useNumbers = baseMapper.countFaultCauseByIdSet(knowledgeBaseIds);
                Map<String, Long> useMap = useNumbers.stream()
                        .collect(Collectors.toMap(k -> k.getKnowledgeBaseId(), v -> v.getNum(), Long::sum));
                pageList.getRecords().forEach(knowledgeBaseRes -> {
                    String id = knowledgeBaseRes.getId();
                    List<CauseSolution> reasonSolutions = knowledgeBaseRes.getReasonSolutions();
                    if (CollUtil.isNotEmpty(reasonSolutions)) {
                        Map<String, FaultCauseProportionNumDTO> happenRateMap = CollUtil.isEmpty(dataMap.get(id)) ?
                                Collections.emptyMap() : dataMap.get(id);
                        for (CauseSolution reasonSolution : reasonSolutions) {
                            FaultCauseProportionNumDTO causeProportionNum = Optional.ofNullable(happenRateMap.get(reasonSolution.getId()))
                                    .orElseGet(FaultCauseProportionNumDTO::new);
                            String happenRate = causeProportionNum.getHappenRate();
//                            if (ObjectUtil.isEmpty(happenRate)) {
//                                happenRate = "0%";
//                            }
                            reasonSolution.setHappenRate(happenRate);
                        }
                    }
                    // 采用数
                    int use = ObjectUtil.isEmpty(useMap.get(id)) ? 0 : useMap.get(id).intValue();
                    knowledgeBaseRes.setUse(use);
                });
            }
        } catch (Exception e) {
            log.error("高级搜索分页查询异常：{}", e.getMessage());
            throw new AiurtBootException("搜索异常！");
        }
        return pageList;
    }

    @Override
    public void synchrodata(HttpServletRequest request, HttpServletResponse response) {
        List<KnowledgeBase> knowledgeBases = faultKnowledgeBaseMapper.synchrodata();
        if (CollUtil.isEmpty(knowledgeBases)) {
            return;
        }
        List<String> levelCodes = knowledgeBases.stream()
                .filter(l -> ObjectUtil.isNotEmpty(l.getFaultLevelCode()))
                .map(KnowledgeBase::getFaultLevelCode)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(levelCodes)) {
            // 故障等级翻译
            List<FaultLevel> levels = faultLevelService.lambdaQuery()
                    .eq(FaultLevel::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(FaultLevel::getCode, levelCodes)
                    .select(FaultLevel::getCode, FaultLevel::getName)
                    .list();
            final Map<String, String> levelMap = levels.stream()
                    .filter(l -> ObjectUtil.isNotEmpty(l.getCode()))
                    .collect(Collectors.toMap(k -> k.getCode(), v -> v.getName()));
            knowledgeBases.forEach(knowledgeBase -> knowledgeBase.setFaultLevelName(levelMap.get(knowledgeBase.getFaultLevelCode())));
        }
        // 采用率默认给0，高级查询的时候会分别进行计算
        knowledgeBases.forEach(knowledgeBase -> {
            Integer use = knowledgeBase.getUse();
            if (ObjectUtil.isEmpty(use)) {
                knowledgeBase.setUse(0);
            }
        });

        List<String> knowledgeBaseIds = knowledgeBases.stream()
                .map(KnowledgeBase::getId)
                .collect(Collectors.toList());
        List<FaultCauseSolution> faultCauseSolutions = faultCauseSolutionService.lambdaQuery()
                .eq(FaultCauseSolution::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(FaultCauseSolution::getKnowledgeBaseId, knowledgeBaseIds)
                .list();
        if (CollUtil.isNotEmpty(faultCauseSolutions)) {
            List<CauseSolution> causeSolutions = new ArrayList<>();
            CauseSolution causeSolution = null;
            for (FaultCauseSolution faultCauseSolution : faultCauseSolutions) {
                causeSolution = new CauseSolution();
                BeanUtils.copyProperties(faultCauseSolution, causeSolution);
                causeSolutions.add(causeSolution);
            }

            List<String> causeSolutionIds = faultCauseSolutions.stream()
                    .map(FaultCauseSolution::getId)
                    .distinct()
                    .collect(Collectors.toList());
            List<FaultSparePart> faultSpareParts = faultSparePartService.lambdaQuery()
                    .eq(FaultSparePart::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(FaultSparePart::getCauseSolutionId, causeSolutionIds)
                    .list();

            List<SparePart> spareParts = new ArrayList<>();
            if (CollUtil.isNotEmpty(faultSpareParts)) {
                List<String> sparePartCodes = faultSpareParts.stream()
                        .map(FaultSparePart::getSparePartCode)
                        .distinct()
                        .collect(Collectors.toList());
                Map<String, String> sparePartCodeMap = CollUtil.isEmpty(sparePartCodes) ? Collections.emptyMap() : sysBaseApi.getMaterialNameByCode(sparePartCodes);
                SparePart sparePart = null;
                for (FaultSparePart faultSparePart : faultSpareParts) {
                    faultSparePart.setSparePartName(sparePartCodeMap.get(faultSparePart.getSparePartCode()));
                    sparePart = new SparePart();
                    BeanUtils.copyProperties(faultSparePart, sparePart);
                    spareParts.add(sparePart);
                }
            }
            Map<String, List<SparePart>> sparePartMap = Collections.emptyMap();
            if (CollUtil.isNotEmpty(spareParts)) {
                sparePartMap = spareParts.stream().collect(Collectors.groupingBy(SparePart::getCauseSolutionId));
            }
            Map<String, Map<String, FaultCauseProportionNumDTO>> causeNumberMap = this.buildCauseNumberMap(knowledgeBaseIds);
            for (CauseSolution solution : causeSolutions) {
                String id = solution.getId();
                String knowledgeBaseId = solution.getKnowledgeBaseId();
                List<SparePart> sparePartList = ObjectUtil.isEmpty(sparePartMap.get(id)) ? Collections.emptyList() : sparePartMap.get(id);
                solution.setSpareParts(sparePartList);

                Map<String, FaultCauseProportionNumDTO> map = causeNumberMap.get(knowledgeBaseId);
                if (CollUtil.isNotEmpty(map)) {
//                    String happenRate = ObjectUtil.isEmpty(map.get(id)) ? "0%" : map.get(id);
                    FaultCauseProportionNumDTO causeProportionNum = Optional.ofNullable(map.get(id))
                            .orElseGet(FaultCauseProportionNumDTO::new);
                    String happenRate = causeProportionNum.getHappenRate();
                    solution.setHappenRate(happenRate);
                }
            }

            // 放入解决方案信息
            Map<String, List<CauseSolution>> causeSolutionMap = causeSolutions.stream()
                    .collect(Collectors.groupingBy(CauseSolution::getKnowledgeBaseId));
            for (KnowledgeBase knowledgeBase : knowledgeBases) {
                knowledgeBase.setReasonSolutions(causeSolutionMap.get(knowledgeBase.getId()));
            }
        }

        // 调用API存入ES
        try {
            BulkResponse[] bulkResponses = elasticApi.saveBatch(knowledgeBases);
        } catch (Exception e) {
            log.error("同步知识库数据异常：{}", e.getMessage());
            throw new AiurtBootException("同步知识库数据异常！");
        }
    }

    @Override
    public IPage<KnowledgeBaseResDTO> knowledgeBaseMatching(Page<KnowledgeBaseResDTO> page, KnowledgeBaseMatchDTO knowledgeBaseMatchDTO) {
        IPage<KnowledgeBaseResDTO> pageList = null;
        try {
            pageList = elasticApi.knowledgeBaseMatching(page, knowledgeBaseMatchDTO);
            List<KnowledgeBaseResDTO> records = pageList.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                // 原因出现率百分比
                List<String> knowledgeBaseIds = records.stream().map(KnowledgeBaseResDTO::getId).collect(Collectors.toList());
                Map<String, Map<String, FaultCauseProportionNumDTO>> numberMap = this.buildCauseNumberMap(knowledgeBaseIds);
                for (KnowledgeBaseResDTO record : records) {
                    List<CauseSolution> reasonSolutions = record.getReasonSolutions();
                    if (CollUtil.isNotEmpty(reasonSolutions)) {
                        String knowledgeBaseId = record.getId();
                        Map<String, FaultCauseProportionNumDTO> happenRateMap = CollUtil.isEmpty(numberMap.get(knowledgeBaseId)) ?
                                Collections.emptyMap() : numberMap.get(knowledgeBaseId);
                        for (CauseSolution solution : reasonSolutions) {
                            FaultCauseProportionNumDTO causeProportionNum = Optional.ofNullable(happenRateMap.get(solution.getId()))
                                    .orElseGet(FaultCauseProportionNumDTO::new);
                            String happenRate = causeProportionNum.getHappenRate();
                            solution.setHappenRate(happenRate);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("知识库匹配搜索异常：{}", e.getMessage());
            throw new AiurtBootException("搜索异常！");
        }
        return pageList;
    }

    @Override
    public List<String> phenomenonMatching(HttpServletRequest request, HttpServletResponse response, KnowledgeBaseMatchDTO knowledgeBaseMatchDTO) {
        List<String> list = null;
        try {
            knowledgeBaseMatchDTO.setStatus(FaultConstant.APPROVED);
            list = elasticApi.phenomenonMatching(knowledgeBaseMatchDTO);
        } catch (Exception e) {
            log.error("智能助手故障现象匹配搜索异常：{}", e.getMessage());
            throw new AiurtBootException("搜索异常！");
        }
        return list;
    }

    /**
     * 标准维修方案要求查询
     *
     * @param faultCauseSolutionIdList
     * @return
     */
    @Override
    public List<FaultSparePart> getStandardRepairRequirements(String[] faultCauseSolutionIdList) {
        if (ObjectUtil.isEmpty(faultCauseSolutionIdList)) {
            return Collections.emptyList();
        }
        List<String> list = Stream.of(faultCauseSolutionIdList).collect(Collectors.toList());
        return baseMapper.getStandardRepairRequirements(list);
    }

    /**
     * 添加
     *
     * @param faultKnowledgeBase
     * @return
     */
    @Override
    public void addFaultKnowledgeBase(FaultKnowledgeBase faultKnowledgeBase) {
        StartBpmnDTO startBpmnDTO = new StartBpmnDTO();
        startBpmnDTO.setModelKey("fault_knowledge_base");
        Map<String, Object> data = BeanUtil.beanToMap(faultKnowledgeBase);
        startBpmnDTO.setBusData(data);
        FlowTaskCompleteCommentDTO flowTaskCompleteCommentDTO = new FlowTaskCompleteCommentDTO();
        flowTaskCompleteCommentDTO.setApprovalType("agree");
        startBpmnDTO.setFlowTaskCompleteDTO(flowTaskCompleteCommentDTO);
        startBpmnDTO.setUserName(faultKnowledgeBase.getUserName());
        flowBaseApi.startAndTakeFirst(startBpmnDTO);
    }
}
