package com.aiurt.boot.materials.service.impl;
import com.aiurt.boot.materials.dto.*;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.entity.DynamicTableDataEntity;
import com.aiurt.modules.common.entity.SelectTable;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.aiurt.boot.materials.dto.PatrolRecordDetailDTO;
import com.aiurt.common.system.base.entity.DynamicTableDataEntity;
import com.aiurt.modules.common.entity.SelectTable;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesItemMapper;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesItemService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.entity.DynamicTableEntity;
import com.aiurt.common.system.base.entity.DynamicTableTitleEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: emergency_materials_invoices_item
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsInvoicesItemServiceImpl extends ServiceImpl<EmergencyMaterialsInvoicesItemMapper, EmergencyMaterialsInvoicesItem> implements IEmergencyMaterialsInvoicesItemService {
    @Autowired
    private EmergencyMaterialsInvoicesItemMapper emergencyMaterialsInvoicesItemMapper;

    @Autowired
    private EmergencyMaterialsInvoicesMapper emergencyMaterialsInvoicesMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private IEmergencyMaterialsService emergencyMaterialsService;

    @Autowired
    private IEmergencyMaterialsInvoicesService invoicesService;

    @Autowired
    private EmergencyMaterialsMapper emergencyMaterialsMapper;


    @Override
    public Page<EmergencyMaterialsInvoicesItem> getPatrolRecord(Page<EmergencyMaterialsInvoicesItem> pageList, String materialsCode, String startTime, String endTime,String  standardCode,String lineCode,String stationCode,String positionCode) {
        //父级
        EmergencyMaterialsInvoices emergencyMaterialsInvoices = new EmergencyMaterialsInvoices();
        LambdaQueryWrapper<EmergencyMaterialsInvoicesItem> emergencyMaterialsInvoicesItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        emergencyMaterialsInvoicesItemLambdaQueryWrapper.eq(EmergencyMaterialsInvoicesItem::getDelFlag,0);
        if(StrUtil.isNotBlank(materialsCode)){
            emergencyMaterialsInvoicesItemLambdaQueryWrapper.eq(EmergencyMaterialsInvoicesItem::getMaterialsCode,materialsCode);
        }
        List<EmergencyMaterialsInvoicesItem> emergencyMaterialsInvoicesItems = emergencyMaterialsInvoicesItemMapper.selectList(emergencyMaterialsInvoicesItemLambdaQueryWrapper);
        if (CollectionUtil.isNotEmpty(emergencyMaterialsInvoicesItems)){
            List<String> collect = emergencyMaterialsInvoicesItems.stream().map(EmergencyMaterialsInvoicesItem::getInvoicesId).collect(Collectors.toList());
            LambdaQueryWrapper<EmergencyMaterialsInvoices> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (CollectionUtil.isNotEmpty(collect)){
                lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getDelFlag,0);
                lambdaQueryWrapper.orderByDesc(EmergencyMaterialsInvoices::getCreateTime);
                 if (StrUtil.isNotBlank(lineCode)){
                    lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getLineCode,lineCode);
                 }
                 if (StrUtil.isNotBlank(stationCode)){
                    lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getStationCode,stationCode);
                 }
                 if (StrUtil.isNotBlank(positionCode)){
                   lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getPositionCode,positionCode);
                 }
                lambdaQueryWrapper.in(EmergencyMaterialsInvoices::getId, collect);
                lambdaQueryWrapper.last("limit 1");
            }
            emergencyMaterialsInvoices = emergencyMaterialsInvoicesMapper.selectOne(lambdaQueryWrapper);
        }
        List<EmergencyMaterialsInvoicesItem> patrolRecord = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(pageList,
                                                 materialsCode,
                                                 startTime,
                                                 endTime,
                                                 StrUtil.isBlank(standardCode)&& ObjectUtil.isNotEmpty(emergencyMaterialsInvoices) ? emergencyMaterialsInvoices.getStandardCode() : standardCode,
                                                  "0",
                                                 lineCode,
                                                 stationCode,
                                                 positionCode);
        patrolRecord.forEach(e->{
            if (StrUtil.isNotBlank(e.getPatrolId())) {
                //根据巡视人id查询巡视人名称
                String[] split = e.getPatrolId().split(",");
                List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(split);
                if (CollUtil.isNotEmpty(loginUsers)){
                    String collect = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    e.setPatrolName(collect);
                }
            }if(StrUtil.isNotBlank(e.getPatrolTeamCode())){
                //根据巡检班组code查询巡检班组名称
                String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(e.getPatrolTeamCode());
                e.setPatrolTeamName(departNameByOrgCode);

                //子级
            }if("0".equals(e.getPid()) && StrUtil.isNotBlank(e.getId())){
                List<EmergencyMaterialsInvoicesItem> patrolRecord1 = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(pageList, materialsCode, startTime, endTime, standardCode, e.getId(),lineCode,stationCode,positionCode);
                e.setSubLevel(patrolRecord1);
            }
        });
        return pageList.setRecords(patrolRecord);
    }

    /**
     * 查询物资的巡检记录
     *
     * @param recordReqDTO
     * @return
     */
    @Override
    public DynamicTableEntity getPatrolRecord(PatrolRecordReqDTO recordReqDTO) {
        DynamicTableEntity dynamicTableEntity = new DynamicTableEntity();
        String standardCode = recordReqDTO.getStandardCode();
//        if (StrUtil.isBlank(standardCode)) {
//            throw new AiurtBootException("请选择巡检表！");
//        }
        // 查询物资
        String id = recordReqDTO.getId();
        EmergencyMaterials emergencyMaterials = emergencyMaterialsService.getById(id);
        if (Objects.isNull(emergencyMaterials)) {
            throw new AiurtBootException("该记录不存在！");
        }
        String materialsCode = emergencyMaterials.getMaterialsCode();


        recordReqDTO.setStandardCode(standardCode);

//        if(StrUtil.isBlank(standardCode)){
//            LambdaQueryWrapper<EmergencyMaterialsInvoicesItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(EmergencyMaterialsInvoicesItem::getMaterialsId,id);
//            lambdaQueryWrapper.eq(EmergencyMaterialsInvoicesItem::getDelFlag, CommonConstant.DEL_FLAG_0);
//            List<EmergencyMaterialsInvoicesItem> list = emergencyMaterialsInvoicesItemMapper.selectList(lambdaQueryWrapper);
//
//            if (CollectionUtil.isNotEmpty(list)){
//                List<String> collect = list.stream().map(EmergencyMaterialsInvoicesItem::getInvoicesId).collect(Collectors.toList());
//                LambdaQueryWrapper<EmergencyMaterialsInvoices> queryWrapper = new LambdaQueryWrapper<>();
//                queryWrapper.eq(EmergencyMaterialsInvoices::getDelFlag, CommonConstant.DEL_FLAG_0);
//                queryWrapper.in(EmergencyMaterialsInvoices::getId,collect);
//                queryWrapper.orderByDesc(EmergencyMaterialsInvoices::getCreateTime);
//                List<EmergencyMaterialsInvoices> emergencyMaterialsInvoices = emergencyMaterialsInvoicesMapper.selectList(queryWrapper);
//                if(CollectionUtil.isNotEmpty(emergencyMaterialsInvoices)){
//                    EmergencyMaterialsInvoices emergencyMaterialsInvoices1 = emergencyMaterialsInvoices.get(0);
//                    recordReqDTO.setStandardCode(emergencyMaterialsInvoices1.getStandardCode());
//                }
//            }
//        }

        // 查询记录数据
        List<EmergencyMaterialsInvoices> list1 = new ArrayList<>();
        Page<EmergencyMaterialsInvoices> page = new Page<>(recordReqDTO.getPageNo(), recordReqDTO.getPageSize());
        if(StrUtil.isNotBlank(standardCode)){
            list1 = invoicesService.queryList(page, recordReqDTO);
        }
        if (CollUtil.isEmpty(list1)) {
            return dynamicTableEntity;
        }


        dynamicTableEntity.setCurrent(page.getCurrent());
        dynamicTableEntity.setTotal(page.getTotal());
        // 只需要构建一次title即可,list集合最大组装title
        LambdaQueryWrapper<EmergencyMaterialsInvoicesItem> queryWrapper = new LambdaQueryWrapper<>();
        Set<String> idSet = list1.stream().map(EmergencyMaterialsInvoices::getId).collect(Collectors.toSet());
        // 查询检修记录结果数据
        queryWrapper.in(EmergencyMaterialsInvoicesItem::getInvoicesId, idSet)
                .eq(EmergencyMaterialsInvoicesItem::getMaterialsCode, materialsCode)
                .orderByDesc(EmergencyMaterialsInvoicesItem::getCreateTime);
        List<EmergencyMaterialsInvoicesItem> maxInvoicesItemList = baseMapper.selectList(queryWrapper);

        Map<String, List<EmergencyMaterialsInvoicesItem>> itemMap = maxInvoicesItemList.stream().collect(Collectors.groupingBy(EmergencyMaterialsInvoicesItem::getInvoicesId));
        AtomicInteger max = new AtomicInteger();
        AtomicReference<String> maxId = new AtomicReference<>("");
        itemMap.forEach((recordId, list)->{
            List<EmergencyMaterialsInvoicesItem> itemList = list.stream().filter(it -> Objects.nonNull(it.getCheck()) && it.getCheck() == 1).collect(Collectors.toList());
            max.set(max.get() > itemList.size() ? max.get():itemList.size());
            maxId.set(max.get() > itemList.size() ? maxId.get() : recordId);
        });

        List<EmergencyMaterialsInvoicesItem> maxItemList = itemMap.getOrDefault(maxId.get(), Collections.emptyList());
        // 获取code
        Set<String> dataIndexSet = maxItemList.stream().filter(it -> Objects.nonNull(it.getCheck()) && it.getCheck() == 1).map(EmergencyMaterialsInvoicesItem::getCode).collect(Collectors.toSet());
        // 组装title
        List<DynamicTableTitleEntity> treeList = maxItemList.stream().map(item -> {
            DynamicTableTitleEntity title = new DynamicTableTitleEntity();
            title.setTitle(item.getContent());
            title.setDataIndex(item.getCode());
            title.setId(item.getId());
            title.setPid(StrUtil.isBlank(item.getPid()) ? "-9999" : item.getPid());
            return title;
        }).collect(Collectors.toList());

        Map<String, DynamicTableTitleEntity> root = new LinkedHashMap<>();

        for (DynamicTableTitleEntity titleEntity : treeList) {
            DynamicTableTitleEntity parent = root.get(titleEntity.getPid());
            if (Objects.isNull(parent)) {
                parent = new DynamicTableTitleEntity();
                root.put(titleEntity.getPid(), parent);
            }
            DynamicTableTitleEntity table = root.get(titleEntity.getId());
            if (Objects.nonNull(table)) {
                titleEntity.setChildren(table.getChildren());
            }
            root.put(titleEntity.getId(), titleEntity);
            parent.addChildren(titleEntity);
        }

        List<DynamicTableTitleEntity> resultList = new ArrayList<>();
        List<DynamicTableTitleEntity> col = root.values().stream().filter(entity -> StrUtil.isBlank(entity.getPid())).collect(Collectors.toList());
        for (DynamicTableTitleEntity entity : col) {
            resultList.addAll(CollectionUtil.isEmpty(entity.getChildren()) ? Collections.emptyList() : entity.getChildren());
        }
        dynamicTableEntity.setTitleList(resultList);


        List<DynamicTableDataEntity> records = new ArrayList<>();
        list1.stream().forEach(record->{
            String recordId = record.getId();
            LambdaQueryWrapper<EmergencyMaterialsInvoicesItem> wrapper = new LambdaQueryWrapper<>();

            // 查询检修记录结果数据
            wrapper.eq(EmergencyMaterialsInvoicesItem::getInvoicesId, recordId)
                    .eq(EmergencyMaterialsInvoicesItem::getMaterialsId, id);
            List<EmergencyMaterialsInvoicesItem> invoicesItemList = baseMapper.selectList(wrapper);


            // 组装dataList,一记录一条数据
            PatrolRecordDetailDTO dataEntity = new PatrolRecordDetailDTO();
//            Integer inspectionResults = record.getInspectionResults();
//            if (Objects.nonNull(inspectionResults)) {
//                dataEntity.setAbnormalCondition(inspectionResults==0?"异常":"正常");
//            }
            dataEntity.setPatrolDate(record.getPatrolDate());
            if (StrUtil.isNotBlank(record.getUserId())) {
                //根据巡视人id查询巡视人名称
                String[] split = record.getUserId().split(",");
                List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(split);
                if (CollUtil.isNotEmpty(loginUsers)){
                    String collect = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    dataEntity.setPatrolName(collect);
                }
            }if(StrUtil.isNotBlank(record.getDepartmentCode())) {
                //根据巡检班组code查询巡检班组名称
                String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(record.getDepartmentCode());
                dataEntity.setPatrolTeamName(departNameByOrgCode);
            }


            Map<String,Object> map = new HashMap<>(16);
            invoicesItemList.stream().forEach(item->{
                CheckResultDTO checkResultDTO = new CheckResultDTO();
                Integer check = item.getCheck();
                String itemId = item.getCode();
                // 检修项
                if (1 == check) {
                    String abnormalCondition = item.getAbnormalCondition();
                    Integer checkResult = item.getCheckResult();
                    if (StrUtil.isNotBlank(abnormalCondition)){
                        dataEntity.setAbnormalCondition(abnormalCondition);
                    }
                    if (checkResult !=null){
                        checkResultDTO.setCheckResult(checkResult);

                    }
                    Integer inputType = item.getInputType();

                    if (Objects.nonNull(inputType) && 1!=inputType){
                        // 巡检的结果
                        String dictCode = item.getDictCode();
                        if (StringUtils.isNotBlank(dictCode)) {
                            if (Objects.nonNull(item.getOptionValue())) {
                                String s = iSysBaseAPI.translateDict(dictCode, String.valueOf(item.getOptionValue()));
                                checkResultDTO.setWriteValue(s);
                            }
                        }else {
                            checkResultDTO.setWriteValue(item.getWriteValue());
                        }
                    }
                    map.put(itemId,checkResultDTO);
                }
            });
            // 不全数据
            dataIndexSet.forEach(code->{
                Object o = map.get(code);
                if (Objects.isNull(o)) {
                    map.put(code, new CheckResultDTO());
                }
            });
            dataEntity.setDynamicData(map);
            records.add(dataEntity);

            dynamicTableEntity.setRecords(records);

        });
        return dynamicTableEntity;
    }
}
