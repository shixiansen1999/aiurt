package com.aiurt.boot.materials.service.impl;
import com.aiurt.boot.materials.dto.MaterialPatrolDTO;
import com.aiurt.boot.materials.dto.PatrolRecordDetailDTO;
import com.aiurt.boot.materials.dto.PatrolStandardDTO;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
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
import com.aiurt.boot.materials.dto.PatrolRecordReqDTO;
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
        // 查询物资
        String id = recordReqDTO.getId();
        EmergencyMaterials emergencyMaterials = emergencyMaterialsService.getById(id);
        if (Objects.isNull(emergencyMaterials)) {
            throw new AiurtBootException("该记录不存在！");
        }
        String materialsCode = emergencyMaterials.getMaterialsCode();

        String categoryCode = emergencyMaterials.getCategoryCode();

        String lineCode = emergencyMaterials.getLineCode();

        String stationCode = emergencyMaterials.getStationCode();

        String positionCode = emergencyMaterials.getPositionCode();
        recordReqDTO.setMaterialsCode(materialsCode);
        recordReqDTO.setLineCode(lineCode);
        recordReqDTO.setStandardCode(standardCode);
        recordReqDTO.setPositionCode(positionCode);
        recordReqDTO.setStationCode(stationCode);
        //查询当前物资对应的最新的巡检标准
        List<PatrolStandardDTO> standingBook = emergencyMaterialsMapper.getStandingBook(materialsCode, categoryCode, lineCode, stationCode, positionCode);
        PatrolStandardDTO patrolStandardDTO = standingBook.get(0);
        if (StrUtil.isBlank(recordReqDTO.getStandardCode())){
            recordReqDTO.setStandardCode(patrolStandardDTO.getStandardCode());
        }
        // 查询记录数据
        Page<EmergencyMaterialsInvoices> page = new Page<>(recordReqDTO.getPageNo(), recordReqDTO.getPageSize());
        List<EmergencyMaterialsInvoices> recordList = invoicesService.queryList(page, recordReqDTO);
        if (CollUtil.isEmpty(recordList)) {
            return dynamicTableEntity;
        }


        dynamicTableEntity.setCurrent(page.getCurrent());
        dynamicTableEntity.setTotal(page.getTotal());
        // 只需要构建一次title即可
        AtomicReference<Boolean> flag = new AtomicReference<>(Boolean.TRUE);
        List<DynamicTableDataEntity> records = new ArrayList<>();
        recordList.stream().forEach(record->{
            String recordId = record.getId();
            LambdaQueryWrapper<EmergencyMaterialsInvoicesItem> queryWrapper = new LambdaQueryWrapper<>();

            // 查询检修记录结果数据
            queryWrapper.eq(EmergencyMaterialsInvoicesItem::getInvoicesId, recordId)
                    .eq(EmergencyMaterialsInvoicesItem::getMaterialsCode, materialsCode)
            .eq(EmergencyMaterialsInvoicesItem::getStorageLocationCode, positionCode);
            List<EmergencyMaterialsInvoicesItem> invoicesItemList = baseMapper.selectList(queryWrapper);
            if (flag.get()) {
                flag.set(false);
                // 组装title
                List<DynamicTableTitleEntity> treeList = invoicesItemList.stream().map(item -> {
                    DynamicTableTitleEntity title = new DynamicTableTitleEntity();
                    title.setTitle(item.getContent());
                    title.setDataIndex(item.getId());
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
                List<DynamicTableTitleEntity> collect = root.values().stream().filter(entity -> StrUtil.isBlank(entity.getPid())).collect(Collectors.toList());
                for (DynamicTableTitleEntity entity : collect) {
                    resultList.addAll(CollectionUtil.isEmpty(entity.getChildren()) ? Collections.emptyList() : entity.getChildren());
                }
                dynamicTableEntity.setTitleList(resultList);
            }


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
                Integer check = item.getCheck();
                String itemId = item.getId();
                // 检修项
                if (1 == check) {
                    String abnormalCondition = item.getAbnormalCondition();
                    Integer checkResult = item.getCheckResult();
                    String dictCode = item.getDictCode();
                    if (StrUtil.isNotBlank(abnormalCondition)){
                        dataEntity.setAbnormalCondition(abnormalCondition);
                    }
                    if (checkResult!=null){
                        map.put(itemId, checkResult);
                    }
                    if (StringUtils.isNotBlank(dictCode)) {
                        if (Objects.nonNull(item.getOptionValue())) {
                            String s = iSysBaseAPI.translateDict(dictCode, String.valueOf(item.getOptionValue()));
                            map.put(itemId, s);
                        }
                    }else {
                        map.put(itemId, item.getWriteValue());
                    }
                }
            });
            dataEntity.setDynamicData(map);
            records.add(dataEntity);

            dynamicTableEntity.setRecords(records);

        });
        return dynamicTableEntity;
    }
}
